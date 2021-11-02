package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.gui.TradeGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandTrade extends AbstractCommand {

    private static final String[] HELP_MESSAGE = Utils.colorize(
            "&f--- &9Trade command &f---",
            "&9/trade &7request - &fsends a trade request to a player",
            "&9/trade &7accept - &faccepts the trade request from a player",
            "&9/trade &7decline - &fdeclines the trade request from a player",
            "&9/trade &7help - &fshows this message");
    private final Map<Player, Player> traders; // stored with executor as key
    private final List<Integer> placeableSlots;
    private TradeGui executorGui, targetGui;
    private Player target;
    private boolean cancelled;

    public CommandTrade() {
        super("trade", Permission.BASIC, true);
        traders = new HashMap<>();
        placeableSlots = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length > 1 && !args[1].isEmpty()) {
            target = Bukkit.getPlayerExact(args[1]);
            if (!PlayerUtils.checkPlayerOnline(executor, target, true)) return true;
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "request":
                    return request();
                case "cancel":
                    return cancelRequest();
                case "accept":
                    return accept();
                case "decline":
                    return decline();
            }
        } else if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            PlayerUtils.message(executor, HELP_MESSAGE);
        }
        return true;
    }

    private boolean request() {
        if (executor == target) {
            PlayerUtils.message(executor, Lang.TRADE_WITH_YOURSELF_DISALLOWED.get());
        } else if (distanceCheck()) {
            traders.put(executor, target);
            PlayerUtils.message(executor, Lang.TRADE_REQUEST_SENT.get(target.getName()));
            PlayerUtils.message(target, new ComponentBuilder()
                    .append(Utils.colorize("&6" + executor.getName() + "&e sent you a trade request!\n"))
                    .append(Utils.colorize("&2&l[accept] "))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to accept trade request!")))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade accept " + executor.getName()))
                    .append(Utils.colorize("&c&l[decline]"))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to decline trade request!")))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade decline " + executor.getName()))
                    .create());
        }
        return true;
    }

    private boolean cancelRequest() {
        if (executor == target) {
            PlayerUtils.message(executor, Lang.TRADE_WITH_YOURSELF_DISALLOWED.get());
        } else if (traders.remove(executor, target)) {
            PlayerUtils.message(executor, Lang.TRADE_REQUEST_CANCELLED.get(target.getName()));
            PlayerUtils.message(target, Lang.TRADE_REQUEST_CANCELLED_BY.get(executor.getName()));
        }
        return true;
    }

    private boolean distanceCheck() {
        if (!plugin.getConfig().getBoolean("trade.allow-creative")
                && (executor.getGameMode() == GameMode.CREATIVE || target.getGameMode() == GameMode.CREATIVE)) {
            PlayerUtils.message(executor, Lang.TRADE_NO_CREATIVE.get());
            return false;
        }
        if (!plugin.getConfig().getBoolean("trade.allow-from-different-worlds")
                && !executor.getWorld().getName().equals(target.getWorld().getName())) {
            PlayerUtils.message(executor, Lang.TRADE_NO_DIFFERENT_WORLDS.get());
            return false;
        }
        int maxDistance = plugin.getConfig().getInt("trade.max-distance");
        if (maxDistance != -1) {
            double realDistance = executor.getLocation().distance(target.getLocation());
            if (realDistance > maxDistance) {
                PlayerUtils.message(executor, Lang.TRADE_DISTANCE_TOO_BIG.get(target.getName(), String.valueOf(maxDistance)));
                return false;
            }
        }
        return true;
    }

    private boolean accept() {
        if (executor == target) {
            PlayerUtils.message(executor, Lang.TRADE_WITH_YOURSELF_DISALLOWED.get());
        } else if (!(traders.get(executor) == target)) {
            PlayerUtils.message(executor, Lang.TRADE_NO_REQUEST.get());
        } else {
            PlayerUtils.message(executor, Lang.TRADE_ACCEPTED.get(target.getName()));
            PlayerUtils.message(target, Lang.TRADE_ACCEPTED_BY.get(executor.getName()));
            executorGui = new TradeGui();
            targetGui = new TradeGui();
            executorGui.open(executor);
            targetGui.open(target);
        }
        return true;
    }

    private boolean decline() {
        if (executor == target) {
            PlayerUtils.message(executor, Lang.TRADE_WITH_YOURSELF_DISALLOWED.get());
        } else if (traders.remove(executor, target)) {
            PlayerUtils.message(executor, Lang.TRADE_DECLINED.get(target.getName()));
            PlayerUtils.message(target, Lang.TRADE_DECLINED_BY.get(executor.getName()));
        } else {
            PlayerUtils.message(executor, Lang.TRADE_NO_REQUEST.get());
        }
        return true;
    }

    private boolean startReadCounter() {
        new BukkitRunnable() {
            int timeInSeconds = 5;
            @Override
            public void run() {
                traders.forEach((executor, target) -> {
                    executor.playSound(executor.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, 2, 2);
                    target.playSound(target.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, 2, 2);
                });
                timeInSeconds--;
                if (cancelled || timeInSeconds == 0) this.cancel();
            }
        }.runTaskTimer(plugin, 1, 20);
        return true;
    }

    public void cancelTrade(String playerName) {
        cancelled = true;
        executor.closeInventory();
        target.closeInventory();
        traders.remove(executor, target);
        PlayerUtils.message(executor, "&cTrade cancelled by " + playerName);
    }

    public Map<Player, Player> getTraders() {
        return traders;
    }

    public List<Integer> getPlaceableSlots() {
        return placeableSlots;
    }

    @Override
    protected List<String> tabComplete(@NotNull String... args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("request", "cancel", "accept", "decline", "help"), new ArrayList<>());
        }
        return super.tabComplete();
    }
}
