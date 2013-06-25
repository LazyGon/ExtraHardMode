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

package com.extrahardmode.features;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.config.messages.MessageConfig;
import com.extrahardmode.config.messages.MessageNode;
import com.extrahardmode.module.DataStoreModule;
import com.extrahardmode.module.MessagingModule;
import com.extrahardmode.module.PlayerModule;
import com.extrahardmode.module.UtilityModule;
import com.extrahardmode.service.ListenerModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;


/**
 * Swimming in water will pull you down if you are carrying too much
 */
public class Water extends ListenerModule
{
    private final ExtraHardMode plugin;

    private final RootConfig CFG;

    private final UtilityModule utils;

    private final MessagingModule messenger;

    private final PlayerModule playerModule;


    public Water(ExtraHardMode plugin)
    {
        super(plugin);
        this.plugin = plugin;
        CFG = plugin.getModuleForClass(RootConfig.class);
        utils = plugin.getModuleForClass(UtilityModule.class);
        messenger = plugin.getModuleForClass(MessagingModule.class);
        playerModule = plugin.getModuleForClass(PlayerModule.class);
    }


    /**
     * when a player moves...
     *
     * @param event
     *         - Event that occurred.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location from = event.getFrom();
        Location to = event.getTo();
        Block fromBlock = from.getBlock();
        Block toBlock = to.getBlock();

        final boolean noSwimingInArmor = CFG.getBoolean(RootNode.NO_SWIMMING_IN_ARMOR, world.getName());
        final boolean playerBypasses = playerModule.playerBypasses(player, Feature.MONSTER_GLYDIA);
        final boolean blockWaterElevators = CFG.getBoolean(RootNode.NO_SWIMMING_IN_ARMOR_BLOCK_ELEVATORS, world.getName());

        final float maxWeight = (float) CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_MAX_POINTS, world.getName());
        final float armorPoints = (float) CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_ARMOR_POINTS, world.getName());
        final float inventoryPoints = (float) CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_INV_POINTS, world.getName());
        final float toolPoints = (float) CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_TOOL_POINTS, world.getName());

        final int drowningRate = CFG.getInt(RootNode.NO_SWIMMING_IN_ARMOR_DROWN_RATE, world.getName());
        final int overEncumbranceExtra = CFG.getInt(RootNode.NO_SWIMMING_IN_ARMOR_ENCUMBRANCE_EXTRA, world.getName());

        final float normalDrownVel = -0.5F;
        final float overwaterDrownVel = -0.7F;

        // FEATURE: no swimming while heavy, only enabled worlds, players without bypass permission and not in creative
        if (noSwimingInArmor && !playerBypasses)
        {
            // only care about moving up
            if (to.getY() > from.getY())
            {
                DataStoreModule.PlayerData playerData = plugin.getModuleForClass(DataStoreModule.class).getPlayerData(player.getName());
                // only when in water
                Block underFromBlock = fromBlock.getRelative(BlockFace.DOWN);
                if (fromBlock.getType() == Material.STATIONARY_WATER && toBlock.getType() == Material.STATIONARY_WATER && underFromBlock.getType() == Material.STATIONARY_WATER && underFromBlock.getRelative(BlockFace.DOWN).getType() == Material.STATIONARY_WATER)
                {
                    // if no cached value, calculate
                    if (playerData.cachedWeightStatus <= 0)
                    {
                        playerData.cachedWeightStatus = playerModule.inventoryWeight(player, armorPoints, inventoryPoints, toolPoints);
                    }
                    // if too heavy let player feel the weight by pulling them down, if in boat can always swim
                    if (playerData.cachedWeightStatus > maxWeight && !player.isInsideVehicle())
                    {
                        drown(player, drowningRate, overEncumbranceExtra, playerData.cachedWeightStatus, maxWeight, normalDrownVel, overwaterDrownVel);
                    }
                }
                //when you swim up waterfalls and basically are flying with only a tip of your body in water
                else if (blockWaterElevators && !playerModule.isPlayerOnLadder(player) && !player.isInsideVehicle() && !player.isFlying())
                {
                    if (playerData.cachedWeightStatus <= 0)
                    {
                        playerData.cachedWeightStatus = playerModule.inventoryWeight(player, armorPoints, inventoryPoints, toolPoints);
                    } else if (playerData.cachedWeightStatus > maxWeight)
                    {
                        //Detect waterfalls
                        BlockFace[] faces = {
                                BlockFace.WEST,
                                BlockFace.NORTH_WEST,
                                BlockFace.NORTH,
                                BlockFace.NORTH_EAST,
                                BlockFace.EAST,
                                BlockFace.SOUTH_EAST,
                                BlockFace.SOUTH,
                                BlockFace.SOUTH_WEST};
                        Location loc = player.getLocation();
                        boolean isWaterNear = false;
                        for (BlockFace face : faces)
                        {
                            Material nearType = loc.getBlock().getRelative(face).getType();
                            if (nearType.equals(Material.STATIONARY_WATER))
                                isWaterNear = true;
                        }
                        if (isWaterNear)
                            drown(player, drowningRate, overEncumbranceExtra, playerData.cachedWeightStatus, maxWeight, normalDrownVel + 0.3F, normalDrownVel + 0.3F); //the water flowing down pulls you down
                    }
                }
            }
        }
    }


    /**
     * Drowns the player at the given rate
     */


    void drown(Player player, int drowningRate, int overEncumbranceExtra, float cachedWeightStatus, float maxWeight, float normalDrownVel, float overwaterDrownVel)
    {
        if (cachedWeightStatus > maxWeight)
        {
            //TODO EhmDrownEvent
            //TODO HIGH EhmPreDrownEvent warn player that he is carrying too much and give him a chance to exit the water
            MessageConfig messages = plugin.getModuleForClass(MessageConfig.class);
            float rdm = plugin.getRandom().nextFloat(); //how expensive is this
            //drownrate + extra when overencumbered
            float drownPercent = ((float) drowningRate / 500.0F) + ((cachedWeightStatus - maxWeight) * overEncumbranceExtra) / 500.0F;
            if (rdm < drownPercent)
            {
                Vector vec = player.getVelocity();
                //when floating on top of water pull down more
                Material material = player.getLocation().getBlock().getRelative((BlockFace.UP)).getType();
                if (material.equals(Material.AIR))
                    vec.setY(overwaterDrownVel);
                else  //when under water
                    vec.setY(normalDrownVel);
                player.setVelocity(vec);
                messenger.sendMessage(player, MessageNode.NO_SWIMMING_IN_ARMOR);
            }
        }
    }


    /**
     * when a player drops an item
     *
     * @param event
     *         - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onPlayerDropItem(PlayerDropItemEvent event)
    {
        // FEATURE: players can't swim when they're carrying a lot of weight
        Player player = event.getPlayer();
        DataStoreModule.PlayerData playerData = plugin.getModuleForClass(DataStoreModule.class).getPlayerData(player.getName());
        playerData.cachedWeightStatus = -1.0F;
    }


    /**
     * when a player picks up an item
     *
     * @param event
     *         - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onPlayerPickupItem(PlayerPickupItemEvent event)
    {
        // FEATURE: players can't swim when they're carrying a lot of weight
        Player player = event.getPlayer();
        DataStoreModule.PlayerData playerData = plugin.getModuleForClass(DataStoreModule.class).getPlayerData(player.getName());
        playerData.cachedWeightStatus = -1.0F;
    }


    /**
     * When a player interacts with an inventory.
     *
     * @param event
     *         - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onPlayerInventoryClick(InventoryClickEvent event)
    {
        // FEATURE: players can't swim when they're carrying a lot of weight
        HumanEntity humanEntity = event.getWhoClicked();
        if (humanEntity instanceof Player)
        {
            Player player = (Player) humanEntity;
            DataStoreModule.PlayerData playerData = plugin.getModuleForClass(DataStoreModule.class).getPlayerData(player.getName());
            playerData.cachedWeightStatus = -1.0F;
        }
    }
}
