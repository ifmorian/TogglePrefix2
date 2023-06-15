package de.orian.toggleprefix.commands.guis;

import de.orian.toggleprefix.Main;
import de.orian.toggleprefix.config.ConfigManager;
import de.orian.toggleprefix.database.MySQL;
import de.orian.toggleprefix.prefix.Prefix;
import de.orian.toggleprefix.utils.Sender;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SelectPrefixInventory  {

    private static final MySQL mySQL = MySQL.getInstance();
    private static final Main plugin = Main.getPlugin();


    public static final String TITLE = ConfigManager.getInstance().getInventoryTitle();
    public static final NamespacedKey key = new NamespacedKey(plugin, "prefix_data");

    private final Player player;
    private int page = 0;
    private Prefix currentPrefix;
    private final List<Prefix> prefixes = new ArrayList<>();

    private Inventory inventory;

    public static final HashMap<UUID, SelectPrefixInventory> openInventories = new HashMap<>();

    public SelectPrefixInventory(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(player, 5 * 9, TITLE);

        player.openInventory(this.inventory);
        openInventories.put(player.getUniqueId(), this);
        new BukkitRunnable() {
            @Override
            public void run() {
                updatePrefix(null);
            }
        }.runTaskAsynchronously(plugin);
    }

    private void fill() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < 21; i++) {
                    int prefixIndex = i + page * 21;
                    Prefix prefix = null;
                    if (prefixIndex < prefixes.size()) prefix = prefixes.get(prefixIndex);
                    SelectPrefixInventory.this.inventory.setItem((i / 7 + 1) * 9 + (i % 7) + 1, prefix == null ? null : createNamedItem(prefix));
                }
                setLoading(false);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void switchPage(int dir) {
        if (page == 0 && dir == -1) return;
        if (prefixes.size() < (page + dir) * 21) return;
        this.page = page + dir;
        fill();
    }

    public void updatePrefix(String prefixName) {
        setLoading(true);
        if (prefixName != null) mySQL.setPlayerPrefix(player, prefixName);
        SelectPrefixInventory.this.currentPrefix = mySQL.getPlayerPrefix(player);
        player.getEffectivePermissions().forEach(perm -> {
            String[] p = perm.getPermission().split("\\.");
            if (p[0].equals("group")) {
                mySQL.getGroupPrefixes(p[1]).forEach(prefix -> {
                    if (prefixes.stream().noneMatch(prefix1 -> prefix1.name.equals(prefix.name))) prefixes.add(prefix);
                });
            }
        });
        mySQL.getPlayerPrefixes(player).forEach(prefix -> {
            if (prefixes.stream().noneMatch(prefix1 -> prefix1.name.equals(prefix.name))) prefixes.add(prefix);
        });
        fill();
    }

    private void setLoading(boolean loading) {
        ItemStack item = createNamedItem(loading ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE, loading ? "loading..." : currentPrefix.display, false);
        for (int i = 0; i < 9; i++) {
            this.inventory.setItem(i, item);
            this.inventory.setItem(i + 4 * 9,item);
            if (i > 4) continue;
            this.inventory.setItem(i * 9, item);
            this.inventory.setItem(i * 9 + 8, item);
        }
        this.inventory.setItem(4 * 9, createNamedItem(Material.ARROW, "Vorherige Seite", false));
        this.inventory.setItem(4 * 9 + 8, createNamedItem(Material.ARROW, "Nächste Seite", false));
    }

    private ItemStack createNamedItem(Material material, String name, boolean enchanted, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (enchanted) meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createNamedItem(Material material, String name, boolean enchanted) {
        return createNamedItem(material, name, enchanted, new ArrayList<>());
    }

    private ItemStack createNamedItem(Prefix prefix) {
        ItemStack item = createNamedItem(Material.getMaterial(prefix.item), prefix.display, prefix.name.equals(currentPrefix.name), new ArrayList<>());
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, prefix.name);
        item.setItemMeta(meta);
        return item;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
