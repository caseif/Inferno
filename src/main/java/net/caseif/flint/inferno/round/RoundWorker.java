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

package net.caseif.flint.inferno.round;

import static com.google.common.base.Preconditions.checkState;

import net.caseif.flint.challenger.Challenger;
import net.caseif.flint.common.round.CommonRoundWorker;
import net.caseif.flint.config.ConfigNode;
import net.caseif.flint.inferno.util.converter.WorldLocationConverter;
import net.caseif.flint.util.physical.Boundary;
import net.caseif.flint.util.physical.Location3D;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class RoundWorker extends CommonRoundWorker {

    public RoundWorker(InfernoRound round) {
        super(round);
    }

    protected void checkPlayerLocations() {
        Boundary bound = this.getRound().getArena().getBoundary();
        for (Challenger challenger : this.getRound().getChallengers()) {
            Optional<Player> player = Sponge.getServer().getPlayer(challenger.getUniqueId());

            checkState(player.isPresent(), "Challenger's player is not present");

            Location3D loc = WorldLocationConverter.of(player.get().getLocation());
            if (!bound.contains(loc)) {
                if (this.getRound().getConfigValue(ConfigNode.ALLOW_EXIT_BOUNDARY)) {
                    challenger.removeFromRound();
                } else {
                    double x = loc.getX() > bound.getUpperBound().getX() ? bound.getUpperBound().getX()
                            : loc.getX() < bound.getLowerBound().getX() ? bound.getLowerBound().getX()
                                    : loc.getX();
                    double y = loc.getY() > bound.getUpperBound().getY() ? bound.getUpperBound().getY()
                            : loc.getY() < bound.getLowerBound().getY() ? bound.getLowerBound().getY()
                                    : loc.getY();
                    double z = loc.getZ() > bound.getUpperBound().getZ() ? bound.getUpperBound().getZ()
                            : loc.getZ() < bound.getLowerBound().getZ() ? bound.getLowerBound().getZ()
                                    : loc.getZ();
                    player.get().setLocation(player.get().getWorld().getLocation(x, y, z));
                }
            }
        }
    }

}
