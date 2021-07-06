package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Utils {

    private static final String name = Main.getInstance().getDescription().getName();

    public static boolean verifyIfIsAPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThe console cannot run this command!");
            return false;
        }
        return true;
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        return ((sender.hasPermission("magmabuildnetwork." + permission.toLowerCase())) ||
                sender.hasPermission("magmabuildnetwork.admin") || sender.isOp());
    }

    public static String colorize(String args) {
        return ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY + args);
    }

    private static void log(LogLevel level, String message) {
            Bukkit.getConsoleSender().sendMessage(colorize("&7[&c" + name + "&7] " + "[" + level.name() + "] " + level.getColor() + message));
    }

    public static void logInfo(String message) {
        log(LogLevel.INFO, message);
    }

    public static void logInfo(String[] messages) {
        for (String message : messages) {
            logInfo(message);
        }
    }

    public static void logWarning(String message) {
        log(LogLevel.WARNING, message);
    }

    public static void logWarning(String[] messages) {
        for (String message : messages) {
            logWarning(message);
        }
    }

    public static void logDebug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public static void logDebug(String[] messages) {
        for (String message : messages) {
            logDebug(message);
        }
    }

    public static void logError(String message) {
        log(LogLevel.ERROR, message);
    }

    public static void logError(String[] messages) {
        for (String message: messages) {
            log(LogLevel.ERROR, message);
        }
    }

    public static void message(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }

    public static void message(CommandSender sender, String[] messages) {
        for (String message : messages) {
            message(sender, message);
        }
    }

    public static void messageNoPermission(CommandSender sender) {
        message(sender, "§cSorry, you do not have permission");
    }

    public static void messageSucceed(CommandSender sender, boolean useYellowColor ,String message) {
        if (useYellowColor) {
            message(sender, "§e" + message);
        } else {
            message(sender, message);
        }
    }

    public static boolean isPlayerOnline(CommandSender sender, String playerToCheck) {
        if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(playerToCheck))) {
            message(sender, "§c" + playerToCheck + " §cis currently not online!");
            return false;
        }
        return true;
    }

    public static boolean checkNotEnoughArgs(CommandSender sender, int arguments, int expectedArguments) {
        if (expectedArguments > arguments) {
            message(sender, "§cPlease specify " + (expectedArguments - arguments) + " §cmore arguments!");
            return true;
        }
        return false;
    }

    public static String getFinalArg(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i != start)
                builder.append(" ");
            builder.append(args[i]);
        }
        return builder.toString();
    }

    public static Player getPlayer(String searchItem, boolean getHidden, boolean getOffline) {
        Player target;
        try {
            target = Main.getInstance().getServer().getPlayer(UUID.fromString(searchItem));
        } catch (IllegalArgumentException ex) {
            if (getOffline) {
                target = Main.getInstance().getServer().getPlayerExact(searchItem);
            } else {
                target = Main.getInstance().getServer().getPlayer(searchItem);
            }
        }
        if (target != null) {
            return target;
        }
        return null;
    }

    public enum LogLevel {
        INFO, WARNING, ERROR, DEBUG;

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
}
