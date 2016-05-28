package net.caseif.flint.inferno.round;

import net.caseif.flint.common.arena.CommonArena;
import net.caseif.flint.common.round.CommonRound;
import net.caseif.flint.component.exception.OrphanedComponentException;
import net.caseif.flint.round.JoinResult;
import net.caseif.flint.round.LifecycleStage;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.NotImplementedException;

import java.util.UUID;

public class InfernoRound extends CommonRound {

    public InfernoRound(CommonArena arena, ImmutableSet<LifecycleStage> stages) {
        super(arena, stages);
    }

    @Override
    public JoinResult addChallenger(UUID uuid) throws OrphanedComponentException {
        throw new NotImplementedException("TODO");
    }

    @Override
    public void broadcast(String message) throws OrphanedComponentException {
        throw new NotImplementedException("TODO");
    }

}
