package de.orian.toggleprefix.commands;

import de.orian.toggleprefix.Main;
import de.orian.toggleprefix.database.MySQL;
import de.orian.toggleprefix.prefix.Prefix;
import de.orian.toggleprefix.scoreboard.ScoreboardManager;
import de.orian.toggleprefix.utils.Sender;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class EditPrefixCommand implements CommandExecutor, TabCompleter {

    private final MySQL mySQL = MySQL.getInstance();
    private final Sender s = Sender.getInstance();
    private final Main plugin = Main.getPlugin();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("toggleprefix.edit")) {
            s.sendError(sender, "Dir fehlt die Berechtigung.");
            return false;
        }

        if (args.length < 3) {
            s.send(sender, """
                    §9Bitte benutze §6/tp_edit [§edisplay§6/§echat§6/§etablist§6/§eitem§6/§epriority§6] <§ename§6> <§ewert§6>
                     §7-> §bdisplay §7- §9Anzeigename in Prefix-Auswahl
                     §7-> §bchat §7- §9Prefix im Chat, §6%name% §9für den Spielernamen
                     §7-> §btablist §7- §9Prefix in Tablist, §6%name% §9für den Spielernamen
                     §7-> §bitem §7- §9Icon des Prefix im GUI §3(Bitte benutze die Bezeichnungen für Spigot Itemstacks)
                     §7-> §bpriority §7- §9Priorität in der Tablist §7-> §bname §7- §9Name des Prefix""");
            return false;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!mySQL.prefixExists(args[1])) {
                    s.sendError(sender, "Prefix existiert nicht.");
                    return;
                }

                switch (args[0]) {
                    case "display" -> {
                        mySQL.updatePrefix(args[1], "display", args[2]);
                        s.sendSuccesss(sender, "Anzeigename von §9" + args[1] + " §aaktualisiert.");
                    }
                    case "chat" -> {
                        mySQL.updatePrefix(args[1], "chat", StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " "));
                        s.sendSuccesss(sender, "Chat-Prefix von §9" + args[1] + " §aaktualisiert.");
                    }
                    case "tablist" -> {
                        mySQL.updatePrefix(args[1], "tablist", StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " "));
                        s.sendSuccesss(sender, "Tablist-Prefix von §9" + args[1] + " §aaktualisiert.");
                    }
                    case "item" -> {
                        if (Material.getMaterial(args[2].toUpperCase()) == null) {
                            s.sendError(sender, "Bitte gib einen richtigen Itemstack ein.\n" +
                                    "§3hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
                            return;
                        }
                        mySQL.updatePrefix(args[1], "item", args[2].toUpperCase());
                        s.sendSuccesss(sender, "Icon von §9" + args[1] + " §aaktualisiert.");
                    }
                    case "priority" -> {
                        mySQL.updatePrefix(args[1], "priority", args[2]);
                        ScoreboardManager.getInstance().updateTeams();
                        s.sendSuccesss(sender, "Priorität von §9" + args[1] + " §aaktualisiert.");
                    }
                    default -> s.send(sender, """
                §9Bitte benutze §6/tp_edit [§edisplay§6/§echat§6/§etablist§6/§eitem§6/§epriority§6] <§ename§6> <§ewert§6>
                 §7-> §bdisplay §7- §9Anzeigename in Prefix-Auswahl
                 §7-> §bchat §7- §9Prefix im Chat, §6%name% §9für den Spielernamen
                 §7-> §btablist §7- §9Prefix in Tablist, §6%name% §9für den Spielernamen
                 §7-> §bitem §7- §9Icon des Prefix im GUI §3(Bitte benutze die Bezeichnungen für Spigot Itemstacks)
                 §7-> §bpriority §7- §9Priorität in der Tablist §7-> §bname §7- §9Name des Prefix""");
                }
            }
        }.runTaskAsynchronously(plugin);

        return false;
    }

    List<String> args1 = new ArrayList<>(List.of("display", "chat", "tablist", "item", "priority"));

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {

            for (String a : args1) {
                if (a.startsWith(args[0].toLowerCase())) result.add(a);
            }

        } else if (args.length == 2) {

            for (String a : Prefix.prefixes) {
                if (a.startsWith(args[1].toLowerCase())) result.add(a);
            }

        }

        return result;
    }

}
