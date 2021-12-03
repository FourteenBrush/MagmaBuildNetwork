package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.User;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandChat extends CommandHandler implements IConsoleCommand {

    public CommandChat() {
        super("chat", Permission.MODERATOR, true);
    }

    @Deprecated
    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("player")) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
            if (!offlinePlayer.hasPlayedBefore()) {
                PlayerUtils.message(sender, "&cThat player is currently not online!");
                return true;
            }
            User user = plugin.getUser(offlinePlayer.getUniqueId());
            if (user.getChannels().isEmpty()) {
                sender.sendMessage(Lang.NO_CHANNELS_FOUND.get());
            } else {
                StringBuilder builder = new StringBuilder();
                user.getChannels().forEach(channel -> builder.append(channel.getName()).append(", "));
                PlayerUtils.message(sender, builder.toString());
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Collections.singleton("player"), new ArrayList<>());
        } else if (args.length == 2){
            return null;
        }
        return super.tabComplete(args);
    }
}
