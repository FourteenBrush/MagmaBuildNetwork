package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Random;

public enum Lang {

    NO_PERMISSION("no-permission", "&cI'm sorry but you do not have permission!"),
    NO_CONSOLE("no-console", "&cI'm sorry but the console cannot execute this!"),
    PLAYER_NOT_ONLINE("player-not-online", "&cPlayer &6{0} &cis currently not online!"),
    MAINTENANCE_ALREADY_ENABLED("maintenance.already-enabled", "&cServer maintenance is already enabled, to turn in off use &6/maintenance disable&c."),
    MAINTENANCE_ALREADY_DISABLED("maintenance.already-disabled", "&cServer maintenance is already disabled!"),
    MAINTENANCE_ENABLED("maintenance.enabled", "&aServer maintenance mode has been enabled, \nkicked all players that don't have permission to stay!"),
    MAINTENANCE_DISABLED("maintenance.disabled", "&a"),
    SPAWN_TELEPORTED_BY_OTHER_PLAYER("spawn.teleported-by-other-player", "&aYou have been teleported to spawn by &6{0}&a."),
    SPAWN_TELEPORTED_OTHER_PLAYER_SUCCESS("spawn.teleport-player-success", "&aSuccessfully teleported &6{0}&a to spawn."),
    SPAWN_DISABLED_IN_COMBAT("spawn.disabled-in-combat", "&cYou cannot go to spawn while you are in combat!"),
    HOME_ALREADY_EXISTS("home.already-exists", "&cYou already have a home with that name, please choose another!"),
    HOME_LIMIT_REACHED("home.limit-reached", "&cYou have reached the maximum amount of homes, please delete one first!"),
    HOME_CREATED_SUCCESS("home.created-success", "&aSuccessfully set your new home ( &6{0}&a )."),
    HOME_REMOVED_SUCCESS("home.removed-success", "&aSuccessfully removed &6{0} &afrom your homes."),
    VANISH_ENABLED("vanish.enabled", "&aYou have been vanished."),
    VANISH_DISABLED("vanish.disabled", "&aYou became visible again."),
    VANISHED_OTHER_PLAYER("vanish.other-player", "&aSuccessfully vanished &6{0}&a."),
    VANISH_ANNOUNCE("vanish.announce", "&e{0} has vanished. Poof."),
    FLY_ENABLED("fly.enabled", "&eSet fly mode &aenabled&e for &6{0}&e."),
    FLY_DISABLED("fly.disabled", "&eSet fly mode &cdisabled&e for &6{0}&e."),
    TRADE_WITH_YOURSELF_DISALLOWED("trade.with-yourself-disallowed", "&cYou cannot trade with yourself!"),
    TRADE_NO_CREATIVE("trade.no-creative", "&cYou are not allowed to trade in creative!"),
    TRADE_NO_DIFFERENT_WORLDS("trade.no-different-worlds", "&cYou are not allowed to trade in a different world than the person you're trading with!"),
    TRADE_DISTANCE_TOO_BIG("trade.distance-too-big", "&cYou are too far from &6{0}&a to trade. Please stand within {1} blocks from each other!"),
    TRADE_REQUEST_SENT("trade.request-sent", "&aSent a trade request to &6{0}&a."),
    TRADE_REQUEST_CANCELLED_BY("trade.request-cancelled-by", "&6{0}&a cancelled his trade request."),
    TRADE_REQUEST_CANCELLED("trade.request-cancelled", "&aCancelled trade request with &6{0}&a."),
    TRADE_NO_REQUEST("trade.no-request", "&cYou don't have a trade request from that player!"),
    TRADE_NO_OUTGOING_REQUEST("trade.no-outgoing-request", "&cYou don't have sent a trade request!"),
    TRADE_ACCEPTED_BY("trade.accepted-by", "&6{0}&a accepted your trade request."),
    TRADE_ACCEPTED("trade.accepted", "&aAccepted the trade request of &6{0}&6."),
    TRADE_DECLINED_BY("trade.declined-by", "&6{0}&a declined your trade request."),
    TRADE_DECLINED("trade.declined", "&aDeclined the trade request of &6{0}&a."),
    RATE_LIMITED("chat.rate-limited", "&cYou are sending messages to fast, Please slow down!"),
    CHANNEL_NOT_JOINED("chat.not-in-channel", "&cYou are not in the channel &6{0}&c!"),
    CHANNEL_NO_CHANNEL_JOINED("chat.no-channel-joined", "&cYou cannot talk as you are not in a channel!"),
    CHANNEL_DOES_NOT_EXISTS("chat.channel-does-not-exists", "&cThe channel with that name doesn't exist!"),
    BANNED_FROM_CHANNEL("chat.banned-from-channel", "&cIt seems like you are banned from this channel!"),
    CHANNEL_JOIN_NOT_PERMITTED("chat.channel-join-not-permitted", "&cYou don't have permission to join &6{0}&c!"),
    CHANNEL_SETMAIN_NOT_PERMITTED("chat.channel-setmain-not-permitted", "&cYou don't have permission to set this channel as your main channel!"),
    CHANNEL_SET_AS_MAIN("chat.channel-set-as-main", "&aYou are now chatting in &6{0}"),
    CHANNEL_JOINED("chat.joined", "&aJoined the channel &6{0}"),
    CHANNEL_LEFT("chat.channel-left", "&aLeft the channel &6{0}&a."),
    CHANNEL_SET("chat.channel-set", "&aYou are now chatting in &6{0}"),
    CHANNEL_ALREADY_IN("channel.already-in", "&cYou are already in that channel!"),
    CHANNEL_KICKED_PLAYER("chat.kicked-player", "&aKicked player &6{0} &afrom &6{1}&a!"),
    CHANNEL_KICKED_FROM("chat.kicked-from", "&aYou have been kicked from &6{0}&a!"),
    CHANNEL_BANNED_PLAYER("chat.banned-player", "&aBanned &6{0} &afrom &6{1}&a!"),
    CHANNEL_BANNED_FROM("chat.banned-from", "&aYou have been banned from &6{0}&a!"),
    CHANNEL_UNBANNED_PLAYER("chat.unbanned-player", "&aUnbanned player &6{0} &afrom &6{1}&a."),
    CHANNEL_UNBANNED_FROM("chat.unbanned-from", "&aYou have been unbanned from &6{0}&a!");

    private String value;
    private String path;
    private String fallback;

    Lang(String path, String fallback) {
        this.path=path;
        this.fallback=fallback;
        FileConfiguration lang = Main.getPlugin(Main.class).getConfigManager().getLang();
        String real = lang.getString(path);
        if (real == null) {
            Utils.logWarning("Missing lang data found: " + path);
            real = fallback;
        }
        value = Utils.colorize(real);
    }

    public String getPath() {
        return path;
    }

    public String getFallback() {
        return fallback;
    }

    public String get(String... args) {
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                value = value.replace("{" + i + "}", args[i]);
            }
        }
        return value;
    }

    private static String messageBanned(String reason) { // todo
        final int appealCode = new Random().nextInt(10001);
        return Utils.colorize("&fYou are banned from this server, if you believe this is wrong,"
                + "\nyou can try to open a ticket at &6discord.gg/KWNYMDGX7H    Apeall code: &b " + appealCode
                + "\n&cReason: &d" + reason);
    }
}
