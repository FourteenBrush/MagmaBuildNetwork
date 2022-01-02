package io.github.FourteenBrush.MagmaBuildNetwork.player.profiles;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.FourteenBrush.MagmaBuildNetwork.chat.framework.ChannelRank;
import io.github.FourteenBrush.MagmaBuildNetwork.chat.framework.ChatChannel;
import io.github.FourteenBrush.MagmaBuildNetwork.player.User;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Lang;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ChatProfile {
    private static final Cache<UUID, Boolean> chatCache = CacheBuilder.newBuilder()
            .expireAfterWrite(800, TimeUnit.MILLISECONDS).build(); // expires after 0.8 seconds

    private final User user;
    private final Map<ChatChannel, ChannelRank> chatChannels;
    private ChatChannel currentChannel;
    private boolean isMuted;

    public ChatProfile(User user) {
        this.user = user;
        chatChannels = new HashMap<>();
        isMuted = false;
    }

    /**
     * @return the current channel
     */
    public ChatChannel getCurrentChannel() {
        return currentChannel;
    }

    /**
     * Tries to set the argument as the new current channel
     * @param channel the new channel to use as current one
     * @return true if the current channel has been changed, false otherwise
     */
    public boolean setCurrentChannel(ChatChannel channel) {
        if (currentChannel == channel) return false;
        currentChannel = channel;
        return true;
    }

    /**
     * Tries to add a channel to the channel list
     * @param channel the channel which needs to be added
     * @return true if the channel was added, false if it was already present
     */
    public boolean addChannel(ChatChannel channel) {
        return chatChannels.put(channel, channel.getDefaultRank()) == null;
    }

    /**
     * Tries to remove a channel from the channel list
     * @param channel the channel which needs to be removed, if the channel equals the current channel,
     *                the current channel will be set to null
     * @return true if the channel was removed, false otherwise
     */
    public boolean removeChannel(ChatChannel channel) {
        if (currentChannel == channel) {
            currentChannel = null;
            return true;
        }
        return chatChannels.remove(channel) != null;
    }

    public boolean wipeChannels() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param channel an element which presence will be tested
     * @return true if the channel is present, false otherwise
     */
    public boolean isInChannel(ChatChannel channel) {
        return chatChannels.containsKey(channel);
    }

    /**
     * @return true if the joined channel list isn't empty, false otherwise
     */
    public boolean isInChannel() {
        return !chatChannels.isEmpty();
    }

    /**
     * @return true if the user is muted, false otherwise
     */
    public boolean isMuted() {
        return isMuted;
    }

    /**
     * Tries to mute the user
     * @param flag a boolean flag indicating whether to mute or not
     * @return true if the flag is different than {@link #isMuted}
     */
    public boolean setMuted(boolean flag) {
        return isMuted != (isMuted = flag);
    }

    /**
     * Gets the current privileges for the channel entered as parameter
     * @param channel the channel you want to get the current privileges for
     * @return the rank, or null if the channel is not present
     */
    public ChannelRank getRank(ChatChannel channel) {
        return chatChannels.get(channel);
    }

    /**
     * @return true if the user may chat (not muted and no cooldown), otherwise false
     */
    public boolean mayChat() {
        return !isMuted && chatCache.getIfPresent(user.getUuid()) == null;
    }
}
