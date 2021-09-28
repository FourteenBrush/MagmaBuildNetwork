package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Utils {

    private static final Main plugin = Main.getPlugin(Main.class);
    private static final String name = plugin.getName();

    public static boolean isPluginEnabled(String plugin) {
        return Bukkit.getServer().getPluginManager().isPluginEnabled(plugin);
    }

    public static boolean verifyIfIsAPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            message(sender, "&cThe console cannot run this command!");
            return false;
        }
        return true;
    }

    public static boolean isAuthorized(CommandSender p, String permission) {
        String prefix = name.toLowerCase() + ".";
        return p.hasPermission(prefix.replaceFirst(name + ".", "") + permission) || p.hasPermission(prefix + "admin");
    }

    public static String colorize(String args) {
        return ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY + args);
    }

    public static List<String> colorize(String... args) {
        List<String> output = new ArrayList<>();
        for (String s : args) {
            output.add(colorize(s));
        }
        return output;
    }

    public static void giveOrDropFor(Player target, ItemStack... items) {
        target.getInventory().addItem(items).values().forEach(overFlownItem -> target.getWorld().dropItem(target.getLocation(), overFlownItem));
    }

    public static void tryAddItemToInventory(Player player, int slot, ItemStack item) {
        slot = player.getInventory().getItem(slot) == null || player.getInventory().getItem(slot).getType() == Material.AIR ? slot : player.getInventory().firstEmpty();
        if (slot > -1) player.getInventory().setItem(slot, item);
        else player.getWorld().dropItem(player.getLocation(), item);
    }

    private static void log(LogLevel level, String message) {
        Bukkit.getConsoleSender().sendMessage(colorize("&7[&c" + name + "&7] " + "[" + level.name() + "] " + level.getColor() + message));
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

    public static void logCritical(String... messages) {
        for (String message : messages) {
            log(LogLevel.ERROR, message);
        }
        log(LogLevel.ERROR, "&cDisabling myself...");
        plugin.getServer().getPluginManager().disablePlugin(plugin);
    }

    public static void message(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }

    public static void message(CommandSender sender, String... messages) {
        for (String message : messages) {
            message(sender, message);
        }
    }

    public static void messageSpigot(Player player, BaseComponent... component) {
        player.spigot().sendMessage(component);
    }

    public static boolean isPlayerOnline(CommandSender sender, String playerToCheck, boolean exactName) {
        if ((exactName && Bukkit.getPlayerExact(playerToCheck) == null) ||
                (!exactName && Bukkit.getPlayer(playerToCheck) == null)) {
            message(sender, "&c" + playerToCheck + " is currently not online!");
            return false;
        }
        return true;
    }

    public static boolean isPlayerOnline(CommandSender sender, Player playerToCheck) {
        if (Bukkit.getOnlinePlayers().contains(playerToCheck)) return true;
        else {
            message(sender, "&cThat player is currently not online!");
            return false;
        }
    }

    public static boolean checkNotEnoughArgs(CommandSender sender, int arguments, int expectedArguments) {
        if (expectedArguments > arguments) {
            message(sender, "&cIncorrect usage! Please specify " + (expectedArguments - arguments) + " more arguments!");
            return true;
        }
        return false;
    }

    public static String getFinalArgs(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        return builder.toString().trim();
    }

    public static String getSafeString(String str) {
        final Pattern strictInvalidChars = Pattern.compile("[^a-z0-9]");
        return strictInvalidChars.matcher(str.toLowerCase(Locale.ENGLISH)).replaceAll("_");
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

    public static Player getPlayer(String searchItem, boolean getOffline) {
        Player target;
        try {
            target = Bukkit.getServer().getPlayer(searchItem);
        } catch (IllegalArgumentException ex) {
            target = getOffline ? Bukkit.getServer().getPlayerExact(searchItem) : null;
        }
        return target;
    }

    public static void broadcast(String message, boolean broadcastConsole) {
        if (broadcastConsole) Bukkit.getServer().broadcastMessage(message);
        else Bukkit.getOnlinePlayers().forEach(player -> message(player, message));
    }

    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(colorize(message)));
    }

    public static TextComponent suggestCommandByClickableText(String message, String command) {
        TextComponent textComponent = new TextComponent(colorize(message));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to run command")));
        return textComponent;
    }

    public static void suggestCommandByClickableText(Player player, String begin, String clickableText, String end, String command) {
        messageSpigot(player, new ComponentBuilder()
                .append(colorize(begin))
                .append(suggestCommandByClickableText(clickableText, command))
                .reset()
                .append(colorize(end))
                .create());
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
