package de.orian.toggleprefix.commands;

import de.orian.toggleprefix.Main;
import de.orian.toggleprefix.database.MySQL;
import de.orian.toggleprefix.luckperms.LuckPermsManager;
import de.orian.toggleprefix.prefix.Prefix;
import de.orian.toggleprefix.utils.Sender;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class AddPrefixCommand implements CommandExecutor, TabCompleter {

    private final Sender s = Sender.getInstance();
    private final MySQL mySQL = MySQL.getInstance();
    private final Main plugin = Main.getPlugin();
    private final LuckPermsManager luckPermsManager = LuckPermsManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("toggleprefix.edit")) {
            s.sendError(sender, "Dir fehlt die Berechtigung.");
            return false;
        }

        if (args.length < 3) {
            s.send(sender, """
                    §9Bitte benutze §6/tp_add [§egroup§6/§eplayer§6] <§ename§6> <§eprefix§6>
                     §7-> §bgroup §7- §9Füge einen Prefix zu einer Gruppe hinzu
                     §7-> §bplayer §7- §9Schalte einen Prefix für einen einzelnen Spieler frei
                     §7-> §bname §7- §9Name der Gruppe oder des Spielers
                    """);
            return false;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                switch (args[0]) {
                    case "group" -> {
                        if (!luckPermsManager.groupExists(args[1])) {
                            s.sendError(sender, "LuckPerms-Gruppe §9" + args[1] + " §cexistiert nicht.");
                            return;
                        }
                        if (!mySQL.prefixExists(args[2])) {
                            s.sendError(sender, "Prefix §9" + args[2] + " §cexistiert nicht.");
                            return;
                        }
                        mySQL.addPrefixToGroup(args[1], args[2]);
                        s.sendSuccesss(sender, "Prefix §9" + args[2] + " §azu Gruppe §6" + args[1] + " §ahinzugefügt.");
                    }
                    case "player" -> {
                        Player player = Bukkit.getPlayerExact(args[1]);
                        if (player == null) {
                            s.sendError(sender, "Spieler§9" + args[1] + " §cexistiert nicht.");
                            return;
                        }
                        if (!mySQL.prefixExists(args[2])) {
                            s.sendError(sender, "Prefix §9" + args[2] + " §cexistiert nicht.");
                            return;
                        }
                        mySQL.addPrefixToPlayer(player, args[2]);
                        s.sendSuccesss(sender, "Prefix §9" + args[2] + " §afür §6" + args[1] + " §afreigeschaltet.");
                    }
                    default -> s.send(sender, """
                    §9Bitte benutze §6/tp_add [§egroup§6/§eplayer§6] <§ename§6> <§eprefix§6>
                     §7-> §bgroup §7- §9Füge einen Prefix zu einer Gruppe hinzu
                     §7-> §bplayer §7- §9Schalte einen Prefix für einen einzelnen Spieler frei
                     §7-> §bname §7- §9Name der Gruppe oder des Spielers
                    """);
                }
            }
        }.runTaskAsynchronously(plugin);

        return false;
    }

    List<String> args1 = new ArrayList<>(List.of("group", "player"));

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : args1) {
                if (a.startsWith(args[0].toLowerCase())) result.add(a);
            }
        } else if (args.length == 2) {

            if (args[0].equals("player")) {

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getDisplayName().toLowerCase().startsWith(args[1].toLowerCase()))
                        result.add(p.getDisplayName());
                }

            } else if (args[0].equals("group")) {

                for (String group : luckPermsManager.getGroups()) {
                    if (group.toLowerCase().startsWith(args[1].toLowerCase())) result.add(group);
                }

            }

        } else if (args.length == 3) {
            for (String prefix : Prefix.prefixes) {
                if (prefix.toLowerCase().startsWith(args[2].toLowerCase())) result.add(prefix);
            }
        }
        return result;
    }
}
