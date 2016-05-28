package net.caseif.flint.inferno.lobby;

import net.caseif.flint.common.arena.CommonArena;
import net.caseif.flint.common.lobby.CommonLobbySign;
import net.caseif.flint.component.exception.OrphanedComponentException;
import net.caseif.flint.util.physical.Location3D;

import org.apache.commons.lang3.NotImplementedException;

public class InfernoLobbySign extends CommonLobbySign {

    protected InfernoLobbySign(Location3D location, CommonArena arena, Type type) {
        super(location, arena, type);
    }

    @Override
    protected boolean validate() {
        throw new NotImplementedException("TODO");
    }

    @Override
    protected int getSignSize() {
        throw new NotImplementedException("TODO");
    }

    @Override
    public void update() throws OrphanedComponentException {
        throw new NotImplementedException("TODO");
    }

}
