package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandManager;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandMagmabuildnetwork extends AbstractCommand implements ConsoleCommand {

    private static final String[] HELP_MESSAGE = Utils.colorize(
            "&f---- &9MagmaBuildNetwork &f---",
            "&9/mbn &7help <command> - &fDisplay info about a command.",
            "&9/mbn &7reload - &fReloads the plugin");
    private static final Set<UUID> bypassingPlayers = new HashSet<>();

    public CommandMagmabuildnetwork() {
        super("magmabuildnetwork", Permission.ADMIN, true);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            PlayerUtils.message(sender, HELP_MESSAGE);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                long start = System.currentTimeMillis();
                CommandManager commandManager = plugin.getCommandManager();
                ConfigManager configManager = plugin.getConfigManager();
                commandManager.shutdown();
                plugin.reloadConfig();
                configManager.startup();
                CommandSpawn.setup();
                commandManager.startup();
                PlayerUtils.message(sender, "&aSuccessfully reloaded " + plugin.getName() + "! (&6" + (System.currentTimeMillis() - start) + " &ams)");
            } else if (args[0].equalsIgnoreCase("bypass")) {
                if (isConsoleSender) {
                    PlayerUtils.message(sender, Lang.NO_CONSOLE.get());
                } else if (bypassingPlayers.remove(executor.getUniqueId())) {
                    PlayerUtils.message(executor, "&6Not longer bypassing cooldowns!");
                } else {
                    bypassingPlayers.add(executor.getUniqueId());
                    PlayerUtils.message(executor, "&aNow bypassing cooldowns!");
                }
            }
        }
        return true;
    }

    public static boolean isBypassing(UUID uuid) {
        return bypassingPlayers.contains(uuid);
    }

    @Override
    protected List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("help", "reload", "bypass"), new ArrayList<>());
        }
        return super.tabComplete();
    }
}
