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

package net.caseif.flint.inferno;

import net.caseif.flint.inferno.listener.misc.LobbyListener;

import com.google.inject.Inject;
import net.minecrell.mcstats.SpongeStatsLite;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

/**
 * The Inferno Sponge plugin.
 */
@Plugin(id = "inferno",
        name = "Inferno",
        description = "The Sponge implementation of the Flint engine.")
public final class InfernoPlugin {

    private static InfernoPlugin instance;

    @Inject private SpongeStatsLite stats;
    @Inject private Logger logger;

    public InfernoPlugin() {
        instance = this;
    }

    @Listener
    public void onPreInitialize(GamePreInitializationEvent event) {
        this.stats.start();

        // Register event listeners
        Sponge.getEventManager().registerListeners(this, new LobbyListener());
    }

    /**
     * Gets the plugin's {@link Logger}.
     *
     * @return The logger
     */
    public Logger getLogger() {
        return this.logger;
    }

    public static InfernoPlugin getInstance() {
        return InfernoPlugin.instance;
    }

}
