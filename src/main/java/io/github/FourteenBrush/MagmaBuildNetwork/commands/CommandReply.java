package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandReply extends CommandHandler implements IConsoleCommand {

    public CommandReply() {
        super("reply", Permission.MODERATOR, false);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        UUID senderUUID = sender instanceof Player ? executor.getUniqueId() : plugin.getConsoleUUID();
        if (!plugin.getMessageManager().sendReply(senderUUID, String.join(" ", args))) {
            sender.sendMessage(Lang.NO_CONVERSATION.get());
        }
        return true;
    }
}
