package io.github.FourteenBrush.MagmaBuildNetwork.user.profiles;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.FourteenBrush.MagmaBuildNetwork.chat.framework.ChannelRank;
import io.github.FourteenBrush.MagmaBuildNetwork.chat.framework.ChatChannel;
import io.github.FourteenBrush.MagmaBuildNetwork.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class ChatProfile {
    private static final Cache<UUID, Boolean> chatCache = CacheBuilder.newBuilder()
                    .expireAfterWrite(800, TimeUnit.MILLISECONDS).build(); // expires after 0.8 seconds

    private final User user;
    /* assuming that the current channel is also present in the channels */
    private final Map<ChatChannel, ChannelRank> chatChannels;
    private ChatChannel currentChannel;
    private boolean isMuted;

    /**
     * Constructs a chat profile, which belongs to an user
     * @param user the user this profile belongs to
     */
    public ChatProfile(User user) {
        this.user = user;
        chatChannels = new HashMap<>();
    }

    /**
     * @return the current channel, or null if there isn't one
     */
    public ChatChannel getCurrentChannel() {
        return currentChannel;
    }

    /**
     * Tries to set the argument as the new current channel
     * @param channel the new channel to use as current one
     * @return true if the current channel has been changed, false otherwise
     */
    public boolean setCurrentChannel(@NotNull ChatChannel channel) {
        chatChannels.putIfAbsent(currentChannel, currentChannel.getDefaultRank());
        return currentChannel != (currentChannel = channel);
    }

    /**
     * Tries to add a channel to the channel list
     * @param channel the channel which needs to be added
     * @return true if the channel was added, false if it was already present
     */
    public boolean addChannel(@NotNull ChatChannel channel) {
        return chatChannels.putIfAbsent(channel, channel.getDefaultRank()) == null;
    }

    /**
     * Tries to remove a channel from the channel list
     * @param channel the channel which needs to be removed, if the channel equals the current channel,
     *                the current channel will be set to null
     * @return true if the channel was removed, false otherwise
     */
    public boolean removeChannel(@NotNull ChatChannel channel) {
        if (currentChannel == channel) {
            currentChannel = null;
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
    public boolean isInChannel(@NotNull ChatChannel channel) {
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
     * Mutes the user, this only applies to <strong>the current</strong> channel
     * If the user changes channel {@link #isMuted} will return false
     * @param flag a boolean flag indicating whether to mute or not
     * @return true if the flag is different than {@link #isMuted}
     */
    public boolean setMuted(boolean flag) {
        return isMuted != (isMuted = flag);
    }

    /**
     * Gets the current privileges for the channel
     * @param channel the channel you want to get the current privileges for
     * @return the rank, or null if the channel is not present
     */
    public ChannelRank getRank(@NotNull ChatChannel channel) {
        return chatChannels.get(channel);
    }

    /**
     * @return true if the user may chat (not muted, no listener rank, no cooldown), otherwise false
     * This only applies to the current channel
     */
    public boolean mayChat() {
        return !isMuted && getRank(currentChannel) != ChannelRank.LISTENER && chatCache.getIfPresent(user.getUuid()) != null;
    }
}
