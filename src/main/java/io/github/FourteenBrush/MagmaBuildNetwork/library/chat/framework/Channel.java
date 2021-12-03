package io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
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
    protected Set<User> recipients;
    protected final MBNPlugin plugin;

    protected Channel(MBNPlugin plugin, String name, String shortName, String prefix, Permission joinPermission) {
        this.plugin = plugin;
        this.name = name;
        this.prefix = Utils.colorize(prefix);
        this.shortName = shortName;
        this.joinPermission = joinPermission;
        this.joinPermission.setDefault(PermissionDefault.FALSE);
        format = getDefaultFormat();
        autoJoinPermission = new Permission("magmabuildnetwork.channels.autojoin." + name, PermissionDefault.FALSE);
        setMainPermission = new Permission("magmabuildnetwork.channels.setmain." + name, PermissionDefault.FALSE);
        recipients = new HashSet<>();
    }

    protected Channel(MBNPlugin plugin, String name, String shortName, String prefix, Permission permission, String format) {
        this(plugin, name, shortName, prefix, permission);
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFormat() {
        return format == null ? getDefaultFormat() : format;
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

    private String getDefaultFormat() {
        return Utils.colorize(plugin.getConfig().getString("default-chat-format"));
    }

    protected void addPlayer(User user) {
        recipients.add(user);
    }

    protected void removePlayer(User user) {
        recipients.remove(user);
    }
    // Will be overridden
    protected Set<User> getRecipients(User user) {
        return recipients;
    }

    public void sendMessage(User from, String message, String format) {
        message = format(from, message, format);;
        for (User c : getRecipients(from)) {
            c.sendMessage(message);
        }
        Utils.logInfo(message);
    }

    protected String applyDefaultFormat(User from, String message, String format) {
        format = format.replace("{CHANNEL_PREFIX}", prefix)
                .replace("{PREFIX}", plugin.getChat().getPlayerPrefix(from.getPlayer()))
                .replace("{NAME}", from.getPlayer().getDisplayName())
                .replace("{SUFFIX}", plugin.getChat().getPlayerSuffix(from.getPlayer()))
                .replace("{MESSAGE}", message);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            format = PlaceholderAPI.setPlaceholders(from.getPlayer(), format);
        }
        return Utils.colorize(format.replaceAll(" {2}", " "));
    }

    protected abstract String format(User from, String message, String format);
}
