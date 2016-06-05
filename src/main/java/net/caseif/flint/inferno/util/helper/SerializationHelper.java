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

package net.caseif.flint.inferno.util.helper;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.translator.ConfigurateTranslator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

public class SerializationHelper {

    public static String serialize(DataSerializable serializable) throws IOException {
        ConfigurateTranslator translator = ConfigurateTranslator.instance();
        ConfigurationNode node = translator.translateData(serializable.toContainer());

        StringWriter strWriter = new StringWriter();
        final BufferedWriter sink = new BufferedWriter(strWriter);
        HoconConfigurationLoader.builder().setSink(() -> sink).build().save(node);

        return strWriter.toString();
    }

    public static <T extends DataSerializable> T deserialize(Class<T> clazz, String serial) throws IOException {
        BufferedReader source = new BufferedReader(new StringReader(serial));
        ConfigurationNode node = HoconConfigurationLoader.builder().setSource(() -> source).build().load();

        DataManager manager = Sponge.getDataManager();
        ConfigurateTranslator translator = ConfigurateTranslator.instance();

        Optional<T> snapshot = manager.deserialize(clazz, translator.translateFrom(node));
        if (snapshot.isPresent()) {
            return snapshot.get();
        } else {
            throw new IOException("Failed to deserialize to " + clazz.getSimpleName());
        }
    }

}
