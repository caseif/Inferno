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
import net.caseif.flint.common.util.agent.rollback.RollbackRecord;
import net.caseif.flint.inferno.InfernoCore;
import net.caseif.flint.inferno.InfernoPlugin;
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
import org.spongepowered.api.world.BlockChangeFlags;
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

    @Override
    protected void delay(Runnable runnable) {
        Sponge.getScheduler().createTaskBuilder().delayTicks(1L).execute(runnable).submit(InfernoPlugin.getInstance());
    }

    public void logBlockChange(BlockSnapshot bs) throws IOException, SQLException {
        checkState(bs.getLocation().isPresent(), "BlockSnapshot does not have attached location");;
        super.logChange(RollbackRecord.createBlockRecord(-1, WorldLocationConverter.of(bs.getLocation().get()),
                        bs.getState().getType().getId(), 0, SerializationHelper.serialize(bs)));
    }

    public void logEntityChange(EntitySnapshot es) throws IOException, SQLException {
        checkState(es.getLocation().isPresent(), "EntitySnapshot does not have attached location");
        super.logChange(RollbackRecord.createEntityChangeRecord(-1,
                es.getUniqueId().orElse(UUID.randomUUID()), // maybe I'm a terrible person for this, idk
                WorldLocationConverter.of(es.getLocation().get()),
                es.getType().getId(), SerializationHelper.serialize(es)));
    }

    private void logEntityCreation(UUID uuid) throws IOException, SQLException {
        super.logChange(RollbackRecord.createEntityCreationRecord(-1, uuid, getArena().getWorld()));
    }

    @Override
    public void rollbackBlock(RollbackRecord record) throws IOException {
        deserializeBlock(record.getStateSerial());
    }

    @Override
    public void rollbackEntityChange(RollbackRecord record) throws IOException {
        deserializeEntity(record.getStateSerial());
    }

    @Override
    public void rollbackEntityCreation(RollbackRecord record) {
        Optional<World> world = Sponge.getServer().getWorld(getArena().getWorld());
        checkState(world.isPresent(), "Arena world is not present");

        Optional<Entity> entity = world.get().getEntity(record.getUuid());
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
        bs.restore(true, BlockChangeFlags.NONE);
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
