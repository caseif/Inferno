package net.caseif.flint.inferno.lobby.type;

import net.caseif.flint.common.arena.CommonArena;
import net.caseif.flint.inferno.lobby.InfernoLobbySign;
import net.caseif.flint.util.physical.Location3D;

public class InfernoChallengerListingLobbySign extends InfernoLobbySign {

    protected InfernoChallengerListingLobbySign(Location3D location, CommonArena arena) {
        super(location, arena, Type.CHALLENGER_LISTING);
    }

}
