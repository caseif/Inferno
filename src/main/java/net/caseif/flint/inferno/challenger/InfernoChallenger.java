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

package net.caseif.flint.inferno.challenger;

import net.caseif.flint.challenger.Challenger;
import net.caseif.flint.common.challenger.CommonChallenger;
import net.caseif.flint.component.exception.OrphanedComponentException;
import net.caseif.flint.inferno.round.InfernoRound;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

/**
 * The implementation of {@link Challenger} for Inferno.
 */
public class InfernoChallenger extends CommonChallenger {

    private final Player player;
    private GameMode previousGameMode;

    public InfernoChallenger(Player player, InfernoRound round) {
        super(player.getUniqueId(), player.getName(), round);

        this.player = player;
    }

    @Override
    public void setSpectating(boolean spectating) throws OrphanedComponentException {
        super.setSpectating(spectating);

        if (spectating) {
            this.previousGameMode = this.player.gameMode().get();
            this.player.gameMode().set(GameModes.SPECTATOR);
        } else if (this.previousGameMode != null) {
            this.player.gameMode().set(this.previousGameMode);
            this.previousGameMode = null;
        }
    }
}
