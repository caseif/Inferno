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

package net.caseif.flint.inferno.listener.misc;

import net.caseif.flint.common.CommonCore;
import net.caseif.flint.common.event.lobby.CommonPlayerClickLobbySignEvent;
import net.caseif.flint.common.lobby.wizard.IWizardManager;
import net.caseif.flint.config.ConfigNode;
import net.caseif.flint.event.lobby.PlayerClickLobbySignEvent;
import net.caseif.flint.inferno.InfernoCore;
import net.caseif.flint.inferno.minigame.InfernoMinigame;
import net.caseif.flint.inferno.util.converter.WorldLocationConverter;
import net.caseif.flint.minigame.Minigame;
import net.caseif.flint.util.physical.Location3D;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Map;
import java.util.Optional;

/**
 * The event listener for the lobby.
 */
public final class LobbyListener {

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        for (Transaction<BlockSnapshot> blockSnapshotTransaction : event.getTransactions()) {
            final BlockSnapshot original = blockSnapshotTransaction.getOriginal();

            if (original.getState().getType() == BlockTypes.STANDING_SIGN
                    || original.getState().getType() == BlockTypes.WALL_SIGN) {
                if (original.getLocation().isPresent()) {
                    final Location3D location3D = WorldLocationConverter.of(original.getLocation().get());

                    // iterate all arenas of all minigames to see if sign is registered
                    //TODO: This is more expensive than it needs to be.
                    //      We should maintain a global index of registered signs.
                    if (InfernoCore.getMinigames().values().stream()
                            .filter(minigame -> minigame.getArenas().stream()
                                    .filter(arena -> arena.getLobbySignAt(location3D).isPresent()).count() > 0)
                            .count() > 0) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @Listener
    public void onPlayerInteract(InteractBlockEvent event) {
        BlockState state = event.getTargetBlock().getState();
        if (state.getType() == BlockTypes.WALL_SIGN || state.getType() == BlockTypes.STANDING_SIGN) {
            Optional<Player> player = event.getCause().first(Player.class);
            if (!event.getTargetBlock().getLocation().isPresent() || !player.isPresent()) {
                return;
            }
            Location3D loc = WorldLocationConverter.of(event.getTargetBlock().getLocation().get());

            CommonCore.getMinigames().values().forEach(mg -> mg.getArenas().stream()
                    .filter(arena -> arena.getLobbySignAt(loc).isPresent()).forEach(arena -> {
                        Optional<Boolean> sneaking = player.get().get(Keys.IS_SNEAKING);
                        if (event instanceof InteractBlockEvent.Primary
                                && sneaking.isPresent() && sneaking.get()
                                || !mg.getConfigValue(ConfigNode.REQUIRE_SNEAK_TO_DESTROY_LOBBY)) {
                            if (player.get().hasPermission(mg.getPlugin() + ".lobby.destroy")
                                    || player.get().hasPermission(mg.getPlugin() + ".lobby.*")) {
                                arena.getLobbySignAt(loc).get().unregister();
                                return;
                            }
                        }
                        mg.getEventBus().post(new CommonPlayerClickLobbySignEvent(
                                player.get().getUniqueId(),
                                arena.getLobbySignAt(loc).get(),
                                event instanceof InteractBlockEvent.Primary
                                        ? PlayerClickLobbySignEvent.ClickType.LEFT
                                        : PlayerClickLobbySignEvent.ClickType.RIGHT
                        ));
                    })
            );
        }
    }

    @Listener
    public void onSignChange(ChangeSignEvent event) {
        for (Map.Entry<String, Minigame> entry : CommonCore.getMinigames().entrySet()) {
            if (event.getText().get(0).get().toPlain().equalsIgnoreCase("[" + entry.getKey() + "]")) {
                if (entry.getValue().getConfigValue(ConfigNode.ENABLE_LOBBY_WIZARD)) {
                    Optional<Player> player = event.getCause().first(Player.class);
                    if (!player.isPresent()) {
                        return;
                    }

                    if (player.get().hasPermission(entry.getKey() + ".lobby.create")
                            || player.get().hasPermission(entry.getKey() + ".lobby.*")) {
                        IWizardManager wm = ((InfernoMinigame) entry.getValue()).getLobbyWizardManager();
                        if (!wm.hasPlayer(player.get().getUniqueId())) {
                            wm.addPlayer(player.get().getUniqueId(),
                                    WorldLocationConverter.of(event.getTargetTile().getLocation()));
                        } else {
                            player.get().sendMessage(Text.builder().color(TextColors.RED)
                                    .append(LiteralText.of("You are already in a lobby sign wizard")).toText());
                        }
                    } else {
                        player.get().sendMessage(Text.builder().color(TextColors.RED)
                                .append(LiteralText.of("You do not have permission to do this")).toText());
                    }
                }
                return;
            }
        }
    }

}
