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

package com.extrahardmode.modules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.mocks.MockPlayer;
import com.extrahardmode.mocks.MockPlayerInventory;
import com.extrahardmode.module.PlayerModule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * @author Max
 */
public class TestInventoryWeight {

    @Mock
    private ExtraHardMode plugin;

    private PlayerModule module;

    private final Player myPlayer = new MockPlayer("Diemex94").get();

    private final ItemStack ironboots = new ItemStack(Material.IRON_BOOTS);

    private final ItemStack ironLeggings = new ItemStack(Material.IRON_LEGGINGS);

    private final ItemStack ironChest = new ItemStack(Material.IRON_CHESTPLATE);

    private final ItemStack ironHelmet = new ItemStack(Material.IRON_HELMET);

    private final ItemStack[] oneArmor = {ironboots, null, null, null};

    private final ItemStack[] twoArmor = {null, ironLeggings, null, ironHelmet};

    private final ItemStack[] fullArmor = {ironboots, ironLeggings, ironChest, ironHelmet};

    private final ItemStack[] emptyArmor = new ItemStack[4];

    private final ItemStack[] emptyInv = new ItemStack[4 * 9];

    @BeforeEach
    public void prepare() {
        module = new PlayerModule(plugin);
    }

    /**
     * Test if armor gets calculated correctly
     */
    @Test
    public void armorTest() {
        new MockPlayerInventory(myPlayer, emptyArmor, emptyInv);
        assertEquals(0, PlayerModule.inventoryWeight(myPlayer, 15, 5, 5), 0, "Empty inventory = no weight ");

        new MockPlayerInventory(myPlayer, oneArmor, emptyInv);
        assertEquals(15, PlayerModule.inventoryWeight(myPlayer, 15, 5, 5), 0, "One piece ");

        new MockPlayerInventory(myPlayer, twoArmor, emptyInv);
        assertEquals(12, PlayerModule.inventoryWeight(myPlayer, 6, 5, 5), 0, "Two pieces ");

        new MockPlayerInventory(myPlayer, fullArmor, emptyInv);
        assertEquals(8, PlayerModule.inventoryWeight(myPlayer, 2, 5, 5), 0, "Full armor ");
    }

    /**
     * loaded inventory
     */
    @Test
    public void fullInvtest() {
        ItemStack[] inv = new ItemStack[4 * 9];

        inv[0] = new ItemStack(Material.DIAMOND_SWORD);
        inv[5] = new ItemStack(Material.DIAMOND_SHOVEL);
        inv[7] = new ItemStack(Material.FLINT_AND_STEEL);

        inv[9] = new ItemStack(Material.BOOK, 32);
        inv[12] = new ItemStack(Material.ARROW, 24);
        inv[13] = new ItemStack(Material.BREAD, 64);

        /*
         * Armor:5, Tools: 1, Stack: 64
         * Armor: 4 * 5 = 20 (full)
         * Tools: 3 * 1 = 3
         * Other: StackWeight (64) / MaxStackSize * Count
         * Book: 32 (64 / 64 * 32)
         * Arrow: 24
         * Bread: 64
         * Total: 143
         */

        new MockPlayerInventory(myPlayer, fullArmor, inv);

        assertEquals(143, PlayerModule.inventoryWeight(myPlayer, 5, 64, 1), 0, "See comment for calculation");
    }
}
