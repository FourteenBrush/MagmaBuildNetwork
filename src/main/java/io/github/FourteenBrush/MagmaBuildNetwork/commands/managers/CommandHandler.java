package io.github.FourteenBrush.MagmaBuildNetwork.commands.managers;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.IConsoleCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class CommandHandler implements CommandExecutor, TabCompleter {

    protected static final MBNPlugin plugin = MBNPlugin.getInstance();
    protected CommandSender sender;
    protected Player executor;

    private final Permission permission;

    public CommandHandler(String commandName, Permission permission, boolean hasTabComplete) {
        if (hasTabComplete)
            plugin.getCommand(commandName).setTabCompleter(this);
        this.permission = permission;
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        this.sender = sender;
        if (!isAuthorized(sender, permission)) return true;
        try {
            return execute(args);
        } catch (Exception e) {
            Utils.logError("An error occurred whilst executing a command: ");
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!isAuthorized(sender, permission)) return null;
        return tabComplete(args);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isAuthorized(CommandSender sender, Permission permission) {
        if (sender instanceof BlockCommandSender) return false;
        if (sender instanceof Player) {
            executor = (Player) sender;
            if (!permission.has(executor, true)) return false;
        }
        if (!(this instanceof IConsoleCommand || sender instanceof Player)) {
            sender.sendMessage(Lang.NO_CONSOLE.get());
            return false;
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

    public List<String> tabComplete(@NotNull String[] args) {
        return null;
    }

    public abstract boolean execute(@NotNull String[] args);
}
