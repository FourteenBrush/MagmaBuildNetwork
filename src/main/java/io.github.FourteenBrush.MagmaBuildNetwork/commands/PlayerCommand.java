package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.inventory.StatsGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.NPC;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.TrailsGui;
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

        if (cmd.getName().equalsIgnoreCase("ignite")) {
                if (args.length < 1) {
                    Utils.message(p, "§cPlease specify a player to ignite!");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (!Utils.isPlayerOnline(p, args[0])) {
                    return true;
                }
                // Sets the player on fire for 500 ticks (there are ~20 ticks in second, so 25 seconds total - that will possibly kill him).
                target.setFireTicks(500);
                Utils.message(p, "§aPlayer " + target.getName() + " §aignited!");
                return true;
            }

        else if (cmd.getName().equalsIgnoreCase("freeze")) {
                if (args.length < 1) {
                    Utils.message(p, "§cPlease specify a player to freeze!");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (!Utils.isPlayerOnline(p, args[0])) {
                    return true;
                }
                if (PlayerCommand.getFrozenPlayers().contains(target.getUniqueId())) {
                    PlayerCommand.getFrozenPlayers().remove(target.getUniqueId());
                    Utils.message(p, "§aPlayer " + target.getName() + " §aunfrozen!");
                } else {
                    PlayerCommand.getFrozenPlayers().add(target.getUniqueId());
                    Utils.message(p, "§aPlayer " + target.getName() + " §afrozen!");
                }
                return true;
            }

        else if (cmd.getName().equalsIgnoreCase("heal")) {
            if (args.length < 1) {
                heal(p, p);
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (!Utils.isPlayerOnline(p, args[0])) {
                return true;
            }
                // Heal the player
                heal(target, p);
                return true;
            }

        else if (cmd.getName().equalsIgnoreCase("invsee")) {
            if (args.length < 1) {
                Utils.message(p, "§cPlease specify a player!");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (!Utils.isPlayerOnline(p, args[0])) {
                return true;
            }
            p.openInventory(target.getInventory());

        }

        else if (cmd.getName().equalsIgnoreCase("stats")) {
            StatsGui statsGui = new StatsGui();
            statsGui.setPlayer(p);
            p.openInventory(statsGui.createInv());
        }

        else if (cmd.getName().equalsIgnoreCase("spawnnpc")) {
                if (args.length == 0) {
                    NPC.createNPC(p, "NPC");
                    Utils.message(p, "§aDefault NPC generated!");
                    return true;
                } else if (args.length > 12) {
                    Utils.message(p, "§cDue to limitations, the name cannot be longer than 14 characters!");
                    return true;
                }
                NPC.createNPC(p, args[0]);
                Utils.message(p, "§aNPC created!");
                return true;
            }

        else if (cmd.getName().equalsIgnoreCase("trails")) {
            if (!Utils.hasPermission(p, "basic")) {
                Utils.messageNoPermission(p);
                return true;
            }
            p.openInventory(new TrailsGui().createInv());
            return true;
            }
        return true;
    }

    public static Set<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    private static void heal(Player p, Player sender) {
        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
        p.setFoodLevel(20);
        p.setFireTicks(0);
        if (p != sender) {
            Utils.message(p, "§aHealed by " + sender.getName());
        }
        Utils.message(p, "§aHealed " + p.getName());
    }

    public static int getTotalMinedBlocks(Player p){
        int total = 0;
        for (Material m : stoneBlocks) {
            total += p.getStatistic(Statistic.MINE_BLOCK, m);
        }
        return total;
    }

    public static int getTotalChoppedTrees(Player p) {
        int total = 0;
        for (Material m : woodenLogs) {
            total += p.getStatistic(Statistic.MINE_BLOCK, m);
        }
        return total;
    }

    private final static EnumSet<Material> stoneBlocks = EnumSet.of(
            Material.STONE,
            Material.ANDESITE,
            Material.COBBLESTONE,
            Material.DIORITE,
            Material.GRANITE,
            Material.DIRT
    );

    private final static EnumSet<Material> woodenLogs = EnumSet.of(
            Material.OAK_LOG,
            Material.SPRUCE_LOG,
            Material.DARK_OAK_LOG,
            Material.JUNGLE_LOG,
            Material.ACACIA_LOG,
            Material.BIRCH_LOG
    );
}