package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandBan extends AbstractCommand {

    private static final Main plugin = Main.getPlugin(Main.class);

    public CommandBan() {
        super("ban", false);
    }

    @Override
    public boolean execute(@NotNull String[] args) {

        if (args.length < 1) {
            Utils.message(executor, "&cPlease specify a player!");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (!Utils.isPlayerOnline(sender, target)) return true;
        String banReason = args.length > 1 ? Utils.getFinalArgs(args, 1) : "Banned by a moderator";
        plugin.getServer().getBanList(BanList.Type.NAME).addBan(target.getName(), banReason, null, executor.getName());
        target.kickPlayer(banReason);
        Utils.message(sender, String.format("&6Banned %s | %s", target.getName(), banReason));
        Utils.logWarning("Player " + target.getName() + " got banned by " + sender.getName());
        return true;
    }
}
