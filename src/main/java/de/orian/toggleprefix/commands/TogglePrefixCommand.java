package de.orian.toggleprefix.commands;

import de.orian.toggleprefix.commands.guis.SelectPrefixInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TogglePrefixCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        new SelectPrefixInventory(player);
        return false;
    }
}
