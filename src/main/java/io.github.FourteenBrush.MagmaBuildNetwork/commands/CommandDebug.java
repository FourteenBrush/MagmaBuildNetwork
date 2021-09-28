package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.gui.TradeGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.MessagesUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandDebug extends AbstractCommand {

    public CommandDebug() {
        super("debug", true);
    }

    @Override
    public boolean execute(@NotNull String[] args) {

        if (isConsole) return MessagesUtils.noConsole(sender);

        if (args.length == 1 && args[0].equalsIgnoreCase("tradegui")) {
            new TradeGui().open(executor);
        } else if (args.length == 1 && args[0].equalsIgnoreCase("displayname")) {
            Utils.message(executor, "Your displayName is " + executor.getDisplayName()); // todo remove
        }
        return true;
    }

    @Override
    protected List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("tradegui", "displayname"), new ArrayList<>());
        }
        return null;
    }
}
