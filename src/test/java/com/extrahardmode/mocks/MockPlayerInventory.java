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

package com.extrahardmode.mocks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Mocks the inventory of a Player and the getInventory of the Player
 *
 * @author Max
 */
public class MockPlayerInventory {
    /**
     * Inventory instance
     */
    private final PlayerInventory inv = mock(PlayerInventory.class);

    /**
     * Constructor
     *
     * @param player name of the Player to whom this inventory belongs to
     */
    public MockPlayerInventory(Player player, ItemStack[] armorContents, ItemStack[] inventoryContents) {
        when(player.getInventory()).thenReturn(inv);
        when(inv.getHolder()).thenReturn(player);
        when(inv.getArmorContents()).thenReturn(armorContents);
        when(inv.getContents()).thenReturn(inventoryContents);
    }

    /**
     * Get the actual Inventory with it's mocked methods
     */
    public PlayerInventory get() {
        return inv;
    }
}
