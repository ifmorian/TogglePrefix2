package de.orian.toggleprefix.utils;

import de.orian.toggleprefix.config.ConfigManager;
import de.orian.toggleprefix.database.MySQL;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

    public static String convertToLetters(String s) {
        return s.substring(0, 3).replace("9", "a")
            .replace("8", "b")
            .replace("7", "c")
            .replace("6", "d")
            .replace("5", "e")
            .replace("4", "f")
            .replace("3", "g")
            .replace("2", "h")
            .replace("1", "i")
            .replace("0", "j") +
            s.substring(3);
    }


    public static byte[] UUIDtoBytes(UUID id) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(id.getMostSignificantBits());
        buffer.putLong(id.getLeastSignificantBits());
        return buffer.array();
    }

    public static UUID BytestoUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }

    public static String clearString(String s) {
        return s.replace("%", "");
    }

    public static String colorTranslate(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] stripped = s.split("&#([0-9]|[a-f]|[A-F]){6}");
        int index = 0;
        if (!s.matches("^&#([0-9]|[a-f]|[A-F]){6}")) {
            stringBuilder.append(stripped[0]);
            index = 1;
        }
        Matcher m = Pattern.compile("&#([0-9]|[a-f]|[A-F]){6}").matcher(s);
        while (m.find()) {
            stringBuilder.append(ChatColor.of(m.group().substring(1)));
            stringBuilder.append(stripped[index]);
            index++;
        }
        s = stringBuilder.toString();
        return s.replace("&", "§");
    }


    public static String setName(String s, String name) {
        return s.replace("%name%", name);
    }

    public static String tablistFormat(String s, Player player) {
        String name = null;
        if (ConfigManager.getInstance().getTablistCustomName()) name = MySQL.getInstance().getPlayerDisplayName(player);
        return colorTranslate(clearString(setName(s, name == null ? player.getDisplayName() : name).replace("%ping%", String.valueOf(player.getPing()))));
    }

}
