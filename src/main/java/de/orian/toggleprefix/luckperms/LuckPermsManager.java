package de.orian.toggleprefix.luckperms;

import de.orian.toggleprefix.utils.Sender;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;

public class LuckPermsManager {

    private static LuckPermsManager instance;

    private static Sender sender = Sender.getInstance();

    private LuckPerms api;

    public LuckPermsManager() {
        instance = this;

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) api = provider.getProvider();
        else sender.consoleError("Could not load LuckPerms.");
    }

    public static LuckPermsManager getInstance() {
        return instance;
    }

    public boolean groupExists(String group) {
        return api.getGroupManager().getGroup(group) != null;
    }

    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        api.getGroupManager().getLoadedGroups().forEach(group -> groups.add(group.getName()));
        return groups;
    }

}
