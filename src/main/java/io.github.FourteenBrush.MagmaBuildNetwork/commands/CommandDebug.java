package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.inventory.TradeGui;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.PlayerListener;
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
        } else if (args.length == 1 && args[0].equalsIgnoreCase("")) {
            PlayerListener.giveRespawnItems(p);
        }
        return true;
    }

    @Override
    protected @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

            if (args.length == 1) {
                arguments.add("tradegui");
                arguments.add("spawnitems");
                return StringUtil.copyPartialMatches(args[0], arguments, new ArrayList<>());
            }
            return null;
    }
}
