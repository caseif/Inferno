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

package net.caseif.flint.inferno.round;

import net.caseif.flint.challenger.Challenger;
import net.caseif.flint.common.CommonCore;
import net.caseif.flint.common.arena.CommonArena;
import net.caseif.flint.common.event.round.challenger.CommonChallengerJoinRoundEvent;
import net.caseif.flint.common.round.CommonJoinResult;
import net.caseif.flint.common.round.CommonRound;
import net.caseif.flint.common.util.helper.CommonPlayerHelper;
import net.caseif.flint.component.exception.OrphanedComponentException;
import net.caseif.flint.config.ConfigNode;
import net.caseif.flint.inferno.InfernoCore;
import net.caseif.flint.inferno.InfernoPlugin;
import net.caseif.flint.inferno.challenger.InfernoChallenger;
import net.caseif.flint.inferno.util.converter.WorldLocationConverter;
import net.caseif.flint.inferno.util.helper.PlayerHelper;
import net.caseif.flint.lobby.LobbySign;
import net.caseif.flint.round.JoinResult;
import net.caseif.flint.round.LifecycleStage;

import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InfernoRound extends CommonRound {

    private final Task tickTask;

    public InfernoRound(CommonArena arena, ImmutableSet<LifecycleStage> stages) {
        super(arena, stages);

        tickTask = Sponge.getScheduler().createTaskBuilder()
                .name("infernoround:" + arena.getId())
                .interval(1, TimeUnit.SECONDS)
                .submit(InfernoPlugin.getInstance());

        try {
            arena.getRollbackAgent().createRollbackDatabase();
        } catch (IOException | SQLException ex) {
            throw new RuntimeException("Failed to create rollback store", ex);
        }
    }

    @Override
    public JoinResult addChallenger(UUID uuid) throws OrphanedComponentException {
        checkState();

        Optional<Player> spongePlayer = Sponge.getServer().getPlayer(uuid);
        if (!spongePlayer.isPresent()) {
            return new CommonJoinResult(JoinResult.Status.PLAYER_OFFLINE);
        }

        if (getChallengers().size() >= getConfigValue(ConfigNode.MAX_PLAYERS)) {
            return new CommonJoinResult(JoinResult.Status.ROUND_FULL);
        }

        if (CommonCore.getChallenger(uuid).isPresent()) {
            return new CommonJoinResult(JoinResult.Status.ALREADY_IN_ROUND);
        }

        Location<World> spawn = WorldLocationConverter.of(nextSpawnPoint());

        InfernoChallenger challenger = new InfernoChallenger(spongePlayer.get(), this);

        try {
            CommonPlayerHelper.storeLocation(spongePlayer.get().getUniqueId(),
                    WorldLocationConverter.of(spongePlayer.get().getLocation()));
        } catch (IllegalArgumentException | IOException ex) {
            return new CommonJoinResult(ex);
        }


        spongePlayer.get().setLocation(spawn);

        getChallengerMap().put(uuid, challenger);

        getArena().getLobbySigns().forEach(LobbySign::update);

        try {
            PlayerHelper.pushInventory(spongePlayer.get());
        } catch (IOException ex) {
            return new CommonJoinResult(ex);
        }

        getArena().getMinigame().getEventBus().post(new CommonChallengerJoinRoundEvent(challenger));
        return new CommonJoinResult(challenger);
    }

    @Override
    public void broadcast(String message) throws OrphanedComponentException {
        for (Challenger ch : getChallengers()) {
            InfernoCore.getChatAgent().processAndSend(ch.getUniqueId(), message);
        }
    }

    boolean isOrphaned() {
        return orphan;
    }

}
