package net.caseif.flint.inferno.lobby.wizard;

import net.caseif.flint.common.lobby.wizard.CommonWizardManager;
import net.caseif.flint.minigame.Minigame;
import net.caseif.flint.util.physical.Location3D;

import org.apache.commons.lang3.NotImplementedException;

import java.util.UUID;

public class InfernoWizardManager extends CommonWizardManager {

    protected InfernoWizardManager(Minigame minigame) {
        super(minigame);
    }

    @Override
    public void addPlayer(UUID uuid, Location3D location) {
        throw new NotImplementedException("TODO");
    }

}
