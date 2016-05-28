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

package net.caseif.flint.inferno.util.agent.chat;

import net.caseif.flint.common.util.agent.chat.IChatAgent;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;
import java.util.UUID;

/**
 * The implementation of {@link IChatAgent}.
 */
public class InfernoChatAgent implements IChatAgent {

    @Override
    public void processAndSend(UUID recipient, String... message) throws IllegalArgumentException {
        for (String msg : message) {
            this.processAndSend(recipient, msg);
        }
    }

    @SuppressWarnings("deprecation")
    private void processAndSend(UUID recipient, String message) throws IllegalArgumentException {
        Optional<Player> player = Sponge.getServer().getPlayer(recipient);

        if (player.isPresent()) {
            player.get().sendMessage(TextSerializers.LEGACY_FORMATTING_CODE.deserialize(message));
        } else {
            throw new IllegalArgumentException("Player with specified UUID not found!");
        }
    }

}
