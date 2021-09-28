package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.gui.PrefixGui;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.ShopGui;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.StatsGui;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.TrailsGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.MessagesUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerCommand implements CommandExecutor {

    private static final Set<UUID> frozenPlayers = new HashSet<>();
    private static Player executor;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (!Utils.verifyIfIsAPlayer(sender)) {
            return MessagesUtils.noConsole(sender);
        }
        executor = (Player) sender;
        if (cmd.getPermission() != null && !Utils.isAuthorized(executor, cmd.getPermission())) {
            return MessagesUtils.noPermission(executor);
        }

        switch(cmd.getName().toLowerCase()) {
            case "ignite":
                return ignite(args);
            case "heal":
                return heal(args);
            case "freeze":
                return freeze(args);
            case "invsee":
                return invsee(args);
            case "stats":
                return stats();
            case "trails":
                return trails();
            case "shop":
                return shop();
            case "prefix":
                return prefix();
        }
        return true;
    }

    private boolean ignite(String[] args) {
        Player target = args.length == 1 ? Bukkit.getPlayer(args[0]) : executor;
        if (!Utils.isPlayerOnline(executor, target)) return true;
        target.setFireTicks(500);
        Utils.message(executor, "&aIgnited " + target.getName());
        return true;
    }

    private boolean heal(String[] args) {
        Player target = args.length == 1 ? Bukkit.getPlayer(args[0]) : executor;
        if (!Utils.isPlayerOnline(executor, target)) return true;
        target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
        target.setFoodLevel(20);
        target.setFireTicks(0);
        Utils.message(target, "&aHealed " + target.getName());
        return true;
    }

    public static Set<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    private boolean freeze(String[] args) {
        Player target = args.length == 1 ? Bukkit.getPlayer(args[0]) : executor;
        if (!Utils.isPlayerOnline(executor, target)) return true;
        if (!frozenPlayers.remove(target.getUniqueId())) {
            frozenPlayers.add(target.getUniqueId());
            Utils.message(executor, "&aPlayer " + target.getName() + " frozen");
            return true;
        }
        Utils.message(executor, "&aPlayer " + target.getName() + " unfrozen");
        return true;
    }

    private boolean invsee(String[] args) {
        Player target = args.length == 1 ? Bukkit.getPlayer(args[0]) : executor;
        if (!Utils.isPlayerOnline(executor, target)) return true;
        executor.openInventory(target.getInventory());
        return true;
    }

    private boolean stats() {
        new StatsGui(executor).open(executor);
        return true;
    }


    private boolean prefix() {
        if (Utils.isPluginEnabled("LuckPerms"))
            new PrefixGui().open(executor);
        return true;
    }

    private boolean trails() {
        new TrailsGui(executor).open(executor);
        return true;
    }

    private boolean shop() {
        new ShopGui().open(executor);
        return true;
    }

    public static int getTotalMinedBlocks(Player p) {
        final Material[] stoneBlocks = new Material[]
                {Material.STONE, Material.ANDESITE, Material.COBBLESTONE, Material.DIORITE, Material.GRANITE, Material.DIRT};
        int total = 0;
        for (Material m : stoneBlocks) {
            total += p.getStatistic(Statistic.MINE_BLOCK, m);
        }
        return total;
    }

    public static int getTotalChoppedTrees(Player p) {
        final Material[] woodenLogs = new Material[]
                {Material.OAK_LOG, Material.SPRUCE_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.BIRCH_LOG};
        int total = 0;
        for (Material m : woodenLogs) {
            total += p.getStatistic(Statistic.MINE_BLOCK, m);
        }
        return total;
    }
}