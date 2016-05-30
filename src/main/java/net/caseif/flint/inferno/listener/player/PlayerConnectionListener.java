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

package net.caseif.flint.inferno.listener.player;

import net.caseif.flint.challenger.Challenger;
import net.caseif.flint.common.CommonCore;
import net.caseif.flint.common.util.helper.CommonPlayerHelper;
import net.caseif.flint.inferno.minigame.InfernoMinigame;
import net.caseif.flint.inferno.round.InfernoRound;
import net.caseif.flint.inferno.util.helper.PlayerHelper;
import net.caseif.flint.minigame.Minigame;

import com.google.common.base.Optional;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.io.IOException;

/**
 * Listener for events relating to players' connections.
 *
 * @author Max Roncacé
 */
public final class PlayerConnectionListener {

    @Listener(order = Order.LAST)
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        Optional<Challenger> ch = CommonCore.getChallenger(event.getTargetEntity().getUniqueId());
        if (ch.isPresent()) {
            // store the player to disk so their inventory and location can be popped later
            ((InfernoRound) ch.get().getRound()).removeChallenger(ch.get(), true, true);

            CommonPlayerHelper.setOfflineFlag(event.getTargetEntity().getUniqueId());
        }

        for (Minigame mg : CommonCore.getMinigames().values()) {
            if (((InfernoMinigame) mg).getLobbyWizardManager().hasPlayer(event.getTargetEntity().getUniqueId())) {
                ((InfernoMinigame) mg).getLobbyWizardManager().removePlayer(event.getTargetEntity().getUniqueId());
                break;
            }
        }
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        tryReset(event.getTargetEntity());
    }

    private void tryReset(Player player) {
        if (!tryReset(player, false)) {
            tryReset(player, true);
        }
    }

    private boolean tryReset(Player player, boolean ignoreOfflineFlag) {
        if (ignoreOfflineFlag || CommonPlayerHelper.checkOfflineFlag(player.getUniqueId())) {
            // these two try-blocks are separate so they can both run even if one fails
            try {
                PlayerHelper.popInventory(player);
            } catch (IllegalArgumentException ex) {
                if (!ignoreOfflineFlag) {
                    CommonCore.logSevere("Failed to pop inventory for player " + player.getName());
                    ex.printStackTrace();
                }
            } catch (IOException ex) {
                CommonCore.logSevere("Failed to pop inventory for player " + player.getName());
                ex.printStackTrace();
            }

            try {
                PlayerHelper.popLocation(player);
            } catch (IllegalArgumentException ex) {
                if (!ignoreOfflineFlag) {
                    CommonCore.logSevere("Failed to pop location for player " + player.getName());
                    ex.printStackTrace();
                }
            } catch (IOException ex) {
                CommonCore.logSevere("Failed to pop location for player " + player.getName());
                ex.printStackTrace();
            }

            return true;
        }

        return false;
    }

}
