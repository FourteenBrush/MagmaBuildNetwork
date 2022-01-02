package io.github.FourteenBrush.MagmaBuildNetwork.chat.framework;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.ChatColor;

public enum ChannelRank {

    LISTENER("", null, false, false, false, false, false, false, false, false, false, false, false),
    SPEAKER("[SPEAKER]", ChatColor.WHITE, true, false, false, false, false, false, false, false, false, true, false),
    MODERATOR("[CHANNEL MOD]", ChatColor.GREEN, true, true, false, true, true, true, true, true, false, true, true),
    OWNER("[OWNER]", ChatColor.GREEN, true, true, true, true, true, true, true, true, true, false, true);

    private final String displayName;
    private final ChatColor messageColor;
    private final boolean[] flags;

    ChannelRank(String displayName,
                ChatColor messageColor,
                boolean canTalk,
                boolean canInvite,
                boolean canSetPassword,
                boolean canSetMotd,
                boolean canSetRank,
                boolean canKick,
                boolean canBan,
                boolean canUnban,
                boolean canDelete,
                boolean canPromovateToOwner,
                boolean show) {
        this(displayName, messageColor, new boolean[] { canTalk, canInvite, canSetPassword, canSetMotd, canSetRank, canKick, canBan, canUnban, canDelete, canPromovateToOwner, show });
    }

    ChannelRank(String displayName, ChatColor messageColor, boolean[] flags) {
        this.displayName = displayName;
        this.messageColor = messageColor;
        this.flags = flags;
    }

    public String getDisplayName() {
        return Utils.colorize(displayName);
    }

    public ChatColor getMessageColor() {
        return messageColor;
    }

    public boolean canTalk() {
        return flags[0];
    }

    public boolean canInvite() {
        return flags[1];
    }

    public boolean canSetPassword() {
        return flags[2];
    }

    public boolean canSetMotd() {
        return flags[3];
    }

    public boolean canSetRank() {
        return flags[4];
    }

    public boolean canKick() {
        return flags[5];
    }

    public boolean canBan() {
        return flags[6];
    }

    public boolean canUnban() {
        return flags[7];
    }

    public boolean canDelete() {
        return flags[8];
    }

    public boolean canPromovateToOwner() {
        return flags[9];
    }

    public boolean showName() {
        return flags[10];
    }
}
