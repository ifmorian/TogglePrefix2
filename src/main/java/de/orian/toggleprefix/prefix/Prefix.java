package de.orian.toggleprefix.prefix;

import de.orian.toggleprefix.Main;
import de.orian.toggleprefix.database.MySQL;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Prefix {

    public String name;
    public String display;
    public String chat;
    public String tablist;
    public String item;
    public String priority;

    public static List<String> prefixes = new ArrayList<>();

    public Prefix(String name, String display, String chat, String tablist, String item, String priority) {
        this.name = name;
        this.display = display;
        this.chat = chat;
        this.tablist = tablist;
        this.item = item;
        this.priority = priority;
    }

    public static void updatePrefixes() {
        new BukkitRunnable() {
            @Override
            public void run() {
                prefixes = MySQL.getInstance().getPrefixes();
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

}
