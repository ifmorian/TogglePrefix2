package de.orian.toggleprefix.commands;

import de.orian.toggleprefix.Main;
import de.orian.toggleprefix.database.MySQL;
import de.orian.toggleprefix.prefix.Prefix;
import de.orian.toggleprefix.scoreboard.ScoreboardManager;
import de.orian.toggleprefix.utils.Sender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class DeletePrefixCommand implements CommandExecutor, TabCompleter {

    private final Sender s = Sender.getInstance();
    private final Main plugin = Main.getPlugin();
    private final MySQL mySQL = MySQL.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("toggleprefix.edit")) {
            s.sendError(sender, "Dir fehlt die Berechtigung.");
            return false;
        }
        if (args.length < 1) {
            s.send(sender, "§9Bitte benutze §6/tp_delete <§ename§6>\n" +
                    " §7-> §bname §7- §9Name des Prefix");
            return false;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!mySQL.prefixExists(args[0])) {
                    s.sendError(sender, "Prefix existiert nicht.");
                    return;
                }
                mySQL.removePrefix(args[0]);
                Prefix.updatePrefixes();
                ScoreboardManager.getInstance().updateTeams();
                s.sendSuccesss(sender, "Prefix entfernt!");
            }
        }.runTaskAsynchronously(plugin);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {

            for (String a : Prefix.prefixes) {
                if (a.startsWith(args[0].toLowerCase())) result.add(a);
            }

        }
        return result;
    }

}
