package de.orian.toggleprefix.utils;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

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
        String[] a = s.split("&#");
        StringBuilder sBuilder = new StringBuilder(a[0]);
        for (int i = 1; i < a.length; i++) {
            String cc = "§x" + Integer.toHexString(Integer.parseInt(a[i].substring(0, 6), 16));
            Sender.getInstance().console(cc);
            sBuilder.append(cc).append(a[i].substring(6));
        }
        s = sBuilder.toString();
        return s.replace("&", "§");
    }

    public static String setName(String s, String name) {
        return s.replace("%name%", name);
    }

    public static String tablistFormat(String s, Player player) {
        return colorTranslate(clearString(setName(s, player.getDisplayName()).replace("%ping%", String.valueOf(player.getPing()))));
    }

}
