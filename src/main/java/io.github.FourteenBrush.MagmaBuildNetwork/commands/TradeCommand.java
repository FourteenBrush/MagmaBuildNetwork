package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.GUI;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.TradeGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class TradeCommand implements CommandExecutor {

    private final Main plugin = Main.getInstance();
    private boolean tradeAccepted = false;
    private HashMap<Player, Player> traders = new HashMap<>();
    List<ItemStack> senderInventory;
    List<ItemStack> targetInventory;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!Utils.verifyIfIsAPlayer(sender)) {
            Utils.message(sender, "§cThis command must be executed by a player!");
            return true;
        }
        final Player p = (Player) sender;
        if (!Utils.hasPermission(p, "basic")) {
            Utils.messageNoPermission(p);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("trade")) {
            if (!Utils.hasPermission(p, "basic")) {
                Utils.messageNoPermission(p);
                return true;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("request")) {
                p.openInventory(new TradeGui().createInv());
                Utils.message(p, "This is for debug purposes only!");
                // request(p, args);
                return true;
            }
            else if (args.length == 2 && args[0].equalsIgnoreCase("accept")) {
                accept(p, args);
                return true;
            }
            else if (args.length == 2 && args[0].equalsIgnoreCase("decline")) {
                decline(p, args);
                return true;
            }
        }
        return true;
    }

    private void request(Player sender, String[] args) {
        List<Player> matches = plugin.getServer().matchPlayer(args[0]);
        if (matches.isEmpty()) {
            Utils.message(sender, "§c" + args[0] + " §cis not currently online!");
            return;
        }
        Player playerTarget = matches.get(0);
        if (sender == playerTarget) {
            Utils.message(sender, "§cYou cannot trade with yourself!");
            return;
        }
        sendRequest(sender, playerTarget);
    }

    private void sendRequest(Player sender, Player playerTarget) {
        traders.put(sender, playerTarget);
    }

    private boolean distanceCheck(Player playerSender, Player playerTarget) {
        int maxDistance = plugin.getConfig().getInt("max_trade_distance");
        if (!plugin.getConfig().getBoolean("trade_from_different_world")) {
            // If you need to trade in the same world
            if (!playerSender.getWorld().getName().equalsIgnoreCase(playerTarget.getWorld().getName())) {
                Utils.message(playerSender, "§cBoth players needs to be in the same world!");
                return false;
            }
            double realDistance = playerSender.getLocation().distance(playerTarget.getLocation());
            if (realDistance > maxDistance) {
                Utils.message(playerSender, new String[] {"§cYou are too far away from the player you want to trade with!",
                "You need to be within " +  "§c" + Integer.toString(maxDistance) + " §cfrom each other!"});
                return false;
            }
        }
        if (!playerSender.getWorld().getName().equalsIgnoreCase(playerTarget.getWorld().getName())) {
            if (maxDistance != 0) {
                Utils.message(playerSender, "§cYou and " + playerTarget.getName() + " §care inside different world and " +
                        "§cthe maximum distance between each other is not equal to 0!");
                return false;
            }
        }
        return true;
    }

    private void accept(Player sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (!Bukkit.getOnlinePlayers().contains(target)) {
            Utils.message(sender, "§c" + target + " §cis not currently online!");
            return;
        }
        tradeAccepted = true;
        startTrade(sender, target);
    }

    private void decline(Player player, String[] args) {

        return;
    }

    private void startTrade(Player sender, Player target) {
        GUI gui = new GUI();
        sender.openInventory(new TradeGui().createInv());
        target.openInventory(new TradeGui().createInv());
    }

    private void acceptTrade(Player playerSender, Player playerTarget) {
        tradeAccepted = true;
        startTrade(playerSender, playerTarget);
    }

    private HashMap<Player, Player> getTraders() {
        return traders;
    }
}
