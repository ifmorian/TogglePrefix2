package de.orian.toggleprefix;

import de.orian.toggleprefix.commands.*;
import de.orian.toggleprefix.config.ConfigManager;
import de.orian.toggleprefix.database.MySQL;
import de.orian.toggleprefix.listeners.ChatListener;
import de.orian.toggleprefix.listeners.CloseInventoryListener;
import de.orian.toggleprefix.listeners.InventoryClickListener;
import de.orian.toggleprefix.listeners.PlayerJoinListener;
import de.orian.toggleprefix.luckperms.LuckPermsManager;
import de.orian.toggleprefix.prefix.Prefix;
import de.orian.toggleprefix.scoreboard.ScoreboardManager;
import de.orian.toggleprefix.utils.Sender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        new Sender("§7[§dTogglePrefix§7] ");
        new LuckPermsManager();
        new ConfigManager();
        ConfigManager.getInstance().updateConfig();
        new MySQL();
        new ScoreboardManager(Bukkit.getScoreboardManager().getMainScoreboard());

        Prefix.updatePrefixes();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new CloseInventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);

        getCommand("tp_new").setExecutor(new NewPrefixCommand());
        getCommand("tp_edit").setExecutor(new EditPrefixCommand());
        getCommand("tp_edit").setTabCompleter(new EditPrefixCommand());
        getCommand("tp_delete").setExecutor(new DeletePrefixCommand());
        getCommand("tp_delete").setTabCompleter(new DeletePrefixCommand());
        getCommand("tp_add").setExecutor(new AddPrefixCommand());
        getCommand("tp_add").setTabCompleter(new AddPrefixCommand());
        getCommand("toggleprefix").setExecutor(new TogglePrefixCommand());
    }

    @Override
    public void onDisable() {
        MySQL.getInstance().close();
    }

    public static Main getPlugin() {
        return plugin;
    }
}
