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

import net.caseif.flint.arena.Arena;
import net.caseif.flint.common.util.agent.rollback.IRollbackAgent;
import net.caseif.flint.common.util.factory.FactoryRegistry;
import net.caseif.flint.common.util.file.CommonDataFiles;
import net.caseif.flint.inferno.listener.misc.LobbyListener;
import net.caseif.flint.inferno.listener.player.PlayerConnectionListener;
import net.caseif.flint.inferno.listener.player.PlayerWorldListener;
import net.caseif.flint.inferno.listener.rollback.RollbackBlockListener;
import net.caseif.flint.inferno.listener.rollback.RollbackEntityListener;
import net.caseif.flint.inferno.listener.rollback.RollbackInventoryListener;
import net.caseif.flint.inferno.util.factory.InfernoArenaFactory;
import net.caseif.flint.lobby.LobbySign;
import net.caseif.flint.minigame.Minigame;
import net.caseif.flint.round.Round;

import com.google.inject.Inject;
import net.minecrell.mcstats.SpongeStatsLite;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;

/**
 * The Inferno Sponge plugin.
 */
@Plugin(id = "inferno",
        name = "Inferno",
        version = "1.3.0-SNAPSHOT",
        description = "The Sponge implementation of the Flint engine.")
public final class InfernoPlugin {

    private static InfernoPlugin instance;

    @Inject private SpongeStatsLite stats;
    @Inject private Logger logger;
    @Inject @ConfigDir(sharedRoot = false) private File configDir;

    public InfernoPlugin() {
        instance = this;
    }

    @Listener(order = Order.PRE)
    public void onPreInitialize(GamePreInitializationEvent event) {
        InfernoCore.initialize();
        CommonDataFiles.createCoreDataFiles();
        registerEventListeners();
        this.stats.start();
    }

    @Listener
    public void onGameStopping(GameStoppingEvent event) {
        InfernoCore.getMinigames().values().forEach(mg -> mg.getRounds().forEach(Round::end));
    }

    /**
     * Gets the plugin's {@link Logger}.
     *
     * @return The logger
     */
    Logger getLogger() {
        return this.logger;
    }

    public File getConfigDirectory() {
        return configDir;
    }

    public static InfernoPlugin getInstance() {
        return InfernoPlugin.instance;
    }

    private void registerEventListeners() {
        // Register event listeners
        Sponge.getEventManager().registerListeners(this, new LobbyListener());

        Sponge.getEventManager().registerListeners(this, new PlayerConnectionListener());
        Sponge.getEventManager().registerListeners(this, new PlayerWorldListener());

        Sponge.getEventManager().registerListeners(this, new RollbackBlockListener());
        Sponge.getEventManager().registerListeners(this, new RollbackEntityListener());
        Sponge.getEventManager().registerListeners(this, new RollbackInventoryListener());
    }

}
