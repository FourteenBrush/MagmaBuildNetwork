package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    protected static final Main plugin = Main.getPlugin(Main.class);
    protected boolean isConsoleSender;
    protected CommandSender sender;
    protected Player executor;

    private final Permission permission;
    private final boolean hasTabComplete;

    public AbstractCommand(String commandName, Permission permission, boolean hasTabComplete) {
        if (hasTabComplete)
            plugin.getCommand(commandName).setTabCompleter(this);
        this.permission = permission;
        this.hasTabComplete = hasTabComplete;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        this.sender = sender;
        if (!isAuthorized(sender, permission)) return true;
        try {
            return execute(args);
        } catch (Exception e) {
            Utils.logError("An error occurred whilst executing a command:");
            e.printStackTrace();
        }
        return true;
    }

    private boolean isAuthorized(CommandSender sender, Permission permission) {
        if (sender instanceof BlockCommandSender) return false;
        if (sender instanceof Player) {
            executor = (Player) sender;
            if (!permission.has(executor, true)) return false;
            isConsoleSender = false;
        } else isConsoleSender = true;
        if (!(this instanceof ConsoleCommand) && isConsoleSender) {
            PlayerUtils.message(sender, Lang.NO_CONSOLE.get());
            return false;
        }
        return true;
    }

    protected List<String> tabComplete(@NotNull String... args) {
        return Collections.emptyList();
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return (!isAuthorized(sender, permission) || !hasTabComplete || isConsoleSender) ? null : tabComplete(args);
    }

    public abstract boolean execute(@NotNull String[] args) throws Exception;
}
