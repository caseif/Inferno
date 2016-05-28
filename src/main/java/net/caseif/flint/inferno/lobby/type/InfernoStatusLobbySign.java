package net.caseif.flint.inferno.lobby.type;

import net.caseif.flint.common.arena.CommonArena;
import net.caseif.flint.inferno.lobby.InfernoLobbySign;
import net.caseif.flint.util.physical.Location3D;

public class InfernoStatusLobbySign extends InfernoLobbySign {

    protected InfernoStatusLobbySign(Location3D location, CommonArena arena, Type type) {
        super(location, arena, Type.STATUS);
    }

}
