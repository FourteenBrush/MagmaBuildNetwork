package io.github.FourteenBrush.MagmaBuildNetwork.library.chat;

import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.Channel;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ChannelManager {

    private final Set<Channel> channels;

    public ChannelManager() {
        channels = new HashSet<>();
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
    }

    public Set<Channel> getChannels() {
        return channels;
    }

    @Nullable
    public Channel getChannel(String name) {
        for (Channel c : channels) {
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }
}
