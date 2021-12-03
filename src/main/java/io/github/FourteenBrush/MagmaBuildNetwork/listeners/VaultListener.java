package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.Channel;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.User;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class VaultListener implements Listener {

    private final MBNPlugin plugin;
    private final Cache<User, String> lastMessage;

    public VaultListener(MBNPlugin plugin) {
        this.plugin = plugin;
        lastMessage = CacheBuilder.newBuilder().expireAfterWrite(plugin.getConfig().getInt("rate-limit"), TimeUnit.MILLISECONDS).build();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        File file = new File(plugin.getDataFolder() + File.separator + "players" + File.separator + player.getUniqueId() + ".yml");
        if (file.exists()) {
            plugin.getChannelManager().getChannels().forEach(channel -> {
                if (player.hasPermission(channel.getAutoJoinPermission()))
                    plugin.getUser(player.getUniqueId()).addChannel(channel);
            });
        }
        User user = plugin.getUser(player.getUniqueId());
        plugin.getChannelManager().getChannels().forEach(channel -> {
            if (player.hasPermission(channel.getSetMainPermission())) {
                if (!user.getChannels().contains(channel))
                    user.addChannel(channel);
                user.setMainChannel(channel);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        User user = plugin.getUser(event.getPlayer().getUniqueId());
        if (lastMessage.getIfPresent(user) != null) {
            event.getPlayer().sendMessage(Lang.RATE_LIMITED.get());
            return;
        }
        lastMessage.put(user, "");
        Channel channel = user.getMainChannel();
        if (channel == null) {
            event.getPlayer().sendMessage(Lang.CHANNEL_NO_CHANNEL_JOINED.get());
        } else channel.sendMessage(user, event.getMessage(), channel.getFormat());
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster) {
            Player player = event.getEntity().getKiller();
            if (player == null) return;
            int amount = ThreadLocalRandom.current().nextInt(11) + 5; // From 5 to 15
            plugin.getEco().depositPlayer(player, amount);
            PlayerUtils.message(player, "&a+&b" + amount + " &3coins");
        }
    }
}
