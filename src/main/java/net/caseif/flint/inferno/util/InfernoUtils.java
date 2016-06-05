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

package net.caseif.flint.inferno.util;

import static com.google.common.base.Preconditions.checkState;

import net.caseif.flint.common.util.PlatformUtils;
import net.caseif.flint.inferno.InfernoPlugin;
import net.caseif.flint.minigame.Minigame;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class InfernoUtils implements PlatformUtils {

    @Override
    public File getDataFolder() {
        return InfernoPlugin.getInstance().getConfigDirectory();
    }

    @Override
    public File getDataFolder(Minigame minigame) {
        Optional<PluginContainer> pc = Sponge.getPluginManager().getPlugin(minigame.getPlugin());
        checkState(pc.isPresent(), "Cannot get PluginContainer from Minigame");

        Optional<Path> path = pc.get().getAssetDirectory();
        checkState(path.isPresent(), "Cannot get Path for plugin " + minigame.getPlugin());

        return path.get().toFile();
    }

}
