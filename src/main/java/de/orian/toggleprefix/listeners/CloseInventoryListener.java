package de.orian.toggleprefix.listeners;

import de.orian.toggleprefix.commands.guis.SelectPrefixInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class CloseInventoryListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Player player)) return;
        SelectPrefixInventory.openInventories.remove(player.getUniqueId());
    }

}
