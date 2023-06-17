package de.orian.toggleprefix.scoreboard;

import de.orian.toggleprefix.Main;
import de.orian.toggleprefix.config.ConfigManager;
import de.orian.toggleprefix.database.MySQL;
import de.orian.toggleprefix.prefix.Prefix;
import de.orian.toggleprefix.utils.Formatter;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class ScoreboardManager {

    private static ScoreboardManager instance;

    private final Main plugin = Main.getPlugin();
    private final MySQL mySQL = MySQL.getInstance();
    private final ConfigManager configManager = ConfigManager.getInstance();
    private final Spark spark;

    private final Scoreboard scoreboard;

    public ScoreboardManager(Scoreboard scoreboard) {
        instance = this;
        this.scoreboard = scoreboard;
        this.spark = SparkProvider.get();
        updateTeams();
        updatePlayers();
        animateTabs();
    }

    public void updateTeams() {
        List<String> teams = mySQL.getTeams();
        disableTeams();
        teams.forEach(scoreboard::registerNewTeam);
    }

    public void disableTeams() {
        for (Team team : scoreboard.getTeams()) {
            team.unregister();
        }
    }

    public void updatePlayer(Player player) {
        Prefix prefix = mySQL.getPlayerPrefix(player);
        player.getScoreboard().getTeam(Formatter.convertToLetters(prefix.priority)).addEntry(player.getName());
        player.setPlayerListName(Formatter.tablistFormat(prefix.tablist, player));
    }

    public void updatePlayers() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!configManager.getTablistUpdate()) return;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    updatePlayer(p);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 150L);
    }

    public void animateTabs() {
        final int[] count1 = {0};
        final int[] count2 = {0};
        final List<String> headers = configManager.getTablistHeader();
        final List<String> footers = configManager.getTablistFooter();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            DoubleStatistic<StatisticWindow.TicksPerSecond> tps = spark.tps();
            double tpsLast10Secs = ((double) Math.round(tps.poll(StatisticWindow.TicksPerSecond.SECONDS_10) * 10)) / 10;
            DoubleStatistic<StatisticWindow.CpuUsage> cpuUsage = spark.cpuSystem();
            double usagelastMin = ((double) Math.round(cpuUsage.poll(StatisticWindow.CpuUsage.MINUTES_1) * 1000)) / 10;
            if(count1[0] >= headers.size()) {
                count1[0] = 0;
            }
            if(count2[0] >= footers.size()) {
                count2[0] = 0;
            }
            String header = headers.get(count1[0])
                    .replace("&", "§")
                    .replace("%onlinePlayers%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                    .replace("%maxPlayers%", String.valueOf(Bukkit.getMaxPlayers()))
                    .replace("%tps%", String.valueOf(tpsLast10Secs))
                    .replace("%cpu%", String.valueOf(usagelastMin));
            String footer = footers.get(count2[0])
                    .replace("&", "§")
                    .replace("%onlinePlayers%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                    .replace("%maxPlayers%", String.valueOf(Bukkit.getMaxPlayers()))
                    .replace("%tps%", String.valueOf(tpsLast10Secs))
                    .replace("%cpu%", String.valueOf(usagelastMin));

            for (Player p : Bukkit.getOnlinePlayers()) {
                String s = header.replace("%ping%", String.valueOf(p.getPing()));
                String t = footer.replace("%ping%", String.valueOf(p.getPing()));
                p.setPlayerListHeaderFooter(s, t);
            }
            count1[0]++;
            count2[0]++;
        }, 0L, 60L);
    }

    public static ScoreboardManager getInstance() {
        return instance;
    }

}
