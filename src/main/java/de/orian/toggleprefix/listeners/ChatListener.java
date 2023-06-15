package de.orian.toggleprefix.listeners;

import de.orian.toggleprefix.config.ConfigManager;
import de.orian.toggleprefix.database.MySQL;
import de.orian.toggleprefix.prefix.Prefix;
import de.orian.toggleprefix.utils.Formatter;
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
        Prefix prefix = mySQL.getPlayerPrefix(player);
        if (prefix.chat == null) return;
        String chat = Formatter.setName(prefix.chat, player.getDisplayName());
        chat = Formatter.clearString(chat);
        chat = Formatter.colorTranslate(chat);
        try {
            if (configManager.getColoredMessages()) event.setMessage(Formatter.colorTranslate(event.getMessage()));
            event.setFormat(chat + " " + "%2$s");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
