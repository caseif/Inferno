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

package net.caseif.flint.inferno.util.unsafe;

import net.caseif.flint.common.lobby.populator.StockChallengerListingLobbySignPopulator;
import net.caseif.flint.common.util.unsafe.CommonUnsafeUtil;
import net.caseif.flint.inferno.lobby.populator.RichStockStatusLobbySignPopulator;
import net.caseif.flint.lobby.populator.LobbySignPopulator;
import net.caseif.flint.util.unsafe.UnsafeUtil;

public class InfernoUnsafeUtil extends CommonUnsafeUtil {

    private static final LobbySignPopulator STATUS_POPULATOR = new RichStockStatusLobbySignPopulator();
    private static final LobbySignPopulator LISTING_POPULATOR = new StockChallengerListingLobbySignPopulator();

    public static void initialize() {
        INSTANCE = new InfernoUnsafeUtil();
    }

    @Override
    public LobbySignPopulator getDefaultStatusLobbySignPopulator() {
        testInternalUse();
        return STATUS_POPULATOR;
    }

    @Override
    public LobbySignPopulator getDefaultChallengerListingLobbySignPopulator() {
        testInternalUse();
        return LISTING_POPULATOR;
    }

}
