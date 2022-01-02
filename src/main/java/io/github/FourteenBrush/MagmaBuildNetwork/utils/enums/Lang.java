package io.github.FourteenBrush.MagmaBuildNetwork.utils.enums;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;

public enum Lang {

    NO_PERMISSION("no-permission", "&cI''m sorry but you don't have permission!"),
    NO_CONSOLE("no-console", "&cI''m sorry but the console can't execute this!"),
    SPECIFY_A_PLAYER("specify-a-player", "&cPlease specify a player!"),
    JOIN_MESSAGE("join-message", "&7[&a&l+&7] &b{0} &7joined the server."),
    LEAVE_MESSAGE("leave-message", "&7[&c&l-&7] &b{0} &7left the server."),
    PLAYER_NOT_ONLINE("player-not-online", "&cI''m sorry but that player is currently not online!"),
    SPAWN_TELEPORTED("spawn.teleported", "&6You teleported to spawn."),
    SPAWN_TELEPORTED_BY_OTHER_PLAYER("spawn.teleported-by-other-player", "&6You have been teleported to spawn by {0}."),
    SPAWN_SET("spawn.set", "&6Spawn successfully set at your location."),
    SPAWN_TELEPORTED_OTHER_PLAYER("spawn.teleport-player", "&6Successfully teleported {0} to spawn."),
    SPAWN_DISABLED_IN_COMBAT("spawn.disabled-in-combat", "&cYou cannot go to spawn while in combat!"),
    HOME_ALREADY_EXISTS("home.already-exists", "&cYou already have a home with that name, please choose another!"),
    HOME_LIMIT_REACHED("home.limit-reached", "&cYou have reached the maximum amount of homes, please delete one first!"),
    HOME_CREATED_SUCCESS("home.created-success", "&6Successfully set your new home ( {0} )."),
    HOME_REMOVED_SUCCESS("home.removed-success", "&6Successfully removed {0} from your homes."),
    HOME_TELEPORTED("home.teleported", "&6Teleported to {0}."),
    VANISH_ENABLED("vanish.enabled", "&6You have been vanished."),
    VANISH_DISABLED("vanish.disabled", "&6You became visible again."),
    VANISHED_OTHER_PLAYER("vanish.other-player", "&6Successfully vanished {0}."),
    VANISH_ANNOUNCE("vanish.announce", "&e{0} has vanished. Poof."),
    VANISH_BACK_VISIBLE_ANNOUNCE("vanish.back-visible-announce", "&e{0} has become visible."),
    VANISH_ALREADY_VANISHED_FOR_QUIT("vanish.already-vanished-for-quit", "&cYou are already vanished!"),
    VANISH_NO_VANISHED_PLAYERS("vanish.no-vanished-players", "&cThere are no vanished players online!"),
    FLY_ENABLED("fly.enabled", "&6Set fly mode &aenabled&6 for {0}."),
    FLY_DISABLED("fly.disabled", "&6Set fly mode &cdisabled&6 for {0}."),
    TRADE_WITH_YOURSELF_DISALLOWED("trade.with-yourself-disallowed", "&cYou cannot trade with yourself!"),
    TRADE_NO_CREATIVE("trade.no-creative", "&cYou are not allowed to trade in creative!"),
    TRADE_NO_DIFFERENT_WORLDS("trade.no-different-worlds", "&cYou are not allowed to trade in a different world than the person you're trading with!"),
    TRADE_DISTANCE_TOO_BIG("trade.distance-too-big", "&cYou are too far from {0} to trade. Please stand within {1} blocks from each other!"),
    TRADE_REQUEST_SENT("trade.request-sent", "&6Sent a trade request to {0}."),
    TRADE_REQUEST_CANCELLED_BY("trade.request-cancelled-by", "&6{0} cancelled his trade request."),
    TRADE_REQUEST_CANCELLED("trade.request-cancelled", "&6Cancelled trade request with {0}."),
    TRADE_NO_REQUEST("trade.no-request", "&cYou don't have a trade request from that player!"),
    TRADE_NO_OUTGOING_REQUEST("trade.no-outgoing-request", "&cYou don't have sent a trade request!"),
    TRADE_ACCEPTED_BY("trade.accepted-by", "&6{0} accepted your trade request."),
    TRADE_ACCEPTED("trade.accepted", "&6Accepted the trade request of {0}."),
    TRADE_DECLINED_BY("trade.declined-by", "&6{0} declined your trade request."),
    TRADE_DECLINED("trade.declined", "&6Declined the trade request of &6{0}&6."),
    RATE_LIMITED("chat.rate-limited", "&cYou are sending messages to fast, Please slow down!"),
    CHANNEL_NOT_JOINED("chat.not-in-channel", "&cYou are not in the channel {0}!"),
    CHANNEL_NO_CHANNEL_JOINED("chat.no-channel-joined", "&cYou cannot talk as you are not in a channel, join one with &l&6/channel join <name>&r&c!"),
    CHANNEL_DOES_NOT_EXISTS("chat.channel-does-not-exists", "&cThe channel with that name doesn't exist!"),
    BANNED_FROM_CHANNEL("chat.banned-from-channel", "&cIt seems like you are banned from this channel!"),
    CHANNEL_JOIN_NOT_PERMITTED("chat.channel-join-not-permitted", "&cYou are not allowed to join the channel {0}&c!"),
    CHANNEL_SETMAIN_NOT_PERMITTED("chat.channel-setmain-not-permitted", "&cYou don't have permission to set this channel as your main channel!"),
    CHANNEL_SET_AS_MAIN("chat.channel-set-as-main", "&6Set {0} as your main channel."),
    CHANNEL_JOINED("chat.joined", "&6Joined the channel {0}."),
    CHANNEL_LEFT("chat.channel-left", "&6Left the channel {0}."),
    CHANNEL_ALREADY_IN("chat.already-in-channel", "&cYou are already in that channel!"),
    CHANNEL_NOT_WHITELISTED("chat.not-whitelisted", "&cYou are not whitelisted to join this channel!"),
    CHANNEL_KICKED_PLAYER("chat.kicked-player", "&6Kicked player {0} from {1}!"),
    CHANNEL_KICKED_FROM("chat.kicked-from", "&6You have been kicked from {0}!"),
    CHANNEL_BANNED_PLAYER("chat.banned-player", "&6Banned {0} from {1}."),
    CHANNEL_BANNED_FROM("chat.banned-from", "&6You have been banned from {0}!"),
    CHANNEL_UNBANNED_PLAYER("chat.unbanned-player", "&6Unbanned player {0} from {1}."),
    CHANNEL_UNBANNED_FROM("chat.unbanned-from", "&6You have been unbanned from {0}!"),
    CHANNELS_YOU_CAN_JOIN("chat.channels-you-can-join", "&6This is a list of channels you are able to join:"),
    CHANNELS_YOU_ARE_IN("chat.channels-you-are-in", "&6This is a list of channels you are in:"),
    NO_CHANNELS_FOUND("chat.no-channels-found", "&cThat player is not in any channels!"),
    NO_CONVERSATION("chat.no-conversation", "&cYou don't have anyone to reply to, start a conversation first"),
    LOCK_PLACE_LOCK("lock.place-lock", "&6Right click a block to lock it!\nOr type /lock cancel to cancel!"),
    LOCK_REMOVE_LOCK("lock.remove-lock", "&6Right click a block to remove the lock!\nOr type /lock cancel to cancel!"),
    LOCK_CANCELLED("lock.cancelled", "&6Cancelled!"),
    LOCK_NOTHING_TO_CANCEL("lock.nothing-to-cancel", "&cNothing to cancel!"),
    LOCK_BYPASSING("lock.bypassing", "&6Now bypassing locks!"),
    LOCK_NOT_LONGER_BYPASSING("lock.not-longer-bypassing", "&6Not longer bypassing locks."),
    COOLDOWNS_BYPASSING("cooldowns.bypassing", "&6You are now bypassing cooldowns."),
    COOLDOWNS_NOT_LONGER_BYPASSING("cooldowns.not-longer-bypassing", "&6You are not longer bypassing cooldowns."),
    COMMAND_COOLDOWN("command.cooldown", "&cPlease wait &e{0}&c before reusing this command!");

    private final String value;

    Lang(String path, String fallback) {
        FileConfiguration lang = MBNPlugin.getInstance().getConfigManager().getLang();
        String real = lang.getString(path);
        if (real == null || real.isEmpty()) {
            Logger.WARNING.log("Missing lang data found on " + path + ", using fallback");
            real = fallback;
        }
        value = Utils.colorize(real);
    }

    public String get(String... args) {
        if (args.length > 0) {
            String result = null;
            for (int i = 0; i < args.length; i++) {
                result = value.replace("{" + i + "}", args[i]);
            }
            return result;
        }
        return value;
    }
}
