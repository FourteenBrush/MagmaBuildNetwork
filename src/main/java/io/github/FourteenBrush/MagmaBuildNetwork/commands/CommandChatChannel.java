package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import com.google.common.collect.Lists;
import io.github.FourteenBrush.MagmaBuildNetwork.chat.framework.ChatChannel;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.user.User;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandChatChannel extends CommandHandler implements IConsoleCommand {

    private final String[] helpMessage = Utils.colorize(
            "&e------------ &7[&eChatchannel Command&7] &e------------",
            "&7Below is a list of all chatchannel commands:",
            "  &6/chatchannel join <name> &7- &6Joins the chatchannel with that name",
            "  &6/chatchannel leave <name> &7- &6Leaves the chatchannel with that name",
            "  &6/chatchannel setcurrent <name> &7- &6Sets the chatchannel with that name as your current channel",
            "  &6/chatchannel moderate <name> <action> <player> &7- &6Performs moderator actions on this chatchannel"
    );

    public CommandChatChannel() {
        super("chatchannel", Permission.BASIC, true);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length == 2) {
            if (denyConsole()) return true;
            plugin.getUserManager().getUserAsync(executor.getUniqueId()).whenComplete((user, th) -> {
                switch (args[0].toLowerCase()) {
                    case "join": joinChannel(args[1], user);
                    case "leave": leaveChannel(args[1], user);
                    case "setcurrent": setCurrent(args[1], user);
                    case "mute": muteChannel(args[1], user);
                    default: sender.sendMessage(helpMessage);
                }
            });
        } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            if (denyConsole()) return true;
            return getChannels();
        } else if (args.length == 4 && args[0].equalsIgnoreCase("moderate")) {
            return moderateChannels(args);
        } else sender.sendMessage(helpMessage);
        return true;
    }

    private void joinChannel(String name, User user) {
        ChatChannel channel = plugin.getChannelManager().getChannel(name);
        if (channel == null) {
            executor.sendMessage(Lang.CHANNEL_DOES_NOT_EXISTS.get());
        } else if (!channel.isAllowedToJoin(user)) {
            executor.sendMessage(Lang.CHANNEL_JOIN_NOT_PERMITTED.get(channel.getPrefix()));
        } else if (!user.getChatProfile().addChannel(channel)) {
            executor.sendMessage(Lang.CHANNEL_ALREADY_IN.get());
        } else if (user.getChatProfile().getCurrentChannel() != channel) {
            user.getChatProfile().setCurrentChannel(channel);
            channel.join(user);
        }
    }


    private void leaveChannel(String name, User user) {
        ChatChannel channel = plugin.getChannelManager().getChannel(name);
        plugin.getUserManager().getUserAsync(UUID.randomUUID()).whenComplete((u,e) -> {
            // handling all the stuff here instead
           if (u.getChatProfile().removeChannel(channel)) {
               channel.leave(u);
           }
        });
        // -------------------------------------------------------
        if (channel == null) {
            executor.sendMessage(Lang.CHANNEL_DOES_NOT_EXISTS.get());
        } else if (user.getChatProfile().removeChannel(channel)) {
            channel.leave(user);
        } else {
            executor.sendMessage(Lang.CHANNEL_NOT_JOINED.get(channel.getName()));
        }
    }

    private void setCurrent(String name, User user) {
        ChatChannel channel = plugin.getChannelManager().getChannel(name);
        if (channel == null) {
            executor.sendMessage(Lang.CHANNEL_DOES_NOT_EXISTS.get());
        } else if (user.getChatProfile())

        else if (user.isBannedFromChannel(channel)) {
            executor.sendMessage(Lang.BANNED_FROM_CHANNEL.get());
        } else if (!executor.hasPermission(channel.getSetMainPermission())) {
            executor.sendMessage(Lang.CHANNEL_SETMAIN_NOT_PERMITTED.get());
        } else if (user.getChannels().contains(channel)) {
            user.setMainChannel(channel);
            executor.sendMessage(Lang.CHANNEL_SET_AS_MAIN.get(channel.getName()));
        } else {
            user.addChannel(channel);
            user.setMainChannel(channel);
            executor.sendMessage(Lang.CHANNEL_JOINED.get(channel.getName()));
            executor.sendMessage(Lang.CHANNEL_SET_AS_MAIN.get(channel.getName()));
        }
    }

    private void muteChannel(String name, User user) {
        // todo
    }

    private boolean moderateChannels(String[] args) {
        if (sender instanceof Player && !Permission.CHANNELS_MODERATE.has(executor, true, args[1], "*")) return true;
        Channel channel = plugin.getChannelManager().getChannel(args[1]);
        if (channel == null) {
            executor.sendMessage(Lang.CHANNEL_DOES_NOT_EXISTS.get());
            return true;
        }
        @Deprecated
        User user = plugin.getUserManager().getUser(Bukkit.getOfflinePlayer(args[2]).getUniqueId());
        switch (args[2]) { // The moderation action
            case "kick":
                if (!user.isInChannel(channel)) return true;
                user.removeChannel(channel);
                sender.sendMessage(Lang.CHANNEL_KICKED_PLAYER.get(user.getOfflinePlayer().getName(), channel.getName()));
                if (user.getPlayer() != null)
                    user.getPlayer().sendMessage(Lang.CHANNEL_KICKED_FROM.get(channel.getName()));
                break;
            case "ban":
                user.banFromChannel(channel);
                sender.sendMessage(Lang.CHANNEL_BANNED_PLAYER.get(user.getOfflinePlayer().getName(), channel.getName()));
                if (user.getPlayer() != null)
                    user.getPlayer().sendMessage(Lang.CHANNEL_BANNED_FROM.get(channel.getName()));
                break;
            case "unban":
                if (!user.isBannedFromChannel(channel)) return true;
                user.unbanFromChannel(channel);
                sender.sendMessage(Lang.CHANNEL_UNBANNED_PLAYER.get(user.getOfflinePlayer().getName(), channel.getName()));
                if (user.getPlayer() != null)
                    user.getPlayer().sendMessage(Lang.CHANNEL_UNBANNED_FROM.get(channel.getName()));
                break;
        }
        return true;
    }

    private boolean getChannels() {
        Set<Channel> channels = plugin.getChannelManager().getChannels().stream().filter(channel -> executor.hasPermission(channel.getJoinPermission())).collect(Collectors.toSet());
        StringBuilder builder = new StringBuilder();
        executor.sendMessage(Lang.CHANNELS_YOU_CAN_JOIN.get());
        if (channels.isEmpty()) {
            PlayerUtils.message(executor, "&6 -");
        } else {
            channels.forEach(channel -> {
                if (builder.length() > 0)
                    builder.append(", ");
                builder.append(channel.getName());
            });
            PlayerUtils.message(executor, "&6 " + builder);
        }
        User user = plugin.getUserManager().getUser(executor.getUniqueId());
        executor.sendMessage(Lang.CHANNELS_YOU_ARE_IN.get());
        if (user.getChannels().isEmpty()) {
            PlayerUtils.message(executor, "&6 -");
        } else {
            builder.setLength(0);
            user.getChannels().forEach(channel -> {
                if (builder.length() > 0)
                    builder.append(", ");
                builder.append(channel.getName());
            });
            PlayerUtils.message(executor, "&6 " + builder);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(@NotNull String[] args) {
        List<String> list = Lists.newArrayList("join", "leave", "setcurrent", "list");
        if (Permission.ADMIN.has(executor)) list.add("moderate");
        if (Permission.MODERATOR.has(executor)) list.add("mute");
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], list, new ArrayList<>());
        } else if (args[0].equalsIgnoreCase("moderate")) {
            switch (args.length) {
                case 2: return StringUtil.copyPartialMatches(args[1], plugin.getChannelManager().getChannels()
                        .stream().map(ChatChannel::getName).filter(name -> Permission.CHANNELS_MODERATE
                                .has(executor, false, name, "*")).collect(Collectors.toList()), new ArrayList<>());
                case 3: if (Permission.ADMIN.has(executor))
                    return StringUtil.copyPartialMatches(args[2], Arrays.asList("kick", "ban", "unban"), new ArrayList<>());
                case 4: return null;
            }
        } else if (args.length == 2) { // Channel leave global -> tab complete the channels
            if (args[0].equalsIgnoreCase("leave")) {
                return StringUtil.copyPartialMatches(args[1], plugin.getUserManager().getUser(executor.getUniqueId())
                        .getChannels().stream().map(ChatChannel::getName).collect(Collectors.toList()), new ArrayList<>());
            } else if (!args[0].equalsIgnoreCase("list")) { // Everything other than list, list will return nothing
                return StringUtil.copyPartialMatches(args[1], plugin.getChannelManager().getChannels()
                        .stream().filter(channel -> executor.hasPermission(channel.getJoinPermission())).map(
                                Channel::getName).collect(Collectors.toList()), new ArrayList<>());
            }
        }
        return super.tabComplete(args);
    }
}