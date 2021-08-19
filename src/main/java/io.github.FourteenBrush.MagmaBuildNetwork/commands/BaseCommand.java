package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    protected final Main plugin = Main.getInstance();
    protected boolean isConsole = false;
    protected CommandSender sender = null;
    protected Player p = null;
    protected final List<String> arguments = new ArrayList<>(); // tabComplete
    protected final Map<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!validate(sender, cmd)) return true;

        try {
            return execute(args);
        } catch (Exception e) {
            Utils.logError("An error occurred whilst executing a command:");
            e.printStackTrace();
        }
        return true;
    }

    public boolean validate(CommandSender sender, Command cmd) {

        if (sender instanceof BlockCommandSender) {
            return false;
        }
        this.sender = sender;

        if (sender instanceof Player) {
            p = (Player) sender;
            if (!Utils.isAuthorized(p, cmd.getPermission())) {
                Utils.messageNoPermission(p);
                return false;
            }
        } else {
            isConsole = true;
        }
        return true;
    }

    public boolean checkCooldown(int secondsIfNotDefault) { // todo
        long time = secondsIfNotDefault == -1 ? 5 : secondsIfNotDefault;
        if (cooldowns.containsKey(p.getUniqueId())) {
            if (cooldowns.get(p.getUniqueId()) > System.currentTimeMillis()) {
                Utils.message(p, "§cPlease wait " + (cooldowns.get(p.getUniqueId()) - System.currentTimeMillis()) / 1000 + " more seconds!");
                return true;
            }
        }
        cooldowns.put(p.getUniqueId(), System.currentTimeMillis() / 1000 + time);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!validate(sender, cmd) || isConsole) return new ArrayList<>();

        return tabComplete(sender, cmd, label, args);
    }

    protected @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }

    protected abstract boolean execute(@NotNull String[] args);

    protected boolean messageNoConsole() {
        Utils.message(p, Utils.colorize("&cThe console cannot execute this!"));
        return true;
    }
}
