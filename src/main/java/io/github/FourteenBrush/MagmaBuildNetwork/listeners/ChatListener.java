package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.Channel;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.ChatPlayer;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ChatListener implements Listener {

    private final Main plugin;
    private final Cache<ChatPlayer, String> lastMessage;

    public ChatListener(Main plugin) {
        this.plugin = plugin;
        lastMessage = CacheBuilder.newBuilder().expireAfterWrite(plugin.getConfig().getInt("rate-limit"), TimeUnit.MILLISECONDS).build();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        File file = new File(plugin.getDataFolder() + File.separator + "players" + File.separator + player.getUniqueId() + ".yml");
        if (file.exists()) return;
        plugin.getChannelManager().getChannels().forEach(channel -> {
            ChatPlayer chatPlayer = plugin.getChatPlayer(player.getUniqueId());
            if (player.hasPermission(channel.getAutoJoinPermission()))
                chatPlayer.forceAddChannel(channel);
            if (player.hasPermission(channel.getSetMainPermission())) {
                if (!chatPlayer.getChannels().contains(channel))
                    chatPlayer.forceAddChannel(channel);
                chatPlayer.setMainChannel(channel);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        ChatPlayer chatPlayer = plugin.getChatPlayer(event.getPlayer().getUniqueId());
        if (lastMessage.getIfPresent(chatPlayer) != null) {
            PlayerUtils.message(chatPlayer.getPlayer(), Lang.RATE_LIMITED.get());
            return;
        }
        lastMessage.put(chatPlayer, "");
        Channel channel = chatPlayer.getMainChannel();
        if (channel == null) {
            PlayerUtils.message(chatPlayer.getPlayer(), Lang.CHANNEL_NO_CHANNEL_JOINED.get());
            return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> {
            channel.sendMessage(chatPlayer, event.getMessage(), event.getFormat());
        });
    }
}
