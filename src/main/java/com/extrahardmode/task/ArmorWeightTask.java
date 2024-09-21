package com.extrahardmode.task;

import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.module.PlayerModule;
import org.bukkit.entity.Player;

/**
 *
 */
public class ArmorWeightTask implements Runnable {
    private final RootConfig CFG;
    private final Player player;

    public ArmorWeightTask(ExtraHardMode plugin, Player player) {
        CFG = plugin.getModuleForClass(RootConfig.class);
        this.player = player;
    }

    float previousSpeed = 0f;

    @Override
    public void run() {
        if (!CFG.getBoolean(RootNode.ARMOR_SLOWDOWN_ENABLE, player.getWorld().getName()))
            return;
        final float basespeed = (float) CFG.getDouble(RootNode.ARMOR_SLOWDOWN_BASESPEED, player.getWorld().getName());
        final int slowdownPercent = CFG.getInt(RootNode.ARMOR_SLOWDOWN_PERCENT, player.getWorld().getName());
        final float armorPoints = PlayerModule.getArmorPoints(player);
        if (armorPoints != 0) {
            float value = basespeed * (1 - armorPoints / 0.8F * (slowdownPercent / 100F));
            if (value != previousSpeed) {
                player.setWalkSpeed(value);
                previousSpeed = value;
            }
        } else if (basespeed != previousSpeed) {
            player.setWalkSpeed(basespeed);
            previousSpeed = basespeed;
        }
    }
}
