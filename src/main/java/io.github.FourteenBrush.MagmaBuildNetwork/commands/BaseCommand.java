package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    protected Main plugin = Main.getInstance();
    protected CommandSender sender = null;
    protected boolean isAuthorized = false;
    protected boolean isConsole = false;
    protected Player p = null;
    protected List<String> arguments = new ArrayList<>(); // tabComplete

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!validate(sender, cmd)) return true;

        try {
            return execute(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean validate(CommandSender sender, Command cmd) {

        // VALIDATE THE PLAYER
        if (sender instanceof BlockCommandSender) {
            return false;
        }
        this.sender = sender;

        if (sender instanceof Player) {
            p = (Player) sender;

            if (Utils.isAuthorized(p, cmd.getPermission())) {
                isAuthorized = true;
            } else {
                Utils.messageNoPermission(p);
                return false;
            }
        } else {
            isConsole = true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!validate(sender, cmd) || isConsole) return new ArrayList<>();

        return tabComplete(sender, cmd, label, args);
    }

    protected @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        // if there is no tabComplete, return
        return new ArrayList<>();
    }

    protected abstract boolean execute(@NotNull String[] args);
}
