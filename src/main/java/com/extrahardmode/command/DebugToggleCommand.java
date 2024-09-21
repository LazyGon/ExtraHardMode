package com.extrahardmode.command;

import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.features.DebugMode;
import com.extrahardmode.service.ICommand;
import com.extrahardmode.service.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Diemex
 */
public class DebugToggleCommand implements ICommand {
    @Override
    public boolean execute(ExtraHardMode plugin, CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PermissionNode.ADMIN.getNode())) {
            sender.sendMessage(ChatColor.RED + plugin.getTag() + " Lack permission: " + PermissionNode.ADMIN.getNode());
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    ChatColor.RED + plugin.getTag() + "You need to be in game to use debugging functionality!");
            return false;
        }

        DebugMode debug = plugin.getModuleForClass(DebugMode.class);
        if (debug.isInDebugMode(sender.getName())) {
            debug.disableDebugMode(sender.getName());
            sender.sendMessage(ChatColor.RED + plugin.getTag() + " Disabled DebugMode " + plugin.getName());
        } else {
            debug.enableDebugMode(sender.getName());
            sender.sendMessage(ChatColor.GREEN + plugin.getTag() + " Enabled DebugMode " + plugin.getName());
        }
        return true;
    }
}
