package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.gui.PrefixGui;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.ShopGui;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.StatsGui;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.TrailsGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.NPC;
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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (!Utils.verifyIfIsAPlayer(sender)) {
            Utils.message(sender, "§cThis command must be executed by a player!");
            return true;
        }
        final Player p = (Player) sender;
        if (!Utils.isAuthorized(p, cmd.getPermission())) {
            Utils.messageNoPermission(p);
            return true;
        }

        switch(cmd.getName().toLowerCase()) {
            case "ignite":
                ignite(p, args);
                break;
            case "heal":
                heal(p, args);
                break;
            case "freeze":
                freeze(p, args);
                break;
            case "invsee":
                invsee(p, args);
                break;
            case "stats":
                stats(p, args);
                break;
            case "spawnnpc":
                spawnNPC(p, args);
                break;
            case "trails":
                trails(p, args);
                break;
            case "shop":
                shop(p, args);
                break;
            case "prefix":
                prefix(p, args);
                break;
        }
        return true;
    }

    private static void ignite(Player p, String[] args) {
        if (args.length < 1) {
            Utils.message(p, "§cPlease specify a player to ignite!");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (!Utils.isPlayerOnline(p, target)) {
            return;
        }
        target.setFireTicks(500);
        Utils.message(p, "§aPlayer " + target.getName() + " §aignited!");
    }

    private static void heal(Player p, String[] args) {
        if (args.length < 1 ) {
            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
            p.setFoodLevel(20);
            p.setFireTicks(0);
            Utils.message(p, "§aHealed" + p.getName());
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (!Utils.isPlayerOnline(p, target)) {
                return;
            }
            target.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
            target.setFoodLevel(20);
            target.setFireTicks(0);
            Utils.message(target, "§aHealed " + p.getName());
        }
    }

    public static Set<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    private static void freeze(Player p, String[] args) {
        if (args.length < 1) {
            getFrozenPlayers().add(p.getUniqueId());
            Utils.message(p, "§aYou just froze yourself!");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (!Utils.isPlayerOnline(p, target)) {
            return;
        }
        if (PlayerCommand.getFrozenPlayers().remove(target.getUniqueId())) {
            Utils.message(p, "§aPlayer " + target.getName() + " §aunfrozen!");
        } else {
            PlayerCommand.getFrozenPlayers().add(target.getUniqueId());
            Utils.message(p, "§aPlayer " + target.getName() + " §afrozen!");
        }
    }

    private static void invsee(Player p, String[] args) {
        if (args.length < 1) {
            Utils.message(p, "§cPlease specify a player!");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (!Utils.isPlayerOnline(p, target)) {
            return;
        }
        p.openInventory(target.getInventory());
    }

    private static void stats(Player p, String[] args) {
        new StatsGui(p).open(p);
    }

    private static void spawnNPC(Player p, String[] args) {
        if (args.length < 1) {
            NPC.createNPC(p, "NPC");
            Utils.message(p, "§aDefault NPC generated!");
            return;
        } else if (args.length > 12) {
            Utils.message(p, "§cDue to limitations, the name cannot be longer than 14 characters!");
            return;
        }
        NPC.createNPC(p, Utils.getFinalArg(args, 0));
        Utils.message(p, "§aNPC created!");
    }

    private static void prefix(Player p, String[] args) {
        new PrefixGui(p).open(p);
    }

    private static void trails(Player p, String[] args) {
        new TrailsGui(p).open(p);
    }

    private static void shop(Player p, String[] args) {
        new ShopGui().open(p);
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