package io.github.FourteenBrush.MagmaBuildNetwork.library.chat.channels;

import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.Channel;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.ChatPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.HashSet;
import java.util.Set;

public class Local extends Channel {

    protected Local() {
        super("local", "l", PLUGIN.getConfig().getString("channels.local.prefix"), new Permission("MagmaBuildNetwork.channels.global"));
    }

    @Override
    protected String format(ChatPlayer from, String message, String format) {
        return applyDefaultFormat(from, message, format);
    }

    @Override
    public Set<ChatPlayer> getRecipients(ChatPlayer chatPlayer) {
        if (chatPlayer.getOfflinePlayer().isOnline()) {
            Player player = chatPlayer.getPlayer();
            Set<ChatPlayer> nearby = new HashSet<>();
            double range = PLUGIN.getConfig().getInt("Channels.Local.Range");
            player.getNearbyEntities(range, range, range).forEach(entity -> {
                if (entity instanceof Player)
                    nearby.add(PLUGIN.getChatPlayer(entity.getUniqueId()));
            });
            nearby.add(chatPlayer);
            return nearby;
        }
        return null;
    }
}
