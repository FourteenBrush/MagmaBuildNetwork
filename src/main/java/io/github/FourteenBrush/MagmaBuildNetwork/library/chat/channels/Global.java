package io.github.FourteenBrush.MagmaBuildNetwork.library.chat.channels;

import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.Channel;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.ChatPlayer;
import org.bukkit.permissions.Permission;

public class Global extends Channel {

    public Global() {
        super("global", "g", PLUGIN.getConfig().getString("channels.global.prefix"), new Permission("MagmaBuildNetwork.channels.global"));
    }

    @Override
    protected String format(ChatPlayer from, String message, String format) {
        return applyDefaultFormat(from, message, format);
    }
}
