package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Debug;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ImageManager;
import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.StatsGui;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.TradeGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.NPC;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.TrailsGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Renderer;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
    private static final Set<UUID> peopleWantingLock = new HashSet<>();
    private static final Set<Player> vanishedPlayers = new HashSet<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (!Utils.verifyIfIsAPlayer(sender)) {
            Utils.message(sender, "§cThis command must be executed by a player!");
            return true;
        }
        final Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("ignite")) {
            if (!Utils.hasPermission(p, "ignite")) {
                Utils.messageNoPermission(p);
                }
                if (args.length < 1) {
                    Utils.message(p, "§cPlease specify a player to ignite!");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (!Utils.isPlayerOnline(sender, args[0])) {
                    return true;
                }
                // Sets the player on fire for 500 ticks (there are ~20 ticks in second, so 25 seconds total - that will possibly kill him).
                target.setFireTicks(500);
                Utils.message(p, "§aPlayer " + target.getName() + " §aignited!");
                return true;
            }

        else if (cmd.getName().equalsIgnoreCase("freeze")) {
            if (!Utils.hasPermission(p, "freeze")) {
                Utils.messageNoPermission(sender);
                return true;
            }
                if (args.length < 1) {
                    Utils.message(p, "§cPlease specify a player to freeze!");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (!Utils.isPlayerOnline(sender, args[0])) {
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
            if (!Utils.hasPermission(p, "heal")) {
                Utils.messageNoPermission(p);
                return true;
            }
            if (args.length < 1) {
                Utils.message(p, "§cPlease specify a player to heal!");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (!Utils.isPlayerOnline(sender, args[0])) {
                return true;
            }
                // Heal the player
                target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
                target.setFoodLevel(20);
                target.setFireTicks(0);
                Utils.message(target, "§aHealed by " + p.getName());
                Utils.message(p, "§aHealed " + target.getName());
            }

        else if (cmd.getName().equalsIgnoreCase("lock")) {
            if (!Utils.hasPermission(p, "basic")) {
                Utils.messageNoPermission(p);
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
                getPlayersWantingLock().add(p.getUniqueId());
                Utils.message(p, "§aRight click a block to lock it!\nOr type /lock cancel to cancel");

            } else if (args.length == 1 && args[0].equalsIgnoreCase("cancel")) {
                if (getPlayersWantingLock().contains(p.getUniqueId())) {
                    getPlayersWantingLock().remove(p.getUniqueId());
                    Utils.message(p, "§aCancelled!");
                } else {
                    Utils.message(p, "§cNothing to cancel!");
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
                // TODO add method to make container public (lock.remove)
                Utils.message(p, "§aRight click a block to remove the lock!\nOr type /lock cancel to cancel");
            } else if (args.length == 0 || args[0].equalsIgnoreCase("info")) {
                Utils.message(p, new String[] {"§f--- §9Lock command §f---\n",
                        "§9/lock §7set - §fsets a lock on the block you right-click on",
                        "§9/lock §7remove - §fremoves the lock from the block you right-click on",
                        "§9/lock §7cancel - §fcancells the lock creation",
                        "§9/lock §7info - §fshows this message"});
            }
        }

        else if (cmd.getName().equalsIgnoreCase("invsee")) {
            if (!Utils.hasPermission(p, "admin")) {
                Utils.messageNoPermission(p);
                return true;
            }
            if (args.length < 1) {
                Utils.message(p, "§cPlease specify a player!");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (!Utils.isPlayerOnline(sender, args[0])) {
                return true;
            }
            p.openInventory(target.getInventory());

        }

        else if (cmd.getName().equalsIgnoreCase("store")) {
            if (!Utils.hasPermission(p, "admin")) {
                Utils.messageNoPermission(p);
                return true;
            }
            if(args.length > 0) {
                String message = Utils.getFinalArg(args, 0);
                ItemStack itemStack = p.getInventory().getItemInMainHand();
                ItemMeta itemMeta = itemStack.getItemMeta();
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                if(container.has(new NamespacedKey(plugin, "MBN"), PersistentDataType.STRING)) {
                    Utils.message(p, "§cThere is already a message stored inside this item!");
                    Utils.message(p, "§aMessage: " + "§2" + container.get(new NamespacedKey(plugin, "MBN"), PersistentDataType.STRING));
                } else {
                    container.set(new NamespacedKey(plugin, "MBN"), PersistentDataType.STRING, message);

                    itemStack.setItemMeta(itemMeta);

                    Utils.message(p, "§aMessage stored!");
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
            StatsGui statsGui = new StatsGui();
            statsGui.setPlayer(p);
            p.openInventory(statsGui.createInv());
        }

        else if (cmd.getName().equalsIgnoreCase("debug")) {
            if (!Utils.hasPermission(p, "admin")) {
                Utils.messageNoPermission(p);
                return true;
            }
                if (args.length == 1 && args[0].equalsIgnoreCase("playersWantingLock")) {
                    Utils.message(p, "§alist of uuid's of " + getPlayersWantingLock().toString());
                    return true;
                } else if (args.length == 1 && args[0].equalsIgnoreCase("tradegui")) {
                    //p.openInventory(new TradeGui().createInv());
                    p.openInventory(Debug.createInv());
                }
            }

        else if (cmd.getName().equalsIgnoreCase("spawnnpc")) {
            if (!Utils.hasPermission(p, "admin")) {
                Utils.messageNoPermission(p);
                return true;
            }
                if (args.length == 0) {
                    NPC.createNPC(p, "NPC");
                    Utils.message(p, "§aDefault NPC generated!");
                    return true;
                }
                if (args.length > 12) {
                    Utils.message(p, "§cDue to limitations, the name cannot be longer than 14 characters!");
                    return true;
                }
                NPC.createNPC(p, args[0]);
                Utils.message(p, "§aNPC created!");
                return true;
            }

        else if (cmd.getName().equalsIgnoreCase("trails")) {
            if (!Utils.hasPermission(sender, "basic")) {
                Utils.messageNoPermission(p);
                return true;
            }
            p.openInventory(new TrailsGui().createInv());
            return true;
            }

        else if (cmd.getName().equalsIgnoreCase("vanish")) {
            if (!Utils.hasPermission(p, "admin")) {
                Utils.messageNoPermission(p);
                return true;
            }
            if (vanishedPlayers.contains(p)) {
                // vanished
                vanishedPlayers.remove(p);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.showPlayer(plugin, p);
                }
                Utils.message(p, "§aYou have unvanished");
            } else {
                // not vanished
                vanishedPlayers.add(p);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.hidePlayer(plugin, p);
                }
                Utils.message(p, "§aYou have vanished");
            }
        }

        else if (cmd.getName().equalsIgnoreCase("createmap")) {
            if (!Utils.hasPermission(p, "admin")) {
                Utils.messageNoPermission(p);
                return true;
            }
            MapView view = Bukkit.createMap(p.getWorld());
            view.getRenderers().clear();

            Renderer renderer = new Renderer();
            if (args.length == 0) {
                Utils.message(p, "§cPlease specify a link with an image!");
                return true;
            }
            if (!renderer.load(args[0])) {
                Utils.message(p, "§cImage could not be loaded!\n" +
                        "§cThe best images are from imgur");
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

    public static Set<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    public static Set<UUID> getPlayersWantingLock() {
        return peopleWantingLock;
    }

    public static Set<Player> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public static int getTotalMinedBlocks(Player p){
        int total = 0;
        for (Material m : stoneBlocks) {
            total += p.getStatistic(Statistic.MINE_BLOCK, m);
        }
        return total;
    }

    static EnumSet<Material> stoneBlocks = EnumSet.of(
            Material.STONE,
            Material.ANDESITE,
            Material.COBBLESTONE,
            Material.DIORITE,
            Material.GRANITE,
            Material.DIRT
    );
}