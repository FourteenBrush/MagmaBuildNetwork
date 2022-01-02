package io.github.FourteenBrush.MagmaBuildNetwork.chat.framework;

import io.github.FourteenBrush.MagmaBuildNetwork.player.User;
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
    protected boolean persistent;
    protected final ChannelRank defaultRank;
    protected final Set<User> joinedUsers;
    protected final Set<UUID> whitelist;
    protected final List<String> motd

    /**
     * Constructs a new chatchannel
     * @param name the channel name, this is internal and won't be displayed to normal users
     * @param prefix the channel prefix which will show in front of a chat message, colorized
     * @param joinPermission the permission to join this chatchannel
     * @param persistent if the channel is persistent to restarts and will stay on the server
     */
    public ChatChannel(String name, String prefix, String joinPermission, boolean persistent) {
        Validate.isTrue(name.length() <= 20, "The channel name cannot be bigger than 20 characters!");
        Validate.isTrue(prefix.length() <= 20, "The channel prefix cannot be bigger than 20 characters!");
        this.name = name;
        this.prefix = Utils.colorize(prefix);
        this.joinPermission = joinPermission;
        this.password = ""; // may be null too
        this.persistent = persistent;
        defaultRank = ChannelRank.LISTENER;
        joinedUsers = new HashSet<>();
        whitelist = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getJoinPermission() {
        return joinPermission;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public ChannelRank getDefaultRank() {
        return defaultRank;
    }

    public void message(String colorizedMsg) {
        joinedUsers.forEach(user -> user.getPlayer().sendMessage(colorizedMsg));
    }

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

    public void ban(User user) {
        whitelist.remove(user.getUuid());

    }

    public boolean join(User user) {
        return join(user, true);
    }

    public boolean join(User user, boolean showMessage) {
        if (!user.getChatProfile().addChannel(this)) return false;
        joinedUsers.add(user);
        if (showMessage) {
            user.getPlayer().sendMessage(Lang.CHANNEL_JOINED.get(name));
        }
        return true;
    }

    public boolean setAsCurrentFor(User user) {
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
