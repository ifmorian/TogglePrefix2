package de.orian.toggleprefix.listeners;

import de.orian.toggleprefix.Main;
import de.orian.toggleprefix.database.MySQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinListener implements Listener {

    private final MySQL mySQL = MySQL.getInstance();
    private final Main plugin = Main.getPlugin();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                mySQL.addPlayer(player);
            }
        }.runTaskAsynchronously(plugin);
    }

}
