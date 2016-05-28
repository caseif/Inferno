package net.caseif.flint.inferno.challenger;

import net.caseif.flint.common.challenger.CommonChallenger;
import net.caseif.flint.common.round.CommonRound;

import org.apache.commons.lang3.NotImplementedException;

import java.util.UUID;

public class InfernoChallenger extends CommonChallenger {

    protected InfernoChallenger(UUID playerUuid, String playerName, CommonRound round) {
        super(playerUuid, playerName, round);
    }

    @Override
    public void setSpectating(boolean spectating) {
        throw new NotImplementedException("TODO");
    }

}
