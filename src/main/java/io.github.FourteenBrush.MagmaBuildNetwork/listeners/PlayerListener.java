package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandLock;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandTrade;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandVanish;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.data.PacketReader;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.PlayerCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.events.RightClickNPCEvent;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.GUI;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.PrefixGui;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.StatsGui;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.TradeGui;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.TrailsGui;
import io.github.FourteenBrush.MagmaBuildNetwork.spawn.Combat;
import io.github.FourteenBrush.MagmaBuildNetwork.spawn.Spawn;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Effects;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.NPC;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.ScoreboardHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class PlayerListener implements Listener {

    private static final Main plugin = Main.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.setDisplayName(Main.getChat().getPlayerPrefix(p) + " " + p.getName());
        for (Player e : Bukkit.getOnlinePlayers()) {
            if (plugin.getConfig().getBoolean("use_scoreboard")) {
                e.setScoreboard(ScoreboardHandler.createScoreboard(e));
            }
            if (CommandVanish.getVanishedPlayers().contains(p) && !CommandVanish.getVanishedPlayers().contains(e))
                // if the player who joins is vanished and the player to hide them from isn't vanished -> hide them
                e.hidePlayer(plugin, p);
        }
        if (!(CommandVanish.getVanishedPlayers().contains(p))) {
            event.setJoinMessage(Utils.colorize("&7[&a&l+&7] &b" + p.getName() + " &7joined the server."));
        } else {
            Utils.message(p, "§aNo join message sent because you joined vanished");
        }
        new PacketReader().inject(p);
        if (!(NPC.getNPCs() == null || NPC.getNPCs().isEmpty())) {
            NPC.addJoinPacket(p);
        }
        if (!p.hasPlayedBefore()) {
            giveRespawnItems(p);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        new PacketReader().unInject(p);
        event.setQuitMessage(Utils.colorize("&7[&c&l-&7] &b" + p.getName() + " &7left the server."));
        Effects d = new Effects(p, p.getUniqueId());
        if (d.hasID())
            d.endTask();
        Combat.remove(p);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (PlayerCommand.getFrozenPlayers().contains(p.getUniqueId())) {
            if (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockY() != event.getFrom().getBlockY() || event.getTo().getBlockZ() != event.getFrom().getBlockZ()) {
                event.setCancelled(true);
            }
        }
        if (!Effects.hasWalkTrail(p.getUniqueId()))
            return;
        Random r = new Random();
        for (int i = 0; i < 5; i++)
            p.getWorld().spawnParticle(Particle.CRIT_MAGIC, p.getLocation().add(
                    r.nextDouble() * 0.5, r.nextDouble() * 0.5, r.nextDouble() * .5),0);
        for (int i = 0; i < 5; i++)
            p.getWorld().spawnParticle(Particle.CRIT_MAGIC, p.getLocation().add(
                    -1 * (r.nextDouble() * 0.5), r.nextDouble() * 0.5, (r.nextDouble() * .5) * -1),0);
    }

    @EventHandler
    public void onClick(RightClickNPCEvent event) {
        Player p = event.getPlayer();
        if (event.getNPC().getId() == 1) {
            Utils.message(p, "§2Hello I'm " + event.getNPC().getName() + "§2!");
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (CommandVanish.getVanishedPlayers().contains((Player) event.getEntity()) &&
        !plugin.getConfig().getBoolean("pickup_items_during_vanish")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        try {
            if (event.getEntity() instanceof Player && plugin.getConfig().getBoolean("disable_hunger_in_vanish")) {
                Player p = (Player) event.getEntity();
                if (event.getFoodLevel() <= p.getFoodLevel()) {
                    event.setCancelled(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (ConfigManager.getConfigConfig().getBoolean("teleport_to_spawn_on.respawn")) {
            event.setRespawnLocation(Spawn.getLocation());
        }
        giveRespawnItems(event.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        if (!(event.getInventory().getHolder() instanceof GUI)) {
            return;
        }
    
        if (event.getInventory().getHolder() instanceof TrailsGui) {
            event.setCancelled(true);
            Effects effect = new Effects(p, p.getUniqueId());
            if (effect.hasID()) {
                effect.endTask();
                effect.removeID();
            }
            switch (slot) {
                case 3:
                    effect.startTotem();
                    p.closeInventory();
                    break;
                case 5:
                    effect.setID(1);
                    p.closeInventory();
                    break;
                case 8:
                    p.closeInventory();
                    break;
                default:
                    break;
            }
        } else if (event.getInventory().getHolder() instanceof TradeGui) {
            List<Integer> placeableSlots = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30);
            List<Integer> functionSlots = Arrays.asList(37, 38, 39, 41);
            Inventory inv = event.getInventory();
            TradeGui senderGui = CommandTrade.getSenderGui();
            TradeGui targetGui = CommandTrade.getTargetGui();
            if (!(event.getRawSlot() > 53 || placeableSlots.contains(slot))) {
                event.setCancelled(true);
            }
            if (inv == senderGui) {
                if (placeableSlots.contains(slot)) {
                    // if clicking on a placeable slot -> place the same item by the other player
                    CommandTrade.setItemInGui(targetGui, slot, inv.getItem(slot));
                } else if (functionSlots.contains(slot)) {
                    CommandTrade.changeClickedSlots(slot, event, p);
                }
            } else if (inv == targetGui) {
                if (placeableSlots.contains(slot)) {
                    // if clicking on a placeable slot -> place the same item by the other player
                    CommandTrade.setItemInGui(senderGui, slot, inv.getItem(slot));
                } else if (functionSlots.contains(slot)) {
                    CommandTrade.changeClickedSlots(slot, event, p);
                }
            }

        } else if (event.getInventory() instanceof PrefixGui) {
            event.setCancelled(true);
            // todo
        } else if (event.getInventory().getHolder() instanceof StatsGui) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (!Main.getVaultActivated()) return;
        if (event.getEntity() instanceof Monster) {
            Player player = event.getEntity().getKiller();
            if (player == null) // if mobs died of a natural dead return
                return;
            Random r = new Random();
            int amount = r.nextInt(10) + 10;
            Main.getEco().depositPlayer(player, amount);
            Utils.message(player, "§2§l+ $" + amount);
        }
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (!(event.getVehicle().getType() == EntityType.BOAT)) {
            return;
        }
        event.getVehicle().remove();
        plugin.getServer().getWorlds().get(0).dropItem(event.getExited().getLocation(), new ItemStack(Material.OAK_BOAT));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        if (LockListener.canLock(b)) {
            LockListener.checkLockStillValid(b.getChunk());
        } else if (b.getType() == Material.OAK_LOG) {
            Material below = b.getLocation().subtract(0, 1, 0).getBlock().getType();
            if (below == Material.GRASS_BLOCK || below == Material.DIRT) {
                event.setCancelled(true);
                b.setType(Material.OAK_SAPLING);
            }
        } else if (LockListener.cannotOpen(LockListener.getOwner(), event.getPlayer()) &&
            !CommandLock.getBypassingLock().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            Utils.message(event.getPlayer(), "§cYou cannot break this");
        }
    }

    public static void giveRespawnItems(Player player) {
        player.getInventory().setExtraContents(new ItemStack[] {new ItemStack(Material.APPLE, 16),
            new ItemStack(Material.WOODEN_AXE)});
    }

    /*@EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        Player p = event.getPlayer();
        BlockState blockState = block.getState();

        if (!(blockState instanceof TileState) || (!(blockState instanceof Lockable) || (!(blockState instanceof Openable)))) return;

        String s = block.getX() + "|" + block.getY() + "|" + block.getZ();
        NamespacedKey chunkKey = new NamespacedKey(MagmaBuildNetwork.getPlugin(), s);

        PersistentDataContainer container = block.getLocation().getChunk().getPersistentDataContainer();

        TileState tileState = (TileState) blockState;
        PersistentDataContainer ChunkContainer = tileState.getPersistentDataContainer();

        if (container.has(keyOwner, PersistentDataType.STRING) && MagmaBuildNetwork.getPlayersWantingLock()
            .contains(p.getUniqueId())) {
            p.sendMessage(ChatColor.RED + "This block is already locked!");
            MagmaBuildNetwork.getPlayersWantingLock().remove(p.getUniqueId());
        }
        if (MagmaBuildNetwork.getPlayersWantingLock().remove(p.getUniqueId())) {
            event.setCancelled(true);
            container.set(keyOwner, PersistentDataType.STRING, p.getUniqueId().toString());
            blockState.update(); // apply the lock!
            p.sendMessage(ChatColor.DARK_GREEN + "Locked!");
        } else {
            String owner = container.get(keyOwner, PersistentDataType.STRING);
            if (owner != null && !owner.equalsIgnoreCase(p.getUniqueId().toString())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot open this!");
            }
        }
    }*/

    /*@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        Player p = event.getPlayer();

        BlockState blockState = block.getState();
        if (!(blockState instanceof TileState) || (!(blockState instanceof Lockable))) return;

        TileState tileState = (TileState) blockState;

        PersistentDataContainer container = tileState.getPersistentDataContainer();

        String lock = container.get(keyOwner, PersistentDataType.STRING);
        if (lock != null && !lock.equalsIgnoreCase(p.getUniqueId().toString())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break this!");
        }
    }*/

    /*@EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Block block = event.getClickedBlock();
        BlockState blockState = block.getState();

        if (!canLock(blockState)) return;

        String s = block.getX() + "/" + block.getY() + "/" + block.getZ();
        NamespacedKey key = new NamespacedKey(MagmaBuildNetwork.getPlugin(), s);

        PersistentDataContainer container = block.getLocation().getChunk().getPersistentDataContainer();

        if (container.has(key, PersistentDataType.STRING)) {
            System.out.println("A Lock exists for this block!");
            if (container.get(key, PersistentDataType.STRING).equals(event.getPlayer().getUniqueId().toString())) {
                System.out.println("The lock is yours!");
            } else {
                System.out.println("The lock is NOT yours!");
                event.setCancelled(true);
            }
            return;
        } else {
            System.out.println("No Lock exists for this block!");
        }

        container.set(key, PersistentDataType.STRING, event.getPlayer().getUniqueId().toString());
    }*/

     // Clean up any left over lock data that is no longer valid.
    /*@EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {

        PersistentDataContainer container = event.getChunk().getPersistentDataContainer();
        BlockState blockState;

        for (NamespacedKey key : container.getKeys()) {
            if (key.getNamespace().equalsIgnoreCase(MagmaBuildNetwork.getPlugin().getName())) {

                String[] data = key.getKey().split("/");
                if (data.length != 3) continue;

                blockState = event.getChunk().getBlock(Integer.valueOf(data[0]), Integer.valueOf(data[1]), Integer.valueOf(data[2])).getState();

                if (!canLock(blockState)) {
                    MagmaBuildNetwork.getPlugin().getLogger().warning(String.format("Unknown Lock removed! %s.", key.getKey()));
                    container.remove(key);
                }
            }
        }
    }*/
}
   /* @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        Player p = event.getPlayer();
        BlockState blockState = block.getState();

        if (!canLock(blockState)) return;

        String s = block.getX() + "/" + block.getY() + "/" + block.getZ();
        NamespacedKey key = new NamespacedKey(MagmaBuildNetwork.getPlugin(), s);

        PersistentDataContainer container = block.getLocation().getChunk().getPersistentDataContainer();

        TileState tileState = (TileState) blockState;
        PersistentDataContainer ChunkContainer = tileState.getPersistentDataContainer();

        if (container.has(keyOwner, PersistentDataType.STRING) && MagmaBuildNetwork.getPlayersWantingLock()
                .contains(p.getUniqueId())) {
            p.sendMessage(ChatColor.RED + "This block is already locked!");
            MagmaBuildNetwork.getPlayersWantingLock().remove(p.getUniqueId());
        }
        if (MagmaBuildNetwork.getPlayersWantingLock().remove(p.getUniqueId())) {
            event.setCancelled(true);
            container.set(keyOwner, PersistentDataType.STRING, p.getUniqueId().toString());
            blockState.update(); // apply the lock!
            p.sendMessage(ChatColor.DARK_GREEN + "Locked!");
        } else {
            String owner = container.get(keyOwner, PersistentDataType.STRING);
            if (owner != null && !owner.equalsIgnoreCase(p.getUniqueId().toString())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot open this!");
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event){

        PersistentDataContainer container = event.getChunk().getPersistentDataContainer();
        BlockState blockState;

        for (NamespacedKey key : container.getKeys()) {
            if (key.getNamespace().equalsIgnoreCase(MagmaBuildNetwork.getPlugin().getName())) {

                String[] data = key.getKey().split("|");
                if (data.length != 3) continue;

                blockState = event.getChunk().getBlock(Integer.valueOf(data[0]), Integer.valueOf(data[1]), Integer.valueOf(data[2])).getState();

                if (!(blockState instanceof TileState || blockState instanceof Lockable || blockState instanceof Openable)) {
                    MagmaBuildNetwork.getPlugin().getLogger().warning(String.format("Unknown Lock removed! %s.", key.getKey()));
                    container.remove(key);
                }

            }
        }
    }*/