package de.orian.toggleprefix.config;

import de.orian.toggleprefix.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;

public class ConfigManager {

    private static ConfigManager instance;

    private final Main plugin = Main.getPlugin();

    private static FileConfiguration cfg = Main.getPlugin().getConfig();

    public ConfigManager() {
        instance = this;
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    public void updateConfig() {
        plugin.reloadConfig();
        plugin.saveDefaultConfig();
        cfg = plugin.getConfig();
    }

    public HashMap<String, String> getDatabase() {
        HashMap<String, String> data = new HashMap<>();
        ConfigurationSection cs = cfg.getConfigurationSection("database");
        data.put("host", cs.getString("host"));
        data.put("port", cs.getString("port"));
        data.put("name", cs.getString("name"));
        data.put("user", cs.getString("user"));
        data.put("password", cs.getString("password"));
        return data;
    }

    public String getInventoryTitle() {
        return cfg.getString("inventoryTitle");
    }

    public boolean getColoredMessages() {
        return cfg.getBoolean("coloredMessages");
    }
    public boolean getTablistUpdate() {
        return cfg.getBoolean("tablistUpdate");
    }

    public List<String> getTablistHeader() {
        return cfg.getStringList("tablistHeader");
    }

    public List<String> getTablistFooter() {
        return cfg.getStringList("tablistFooter");
    }

    public long getTablistSpeed() {
        return cfg.getLong("tablistSpeed");
    }

    public boolean getTablistCustomName() {
        return cfg.getBoolean("tablistCustomName");
    }
}
