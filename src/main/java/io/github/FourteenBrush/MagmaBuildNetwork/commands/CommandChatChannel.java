package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import com.google.common.collect.Lists;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.Channel;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.ChatPlayer;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandChatChannel extends AbstractCommand implements ConsoleCommand {

    private static final String[] HELP_MESSAGE = Utils.colorize(
        "&e------------ &7[&eChatchannel Command&7] &e------------",
            "&7Below is a list of all chatchannel commands:",
            "  &6/chatchannel join <name> &7- &6Joins the chatchannel with that name",
            "  &6/chatchannel leave <name> &7- &6Leaves the chatchannel with that name",
            "  &6/chatchannel setmain <name> &7- &6Sets the chatchannel with that name as main channel",
            "  &6/chatchannel moderate <name> &7- &6Performs moderator actions on this chatchannel"
    );

    public CommandChatChannel() {
        super("chatchannel", Permission.BASIC, true);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length == 0 || args.length > 4 || args[0].equalsIgnoreCase("help")) {
            PlayerUtils.message(executor, HELP_MESSAGE);
        } else if (args.length == 2) {
            if (isConsoleSender) {
                PlayerUtils.message(sender, Lang.NO_CONSOLE.get());
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "join":
                    return joinChannel(args[1], plugin.getChatPlayer(executor.getUniqueId()));
                case "leave":
                    return leaveChannel(args[1], plugin.getChatPlayer(executor.getUniqueId()));
                case "setmain":
                    return setMain(args[1], plugin.getChatPlayer(executor.getUniqueId()));
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("moderate")) {
            return moderateChannels(args);
        }
        return true;
    }

    private boolean joinChannel(String name, ChatPlayer chatPlayer) {
        Channel channel = plugin.getChannelManager().getChannel(name);
        if (channel == null) {
            PlayerUtils.message(executor, Lang.CHANNEL_DOES_NOT_EXISTS.get());
            return true;
        }
        if (chatPlayer.isInChannel(channel)) {
            PlayerUtils.message(executor, Lang.CHANNEL_ALREADY_IN.get());
            return true;
        } else {
            if (chatPlayer.isBannedFromChannel(channel)) {
                PlayerUtils.message(executor, Lang.BANNED_FROM_CHANNEL.get());
                return true;
            }
            if (!executor.hasPermission(channel.getJoinPermission())) {
                PlayerUtils.message(executor, Lang.CHANNEL_JOIN_NOT_PERMITTED.get(channel.getName()));
                return true;
            }
            chatPlayer.addChannel(channel);
            PlayerUtils.message(executor, Lang.CHANNEL_JOINED.get(channel.getName()));
        }
        return true;
    }

    private boolean leaveChannel(String name, ChatPlayer chatPlayer) {
        Channel channel = plugin.getChannelManager().getChannel(name);
        if (channel == null) {
            PlayerUtils.message(executor, Lang.CHANNEL_DOES_NOT_EXISTS.get());
            return true;
        }
        if (chatPlayer.isInChannel(channel)) {
            chatPlayer.removeChannel(channel);
            PlayerUtils.message(executor, Lang.CHANNEL_LEFT.get(channel.getName()));
        } else {
            PlayerUtils.message(executor, Lang.CHANNEL_NOT_JOINED.get());
        }
        return true;
    }

    private boolean setMain(String name, ChatPlayer chatPlayer) {
        Channel channel = plugin.getChannelManager().getChannel(name);
        if (channel == null) {
            PlayerUtils.message(executor, Lang.CHANNEL_DOES_NOT_EXISTS.get());
            return true;
        }
        if (chatPlayer.isBannedFromChannel(channel)) {
            PlayerUtils.message(executor, Lang.BANNED_FROM_CHANNEL.get());
            return true;
        }
        if (!executor.hasPermission(channel.getSetMainPermission())) {
            PlayerUtils.message(executor, Lang.CHANNEL_SETMAIN_NOT_PERMITTED.get());
            return true;
        }
        if (chatPlayer.getChannels().contains(channel)) {
            PlayerUtils.message(executor, Lang.CHANNEL_SET.get(channel.getName()));
        } else {
            chatPlayer.addChannel(channel);
            PlayerUtils.message(executor, Lang.CHANNEL_JOINED.get(channel.getName()));
        }
        chatPlayer.setMainChannel(channel);
        return true;
    }

    private boolean moderateChannels(String[] args) {
        if (!executor.hasPermission("MagmaBuildNetwork.moderate-channel." + args[1])) {
            PlayerUtils.message(executor, Lang.NO_PERMISSION.get());
            return true;
        }
        Channel channel = plugin.getChannelManager().getChannel(args[1]);
        if (channel == null) {
            PlayerUtils.message(executor, Lang.CHANNEL_DOES_NOT_EXISTS.get());
            return true;
        }
        @Deprecated
        ChatPlayer chatPlayer = plugin.getChatPlayer(Bukkit.getOfflinePlayer(args[2]).getUniqueId());
        switch (args[2]) { // The moderation action
            case "kick":
                if (chatPlayer.isInChannel(channel)) {
                    chatPlayer.removeChannel(channel);
                    PlayerUtils.message(executor, Lang.CHANNEL_KICKED_PLAYER.get(chatPlayer.getOfflinePlayer().getName(), channel.getName()));
                    if (chatPlayer.getOfflinePlayer().isOnline())
                            PlayerUtils.message(chatPlayer.getPlayer(), Lang.CHANNEL_KICKED_FROM.get(channel.getName()));
                    break;
                }
            case "ban":
                chatPlayer.banFromChannel(channel);
                PlayerUtils.message(executor, Lang.CHANNEL_BANNED_PLAYER.get(chatPlayer.getOfflinePlayer().getName()), channel.getName());
                if (chatPlayer.getOfflinePlayer().isOnline())
                    PlayerUtils.message(chatPlayer.getPlayer(), Lang.CHANNEL_BANNED_FROM.get(channel.getName()));
                break;
            case "unban":
                if (chatPlayer.isBannedFromChannel(channel)) {
                    chatPlayer.unbanFromChannel(channel);
                    PlayerUtils.message(executor, Lang.CHANNEL_UNBANNED_PLAYER.get(chatPlayer.getOfflinePlayer().getName(), channel.getName()));
                    if (chatPlayer.getOfflinePlayer().isOnline())
                        PlayerUtils.message(chatPlayer.getPlayer(), Lang.CHANNEL_UNBANNED_FROM.get(channel.getName()));
                    break;
                }
        }
        return true;
    }

    @Override
    protected List<String> tabComplete(@NotNull String... args) {
        if (args.length == 1) {
            List<String> l = Lists.newArrayList("join", "leave", "setmain");
            if (Permission.ADMIN.has(executor)) l.add("moderate");
            return StringUtil.copyPartialMatches(args[0], l, new ArrayList<>());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("moderate")) {
            return StringUtil.copyPartialMatches(args[1], plugin.getChannelManager().getChannels().stream().map(Channel::getName).collect(Collectors.toList()), new ArrayList<>());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("moderate")) {
            return StringUtil.copyPartialMatches(args[2], Arrays.asList("ban", "kick", "unban"), new ArrayList<>());
        }
        return super.tabComplete(args);
    }
}