package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GlobalCommand implements CommandExecutor {

    private final Main plugin = Main.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("MagmaBuildNetwork")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                Utils.message(sender, new String[] {"§f---- §9MagmaBuildNetwork §f---\n",
                        "§9/mbn §7help <command> - §fDisplay info about a command.",
                        "§9/mbn §7reload - §fReloads the plugin"});
            }
            else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!Utils.hasPermission(sender, "reload")) {
                    Utils.messageNoPermission(sender);
                    return true;
                }
                plugin.reloadConfig();
                Utils.message(sender, "§2Successfully reloaded " + plugin.getName());
            }
        }

        return true;
    }
}
