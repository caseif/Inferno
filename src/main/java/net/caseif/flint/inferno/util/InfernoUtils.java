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

import net.caseif.flint.common.util.PlatformUtils;
import net.caseif.flint.inferno.InfernoPlugin;
import net.caseif.flint.minigame.Minigame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InfernoUtils implements PlatformUtils {

    private static final String MINIGAME_DATA_FOLDER = "minigames";

    @Override
    public File getDataFolder() {
        File folder = InfernoPlugin.getInstance().getConfigDirectory();
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder;
    }

    @Override
    public File getDataFolder(Minigame minigame) {
        Path dir = Paths.get(getDataFolder().getAbsolutePath(), MINIGAME_DATA_FOLDER, minigame.getPlugin());
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to create data directory for minigame " + minigame.getPlugin(), ex);
            }
        }
        return dir.toFile();
    }

}
