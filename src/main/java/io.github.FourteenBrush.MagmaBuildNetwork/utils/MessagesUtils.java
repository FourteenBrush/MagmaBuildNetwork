package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import org.bukkit.command.CommandSender;

public class MessagesUtils {

    public static String messageNoPermission() {
        return Utils.colorize("&cI'm sorry but you do not have permission");
    }

    public static boolean noPermission(CommandSender sender) {
        Utils.message(sender, messageNoPermission());
        return true;
    }

    public static String messageNoConsole() {
        return Utils.colorize("&cThe console cannot execute this!");
    }

    public static boolean noConsole(CommandSender sender) {
        Utils.message(sender, messageNoConsole());
        return true;
    }

    public static String messageTeleportedOtherPlayer() {
        return ConfigManager.getMessagesConfig().getString("messages.spawn.teleported-other-player");
    }

    public static String messageDisableSpawnCommandInCombat() {
        return ConfigManager.getMessagesConfig().getString("messages.spawn.disable-spawn-command-in-combat-message");
    }
}
