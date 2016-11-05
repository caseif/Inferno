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

package net.caseif.flint.inferno.lobby.populator;

import net.caseif.flint.common.lobby.populator.StockStatusLobbySignPopulator;
import net.caseif.flint.lobby.LobbySign;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

public class RichStockStatusLobbySignPopulator extends StockStatusLobbySignPopulator {

    private static final TextColor[] STATUS_COLORS = new TextColor[] {TextColors.DARK_AQUA, TextColors.DARK_PURPLE,
            TextColors.DARK_PURPLE, TextColors.DARK_BLUE};

    public String first(LobbySign sign) {
        return colorize(super.first(sign), STATUS_COLORS[0]);
    }

    public String second(LobbySign sign) {
        return colorize(super.second(sign), STATUS_COLORS[1]);
    }

    public String third(LobbySign sign) {
        return colorize(super.third(sign), STATUS_COLORS[2]);
    }

    public String fourth(LobbySign sign) {
        return colorize(super.fourth(sign), STATUS_COLORS[3]);
    }

    private static String colorize(String str, TextColor color) {
        return serialize(Text.of(color, str));
    }

    @SuppressWarnings("deprecation")
    private static String serialize(Text text) {
        return TextSerializers.LEGACY_FORMATTING_CODE.serialize(text);
    }

}
