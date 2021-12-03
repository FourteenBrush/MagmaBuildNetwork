package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Utils {

    private Utils() {}

    public static String colorize(String args) {
        return ChatColor.translateAlternateColorCodes('&', args);
    }

    public static String[] colorize(String... args) {
        String[] result = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = colorize(args[i]);
        }
        return result;
    }

    public static void logInfo(String... messages) {
        for (String message : messages) {
            log(LogLevel.INFO, message);
        }
    }

    public static void logWarning(String... messages) {
        for (String message : messages) {
            log(LogLevel.WARNING, message);
        }
    }

    public static void logDebug(String... messages) {
        for (String message : messages) {
            log(LogLevel.DEBUG, message);
        }
    }

    public static void logError(String... messages) {
        for (String message: messages) {
            log(LogLevel.ERROR, message);
        }
    }

    public static void logFatal(Throwable th ,String... messages) {
        for (String message : messages) {
            log(LogLevel.ERROR, message);
        }
        th.printStackTrace();
        log(LogLevel.WARNING, "&cDisabling myself...");
        Bukkit.getPluginManager().disablePlugin(MBNPlugin.getInstance());
    }

    private static void log(LogLevel level, String message) {
        Bukkit.getConsoleSender().sendMessage(colorize("&7[&cMagmaBuildNetwork&7] " + "[" + level.name() + "] " + level.getColor() + message));
    }

    public static boolean checkNotEnoughArgs(CommandSender sender, int arguments, int expectedArguments) {
        if (expectedArguments > arguments) {
            PlayerUtils.message(sender, "&cIncorrect usage! Please specify " + (expectedArguments - arguments) + " more arguments!");
            return true;
        }
        return false;
    }

    public static String getFinalArgs(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (; start < args.length; start++) {
            builder.append(args[start]).append(" ");
        }
        return builder.toString().trim();
    }

    public static String getSafeString(String str) {
        final Pattern strictInvalidChars = Pattern.compile("[^a-z0-9]");
        return strictInvalidChars.matcher(str.toLowerCase(Locale.ENGLISH)).replaceAll("_");
    }

    public static boolean isValidConfigurationSection(FileConfiguration configuration, String path) {
        return configuration.isConfigurationSection(path) && !configuration.getConfigurationSection(path).getKeys(false).isEmpty();
    }
    
    public static String millisToReadable(long millis) {
        final long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        final long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        String str = days > 0 ? days + " days " : "";
        if (hours > 0) str += hours + " hours ";
        if (minutes > 0) str += minutes + " minutes ";
        if (seconds > 0) str += seconds + " seconds ";
        return str;
    }

    private enum LogLevel {
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
