package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.user.User;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Lang;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.ThreadLocalRandom;

public class VaultListener implements Listener {

    private final MBNPlugin plugin;

    public VaultListener(MBNPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        User user = plugin.getUserManager().getUser(event.getPlayer().getUniqueId());
        userCache.getIfPresent(user);
        if (userCache.getIfPresent(user) != null) {
            event.getPlayer().sendMessage(Lang.RATE_LIMITED.get());
            return;
        }
        userCache.put(user, "");
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
