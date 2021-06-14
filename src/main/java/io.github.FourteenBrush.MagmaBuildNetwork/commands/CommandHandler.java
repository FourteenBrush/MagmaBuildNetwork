package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.NPC;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.GUI;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CommandHandler implements CommandExecutor {

    private static final Set<UUID> frozenPlayers = new HashSet<>();
    private static  final Set<UUID> peopleWantingLock = new HashSet<>();

    EnumSet<Material> materials = EnumSet.of(
            Material.STONE,
            Material.ANDESITE,
            Material.COBBLESTONE,
            Material.DIORITE,
            Material.GRANITE
    );

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("MagmaBuildNetwork")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                //TODO main command info
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (Utils.hasPermission(sender, "reload")) {
                    Main.getInstance().reloadConfig();
                    sender.sendMessage(ChatColor.DARK_GREEN + "Successfully reloaded MagmaBuildNetwork ");
                    return true;
                }
            }
        }

        else if (cmd.getName().equalsIgnoreCase("ignite")) {
            if (Utils.verifyIfIsAPlayer(sender)) {
                return true;
            }
            final Player p = (Player) sender;
            if (Utils.hasPermission(p, "ignite")) {
                if (args.length != 1) {
                    return false;
                }

                Player target = Bukkit.getPlayer(args[0]);
                // Make sure the player is online.
                if (!Bukkit.getOnlinePlayers().contains(target)) {
                    sender.sendMessage(ChatColor.DARK_RED + args[0] + " is not currently online.");
                    return true;
                }

                // Sets the player on fire for 1,000 ticks (there are ~20 ticks in second, so 50 seconds total).
                target.setFireTicks(1000);
                return true;
            }
        }

        else if (cmd.getName().equalsIgnoreCase("freeze")) {
            if (Utils.verifyIfIsAPlayer(sender)) {
                return true;
            }
            final Player p = (Player) sender;
            if (Utils.hasPermission(p, "freeze")) {

                if (args.length != 1) {
                    return false;
                }
                Player target = Bukkit.getPlayer(args[0]);

                // Make sure the player is online.
                if (!(Bukkit.getOnlinePlayers().contains(target))) {
                    sender.sendMessage(ChatColor.DARK_RED + args[0] + " is not currently online.");
                    return true;
                }

                if (CommandHandler.getFrozenPlayers().contains(target.getUniqueId())) {
                    CommandHandler.getFrozenPlayers().remove(target.getUniqueId());
                    p.sendMessage(ChatColor.DARK_GREEN + "Player " + args[0] + " unfrozen!");
                } else {
                    CommandHandler.getFrozenPlayers().add(target.getUniqueId());
                    p.sendMessage(ChatColor.DARK_GREEN + "Player " + args[0] + " frozen!");
                }
                return true;
            }
        }

        else if (cmd.getName().equalsIgnoreCase("heal")) {
            // Make sure that the player specified exactly one argument (the name of the player to ignite).
            if (args.length != 1) {
                // When onCommand() returns false, the help message associated with that command is displayed.
                return false;
            }
            if (Utils.hasPermission(sender, "heal")) {

                // Get the player who should be healed. Remember that indices start with 0, not 1.
                Player target = Bukkit.getPlayer(args[0]);

                // Make sure the player is online.
                if (!(Bukkit.getOnlinePlayers().contains(target))) {
                    sender.sendMessage(args[0] + " is not currently online.");
                    return true;
                }

                // Heal the player
                target.setHealth(20.0);
                target.sendMessage(ChatColor.DARK_GREEN + "Healed by " + sender.getName());
                sender.sendMessage(ChatColor.DARK_GREEN + "Healed " + args[0]);
            }
        }

        else if (cmd.getName().equalsIgnoreCase("lock")) {
            if (Utils.verifyIfIsAPlayer(sender)) {
                return true;
            }
            final Player p = (Player) sender;
            if (Utils.hasPermission(p, "basic")) {

                if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
                    getPlayersWantingLock().add(p.getUniqueId());
                    p.sendMessage(ChatColor.DARK_GREEN + "Right click a block to lock it!\nOr type /lock cancel to cancel");

                } else if (args.length == 1 && args[0].equalsIgnoreCase("cancel")) {
                    if (getPlayersWantingLock().contains(p.getUniqueId())) {
                        getPlayersWantingLock().remove(p.getUniqueId());
                        p.sendMessage(ChatColor.DARK_GREEN + "Cancelled!");
                    }
                } else if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
                    // TODO add method to make container public (lock.remove)
                }
            }
        }

        else if (cmd.getName().equalsIgnoreCase("stats")) {
            if (Utils.verifyIfIsAPlayer(sender)) {
                return true;
            }
            final Player p = (Player) sender;
            if (Utils.hasPermission(p, "basic")) {
                Inventory gui = Bukkit.createInventory(null, 6 * 9, ChatColor.RED + "Stats");

                //This is where you create the item
                ItemStack stonePickaxe = new ItemStack(Material.STONE_PICKAXE);
                ItemStack stoneSword = new ItemStack(Material.STONE_SWORD);
                ItemStack witherSkeletonSkull = new ItemStack(Material.WITHER_SKELETON_SKULL);

                //This is where you set the display name of the item
                ItemMeta stonePickaxeMeta = stonePickaxe.getItemMeta();
                ItemMeta stoneSwordMeta = stoneSword.getItemMeta();
                ItemMeta witherSkeletonSkullMeta = witherSkeletonSkull.getItemMeta();

                //This is where you set the lore of the item
                String blocksMined = getTotalMinedBlocks(p) + " blocks mined";
                stonePickaxeMeta.setDisplayName(blocksMined);

                //This is where you decide what slot the item goes into
                gui.setItem(11, stonePickaxe);
                gui.setItem(14, stoneSword);
                gui.setItem(17, witherSkeletonSkull);
                gui.setItem(38, witherSkeletonSkull);

                ItemStack[] menuItems = {stonePickaxe, stoneSword, witherSkeletonSkull};
                gui.setContents(menuItems);
                p.openInventory(gui);
            }
        }

        else if (cmd.getName().equalsIgnoreCase("debug")) {
            if (Utils.verifyIfIsAPlayer(sender)) {
                return true;
            }
            Player p = (Player) sender;
            if (Utils.hasPermission(p, "admin")) {
                if (args.length == 1 && args[0].equalsIgnoreCase("playersWantingLock")) {
                    p.sendMessage("list of uuid's of " + getPlayersWantingLock().toString());
                    return true;
                }
            }
        }

        else if (cmd.getName().equalsIgnoreCase("spawnnpc")) {
            if (Utils.verifyIfIsAPlayer(sender)) {
                return true;
            }
            final Player p = (Player) sender;
            if (Utils.hasPermission(sender, "admin")) {
                if (args.length == 0) {
                    NPC.createNPC(p, "NPC");
                    p.sendMessage(ChatColor.DARK_GREEN + "DEFAULT NPC CREATED!");
                    return true;
                }
                NPC.createNPC(p, args[0]);
                p.sendMessage(ChatColor.DARK_GREEN + "NPC CREATED!");
                return true;
            }
        }

        else if (cmd.getName().equalsIgnoreCase("trails")) {
            if (Utils.verifyIfIsAPlayer(sender)) {
                return true;
            }
            final Player p = (Player) sender;
            if (!Utils.hasPermission(sender, "basic")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to do this!");
            }
            GUI menu = new GUI();
            menu.openInventory(p);
            return true;
            }

        return true;
    }

    public int getTotalMinedBlocks(Player p){
        int total = 0;
        for (Material m : materials) {
            total += p.getStatistic(Statistic.MINE_BLOCK, m);
        }
        return total;
    }

    public static Set<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    public static Set<UUID> getPlayersWantingLock() {
        return peopleWantingLock;
    }
}