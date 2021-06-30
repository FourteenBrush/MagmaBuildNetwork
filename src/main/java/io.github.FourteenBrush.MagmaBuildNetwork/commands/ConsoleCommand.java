package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ConsoleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (Utils.verifyIfIsAPlayer(sender)) {
            Utils.message(sender, "§This command must be ran by the console!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("console")) {
            Utils.logWarning("This works!");
            return true;
        }

        return true;
    }
}
