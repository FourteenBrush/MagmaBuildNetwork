package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.inventory.TradeGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommandDebug extends BaseCommand {


    @Override
    protected boolean execute(@NotNull String[] args) {

        if (isConsole) return true;

        if (args.length == 1 && args[0].equalsIgnoreCase("tradegui")) {
            p.openInventory(new TradeGui().createInv());
        }
        return true;
    }

    @Override
    protected @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

            arguments.add("tradegui");
        return StringUtil.copyPartialMatches(args[0], arguments, new ArrayList<>());
    }
}
