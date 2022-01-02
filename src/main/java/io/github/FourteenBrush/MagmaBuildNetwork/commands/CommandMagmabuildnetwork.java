package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandManager;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandMagmabuildnetwork extends CommandHandler implements IConsoleCommand {

    private final String[] helpMessage = Utils.colorize(
            "&e------------ &7[MagmaBuildNetwork v" + plugin.getDescription().getVersion() + "&7] &e------------",
            "&7Below is a list of all main commands:",
            "  &6/magmabuildnetwork reload &7- &6Reloads the plugin",
            "  &6/magmabuildnetwork help &7- &6Shows this message"
    );
    private static final Set<UUID> bypassingPlayers = new HashSet<>();

    public CommandMagmabuildnetwork() {
        super("magmabuildnetwork", Permission.ADMIN, true);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                long start = System.currentTimeMillis();
                CommandManager commandManager = plugin.getCommandManager();
                ConfigManager configManager = plugin.getConfigManager();
                commandManager.shutdown();
                configManager.startup();
                plugin.reloadConfig();
                CommandSpawn.getInstance().setup();
                commandManager.startup();
                sender.sendMessage(ChatColor.GOLD + "Successfully reloaded MagmaBuildNetwork! (" + (System.currentTimeMillis() - start) + " ms)");
            } else if (args[0].equalsIgnoreCase("bypass")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Lang.NO_CONSOLE.get());
                } else if (bypassingPlayers.remove(executor.getUniqueId())) {
                    executor.sendMessage(Lang.COOLDOWNS_NOT_LONGER_BYPASSING.get());
                } else {
                    bypassingPlayers.add(executor.getUniqueId());
                    executor.sendMessage(Lang.COOLDOWNS_BYPASSING.get());
                }
            }
        } else {
            sender.sendMessage(helpMessage);
        }
        return true;
    }

    public static boolean isBypassing(UUID uuid) {
        return bypassingPlayers.contains(uuid);
    }


    @Override
    public List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("help", "reload", "bypass"), new ArrayList<>());
        }
        return super.tabComplete(args);
    }
}
