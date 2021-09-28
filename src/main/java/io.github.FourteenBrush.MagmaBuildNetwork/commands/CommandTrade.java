package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.gui.TradeGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.MessagesUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandTrade extends AbstractCommand {

    private static final List<Integer> placeableSlots = new ArrayList<>(Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30));
    private static final Set<Player> traders = new HashSet<>();
    private static final List<ItemStack> senderTradeItems = new ArrayList<>(), targetTradeItems = new ArrayList<>();
    private Player target;
    private final TradeGui senderGui, targetGui;
    private static boolean cancelled = false;

    public CommandTrade() {
        super("trade", true);
        senderGui = new TradeGui();
        targetGui = new TradeGui();
    }

    @Override
    public boolean execute(@NotNull String[] args) {

        if (isConsole) return MessagesUtils.noConsole(sender);

        if (args.length > 1 && !Utils.isPlayerOnline(executor, args[1], true)) return true;
        target = Bukkit.getPlayer(args[1]);

        if (args.length == 2 && args[0].equalsIgnoreCase("request")) {
            request();
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("accept")) {
            accept(executor);
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("decline")) {
            decline(executor);
            return true;
        } else if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Utils.message(executor, "&f--- &9Trade command &f---",
                    "&9/trade &7request - &fsends a trade request to a player",
                    "&9/trade &7accept - &faccepts the trade request from a player",
                    "&9/trade &7decline - &fdeclines the trade request from a player",
                    "&9/trade &7help - &fshows this message");
        }
        return true;
    }

    private void request() {
        if (executor.getName().equalsIgnoreCase(target.getName())) {
            Utils.message(executor, "&cYou cannot trade with yourself!");
        } else if (distanceCheckSuccess()) {
            traders.add(executor);
            traders.add(target);
            Utils.messageSpigot(target, Utils.suggestCommandByClickableText("&e" + executor.getName() + " &asent you a trade request!" +
                    "Type &6/trade accept|decline <player> &ato accept or decline!", "/trade"));
            Utils.message(executor, "&aSent a trade request to " + target.getName());
        }
    }

    private void accept(Player sender) {
        if (!traders.contains(sender)) {
            Utils.message(executor, "&cYou don't have a trade request from that player!");
        }
        for (int i : placeableSlots) {
            senderTradeItems.add(getSenderGui().getInv().getItem(i));
            targetTradeItems.add(getTargetGui().getInv().getItem(i));
        }
        startTrade(sender, target);
    }

    private void decline(Player player) {
        traders.remove(player);
        traders.remove(executor);
        Utils.message(target, "&c" + player.getName() + " &cdeclined your trade request!");
        Utils.message(executor, "&cDeclined &athe trade request of " + player.getName());
    }

    private boolean distanceCheckSuccess() {
        int maxDistance = plugin.getConfig().getInt("max_trade_distance");
        if (!plugin.getConfig().getBoolean("trade_from_different_world")) {
            // If you need to trade in the same world
            if (!executor.getWorld().getName().equalsIgnoreCase(target.getWorld().getName())) {
                Utils.message(executor, "&cBoth players needs to be in the same world!");
                return false;
            }
            double realDistance = executor.getLocation().distance(target.getLocation());
            if (realDistance > maxDistance) {
                Utils.message(executor, "&cYou are too far away from the player you want to trade with!",
                        "You need to be within " +  "&c" + maxDistance + " &cfrom each other!");
                return false;
            }
        } else if (maxDistance != 0) {
            Utils.message(executor, "&cYou and " + target.getName() + " &care in " +
                    "&ca different world and the maximum distance from each other is not equal to 0!",
                    "&cJust go to the same world!");
            return false;
        }
        return true;
    }

    private void startTrade(Player sender, Player target) {
        senderGui.open(sender);
        targetGui.open(target);
        // button clicks are handled by the tradegui itself
    }

    private void startReadyCounter() {
        if (cancelled) return;
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : traders) {
                p.playSound(p.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, 2, 2);
            }
        }, 1, 100);
    }

    public void completeTrade(Player sender, Player target) {
        startReadyCounter();
        // wait 5 seconds after the method call
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (cancelled) return;
            PlayerInventory senderInv = sender.getInventory();
            PlayerInventory targetInv = target.getInventory();
            for (int i : placeableSlots) {
                // transfer from sender to target
                Utils.giveOrDropFor(sender, targetTradeItems.get(i));
                targetInv.removeItem(targetTradeItems.get(i));
                // transfer from target to sender
                Utils.giveOrDropFor(target, senderInv.getItem(i));
                senderInv.removeItem(senderTradeItems.get(i));
            }
        }, 100);
    }

    public TradeGui getSenderGui() {
        return senderGui;
    }

    public TradeGui getTargetGui() {
        return targetGui;
    }

    public static List<Integer> getPlaceableSlots() {
        return placeableSlots;
    }

    public static void cancel() {
        cancelled = true;
    }

    public static Set<Player> getTraders() {
        return traders;
    }

    @Override
    protected List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("help", "accept", "request", "decline"), new ArrayList<>());
        }
        return null;
    }
}
