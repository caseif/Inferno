/*
 * This file is part of Inferno, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, Max Roncace <me@caseif.net>
 * Copyright (c) 2016, contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.caseif.flint.inferno.util.agent.rollback;

import static com.google.common.base.Preconditions.checkState;

import net.caseif.flint.common.arena.CommonArena;
import net.caseif.flint.common.util.agent.rollback.CommonRollbackAgent;
import net.caseif.flint.inferno.InfernoCore;
import net.caseif.flint.inferno.util.converter.WorldLocationConverter;
import net.caseif.flint.util.physical.Location3D;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.translator.ConfigurateTranslator;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.world.World;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

//TODO: this whole class is untested and may explode horribly
public class InfernoRollbackAgent extends CommonRollbackAgent {

    public InfernoRollbackAgent(CommonArena arena) {
        super(arena);
    }

    public void logBlockChange(BlockSnapshot bs) throws IOException, SQLException {
        checkState(bs.getLocation().isPresent(), "BlockSnapshot does not have attached location");
        super.logChange(RECORD_TYPE_BLOCK_CHANGED, WorldLocationConverter.of(bs.getLocation().get()), null,
                bs.getState().getType().getId(), 0, serialize(bs));
    }

    public void logEntityChange(EntitySnapshot es) throws IOException, SQLException {
        checkState(es.getLocation().isPresent(), "EntitySnapshot does not have attached location");
        super.logChange(RECORD_TYPE_ENTITY_CHANGED, WorldLocationConverter.of(es.getLocation().get()), null,
                es.getType().getId(), 0, serialize(es));
    }

    public void logEntityCreation(Entity entity) throws IOException, SQLException {
        super.logChange(RECORD_TYPE_ENTITY_CREATED, WorldLocationConverter.of(entity.getLocation()),
                entity.getUniqueId(), entity.getType().getId(), 0, null);
    }

    @Override
    public void rollbackBlock(int id, Location3D location, String type, int data, String stateSerial)
            throws IOException {
        deserializeBlock(stateSerial);
    }

    @Override
    public void rollbackEntityChange(int id, UUID uuid, Location3D location, String type, String stateSerial)
            throws IOException {
        deserializeEntity(stateSerial);
    }

    @Override
    public void rollbackEntityCreation(int id, UUID uuid) {
        Optional<World> world = Sponge.getServer().getWorld(getArena().getWorld());
        checkState(world.isPresent(), "Arena world is not present");

        Optional<Entity> entity = world.get().getEntity(uuid);
        if (entity.isPresent()) {
            entity.get().remove();
        }
    }

    @Override
    public void cacheEntities() {
        throw new UnsupportedOperationException(); // not actually necessary on Sponge
    }

    private String serialize(DataSerializable serializable) throws IOException {
        ConfigurateTranslator translator = ConfigurateTranslator.instance();
        ConfigurationNode node = translator.translateData(serializable.toContainer());

        StringWriter strWriter = new StringWriter();
        final BufferedWriter sink = new BufferedWriter(strWriter);
        HoconConfigurationLoader.builder().setSink(() -> sink).build().save(node);

        return strWriter.toString();
    }

    private <T extends DataSerializable> T deserialize(Class<T> clazz, String serial) throws IOException {
        BufferedReader source = new BufferedReader(new StringReader(serial));
        ConfigurationNode node = HoconConfigurationLoader.builder().setSource(() -> source).build().load();

        DataManager manager = Sponge.getDataManager();
        ConfigurateTranslator translator = ConfigurateTranslator.instance();

        Optional<T> snapshot = manager.deserialize(clazz, translator.translateFrom(node));
        if (snapshot.isPresent()) {
            return snapshot.get();
        } else {
            throw new IOException("Failed to deserialize to BlockSnapshot!");
        }
    }

    private void deserializeBlock(String serial) throws IOException {
        BlockSnapshot bs = deserialize(BlockSnapshot.class, serial);
        bs.restore(true, false);
    }

    private void deserializeEntity(String serial) throws IOException {
        EntitySnapshot es = deserialize(EntitySnapshot.class, serial);
        Optional<Entity> entity = es.restore();

        if (!entity.isPresent()) {
            InfernoCore.logVerbose("Failed to restore entity in arena " + getArena().getId() + " with UUID "
                    + es.getUniqueId());
        }
    }

}
