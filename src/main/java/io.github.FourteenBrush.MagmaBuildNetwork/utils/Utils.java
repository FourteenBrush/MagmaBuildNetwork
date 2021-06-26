package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utils {

    public static String color(String args) {
        return ChatColor.translateAlternateColorCodes('&', args);
    }

    public static boolean hasPermission(Player p, String permission) {
        return !(!p.hasPermission("magmabuildnetwork." + permission.toLowerCase()) && !p.hasPermission("MagmaBuildNetwork.*"));
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        return !(!sender.hasPermission("MagmaBuildNetwork." + permission.toLowerCase()) && !sender.hasPermission("MagmaBuildNetwork.*"));
    }

    public static boolean verifyIfIsAPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("the console cannot run this command!");
            return true;
        }
        return false;
    }
}
