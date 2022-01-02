package io.github.FourteenBrush.MagmaBuildNetwork.chat;

import io.github.FourteenBrush.MagmaBuildNetwork.chat.framework.ChatChannel;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ChannelManager {

    private final Set<ChatChannel> channels;

    public ChannelManager() {
        channels = new HashSet<>();
    }

    public void addChannel(ChatChannel channel) {
        channels.add(channel);
    }

    public void removeChannel(ChatChannel channel) {
        channels.remove(channel);
    }

    public Set<ChatChannel> getChannels() {
        return channels;
    }

    @Nullable
    public ChatChannel getChannel(String name) {
        for (ChatChannel c : channels) {
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }
}
