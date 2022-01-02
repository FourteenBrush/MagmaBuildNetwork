package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.concurrent.TimeUnit;

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
}
