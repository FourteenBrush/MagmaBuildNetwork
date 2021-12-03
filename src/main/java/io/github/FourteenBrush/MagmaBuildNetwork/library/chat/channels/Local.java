package io.github.FourteenBrush.MagmaBuildNetwork.library.chat.channels;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.Channel;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.HashSet;
import java.util.Set;

public class Local extends Channel {

    public Local(MBNPlugin plugin) {
        super(plugin, "local", "l", plugin.getConfig().getString("channels.local.prefix"), new Permission("magmabuildnetwork.channels.join.local"));
    }

    @Override
    protected String format(User from, String message, String format) {
        return applyDefaultFormat(from, message, format);
    }

    @Override
    public Set<User> getRecipients(User user) {
        Set<User> nearby = new HashSet<>();
        if (user.getPlayer() != null) {
            Player player = user.getPlayer();
            double range = plugin.getConfig().getDouble("channels.local.range");
            Bukkit.getScheduler().runTask(plugin, () ->
                    player.getNearbyEntities(range, range, range).forEach(entity -> {
                if (entity instanceof Player)
                    nearby.add(plugin.getUser(entity.getUniqueId()));
            }));
            nearby.add(user);
        }
        return nearby;
    }
}
