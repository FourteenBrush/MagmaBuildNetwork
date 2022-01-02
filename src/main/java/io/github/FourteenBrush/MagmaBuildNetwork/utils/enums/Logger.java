package io.github.FourteenBrush.MagmaBuildNetwork.utils.enums;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public enum Logger {
    INFO,
    WARNING,
    ERROR,
    DEBUG;

    public void log(String first, String... rest) {
        log(first);
        for (String message : rest) {
            log(message);
        }
    }

    private void log(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + ChatColor.RED + "MagmaBuildNetwork" + ChatColor.GRAY + "] [" + name() + "] " + getColor() + message);
    }

    private ChatColor getColor() {
        switch (this) {
            case WARNING:
            case ERROR:
                return ChatColor.RED;
            case DEBUG:
                return ChatColor.BLUE;
            default:
                return ChatColor.GRAY;
        }
    }
}
