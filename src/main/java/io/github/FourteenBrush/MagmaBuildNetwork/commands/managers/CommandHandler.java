package io.github.FourteenBrush.MagmaBuildNetwork.commands.managers;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.IConsoleCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Logger;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class CommandHandler implements CommandExecutor, TabCompleter {

    protected CommandSender sender;
    protected Player executor;
    protected final MBNPlugin plugin;
    private final Permission permission;

    public CommandHandler(String commandName, Permission permission, boolean hasTabComplete) {
        this.permission = permission;
        plugin = MBNPlugin.getInstance();
        if (hasTabComplete) {
            Bukkit.getPluginCommand(commandName).setTabCompleter(this);
        }
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!isAuthorized(sender, permission)) return true;
        this.sender = sender;
        try {
            return execute(args);
        } catch (Exception e) {
            Logger.ERROR.log("An error occurred whilst executing a command: ");
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!isAuthorized(sender, permission) || denyConsole()) return null;
        return tabComplete(args);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isAuthorized(CommandSender sender, Permission permission) {
        if (sender instanceof BlockCommandSender) return false;
        if (!(this instanceof IConsoleCommand || sender instanceof Player)) {
            sender.sendMessage(Lang.NO_CONSOLE.get());
            return false;
        }
        if (sender instanceof Player) {
            executor = (Player) sender;
            return permission.has(executor, true);
        }
        return true;
    }

    protected boolean denyConsole() {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.NO_CONSOLE.get());
            return true;
        }
        return false;
    }

    protected List<String> tabComplete(@NotNull String[] args) {
        return null;
    }

    protected abstract boolean execute(@NotNull String[] args);
}
