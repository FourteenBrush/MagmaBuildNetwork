package io.github.FourteenBrush.MagmaBuildNetwork.chat;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Logger;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageManager {

    private final MBNPlugin plugin;
    private final Map<UUID, UUID> conversations;

    public MessageManager(MBNPlugin plugin) {
        this.plugin = plugin;
        conversations = new HashMap<>();
    }

    public void sendMessage(UUID sender, UUID receiver, String message) {
        message = applyFormat(sender, receiver, plugin.getConfig().getString("private-message-format"), message);
        sendMessageToUUID(sender, message);
        sendMessageToUUID(receiver, message);
        conversations.put(sender, receiver);
        conversations.put(receiver, sender);
    }

    public boolean sendReply(UUID sender, String message) {
        if (!conversations.containsKey(sender)) return false;
        UUID value = conversations.get(sender);
        if (!value.equals(plugin.getConsoleUUID()) && Bukkit.getPlayer(value) == null) {
            conversations.remove(sender);
            return false;
        }
        sendMessage(sender, conversations.get(sender), message);
        return true;
    }

    private void sendMessageToUUID(UUID receiver, String message) {
        if (receiver.equals(plugin.getConsoleUUID())) {
            Logger.INFO.log(message);
        } else PlayerUtils.message(Bukkit.getPlayer(receiver), message);
    }

    private String applyFormat(UUID sender, UUID receiver, String format, String message) {
        if (!sender.equals(plugin.getConsoleUUID())) {
            Player senderPlayer = Bukkit.getPlayer(sender);
            format = format.replace("{SENDER_PREFIX}", plugin.getChat().getPlayerPrefix(senderPlayer))
                    .replace("{SENDER}", senderPlayer.getDisplayName())
                    .replace("{SENDER_SUFFIX}", plugin.getChat().getPlayerSuffix(senderPlayer));

        } else {
            format = format.replace("{SENDER_PREFIX}", "")
                    .replace("{SENDER}", "Console")
                    .replace("{SENDER_SUFFIX}", "");
        }
        if (!receiver.equals(plugin.getConsoleUUID())) {
            Player receiverPlayer = Bukkit.getPlayer(receiver);
            format = format.replace("{RECEIVER_PREFIX}", plugin.getChat().getPlayerPrefix(receiverPlayer))
                    .replace("{RECEIVER}", receiverPlayer.getDisplayName())
                    .replace("{RECEIVER_SUFFIX}", plugin.getChat().getPlayerSuffix(receiverPlayer))
                    .replace("{RECEIVER_SUFFIX}", plugin.getChat().getPlayerSuffix(receiverPlayer));
        } else {
            format = format.replace("{RECEIVER_PREFIX}", "")
                    .replace("{RECEIVER}", "Console")
                    .replace("{RECEIVER_SUFFIX}", "");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && !sender.equals(plugin.getConsoleUUID())) {
            format = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(sender), format);
        }
        return Utils.colorize(format.replace("{MESSAGE}", message).replaceAll(" {2}", " "));
    }
}
