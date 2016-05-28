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

package net.caseif.flint.inferno.util.factory;

import net.caseif.flint.common.util.factory.IMinigameFactory;
import net.caseif.flint.inferno.minigame.InfernoMinigame;
import net.caseif.flint.minigame.Minigame;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Optional;

/**
 * The implementation of {@link IMinigameFactory}.
 */
public class InfernoMinigameFactory implements IMinigameFactory {

    @Override
    public Minigame createMinigame(String pluginId) {
        Optional<PluginContainer> pluginContainer = Sponge.getPluginManager().getPlugin(pluginId);

        if (pluginContainer.isPresent()) {
            return new InfernoMinigame(pluginContainer.get());
        } else {
            throw new UnsupportedOperationException("No plugin exists of id: " + pluginId + "!");
        }
    }
}
