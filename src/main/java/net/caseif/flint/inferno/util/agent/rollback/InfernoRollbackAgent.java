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

import net.caseif.flint.arena.Arena;
import net.caseif.flint.common.arena.CommonArena;
import net.caseif.flint.common.util.agent.rollback.CommonRollbackAgent;
import net.caseif.flint.inferno.InfernoCore;
import net.caseif.flint.inferno.arena.InfernoArena;
import net.caseif.flint.inferno.util.converter.WorldLocationConverter;
import net.caseif.flint.inferno.util.helper.SerializationHelper;
import net.caseif.flint.util.physical.Location3D;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
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
                bs.getState().getType().getId(), 0, SerializationHelper.serialize(bs));
    }

    public void logEntityChange(EntitySnapshot es) throws IOException, SQLException {
        checkState(es.getLocation().isPresent(), "EntitySnapshot does not have attached location");
        super.logChange(RECORD_TYPE_ENTITY_CHANGED, WorldLocationConverter.of(es.getLocation().get()),
                es.getUniqueId().orElse(UUID.randomUUID()), // maybe I'm a terrible person for this, idk
                es.getType().getId(), 0, SerializationHelper.serialize(es));
    }

    private void logEntityCreation(UUID uuid) throws IOException, SQLException {
        super.logChange(RECORD_TYPE_ENTITY_CREATED, new Location3D(getArena().getWorld(), 0, 0, 0), uuid, "", 0, null);
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
        // the ONE method I decide to call from Common. gotta settle for a no-op.
    }

    public static void checkBlockChange(Transaction<BlockSnapshot> transaction) {
        checkBlockChange(transaction.getOriginal());
    }

    public static void checkBlockChange(BlockSnapshot block) {
        Optional<Location<World>> loc = block.getLocation();
        if (!loc.isPresent()) {
            return;
        }

        List<Arena> arenas = checkChangeAtLocation(WorldLocationConverter.of(loc.get()));
        for (Arena arena : arenas) {
            try {
                ((InfernoRollbackAgent) ((InfernoArena) arena).getRollbackAgent()).logBlockChange(block);
            } catch (IOException | SQLException ex) {
                throw new RuntimeException("Failed to log block mutation event for rollback in arena "
                        + arena.getDisplayName(), ex);
            }
        }
    }

    public static void checkEntityChange(Entity entity) {
        checkEntityChange(entity, false);
    }

    public static void checkEntityChange(Entity entity, boolean newlyCreated) {
        Location<World> loc = entity.getLocation();
        List<Arena> arenas = checkChangeAtLocation(WorldLocationConverter.of(loc));
        for (Arena arena : arenas) {
            try {
                if (newlyCreated) {
                    ((InfernoRollbackAgent) ((InfernoArena) arena).getRollbackAgent())
                            .logEntityCreation(entity.getUniqueId());
                } else {
                    ((InfernoRollbackAgent) ((InfernoArena) arena).getRollbackAgent())
                            .logEntityChange(entity.createSnapshot());
                }
            } catch (IOException | SQLException ex) {
                throw new RuntimeException("Failed to log entity mutation event for rollback in arena "
                        + arena.getDisplayName(), ex);
            }
        }
    }

    private void deserializeBlock(String serial) throws IOException {
        BlockSnapshot bs = SerializationHelper.deserialize(BlockSnapshot.class, serial);
        bs.restore(true, BlockChangeFlag.NONE);
    }

    private void deserializeEntity(String serial) throws IOException {
        EntitySnapshot es = SerializationHelper.deserialize(EntitySnapshot.class, serial);
        Optional<Entity> entity = es.restore();

        if (!entity.isPresent()) {
            InfernoCore.logVerbose("Failed to restore entity in arena " + getArena().getId() + " with UUID "
                    + es.getUniqueId());
        }
    }

}
