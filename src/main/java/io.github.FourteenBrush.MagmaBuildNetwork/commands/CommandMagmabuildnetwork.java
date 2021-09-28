package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.MessagesUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandMagmabuildnetwork extends AbstractCommand {

    private static final Set<UUID> bypassingPlayers = new HashSet<>();

    public CommandMagmabuildnetwork() {
        super("magmabuildnetwork", true);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Utils.message(sender, "&f---- &9MagmaBuildNetwork &f---",
                    "&9/mbn &7help <command> - &fDisplay info about a command.",
                    "&9/mbn &7reload - &fReloads the plugin");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            long start = System.currentTimeMillis();
            ConfigManager.createFiles();
            plugin.reloadConfig();
            CommandManager.onEnable();
            Utils.message(sender, "&aSuccessfully reloaded " + plugin.getName() + "! (" + (start - System.currentTimeMillis()) + ")");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("bypass")) {
            if (isConsole) return MessagesUtils.noConsole(sender);
            if (bypassingPlayers.remove(executor.getUniqueId())) Utils.message(executor, "&6Not longer bypassing cooldowns!");
            else {
                bypassingPlayers.add(executor.getUniqueId());
                Utils.message(executor, "&aNow bypassing cooldowns!");
            }
        }
        return true;
    }

    public static Set<UUID> getBypassingPlayers() {
        return bypassingPlayers;
    }

    @Override
    protected List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("help", "reload", "bypass"), new ArrayList<>());
        }
        return null;
    }
}
