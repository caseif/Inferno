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
import net.caseif.flint.common.lobby.wizard.IWizardManager;
import net.caseif.flint.config.ConfigNode;
import net.caseif.flint.inferno.minigame.InfernoMinigame;
import net.caseif.flint.inferno.util.converter.LocationConverter;
import net.caseif.flint.inferno.util.helper.ChatHelper;
import net.caseif.flint.minigame.Minigame;
import net.caseif.flint.util.physical.Boundary;

import com.google.common.base.Optional;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.Iterator;

/**
 * Listener for events relating to players in the world.
 */
public class PlayerWorldListener {

    @Listener(order = Order.LAST)
    public void onEntityTeleport(DisplaceEntityEvent.Teleport event) {
        if (event.getTargetEntity().getType() != EntityTypes.PLAYER) {
            return; // not a player
        }

        if (event.getFromTransform().getPosition().equals(event.getToTransform().getPosition())) {
            return; // player hasn't moved
        }

        Optional<Challenger> chal = CommonCore.getChallenger(event.getTargetEntity().getUniqueId());
        if (chal.isPresent()) {
            Boundary bound = chal.get().getRound().getArena().getBoundary();
            if (!bound.contains(LocationConverter.of(event.getToTransform().getPosition()))) {
                if (chal.get().getRound().getConfigValue(ConfigNode.ALLOW_EXIT_BOUNDARY)) {
                    chal.get().removeFromRound();;
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener(order = Order.EARLY)
    public void onPlayerChat(MessageChannelEvent.Chat event) {
        java.util.Optional<Player> player = event.getCause().first(Player.class);
        if (!player.isPresent()) {
            return;
        }

        for (Minigame mg : CommonCore.getMinigames().values()) {
            IWizardManager wm = ((InfernoMinigame) mg).getLobbyWizardManager();
            // check if the player is in a wizard
            if (wm.hasPlayer(player.get().getUniqueId())) {
                event.setCancelled(true);
                // send the original message since the event is cancelled
                player.get().sendMessage(Text.builder().append(Text.of("<"))
                        .append(player.get().getDisplayNameData().displayName().get()).append(Text.of("> "))
                        .append(event.getMessage()).build());

                // feed the message to the wizard manager and get the response
                String[] response = wm.accept(player.get().getUniqueId(), event.getMessage().toPlain());
                CommonCore.getChatAgent().processAndSend(player.get().getUniqueId(), response);

                return; // no need for further checks
            }

            if (!event.getChannel().isPresent()) {
                return;
            }

            Iterator<MessageReceiver> it = event.getChannel().get().getMembers().iterator();
            while (it.hasNext()) {
                MessageReceiver recip = it.next();

                if (!(recip instanceof Player)) {
                    return;
                }

                // check if the recipient is in a lobby wizard
                if (((InfernoMinigame) mg).getLobbyWizardManager().hasPlayer(((Player) recip).getUniqueId())) {
                    ((InfernoMinigame) mg).getLobbyWizardManager().withholdMessage(
                            ((Player) recip).getUniqueId(),
                            player.get().getDisplayNameData().displayName().get().toPlain(),
                            event.getMessage().toPlain()
                    );
                    it.remove();
                    continue;
                }

                // check if a chat barrier exists between the sender and recipient
                if (ChatHelper.isBarrierPresent(player.get().getUniqueId(), ((Player) recip).getUniqueId())) {
                    it.remove();
                }
            }
        }
    }

}
