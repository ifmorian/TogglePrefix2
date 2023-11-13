package de.orian.toggleprefix.listeners;

import de.orian.toggleprefix.commands.guis.SelectPrefixInventory;
import de.orian.toggleprefix.config.ConfigManager;
import de.orian.toggleprefix.database.MySQL;
import de.orian.toggleprefix.prefix.Prefix;
import de.orian.toggleprefix.utils.Formatter;
import de.orian.toggleprefix.utils.Sender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private static final MySQL mySQL = MySQL.getInstance();
    private static final ConfigManager configManager = ConfigManager.getInstance();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (SelectPrefixInventory.currentlyNameChanging.contains(player.getUniqueId())) {
            if (mySQL.setPlayerDisplayName(player, event.getMessage())) {
                Sender.getInstance().sendSuccesss(player, "Dein Name wurde geändert!");
                SelectPrefixInventory.currentlyNameChanging.remove(player.getUniqueId());
            } else {
                Sender.getInstance().sendError(player, "Etwas ist schief gelaufen.");
            }

            event.setCancelled(true);
            return;
        }

        Prefix prefix = mySQL.getPlayerPrefix(player);
        if (prefix.chat == null) return;

        String displayName = mySQL.getPlayerDisplayName(player);
        String chat = Formatter.setName(prefix.chat, displayName != null ? displayName : player.getDisplayName());
        chat = Formatter.clearString(chat);
        chat = Formatter.colorTranslate(chat);
        try {
            if (player.hasPermission("toggleprefix.color") && configManager.getColoredMessages()) event.setMessage(Formatter.colorTranslate(event.getMessage()));
            event.setFormat(chat + " " + "%2$s");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
