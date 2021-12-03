package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandTell extends CommandHandler implements IConsoleCommand {

    public CommandTell() {
        super("tell", Permission.MODERATOR, false);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length > 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (!PlayerUtils.checkPlayerOnline(sender, target)) return true;
            UUID senderUUID = sender instanceof Player ? executor.getUniqueId() : plugin.getConsoleUUID();
            plugin.getMessageManager().sendMessage(senderUUID, target.getUniqueId(), Utils.getFinalArgs(args, 1));
        } else {
            PlayerUtils.message(sender, "&6/tell <player> <message> &7- &6Sends a private message to a player");
        }
        return true;
    }
}
