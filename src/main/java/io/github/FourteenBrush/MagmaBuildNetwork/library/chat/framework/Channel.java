package io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashSet;
import java.util.Set;

public abstract class Channel {

    private final String name;
    private final String prefix;
    private final String shortName;
    private String format;
    private final Permission joinPermission; // The permission required to join the channel
    private final Permission autoJoinPermission;
    private final Permission setMainPermission; // The permission required to have this channel set as your main channel on every join
    protected Set<ChatPlayer> players;
    protected static final Main PLUGIN = Main.getPlugin(Main.class);

    protected Channel(String name, String shortName, String prefix, Permission permission) {
        this.name = name;
        this.prefix = prefix;
        this.shortName = shortName;
        this.joinPermission = permission;
        permission.setDefault(PermissionDefault.FALSE);
        format = getDefaultFormat();
        autoJoinPermission = new Permission("MagmaBuildNetwork.channels.autojoin" + name, PermissionDefault.FALSE);
        setMainPermission = new Permission("MagmaBuildNetwork.channels.setmain." + name, PermissionDefault.FALSE);
        players = new HashSet<>();
    }

    protected Channel(String name, String shortName, String prefix, Permission permission, String format) {
        this(name, shortName, prefix, permission);
        this.format = format;
    }

    private String getDefaultFormat() {
        return PLUGIN.getConfig().getString("default-chat-format");
    }

    public String getFormat() {
        return format == null ? getDefaultFormat() : format;
    }

    protected void addPlayer(ChatPlayer chatPlayer) {
        forceAddPlayer(chatPlayer);
    }

    protected void forceAddPlayer(ChatPlayer chatPlayer) {
        players.add(chatPlayer);
    }

    protected void removePlayer(ChatPlayer chatPlayer) {
        players.remove(chatPlayer);
    }

    @SuppressWarnings("unused") // Will be overridden
    protected Set<ChatPlayer> getRecipients(ChatPlayer chatPlayer) {
        return players;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    private String getPrefix() {
        return prefix;
    }

    public Permission getJoinPermission() {
        return joinPermission;
    }

    public Permission getAutoJoinPermission() {
        return autoJoinPermission;
    }

    public Permission getSetMainPermission() {
        return setMainPermission;
    }

    public void sendMessage(ChatPlayer from, String message, String format) {
        message = format(from, message, format);
        for (ChatPlayer c : getRecipients(from)) {
            c.sendMessage(message);
        }
        Utils.logInfo(message);
    }

    protected String applyDefaultFormat(ChatPlayer from, String message, String format) {
        format = format.replace("{CHANNEL_PREFIX}", prefix);
        format = format.replace("{PREFIX}", PLUGIN.getChat().getPlayerPrefix(from.getPlayer()));
        format = format.replace("{NAME}", from.getPlayer().getDisplayName());
        format = format.replace("{SUFFIX}", PLUGIN.getChat().getPlayerSuffix(from.getPlayer()));
        format = format.replace("{MESSAGE}", message);
        return format;
    }

    protected abstract String format(ChatPlayer from, String message, String format);
}
