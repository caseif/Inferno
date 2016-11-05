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

package net.caseif.flint.inferno.arena;

import static com.google.common.base.Preconditions.checkArgument;

import net.caseif.flint.common.arena.CommonArena;
import net.caseif.flint.common.lobby.CommonLobbySign;
import net.caseif.flint.common.minigame.CommonMinigame;
import net.caseif.flint.component.exception.OrphanedComponentException;
import net.caseif.flint.exception.rollback.RollbackException;
import net.caseif.flint.inferno.InfernoPlugin;
import net.caseif.flint.inferno.lobby.type.InfernoChallengerListingLobbySign;
import net.caseif.flint.inferno.lobby.type.InfernoStatusLobbySign;
import net.caseif.flint.inferno.util.agent.rollback.InfernoRollbackAgent;
import net.caseif.flint.inferno.util.converter.LocationConverter;
import net.caseif.flint.inferno.util.converter.WorldLocationConverter;
import net.caseif.flint.lobby.LobbySign;
import net.caseif.flint.lobby.type.ChallengerListingLobbySign;
import net.caseif.flint.lobby.type.StatusLobbySign;
import net.caseif.flint.util.physical.Boundary;
import net.caseif.flint.util.physical.Location3D;

import com.google.common.base.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.sql.SQLException;

public class InfernoArena extends CommonArena {

    public InfernoArena(CommonMinigame parent, String id, String name, Location3D[] initialSpawn, Boundary boundary)
            throws IllegalArgumentException {
        super(parent, id, name, initialSpawn, boundary);
    }

    @Override
    public Optional<StatusLobbySign> createStatusLobbySign(Location3D location)
            throws IllegalArgumentException, OrphanedComponentException {
        checkState();

        if (checkLocationForLobbySign(location)) {
            return storeAndWrap(new InfernoStatusLobbySign(location, this));
        }
        return Optional.absent();
    }

    @Override
    public Optional<ChallengerListingLobbySign> createChallengerListingLobbySign(Location3D location, int index)
            throws IllegalArgumentException, OrphanedComponentException {
        checkState();

        if (checkLocationForLobbySign(location)) {
            return storeAndWrap(new InfernoChallengerListingLobbySign(location, this, index));
        }
        return Optional.absent();
    }

    @Override
    public void markForRollback(Location3D location)
            throws IllegalArgumentException, RollbackException, OrphanedComponentException {
        checkState();

        checkArgument(location.getWorld().equals(getWorld()),
                "Cannot roll back block change in separate world from arena");

        try {
            ((InfernoRollbackAgent) getRollbackAgent())
                    .logBlockChange(WorldLocationConverter.of(location).createSnapshot());
        } catch (IOException | SQLException ex) {
            throw new RollbackException(ex);
        }
    }

    private boolean checkLocationForLobbySign(Location3D location) throws IllegalArgumentException {
        checkArgument(location.getWorld().isPresent(), "Location for lobby sign must contain world");
        java.util.Optional<World> world = Sponge.getServer().getWorld(location.getWorld().get());
        if (!world.isPresent()) {
            throw new IllegalArgumentException("Invalid world for lobby sign location");
        }

        Location<World> loc = WorldLocationConverter.of(location);
        if (!getLobbySignMap().containsKey(location)) {
            java.util.Optional<TileEntity> tile = loc.getExtent().getTileEntity(LocationConverter.floor(location));
            if (!tile.isPresent() || tile.get().getType() != TileEntityTypes.SIGN) {
                //TODO: maybe detect which sign type it should be?
                loc.setBlockType(BlockTypes.WALL_SIGN,
                        Cause.of(NamedCause.of("inferno:lobbyCreation", InfernoPlugin.getInstance())));
            }
            return true;
        }
        return false;
    }

    private <T extends LobbySign> Optional<T> storeAndWrap(T sign) {
        ((CommonLobbySign) sign).store();
        getLobbySignMap().put(sign.getLocation(), sign);
        return Optional.of(sign);
    }

}
