/*
 * This file is part of Inferno, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, Max Roncace <me@caseif.net>
 * Copyright (c) 2016, contributors
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

package net.caseif.flint.inferno.lobby;

import net.caseif.flint.common.arena.CommonArena;
import net.caseif.flint.common.lobby.CommonLobbySign;
import net.caseif.flint.component.exception.OrphanedComponentException;
import net.caseif.flint.inferno.InfernoPlugin;
import net.caseif.flint.inferno.util.converter.WorldLocationConverter;
import net.caseif.flint.util.physical.Location3D;

import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class InfernoLobbySign extends CommonLobbySign {

    private static final int SIGN_SIZE = 4;

    public InfernoLobbySign(Location3D location, CommonArena arena, Type type) {
        super(location, arena, type);

        Sponge.getScheduler()
                .createTaskBuilder()
                .execute(this::update)
                .submit(InfernoPlugin.getInstance());
    }

    @Override
    protected boolean validate() {
        final Location<World> location = WorldLocationConverter.of(this.getLocation());
        return location.getExtent().getBlock(location.getBlockPosition()).getType() == BlockTypes.WALL_SIGN
                || location.getExtent().getBlock(location.getBlockPosition()).getType() == BlockTypes.STANDING_SIGN;
    }

    @Override
    protected int getSignSize() {
        return SIGN_SIZE;
    }

    @Override
    public void update() throws OrphanedComponentException {
        this.checkState();

        switch (this.getType()) {
            case STATUS:
                break;
            case CHALLENGER_LISTING:
                break;
            default: // wtf
                throw new AssertionError();
        }
        throw new NotImplementedException("TODO");
    }

}
