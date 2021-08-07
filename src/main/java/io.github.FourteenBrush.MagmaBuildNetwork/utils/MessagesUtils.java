package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;

public class MessagesUtils {

    public static String getMessageTeleportedOtherPlayer() {
        return ConfigManager.getMessagesConfig().getString("messages.spawn.teleported_other_player");
    }

    public static String getMessageDisableSpawnCommandInCombat() {
        return ConfigManager.getMessagesConfig().getString("disable_spawn_command_in_combat_message");
    }
}
