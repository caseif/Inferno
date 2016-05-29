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
