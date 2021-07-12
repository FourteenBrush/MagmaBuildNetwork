package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandMagmabuildnetwork extends BaseCommand {

    @Override
    protected boolean execute(@NotNull String[] args) {

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Utils.message(sender, new String[] {"§f---- §9MagmaBuildNetwork §f---\n",
                    "§9/mbn §7help <command> - §fDisplay info about a command.",
                    "§9/mbn §7reload - §fReloads the plugin"});
            return true;
        }
        else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            Utils.message(sender, "§aSuccessfully reloaded " + plugin.getName());
            return true;
        }
        return true;
    }

    @Override
    protected @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (arguments.isEmpty()) {
            arguments.add("help");
            arguments.add("reload");
        }
        return StringUtil.copyPartialMatches(args[0], arguments, new ArrayList<>());
    }
}
