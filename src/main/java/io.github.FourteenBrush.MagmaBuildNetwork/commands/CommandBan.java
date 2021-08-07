package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandBan extends BaseCommand {

    private static final Main plugin = Main.getInstance();

    @Override
    protected boolean execute(@NotNull String[] args) {

        if (args.length < 1) {
            Utils.message(p, "§cPlease specify a player!");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (!Utils.isPlayerOnline(sender, target)) {
            return true;
        }
        String banreason = "Banned by a moderator";
        if (args.length > 1) {
            banreason = Utils.getFinalArg(args, 1);
        }
        plugin.getServer().getBanList(BanList.Type.NAME).addBan(target.getName(), banreason, null, p.getName());
        Utils.message(sender, String.format("§6Banned %s | %s", target.getName(), banreason));
        Utils.logWarning("Player " + target.getName() + " got banned by " + p.getName());
        return true;
    }
}
