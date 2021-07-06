package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GlobalCommand implements CommandExecutor {

    private final Main plugin = Main.getInstance();
    private Player p = null;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player) {
            p = (Player) sender;
        }

        if (cmd.getName().equalsIgnoreCase("MagmaBuildNetwork")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                Utils.message(sender, new String[] {"§f---- §9MagmaBuildNetwork §f---\n",
                        "§9/mbn §7help <command> - §fDisplay info about a command.",
                        "§9/mbn §7reload - §fReloads the plugin"});
                return true;
            }
            else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!Utils.hasPermission(sender, "reload")) {
                    Utils.messageNoPermission(sender);
                    return true;
                }
                plugin.reloadConfig();
                Utils.message(sender, "§aSuccessfully reloaded " + plugin.getName());
                return true;
            }
        }

        else if (cmd.getName().equalsIgnoreCase("ban")) {
            if (!Utils.hasPermission(p, "admin")) {
                Utils.messageNoPermission(p);
                return true;
            }
            if (args.length < 1) {
                Utils.message(p, "§cPlease specify a player!");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (!Utils.isPlayerOnline(sender, args[0])) {
                return true;
            }
            // TODO
            String banReason = Utils.getFinalArg(args, 1);
            boolean noMatch = false;
            if (!Utils.checkNotEnoughArgs(sender, args.length, 1)) {
                return true;
            }
            plugin.getServer().getBanList(BanList.Type.NAME).addBan(target.getName(), banReason, null, p.getName());
            Utils.logWarning("Player " + target.getName() + " got banned by " + p.getName());
        }
        return true;
    }
}
