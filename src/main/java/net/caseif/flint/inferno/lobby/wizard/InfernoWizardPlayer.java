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
