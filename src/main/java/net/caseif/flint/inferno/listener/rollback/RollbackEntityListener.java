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

package net.caseif.flint.inferno.listener.rollback;

import net.caseif.flint.inferno.util.agent.rollback.InfernoRollbackAgent;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.tileentity.TargetTileEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.entity.TargetEntityEvent;

public class RollbackEntityListener {

    @Listener(order = Order.POST)
    public void onSpawnEntity(SpawnEntityEvent event) {
        event.getEntities().stream().filter(entity -> entity.getType() != EntityTypes.PLAYER)
                .forEach(entity -> InfernoRollbackAgent.checkEntityChange(entity, true));
    }

    @Listener(order = Order.POST)
    public void onTargetEntity(TargetEntityEvent event) {
        if (event instanceof MoveEntityEvent) {
            return; // listening to move events is not f*cking necessary
        }

        if (event.getTargetEntity().getType() != EntityTypes.PLAYER) {
            InfernoRollbackAgent.checkEntityChange(event.getTargetEntity());
        }
    }

    @Listener(order = Order.POST)
    public void onTargetTileEntity(TargetTileEntityEvent event) {
        InfernoRollbackAgent.checkBlockChange(event.getTargetTile().getLocation().createSnapshot());
    }

}
