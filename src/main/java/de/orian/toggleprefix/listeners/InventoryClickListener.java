package de.orian.toggleprefix.listeners;

import de.orian.toggleprefix.Main;
import de.orian.toggleprefix.commands.guis.SelectPrefixInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!SelectPrefixInventory.openInventories.containsKey(event.getWhoClicked().getUniqueId()) || !event.getView().getTitle().equals(SelectPrefixInventory.TITLE)) return;
        event.setCancelled(true);

        SelectPrefixInventory inv = SelectPrefixInventory.openInventories.get(event.getWhoClicked().getUniqueId());
        int slot = event.getSlot();
        if (slot < 0) return;
        if (slot == 4 * 9) inv.switchPage(-1);
        else if (slot == 4 * 9 + 8) inv.switchPage(1);
        else if (slot == 4 * 9 + 4) inv.switchName((Player) event.getWhoClicked());
        ItemStack clicked = inv.getInventory().getItem(slot);
        if (clicked == null) return;
        PersistentDataContainer container = clicked.getItemMeta().getPersistentDataContainer();
        if (!container.has(SelectPrefixInventory.key, PersistentDataType.STRING)) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                inv.updatePrefix(container.get(SelectPrefixInventory.key, PersistentDataType.STRING));
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

}
