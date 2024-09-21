package com.extrahardmode.command;

import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.service.ICommand;
import com.extrahardmode.service.PermissionNode;
import com.extrahardmode.task.RemoveExposedTorchesTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Diemex
 */
public class DebugCommand implements ICommand {
    @Override
    public boolean execute(ExtraHardMode plugin, CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(PermissionNode.ADMIN.getNode())) {
            if (sender instanceof Player player) {
                if (args.length > 0) {
                    if (args[0].equals("RemoveTorches")) {
                        plugin.getServer().getScheduler().runTask(plugin,
                                new RemoveExposedTorchesTask(plugin, player.getLocation().getChunk(), true));
                        sender.sendMessage(
                                ChatColor.GREEN + plugin.getTag() + "Removed Torches and Crops in the current chunk!");
                    }
                    return true;
                } else {
                    sender.sendMessage(
                            ChatColor.RED + plugin.getTag() + " You need to specify what you want to debug!");
                    sender.sendMessage(ChatColor.RED + plugin.getTag() + " Available methods \"RemoveTorches\"");
                }
            } else
                sender.sendMessage(
                        ChatColor.RED + plugin.getTag() + "You need to be in game to use debugging functionality!");
        } else {
            sender.sendMessage(ChatColor.RED + plugin.getTag() + " Lack permission: " + PermissionNode.ADMIN.getNode());
        }
        return false;
    }
}
