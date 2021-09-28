package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.CooldownManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.MessagesUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    protected static final Main plugin = Main.getPlugin(Main.class);
    protected boolean isConsole = false;
    protected CommandSender sender;
    protected Player executor;

    private final boolean hasTabComplete;

    public AbstractCommand(String commandName, boolean hasTabComplete) {
        if (hasTabComplete) plugin.getCommand(commandName).setTabCompleter(this);
        this.hasTabComplete = hasTabComplete;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        this.sender = sender;
        if (!isAuthorized(sender, cmd)) return true;
        try {
            return execute(args);
        } catch (Exception e) {
            Utils.logError("An error occurred whilst executing a command:");
            e.printStackTrace();
        }
        return true;
    }

    private boolean isAuthorized(CommandSender sender, Command cmd) {
        if (sender instanceof BlockCommandSender) return false;
        if (cmd.getPermission() != null && !Utils.isAuthorized(sender, cmd.getPermission())) {
            return !MessagesUtils.noPermission(sender);
        }
        if (sender instanceof Player) executor = (Player) sender;
        else isConsole = true;
        return true;
    }

    protected boolean handleCooldown(CooldownManager cm, long cooldown, TimeUnit timeUnit) {
        if (CommandMagmabuildnetwork.getBypassingPlayers().contains(executor.getUniqueId())) return false;
        long timeLeft = cm.getCooldown(executor.getUniqueId());
        if (TimeUnit.MILLISECONDS.convert(timeLeft, timeUnit) < cooldown) {
            Utils.message(executor, "&cPlease wait&e " + Utils.millisToReadable(timeUnit.toMillis(1) - timeLeft) + " &cbefore reusing this command!");
            return true;
        } else cm.setCooldown(executor.getUniqueId(), System.currentTimeMillis());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return (!isAuthorized(sender, cmd) || isConsole || !hasTabComplete) ? null : tabComplete(args);
    }

    protected @Nullable List<String> tabComplete(@NotNull String[] args) {
        return null; // this is never called
    }

    public abstract boolean execute(@NotNull String[] args);
}
