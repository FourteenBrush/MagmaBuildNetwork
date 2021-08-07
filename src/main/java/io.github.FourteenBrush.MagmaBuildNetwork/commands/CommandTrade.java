package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.TradeGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandTrade extends BaseCommand {

    private static final Main plugin = Main.getInstance();
    private static final List<Integer> placeableSlots = new ArrayList<>(Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30));
    private static boolean cancelled = false;
    private static final List<Player> traders = new ArrayList<>();
    private static final ArrayList<ItemStack> senderTradeItems = new ArrayList<>(), targetTradeItems = new ArrayList<>();
    private static Player target = null;
    private final TradeGui senderGui = new TradeGui(), targetGui = new TradeGui();

    @Override
    protected boolean execute(@NotNull String[] args) {

        if (isConsole) return true;

        if (args.length > 1 && !Utils.isPlayerOnline(p, args[1])) return true;
        target = Bukkit.getPlayer(args[1]);

        if (args.length == 2 && args[0].equalsIgnoreCase("request")) {
            request();
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("accept")) {
            accept(p);
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("decline")) {
            decline(p);
            return true;
        } else if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Utils.message(p, new String[] {"§f--- §9Trade command §f---",
                    "§9/trade §7request - §fsends a trade request to a player",
                    "§9/trade §7accept - §faccepts the trade request from a player",
                    "§9/trade §7decline - §fdeclines the trade request from a player",
                    "§9/trade §7help - §fshows this message"});
        }
        return true;
    }

    private void request() {
        if (p.getName().equalsIgnoreCase(target.getName())) {
            Utils.message(p, "§cYou cannot trade with yourself!");
            return;
        }
        if (distanceCheckSuccess()) {
            traders.add(p);
            traders.add(target);
            Utils.message(target, new String[] {"§e" + p.getName() + " §asent you a trade request!",
                    "Type §6/trade accept|decline <player> §ato accept or decline!"});
            Utils.message(p, "§aSent a trade request to " + target.getName());
        }
    }

    private void accept(Player sender) {
        for (int i : placeableSlots) {
            senderTradeItems.add(getSenderGui().getInv().getItem(i));
            targetTradeItems.add(getTargetGui().getInv().getItem(i));
        }
        startTrade(sender, target);
    }

    private void decline(Player player) {
        traders.remove(player);
        Utils.message(target, "§c" + player.getName() + " §cdeclined your trade request!");
    }

    private boolean distanceCheckSuccess() {
        int maxDistance = plugin.getConfig().getInt("max_trade_distance");
        if (!plugin.getConfig().getBoolean("trade_from_different_world")) {
            // If you need to trade in the same world
            if (!p.getWorld().getName().equalsIgnoreCase(target.getWorld().getName())) {
                Utils.message(p, "§cBoth players needs to be in the same world!");
                return false;
            }
            double realDistance = p.getLocation().distance(target.getLocation());
            if (realDistance > maxDistance) {
                Utils.message(p, new String[] {"§cYou are too far away from the player you want to trade with!",
                        "You need to be within " +  "§c" + maxDistance + " §cfrom each other!"});
                return false;
            }
        } else if (maxDistance != 0) {
            Utils.message(p, new String[]{"§cYou and " + target.getName() + " §care in " +
                    "§ca different world and the maximum distance from each other is not equal to 0!",
                    "§cJust go to the same world!"});
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
        }, 0, 100);
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
                giveOrDropFor(sender, targetTradeItems.get(i));
                targetInv.removeItem(targetTradeItems.get(i));
                // transfer from target to sender
                giveOrDropFor(target, senderInv.getItem(i));
                senderInv.removeItem(senderTradeItems.get(i));
            }
        }, 100);
    }

    public void giveOrDropFor(final Player target, final ItemStack... items) {
        final World world = target.getWorld();
        final Location playerLoc = target.getLocation();
        target.getInventory().addItem(items).values().forEach(overFlownItem -> world.dropItemNaturally(playerLoc, overFlownItem));
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

    public static List<Player> getTraders() {
        return traders;
    }

    @Override
    protected @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            arguments.add("help");
            arguments.add("accept");
            arguments.add("request");
            arguments.add("decline");
            return StringUtil.copyPartialMatches(args[0], arguments, new ArrayList<>());
        }
        return null;
    }
}
