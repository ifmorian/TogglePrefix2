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
import org.bukkit.scheduler.BukkitRunnable;

public class NewPrefixCommand implements CommandExecutor {

    private final MySQL mySQL = MySQL.getInstance();
    private final Sender s = Sender.getInstance();
    private final Main plugin = Main.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("toggleprefix.edit")) {
            s.sendError(sender, "Dir fehlt die Berechtigung.");
            return false;
        }

        if (args.length < 4) {
            s.send(sender, """
                    §9Bitte benutze §6/tp_new <§ename§6> <§eprefix§6> <§eitemstack§6> <§epriority§6>
                     §7-> §bname §7- §9Name des Prefix
                     §7-> §bprefix §7- §9Prefix im Chat und in der Tablist, §6%name% §9für den Spielernamen
                     §7-> §bitemstack §7- §9Icon des Prefix im GUI §3(Bitte benutze die Bezeichnungen für Spigot Itemstacks)
                     §7-> §bpriority §7- §9Priorität in der Tablist""");
            return false;
        }

        if (Material.getMaterial(args[args.length - 2].toUpperCase()) == null) {
            s.sendError(sender, "Bitte gib einen richtigen Itemstack ein.\n" +
                    "§3hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
            return false;
        }

        if (!args[args.length - 1].matches("[1-9][0-9]{0,2}")) {
            s.sendError(sender, "Bitte gib eine Priorität zwischen 1 und 999 ein.");
            return false;
        }

        String chat = StringUtils.join(ArrayUtils.subarray(args, 1, args.length - 2), " ");
        Prefix prefix = new Prefix(args[0], args[0], chat, chat, args[args.length - 2].toUpperCase(), StringUtils.leftPad(args[args.length - 1], 3, '0'));

        new BukkitRunnable() {

            @Override
            public void run() {
                if (mySQL.prefixExists(prefix)) {
                    s.sendError(sender,"Name oder Priorität existieren bereits.");
                    return;
                }

                mySQL.addPrefix(prefix);
                Prefix.updatePrefixes();
                ScoreboardManager.getInstance().updateTeams();
                s.sendSuccesss(sender, "Prefix §9" + args[0] + " §aerstellt!");
            }
        }.runTaskAsynchronously(plugin);

        return false;
    }
}
