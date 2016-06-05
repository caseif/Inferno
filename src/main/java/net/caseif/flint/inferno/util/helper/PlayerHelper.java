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

import net.caseif.flint.common.util.file.CommonDataFiles;
import net.caseif.flint.inferno.InfernoCore;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Optional;

public class PlayerHelper {

    public static void pushInventory(Player player) throws IOException {
        File dir = CommonDataFiles.PLAYER_INVENTORY_DIR.getFile();
        File store = new File(dir, player.getUniqueId().toString() + ".json");

        JsonArray json = new JsonArray();
        player.getInventory().slots().forEach(slot -> {
            Optional<ItemStack> item = slot.peek();
            if (item.isPresent()) {
                try {
                    json.add(new JsonPrimitive(SerializationHelper.serialize(item.get())));
                } catch (IOException ex) {
                    InfernoCore.logWarning("Failed to serialize ItemStack from inventory of player "
                            + player.getName());
                    ex.printStackTrace();
                }
            } else {
                json.add(JsonNull.INSTANCE);
            }
        });

        if (!store.exists()) {
            Files.createFile(store.toPath());
        }
        try (FileWriter writer = new FileWriter(store)) {
            writer.write(json.toString());
        }

        player.getInventory().clear();
    }

    public static void popInventory(Player player) throws IllegalStateException, IOException {
        File dir = CommonDataFiles.PLAYER_INVENTORY_DIR.getFile();
        File store = new File(dir, player.getUniqueId().toString() + ".json");

        if (!store.exists()) {
            throw new IllegalStateException("Inventory for player " + player.getName() + " is not stored");
        }

        try (FileReader reader = new FileReader(store)) {
            JsonElement json = new JsonParser().parse(reader);
            if (!(json instanceof JsonArray)) {
                throw new IOException("Invalid inventory serialization for player " + player.getName()
                        + " (try deleting the file?)");
            }

            player.getInventory().clear();

            Iterator<JsonElement> it = json.getAsJsonArray().iterator();
            player.getInventory().slots().forEach(slot -> {
                if (!it.hasNext()) {
                    return;
                }

                JsonElement el = it.next();

                if (el.isJsonPrimitive()) {
                    try {
                        ItemStack is = SerializationHelper.deserialize(ItemStack.class, el.getAsString());
                        slot.offer(is);
                    } catch (IOException ex) {
                        InfernoCore.logWarning("Failed to deserialize ItemStack from inventory of player "
                                + player.getName());
                        ex.printStackTrace();
                    }
                } else if (el.isJsonNull()) {
                    player.getInventory().next();
                } else {
                    InfernoCore.logWarning("Found non-primitive in inventory store for player " + player.getName());
                    return;
                }
            });
        }

        Files.delete(store.toPath());
    }

}
