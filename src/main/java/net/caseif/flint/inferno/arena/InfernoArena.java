package net.caseif.flint.inferno.arena;

import net.caseif.flint.common.arena.CommonArena;
import net.caseif.flint.common.minigame.CommonMinigame;
import net.caseif.flint.component.exception.OrphanedComponentException;
import net.caseif.flint.exception.rollback.RollbackException;
import net.caseif.flint.lobby.type.ChallengerListingLobbySign;
import net.caseif.flint.lobby.type.StatusLobbySign;
import net.caseif.flint.util.physical.Boundary;
import net.caseif.flint.util.physical.Location3D;

import com.google.common.base.Optional;
import org.apache.commons.lang3.NotImplementedException;

public class InfernoArena extends CommonArena {

    protected InfernoArena(CommonMinigame parent, String id, String name, Location3D initialSpawn, Boundary boundary)
            throws IllegalArgumentException {
        super(parent, id, name, initialSpawn, boundary);
    }

    @Override
    public Optional<StatusLobbySign> createStatusLobbySign(Location3D location) throws IllegalArgumentException, OrphanedComponentException {
        throw new NotImplementedException("TODO");
    }

    @Override
    public Optional<ChallengerListingLobbySign> createChallengerListingLobbySign(Location3D location, int index)
            throws IllegalArgumentException, OrphanedComponentException {
        throw new NotImplementedException("TODO");
    }

    @Override
    public void markForRollback(Location3D location) throws IllegalArgumentException, RollbackException, OrphanedComponentException {
        throw new NotImplementedException("TODO");
    }

}
