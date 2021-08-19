package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.ActionBar;
import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandLock;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandVanish;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.data.PacketReader;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.PlayerCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.events.RightClickNPCEvent;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.GuiCreator;
import io.github.FourteenBrush.MagmaBuildNetwork.spawn.Combat;
import io.github.FourteenBrush.MagmaBuildNetwork.spawn.Spawn;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Effects;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.NPC;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

@SuppressWarnings("unused")
public class PlayerListener implements Listener {

    private static final Main plugin = Main.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        new PacketReader().inject(p);
        if (!(NPC.getNPCs() == null || NPC.getNPCs().isEmpty())) {
            NPC.addJoinPacket(p);
        }
        p.setCollidable(false);
        new ActionBar(p).runTaskTimer(plugin, 1, 6);
        for (Player e : Bukkit.getOnlinePlayers()) {
            if (CommandVanish.getVanishedPlayers().contains(p.getUniqueId()) && !CommandVanish.getVanishedPlayers().contains(e.getUniqueId()))
                // if the player who joins is vanished and the player to hide them from isn't vanished -> hide them
                e.hidePlayer(plugin, p);
        }
        event.setJoinMessage(CommandVanish.getVanishedPlayers().contains(p.getUniqueId()) ? null : Utils.colorize("&7[&a&l+&7] &b" + p.getName() + " &7joined the server."));
        if (!p.hasPlayedBefore()) {
            giveRespawnItems(p);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        GuiCreator.openInventories.remove(p.getUniqueId());
        Combat.remove(p.getUniqueId());
        new PacketReader().unInject(p);
        event.setQuitMessage(CommandVanish.getVanishedPlayers().contains(p.getUniqueId()) ? null : Utils.colorize("&7[&c&l-&7] &b" + p.getName() + " &7left the server."));
        Effects d = new Effects(p, p.getUniqueId());
        if (d.hasID())
            d.endTask();
        if (CommandVanish.getVanishedPlayers().contains(p.getUniqueId())) {
            ConfigManager.getDataConfig().set("vanished_players", CommandVanish.getVanishedPlayers());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (PlayerCommand.getFrozenPlayers().contains(p.getUniqueId())) {
            if (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockY() != event.getFrom().getBlockY() || event.getTo().getBlockZ() != event.getFrom().getBlockZ()) {
                event.setCancelled(true);
            }
        }
        if (Effects.hasWalkTrail(p.getUniqueId())) {
            Random r = new Random();
            for (int i = 0; i < 5; i++) {
                p.getWorld().spawnParticle(Particle.CRIT_MAGIC, p.getLocation().add(
                        r.nextDouble() * 0.5, r.nextDouble() * 0.5, r.nextDouble() * .5), 0);
            }
            for (int i = 0; i < 5; i++) {
                p.getWorld().spawnParticle(Particle.CRIT_MAGIC, p.getLocation().add(
                        -1 * (r.nextDouble() * 0.5), r.nextDouble() * 0.5, (r.nextDouble() * .5) * -1), 0);
            }
        }
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
        if (CommandVanish.getVanishedPlayers().contains(event.getEntity().getUniqueId()) &&
                !plugin.getConfig().getBoolean("pickup_items_during_vanish")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (CommandVanish.getVanishedPlayers().contains(player.getUniqueId()) && plugin.getConfig().getBoolean("disable_hunger_in_vanish")) {
                if (event.getFoodLevel() < player.getFoodLevel()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (ConfigManager.getConfig().getBoolean("teleport_to_spawn_on.respawn")) {
            event.setRespawnLocation(Spawn.getLocation());
        }
        giveRespawnItems(event.getPlayer());
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
            // todo
        } else if (LockListener.cannotOpen(LockListener.getOwner(), event.getPlayer()) &&
                !CommandLock.getBypassingLock().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            Utils.message(event.getPlayer(), "§cYou cannot break this");
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!SWORDS.contains(player.getInventory().getItemInMainHand().getType())) {
                event.setDamage(0);
            }
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity)
            ((LivingEntity) event.getEntity()).setCollidable(false);
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getNewGameMode() == GameMode.SURVIVAL && CommandVanish.getVanishedPlayers().contains(player.getUniqueId())) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent event) {
        event.setCancelled(CommandVanish.getVanishedPlayers().contains(event.getPlayer().getUniqueId()));
    }

    private static void giveRespawnItems(Player player) {
        PlayerInventory inv = player.getInventory();
        inv.setItem(1, new ItemStack(Material.WOODEN_PICKAXE));
        inv.setItem(0, new ItemStack(Material.APPLE, 16));
    }

    private static final Set<Material> SWORDS = EnumSet.of(
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD
    );
}