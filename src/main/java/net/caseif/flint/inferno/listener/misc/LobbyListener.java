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

import net.caseif.flint.inferno.InfernoCore;
import net.caseif.flint.inferno.util.converter.WorldLocationConverter;
import net.caseif.flint.minigame.Minigame;
import net.caseif.flint.util.physical.Location3D;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

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

                    for (Minigame minigame : InfernoCore.getMinigames().values()) {
                        minigame.getArenas().stream().filter(arena -> arena.getLobbySignAt(location3D).isPresent())
                                .forEach(arena -> event.setCancelled(true));
                    }
                }
            }
        }
    }
}
