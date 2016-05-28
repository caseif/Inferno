package net.caseif.flint.inferno.util.agent.rollback;

import net.caseif.flint.common.arena.CommonArena;
import net.caseif.flint.common.util.agent.rollback.CommonRollbackAgent;
import net.caseif.flint.util.physical.Location3D;

import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.util.UUID;

public class InfernoRollbackAgent extends CommonRollbackAgent {

    protected InfernoRollbackAgent(CommonArena arena) {
        super(arena);
    }

    @Override
    public void rollbackBlock(int id, Location3D location, String type, int data, String stateSerial) throws IOException {
        throw new NotImplementedException("TODO");
    }

    @Override
    public void rollbackEntityChange(int id, UUID uuid, Location3D location, String type, String stateSerial) throws IOException {
        throw new NotImplementedException("TODO");
    }

    @Override
    public void rollbackEntityCreation(int id, UUID uuid) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public void cacheEntities() {
        throw new NotImplementedException("TODO");
    }

}
