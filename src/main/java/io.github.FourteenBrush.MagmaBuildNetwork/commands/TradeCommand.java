package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.TradeGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TradeCommand implements CommandExecutor {

    private final Main plugin = Main.getInstance();
    private final List<Integer> placeableSlots = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30);
    private boolean cancelled = false;
    private final HashMap<Player, Player> traders = new HashMap<>();
    private ArrayList<ItemStack> senderTradeItems;
    private ArrayList<ItemStack> targetTradeItems;
    private final static TradeGui senderGui = new TradeGui();
    private final static TradeGui targetGui = new TradeGui();

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
                request(p, args);
                return true;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("accept")) {
                accept(p, args);
                return true;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("decline")) {
                decline(p, args);
                return true;
            } else if (args.length == 0 || args[0].equalsIgnoreCase("info")) {
                Utils.message(p, new String[] {"§f--- §9Trade command §f---\n",
                        "§9/trade §7request - §fsends a trade request to a player",
                        "§9/trade §7accept - §faccepts the trade request from a player",
                        "§9/trade §7decline - §fdeclines the trade request from a player",
                        "§9/trade §7info - §fshows this message"});
            }
        }
        return true;
    }

    private void request(Player sender, String[] args) {
        if (args.length < 2) {
            Utils.message(sender, "§cPlease specify a player to trade with!");
            return;
        }
        if (!Utils.isPlayerOnline(sender, args[1])) {
            return;
        }
        if (sender.getName().equalsIgnoreCase(args[1])) {
            Utils.message(sender, "§cYou cannot trade with yourself!");
            return;
        }
        if (distanceCheck(sender, Bukkit.getPlayer(args[1]))) {
            sendRequest(sender, Bukkit.getPlayer(args[1]));
        }
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
                        "You need to be within " +  "§c" + maxDistance + " §cfrom each other!"});
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

    private void sendRequest(Player sender, Player target) {
        traders.put(sender, target);
        Utils.message(target, new String[] {"§e" + sender.getName() + " §asent you a trade request!",
        "Type §6/trade accept/decline <playername> " + "§ato accept or decline!"});
    }

    private void accept(Player sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (!Bukkit.getOnlinePlayers().contains(target)) {
            Utils.message(sender, "§c" + args[1] + " §cis not currently online!");
            return;
        }
        for (int i : placeableSlots) {
            senderTradeItems.add(sender.getInventory().getItem(i));
            targetTradeItems.add(target.getInventory().getItem(i));
        }
        startTrade(sender, target);
    }

    private void decline(Player player, String[] args) {
         Player target = Bukkit.getPlayer(args[1]);
         Utils.message(target, "§c" + player.getName() + " §cdeclined your trade request!");
    }

    private void startTrade(Player sender, Player target) {
        sender.openInventory(senderGui.createInv());
        target.openInventory(targetGui.createInv());

        // the rest is done by the PlayerListener (i guess)
    }

    private void startReadyCounter() {
        if (cancelled) {return;}
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            int time = 5;
                if (time == 0) {
                    return;
                }
                for (Player p : getTraders().values()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, 2, 2);
                    time--;
            }
        }, 20L, 5L);
    }

    public void completeTrade(Player sender, Player target, TradeGui gui) {
        startReadyCounter();
        PlayerInventory senderInv = sender.getInventory();
        PlayerInventory targetInv = target.getInventory();
        // transfer from sender to target
        for (int i : placeableSlots) {
            senderInv.addItem(targetTradeItems.get(i));
            targetInv.removeItem(targetTradeItems.get(i));
        }
        // transfer from target to sender
        for (int i : placeableSlots) {
            targetInv.addItem(senderTradeItems.get(i));
            senderInv.removeItem(senderTradeItems.get(i));
        }
    }

    private HashMap<Player, Player> getTraders() {
        return traders;
    }

    public static TradeGui getSenderGui() {
        return senderGui;
    }

    public static TradeGui getTargetGui() {
        return targetGui;
    }

    public static void setItemInGui(TradeGui gui, int slot, ItemStack item) {
        gui.getInventory().setItem(slot, item);
    }


    public static void changeClickedSlots(int slot, InventoryClickEvent event) {
        switch (slot) {
            case 37:
            ItemStack item = event.getInventory().getItem(37);
            item.setType(Material.LIME_WOOL);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§2Ready!");
            meta.setLore(null);
            item.setItemMeta(meta);
            break;
            // TODO
        }
    }
}
