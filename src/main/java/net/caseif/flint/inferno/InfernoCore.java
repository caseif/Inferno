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

import net.caseif.flint.FlintCore;
import net.caseif.flint.arena.Arena;
import net.caseif.flint.common.CommonCore;
import net.caseif.flint.common.component.CommonComponent;
import net.caseif.flint.common.util.agent.chat.IChatAgent;
import net.caseif.flint.common.util.agent.rollback.IRollbackAgent;
import net.caseif.flint.common.util.factory.FactoryRegistry;
import net.caseif.flint.inferno.util.InfernoUtils;
import net.caseif.flint.inferno.util.agent.chat.InfernoChatAgent;
import net.caseif.flint.inferno.util.factory.InfernoArenaFactory;
import net.caseif.flint.inferno.util.factory.InfernoLobbySignFactory;
import net.caseif.flint.inferno.util.factory.InfernoMinigameFactory;
import net.caseif.flint.inferno.util.factory.InfernoRollbackAgentFactory;
import net.caseif.flint.inferno.util.factory.InfernoRoundFactory;
import net.caseif.flint.inferno.util.unsafe.InfernoUnsafeUtil;
import net.caseif.flint.lobby.LobbySign;
import net.caseif.flint.minigame.Minigame;
import net.caseif.flint.round.Round;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;

import java.util.concurrent.TimeUnit;

/**
 * The Sponge implementation of {@link FlintCore}.
 */
public class InfernoCore extends CommonCore {

    private static final InfernoChatAgent CHAT_AGENT = new InfernoChatAgent();

    static void initialize() {
        INSTANCE = new InfernoCore();

        CommonCore.initializeCommon();
        registerFactories();
        InfernoUnsafeUtil.initialize();

        PLATFORM_UTILS = new InfernoUtils();
    }

    @Override
    protected void logInfo0(String message) {
        InfernoPlugin.getInstance().getLogger().info(message);
    }

    @Override
    protected void logWarning0(String message) {
        InfernoPlugin.getInstance().getLogger().warn(message);
    }

    @Override
    protected void logSevere0(String message) {
        InfernoPlugin.getInstance().getLogger().error(message);
    }

    @Override
    protected void logVerbose0(String message) {
        this.logInfo0(message); //TODO: do config check for verbosity
    }

    @Override
    protected void orphan0(CommonComponent<?> component) {
        Sponge.getScheduler().createSyncExecutor(InfernoPlugin.getInstance())
                .schedule(component::setOrphanFlag, 0, TimeUnit.SECONDS);
    }

    @Override
    protected IChatAgent getChatAgent0() {
        return CHAT_AGENT;
    }

    @Override
    protected String getImplementationName0() {
        return StringUtils.capitalize(InfernoCore.class.getPackage().getImplementationTitle());
    }

    private static void registerFactories() {
        FactoryRegistry.registerFactory(Arena.class, new InfernoArenaFactory());
        FactoryRegistry.registerFactory(LobbySign.class, new InfernoLobbySignFactory());
        FactoryRegistry.registerFactory(Minigame.class, new InfernoMinigameFactory());
        FactoryRegistry.registerFactory(IRollbackAgent.class, new InfernoRollbackAgentFactory());
        FactoryRegistry.registerFactory(Round.class, new InfernoRoundFactory());
    }

}
