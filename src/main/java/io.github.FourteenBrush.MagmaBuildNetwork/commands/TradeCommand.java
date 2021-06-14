package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.listeners.TradeListener;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TradeCommand implements CommandExecutor {

    private final HashMap<Player, Player> requestTrade = new HashMap<Player, Player>();

    TradeListener tradeList;

    public TradeCommand(TradeListener listener) {
        tradeList = listener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (Utils.verifyIfIsAPlayer(sender)) {
            return true;
        }
        final Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("trade")) {

            if (args.length == 2 && args[0].equalsIgnoreCase("request")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (Bukkit.getOnlinePlayers().contains(target)) {
                    p.sendMessage("You sent a trade request to " + args[1]);
                    requestTrade.put(target, p);
                    target.sendMessage(p.getName() + " wants to trade with you!");
                } else {
                    p.sendMessage(args[1] + " is not currently online.");
                    return true;
                }
            }
            else if (args.length == 2 && args[0].equalsIgnoreCase("accept")) {
                if (!(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1])))) {
                    p.sendMessage("There is no incoming trade with " + args[1]);
                }
                if (requestTrade.containsKey(p))  { //accepted trade request
                    Player tradeWith = requestTrade.get(p);
                    if (Bukkit.getOnlinePlayers().contains(tradeWith)) {
                        Inventory tradeInv = Bukkit.createInventory(null, 54, "Trade");

                        ItemStack glass = new ItemStack(Material.GLASS);
                        ItemStack buttom = new ItemStack(Material.REDSTONE_BLOCK);
                        tradeInv.setItem(9, glass);
                        tradeInv.setItem(10, glass);
                        tradeInv.setItem(11, glass);
                        tradeInv.setItem(12, glass);
                        tradeInv.setItem(13, glass);
                        tradeInv.setItem(14, glass);
                        tradeInv.setItem(15, glass);
                        tradeInv.setItem(16, glass);
                        tradeInv.setItem(17, glass);

                        p.openInventory(tradeInv);
                        tradeWith.openInventory(tradeInv);
                        requestTrade.remove(p);
                        tradeList.addPlayersToTradeList(p, tradeWith);
                    } else {
                        p.sendMessage("Player is not online anymore");
                        requestTrade.remove(p);
                    }
                } else {
                    //do nothing?
                }
            }
        }
        return false;
    }
}
