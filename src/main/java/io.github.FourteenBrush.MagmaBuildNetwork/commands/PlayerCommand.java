package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.data.ImageManager;
import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.NPC;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.GUI;
import io.github.FourteenBrush.MagmaBuildNetwork.util.Renderer;
import io.github.FourteenBrush.MagmaBuildNetwork.util.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerCommand implements CommandExecutor {

    private final Main plugin = Main.getInstance();
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (!Utils.verifyIfIsAPlayer(sender)) {
            Utils.message(sender, "§cThis command must be executed by a player!");
            return true;
        }
        final Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("MagmaBuildNetwork")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                // TODO main command info
            }
            else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!Utils.hasPermission(sender, "reload")) {
                    Utils.messageNoPermission(sender);
                    return true;
                }
                plugin.reloadConfig();
                Utils.message(sender, "§2Successfully reloaded " + plugin.getName());
            }
        }

        else if (cmd.getName().equalsIgnoreCase("ignite")) {
            if (!Utils.hasPermission(p, "ignite")) {
                Utils.messageNoPermission(p);
                }
                if (args.length != 1) {
                    Utils.message(p, "§cYou need to specify exactly one player!");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                // Make sure the player is online.
                if (!Bukkit.getOnlinePlayers().contains(target)) {
                    Utils.message(p, "§c" + args[0] + " §cis not currently online!");
                    return true;
                }
                // Sets the player on fire for 1,000 ticks (there are ~20 ticks in second, so 50 seconds total - that will kill him).
                target.setFireTicks(1000);
                return true;
            }

        else if (cmd.getName().equalsIgnoreCase("freeze")) {
            if (!Utils.hasPermission(p, "freeze")) {
                Utils.messageNoPermission(sender);
                return true;
            }
                if (args.length != 1) {
                    return false;
                }
                Player target = Bukkit.getPlayer(args[0]);
                // Make sure the player is online.
                if (!(Bukkit.getOnlinePlayers().contains(target))) {
                    sender.sendMessage(ChatColor.DARK_RED + args[0] + " is not currently online.");
                    return true;
                }
                if (PlayerCommand.getFrozenPlayers().contains(target.getUniqueId())) {
                    PlayerCommand.getFrozenPlayers().remove(target.getUniqueId());
                    p.sendMessage(ChatColor.DARK_GREEN + "Player " + args[0] + " unfrozen!");
                } else {
                    PlayerCommand.getFrozenPlayers().add(target.getUniqueId());
                    p.sendMessage(ChatColor.DARK_GREEN + "Player " + args[0] + " frozen!");
                }
                return true;
            }

        else if (cmd.getName().equalsIgnoreCase("heal")) {
            if (!Utils.hasPermission(p, "heal")) {
                Utils.messageNoPermission(p);
                return true;
            }
            // Make sure that the player specified exactly one argument (the name of the player to ignite).
            if (args.length != 1) {
                // When onCommand() returns false, the help message associated with that command is displayed.
                return false;
            }
                // Get the player who should be healed. Remember that indices start with 0, not 1.
                Player target = Bukkit.getPlayer(args[0]);

                // Make sure the player is online.
                if (!(Bukkit.getOnlinePlayers().contains(target))) {
                    sender.sendMessage(args[0] + " is not currently online.");
                    return true;
                }

                // Heal the player
                target.setHealth(20.0);
                target.setFoodLevel(20);
                target.sendMessage(ChatColor.DARK_GREEN + "Healed by " + sender.getName());
                sender.sendMessage(ChatColor.DARK_GREEN + "Healed " + args[0]);
            }

        else if (cmd.getName().equalsIgnoreCase("lock")) {
            if (!Utils.hasPermission(p, "basic")) {
                Utils.messageNoPermission(p);
                return true;
            }
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

        else if (cmd.getName().equalsIgnoreCase("store")) {
            if (Utils.hasPermission(p, "admin")) {
                Utils.messageNoPermission(p);
                return true;
            }
            if(args.length > 0) {

                StringBuilder message = new StringBuilder();
                for(String arg : args) {
                    message.append(arg).append(" ");
                }

                ItemStack itemStack = p.getInventory().getItemInMainHand();
                ItemMeta itemMeta = itemStack.getItemMeta();
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                if(container.has(new NamespacedKey(Main.getInstance(), "MBN"), PersistentDataType.STRING)) {
                    p.sendMessage(ChatColor.GREEN + "There is already a message stored inside this item!");
                    p.sendMessage(ChatColor.GREEN + "Message: " + ChatColor.GREEN + container.get(new NamespacedKey(Main.getInstance(), "MBN"), PersistentDataType.STRING));
                }
                else {
                    container.set(new NamespacedKey(Main.getInstance(), "MBN"), PersistentDataType.STRING, message.toString());

                    itemStack.setItemMeta(itemMeta);

                    p.sendMessage(ChatColor.GREEN + "Message stored!");
                }
            }
            else {
                Utils.message(p,"§cYou need to provide a message to store!");
            }
        }

        else if (cmd.getName().equalsIgnoreCase("stats")) {
            if (!Utils.hasPermission(p, "basic")) {
                Utils.messageNoPermission(p);
                return true;
            }
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

        else if (cmd.getName().equalsIgnoreCase("debug")) {
            if (!Utils.hasPermission(p, "admin")) {
                Utils.messageNoPermission(p);
                return true;
            }
                if (args.length == 1 && args[0].equalsIgnoreCase("playersWantingLock")) {
                    System.out.println("this works (debug)");
                    Utils.message(p, "list of uuid's of " + getPlayersWantingLock().toString());
                    return true;
                }
            }

        else if (cmd.getName().equalsIgnoreCase("spawnnpc")) {
            if (!Utils.hasPermission(p, "admin")) {
                Utils.messageNoPermission(p);
                return true;
            }
                if (args.length == 0) {
                    NPC.createNPC(p, "NPC");
                    p.sendMessage(ChatColor.DARK_GREEN + "DEFAULT NPC CREATED!");
                    return true;
                }
                NPC.createNPC(p, args[0]);
                p.sendMessage(ChatColor.DARK_GREEN + "NPC CREATED!");
                return true;
            }

        else if (cmd.getName().equalsIgnoreCase("trails")) {
            if (!Utils.hasPermission(sender, "basic")) {
                Utils.messageNoPermission(p);
                return true;
            }
            new GUI().openInventory(p);
            return true;
            }

        else if (cmd.getName().equalsIgnoreCase("createmap")) {
            if (!Utils.hasPermission(p, "admin")) {
                Utils.messageNoPermission(p);
                return true;
            }
            MapView view = Bukkit.createMap(p.getWorld());
            view.getRenderers().clear();

            Renderer renderer = new Renderer();
            if (!renderer.load(args[0])) {
                Utils.message(p, "§cImage could not be loaded!");
                return true;
            }
            view.addRenderer(renderer);
            ItemStack map = new ItemStack(Material.FILLED_MAP);
            MapMeta meta = (MapMeta) map.getItemMeta();

            meta.setMapView(view);
            map.setItemMeta(meta);

            p.getInventory().addItem(map);
            Utils.message(p,"§aImage created!");

            ImageManager manager = ImageManager.getInstance();
            manager.saveImage(view.getId(), args[0]); // args[0] is the url
            return true;
        }

        return true;
    }

    private int getTotalMinedBlocks(Player p){
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