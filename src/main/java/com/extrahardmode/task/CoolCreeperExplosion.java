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
import com.extrahardmode.config.ExplosionType;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.module.UtilityModule;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

/**
 * Launches a creeper into the air with fireworks and lets him explode midair
 */
public class CoolCreeperExplosion implements Runnable {
    private final Creeper creeper;

    private final Location loc;

    private final ExtraHardMode plugin;

    private final UtilityModule utils;

    private final BukkitScheduler scheduler;

    private final RootConfig CFG;

    private int numOfFireworks = 3;

    private long mainDelay = 0L;

    private double creeperAscendSpeed = 0.5;

    public CoolCreeperExplosion(Creeper entity, ExtraHardMode plugin) {
        this.plugin = plugin;
        CFG = plugin.getModuleForClass(RootConfig.class);
        creeper = entity;
        loc = creeper.getLocation();
        utils = plugin.getModuleForClass(UtilityModule.class);
        scheduler = plugin.getServer().getScheduler();
        numOfFireworks = CFG.getInt(RootNode.FLAMING_CREEPERS_FIREWORK, loc.getWorld().getName());
        creeperAscendSpeed = CFG.getDouble(RootNode.FLAMING_CREEPERS_ROCKET, loc.getWorld().getName());
    }

    /**
     * Contains the mainLogic for creating a cool explosion
     */
    @Override
    public void run() {
        // Everyone loves fireworks
        for (int i = 0; i < numOfFireworks; i++) {
            int ticksBetweenFireworks = 5;
            mainDelay += ticksBetweenFireworks;
            scheduler.runTaskLater(plugin, new Firework(), mainDelay);
        }
        // Catapult into air and explode midair
        int ticksBeforeCatapult = 3;
        mainDelay += ticksBeforeCatapult;
        scheduler.runTaskLater(plugin, new AscendToHeaven(), mainDelay);
    }

    private class Firework implements Runnable {
        @Override
        public void run() {
            utils.fireWorkRandomColors(FireworkEffect.Type.CREEPER, loc);
        }
    }

    /**
     * Schedules multiple tasks to slowly let a creeper float upwards
     */
    private class AscendToHeaven implements Runnable {// Catapult Creeper into sky, afterwards explode in midair

        @Override
        public void run() {
            if (creeper != null) {
                int ticksInbetween = 1;
                creeper.setTarget(null);
                for (int i = 0; i < 10; i++) {
                    scheduler.runTaskLater(plugin, new RiseToGlory(), ticksInbetween);
                    ticksInbetween += i;
                }
                int ticksBeforeSuicide = 8;
                scheduler.runTaskLater(plugin, new Suicide(), ticksBeforeSuicide);
            }
        }
    }

    /**
     * This task slowly lets a creeper float upwards, it has to be called multiple
     * times
     */
    private class RiseToGlory implements Runnable {
        @Override
        public void run() {
            if (creeper != null) {
                Vector holyGrail = creeper.getVelocity().setY(creeperAscendSpeed);
                creeper.setVelocity(holyGrail);
            }
        }
    }

    /**
     * Creeper explodes in midair
     */
    private class Suicide implements Runnable {
        @Override
        public void run() {
            if (creeper != null && !creeper.isDead()) {
                final boolean creeperExplosion = CFG.getBoolean(RootNode.EXPLOSIONS_CREEPERS_ENABLE,
                        creeper.getWorld().getName());
                if (creeperExplosion) {
                    new CreateExplosionTask(plugin, creeper.getLocation(), ExplosionType.CREEPER, creeper).run();
                }
            }
            if (creeper != null) {
                creeper.remove();
            }
        }
    }
}