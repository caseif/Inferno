package net.caseif.flint.inferno.lobby.wizard;

import net.caseif.flint.common.lobby.wizard.CommonWizardPlayer;
import net.caseif.flint.common.lobby.wizard.IWizardManager;
import net.caseif.flint.util.physical.Location3D;

import org.apache.commons.lang3.NotImplementedException;

import java.util.UUID;

public class InfernoWizardPlayer extends CommonWizardPlayer {

    protected InfernoWizardPlayer(UUID uuid, Location3D location, IWizardManager manager) {
        super(uuid, location, manager);
    }

    @Override
    protected void recordTargetBlockState() {
        throw new NotImplementedException("TODO");
    }

    @Override
    protected void restoreTargetBlock() {
        throw new NotImplementedException("TODO");
    }

    @Override
    public void playbackWithheldMessages() {
        throw new NotImplementedException("TODO");
    }

}
