package io.github.FourteenBrush.MagmaBuildNetwork.library.chat.channels;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.Channel;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.User;
import org.bukkit.permissions.Permission;

public class Global extends Channel {

    public Global(MBNPlugin plugin) {
        super(plugin, "global", "g", plugin.getConfig().getString("channels.global.prefix"), new Permission("magmambuildnetwork.channels.join.global"));
    }

    @Override
    protected String format(User from, String message, String format) {
        return applyDefaultFormat(from, message, format);
    }
}
