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

import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.service.OurRandom;

import java.util.List;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Task to handle the dragon's attack pattern.
 */
public class DragonAttackPatternTask implements Runnable {
    /**
     * Plugin instance.
     */
    private final ExtraHardMode plugin;

    /**
     * Target player.
     */
    private final Player player;

    /**
     * Dragon entity.
     */
    private final LivingEntity dragon;

    /**
     * Constructor.
     *
     * @param plugin                - plugin instance.
     * @param dragon                - Dragon.
     * @param player                - Target player.
     * @param playersFightingDragon - All fighting players.
     */
    public DragonAttackPatternTask(ExtraHardMode plugin, LivingEntity dragon, Player player,
                                   List<String> playersFightingDragon) {
        this.plugin = plugin;
        this.dragon = dragon;
        this.player = player;
    }

    @Override
    public void run() {
        if (this.dragon.isDead()) {
            return;
        }

        World world = this.dragon.getWorld();

        // if the player has been defeated
        if (!this.player.isOnline() || world != this.player.getWorld() || this.player.isDead()) {
            // restore some of the dragon's health
            double maxHealth = this.dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            int newHealth = (int) (this.dragon.getHealth() + maxHealth * 0.25);
            if (newHealth > maxHealth) {
                this.dragon.setHealth(maxHealth);
            } else {
                this.dragon.setHealth(newHealth);
            }

            return;
        }

        for (int i = 0; i < 3; i++) {
            DragonAttackTask task = new DragonAttackTask(this.dragon, this.player);
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task,
                    20L * (long) i + (long) (OurRandom.nextInt(20)));
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, 20L * 30L);
    }
}
