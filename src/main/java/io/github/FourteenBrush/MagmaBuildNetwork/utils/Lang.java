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
    VANISHED_OTHER_PLAYER("vanish.other-player", "&aSuccessfully vanished &6{0}&a."),
    VANISH_ENABLED("vanish.enabled", "&aYou have been vanished."),
    VANISH_DISABLED("vanish.disabled", "&aYou became visible again."),
    TRADE_WITH_YOURSELF_DISALLOWED("trade.with-yourself-disallowed", "&cYou cannot trade with yourself!"),
    TRADE_NO_CREATIVE("trade.no-creative", "&cYou are not allowed to trade in creative!"),
    TRADE_NO_DIFFERENT_WORLDS("trade.no-different-worlds", "&cYou are not allowed to trade in a different world than the person you're trading with!"),
    TRADE_DISTANCE_TOO_BIG("trade.distance-too-big", "&cYou are too far from &6{0}&a to trade. Please stand within {1} blocks from each other!"),
    TRADE_REQUEST_SENT("trade.request-sent", "&aSent a trade request to &6{0}&a."),
    TRADE_REQUEST_CANCELLED_BY("trade.request-cancelled-by", "&6{0}&a cancelled his trade request."),
    TRADE_REQUEST_CANCELLED("trade.request-cancelled", "&aCancelled trade request with &6{0}&a."),
    TRADE_NO_REQUEST("trade.no-request", "&cYou don't have a trade request from that player!"),
    TRADE_ACCEPTED_BY("trade.accepted-by", "&6{0}&a accepted your trade request."),
    TRADE_ACCEPTED("trade.accepted", "&aAccepted the trade request of &6{0}&6."),
    TRADE_DECLINED_BY("trade.declined-by", "&6{0}&a declined your trade request."),
    TRADE_DECLINED("trade.declined", "&aDeclined the trade request of &6{0}&a.");

    private String value;

    Lang(String path, String fallback) {
        FileConfiguration lang = Main.getPlugin(Main.class).getConfigManager().getLang();
        String real = lang.getString(path);
        if (real == null) {
            Utils.logWarning("Missing lang data found: " + path);
            real = fallback;
        }
        value = Utils.colorize(real);
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
