/*
 * This file is part of Inferno, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, Max Roncace and contributors <me@caseif.net>
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

package net.caseif.flint.inferno.minigame;

import net.caseif.flint.arena.Arena;
import net.caseif.flint.common.minigame.CommonMinigame;
import net.caseif.flint.inferno.InfernoCore;
import net.caseif.flint.minigame.Minigame;
import net.caseif.flint.util.physical.Boundary;
import net.caseif.flint.util.physical.Location3D;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * The implementation of {@link Minigame} for Inferno.
 */
public class InfernoMinigame extends CommonMinigame {

    private final PluginContainer pluginContainer;

    public InfernoMinigame(PluginContainer pluginContainer) {
        super();

        this.pluginContainer = pluginContainer;
    }

    @Override
    protected int checkPhysicalLobbySign(Location3D loc) {
        if (loc.getWorld().isPresent()) {
            Optional<World> world = Sponge.getGame().getServer().getWorld(loc.getWorld().get());

            if (world.isPresent()) {
                BlockState blockState = world.get().getBlock(
                        (int) Math.floor(loc.getX()),
                        (int) Math.floor(loc.getY()),
                        (int) Math.floor(loc.getZ())
                );

                if (blockState.getType() == BlockTypes.STANDING_SIGN
                        || blockState.getType() == BlockTypes.WALL_SIGN) {
                    return 0;
                } else {
                    InfernoCore.logWarning("Found lobby sign with location not containing a sign block. Removing...");
                    return 2;
                }
            } else {
                InfernoCore.logVerbose("Cannot load world \"" + loc.getWorld().get()
                        + "\" - not loading contained lobby sign");
                return 1;
            }
        } else {
            InfernoCore.logWarning("Found lobby sign in store with invalid location serial. Removing...");
            return 2;
        }
    }

    @Override
    public String getPlugin() {
        return this.pluginContainer.getId();
    }

    @Override
    public Arena createArena(String id, String name, Location3D location, Boundary boundary)
            throws IllegalArgumentException {
        return InfernoCore.getFactoryRegistry().getArenaFactory().createArena(this, id, name, location, boundary);
    }
}
