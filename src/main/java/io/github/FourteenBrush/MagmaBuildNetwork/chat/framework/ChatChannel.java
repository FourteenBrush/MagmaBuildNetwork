package io.github.FourteenBrush.MagmaBuildNetwork.chat.framework;

import io.github.FourteenBrush.MagmaBuildNetwork.user.User;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Lang;
import org.apache.commons.lang.Validate;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

public class ChatChannel {
    protected final String name;
    protected final String prefix;
    protected final String joinPermission;
    protected String password;
    protected final boolean persistent;
    protected final ChannelRank defaultRank;
    protected final Set<User> joinedUsers;
    protected final Set<UUID> whitelist;
    protected final List<String> motd;

    /**
     * Constructs a new chat channel
     * @param name the channel name, this is internal and won't be displayed to normal users
     * @param prefix the channel prefix which will show in front of a chat message, colorized
     * @param joinPermission the permission to join this chat channel
     * @param persistent if the channel is persistent to restarts and will stay on the server
     */
    public ChatChannel(String name, String prefix, String joinPermission, boolean persistent) {
        Validate.isTrue(name.length() <= 20, "The channel name cannot be bigger than 20 characters!");
        Validate.isTrue(prefix.length() <= 22, "The channel prefix cannot be bigger than 20 characters!");
        this.name = name;
        this.prefix = Utils.colorize(prefix);
        this.joinPermission = joinPermission;
        this.password = ""; // may be null too
        this.persistent = persistent;
        defaultRank = ChannelRank.LISTENER;
        joinedUsers = new HashSet<>();
        whitelist = new HashSet<>();
        motd = new ArrayList<>();
    }

    /**
     * Gets the channel name, this is mostly used in identification situations and internal uses
     * @return the channel name
     * @see #getPrefix() which may better meet your needs
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the channel prefix (the form in which the channel will be visible on the screen), like "[LOCAL]"
     * This may include color codes
     * @return the prefix of the channel (the way it's shown ingame), f.e. "[LOCAL]"
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * gets the permission to join this channel, in string form
     * <strong>NOTE:</strong> moderators and users which can override permissions will still be
     * able to access the channel
     * @return the join permission
     */
    public String getJoinPermission() {
        return joinPermission;
    }

    /**
     * @return the password of the channel
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for the current channel
     * This password will be used for authentication when joining a channel
     * @param password the password to use
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets whether or not this channel is persistent, persistent channels are channels like global, local
     * which will be present at every time
     * @return true if this channel is persistent, false otherwise
     */
    public boolean isPersistent() {
        return persistent;
    }

    /**
     * Gets the default rank to apply to someone joining the channel
     * @return the default rank
     */
    public ChannelRank getDefaultRank() {
        return defaultRank;
    }

    /**
     * Sends a message to all users of the channel
     * @param msg a message which may include color codes
     */
    public void message(String msg) {
        joinedUsers.forEach(user -> PlayerUtils.message(user.getPlayer(), msg));
    }

    /**
     * Send a message to the channel based on a chat event
     * @param user the user which sent the message
     * @param event the chat event to base the message on
     */
    public void message(User user, AsyncPlayerChatEvent event) {
        ChannelRank rank = user.getChatProfile().getRank(this);
        if (!rank.canTalk()) {
            event.setCancelled(true);
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(prefix)
                .append(" ")
                .append(event.getPlayer().getDisplayName())
                .append(" ");
        if (rank.showName()) {
            builder.append(rank.getDisplayName()).append(" ");
        }
        String message = builder.append(rank.getMessageColor())
                .append(user.getPlayer().getDisplayName())
                .append(":")
                .append(event.getMessage()).toString();
        event.setFormat(message);
    }

    /**
     * Bans an user from this channel
     * More specifically, it will remove the whitelist for this user
     * @param uuid
     * @return
     */
    public boolean ban(UUID uuid) {
        return whitelist.remove(uuid);
    }

    public boolean isAllowedToJoin(User user) {
        return user.getPlayer().hasPermission(joinPermission) && !whitelist.contains(user.getUuid());
    }

    public boolean join(User user) {
        return join(user, true, true, false);
    }

    /**
     * Attempts to join a channel, this can fail under difference circumstances
     * <li>- The channel requires a whitelist and the user isn't whitelisted</li>
     * <li>- The user is already in this channel</li>
     * @param user the user to add
     * @param showMessage indicating whether or not to send a join message
     * @param checkWhitelist indicating whether or not to check the whitelist
     * @return true if the user was able to join, false otherwise
     */
    public boolean join(User user, boolean showMessage, boolean checkWhitelist, boolean forced) {
        if (!user.getChatProfile().addChannel(this) || joinedUsers.contains(user)) {
            user.getPlayer().sendMessage(Lang.CHANNEL_ALREADY_IN.get());
            return false;
        }
        if (checkWhitelist && !whitelist.contains(user.getUuid())) {
            user.getPlayer().sendMessage(Lang.CHANNEL_NOT_WHITELISTED.get());
            return false;
        }
        joinedUsers.add(user);
        if (showMessage) {
            user.getPlayer().sendMessage(Lang.CHANNEL_JOINED.get(prefix));
        }
        return true;
    }

    public boolean setAsCurrentFor(User user) {
        join(user, true, true, false);
        return user.getChatProfile().setCurrentChannel(this);
    }

    public boolean leave(User user) {
        return leave(user, true);
    }

    public boolean leave(User user, boolean showMessage) {
        if (!user.getChatProfile().removeChannel(this)) return false;
        joinedUsers.remove(user);
        if (showMessage) {
            user.sendMessage(Lang.CHANNEL_LEFT.get(name));
        }
        return true;
    }
}
