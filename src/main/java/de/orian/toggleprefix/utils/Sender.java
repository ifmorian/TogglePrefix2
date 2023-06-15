package de.orian.toggleprefix.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Sender {

    private static Sender instance;

    private String prefix;

    public Sender(String prefix) {
        this.prefix = prefix;
        instance = this;
    }

    public static Sender getInstance() {
        return instance;
    }

    public void console(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + message);
    }

    public void console(CommandSender receiver, String message) {
        receiver.sendMessage(prefix + message);
    }

    public void consoleError(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§c" + message);
    }

    public void consoleError(CommandSender receiver, String message) {
        receiver.sendMessage(prefix + "§c" + message);
    }

    public void consoleSuccess(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + "§a" + message);
    }

    public void consoleSuccess(CommandSender receiver, String message) {
        receiver.sendMessage(prefix + "§a" + message);
    }

    public void send(CommandSender sender, String msg) {
        sender.sendMessage(prefix + msg);
    }

    public void sendError(CommandSender sender, String msg) {
        sender.sendMessage(prefix + "§c" + msg);
    }

    public void sendSuccesss(CommandSender sender, String msg) {
        sender.sendMessage(prefix + "§a" + msg);
    }
}
