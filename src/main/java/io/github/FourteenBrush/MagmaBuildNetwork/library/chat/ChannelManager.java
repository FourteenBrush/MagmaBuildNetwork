package io.github.FourteenBrush.MagmaBuildNetwork.library.chat;

import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.Channel;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChannelManager {

    private List<Channel> channels;

    public ChannelManager() {
        channels = new ArrayList<>();
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public boolean doesChannelExist(String name) {
        return getChannel(name) != null;
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
