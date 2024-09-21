/*
 * This file is part of
 * ExtraHardMode Server Plugin for Minecraft
 *
 * Copyright (C) 2012 Ryan Hamshire
 * Copyright (C) 2013 Diemex
 *
 * ExtraHardMode is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ExtraHardMode is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero Public License
 * along with ExtraHardMode.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.extrahardmode.task;

import com.extrahardmode.service.OurRandom;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Task to allow a dragon to do additional attacks.
 */
public class DragonAttackTask implements Runnable {

    /**
     * Target player.
     */
    private final Player player;

    /**
     * Attacking dragon.
     */
    private final Entity dragon;

    /**
     * Constructor.
     *
     * @param dragon - Dragon.
     * @param player - Target player.
     */
    public DragonAttackTask(Entity dragon, Player player) {
        this.dragon = dragon;
        this.player = player;
    }

    @Override
    public void run() {
        if (this.dragon.isDead()) {
            return;
        }
        if (!this.player.isOnline()) {
            return;
        }

        World world = this.dragon.getWorld();
        if (world != this.player.getWorld()) {
            return;
        }

        Location dragonLocation = this.dragon.getLocation();

        Location targetLocation;
        if (OurRandom.percentChance(20)) {
            targetLocation = world.getHighestBlockAt(dragonLocation).getLocation();
        } else {
            targetLocation = player.getLocation();
        }

        Location offsetLocation = targetLocation.add(OurRandom.nextInt(10) - 5,
                OurRandom.nextInt(3) - 1,
                OurRandom.nextInt(10) - 5);

        Vector vector = new Vector(offsetLocation.getX() - dragonLocation.getX(),
                offsetLocation.getY() - dragonLocation.getY(), offsetLocation.getZ()
                - dragonLocation.getZ());

        Fireball fireball = (Fireball) world.spawnEntity(dragonLocation, EntityType.FIREBALL);
        fireball.setShooter((EnderDragon) this.dragon);
        fireball.setDirection(vector);
    }
}
