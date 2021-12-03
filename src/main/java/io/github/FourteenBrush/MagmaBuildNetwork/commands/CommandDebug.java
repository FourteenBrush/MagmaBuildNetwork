package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.TradeGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandDebug extends CommandHandler {

    public CommandDebug() {
        super("debug", Permission.ADMIN, true);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "tradegui":
                    new TradeGui().open(executor);
                    break;
                case "displayname":
                    PlayerUtils.message(executor, "Your displayName is " + executor.getDisplayName());
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("tradegui", "displayname"), new ArrayList<>());
        }
        return super.tabComplete(args);
    }
}
