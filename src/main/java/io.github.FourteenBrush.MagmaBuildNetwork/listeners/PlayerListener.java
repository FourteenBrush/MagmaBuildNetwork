package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandLock;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandVanish;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.PlayerCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.GuiCreator;
import io.github.FourteenBrush.MagmaBuildNetwork.spawn.Combat;
import io.github.FourteenBrush.MagmaBuildNetwork.spawn.Spawn;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.ActionBar;
import io.github.FourteenBrush.MagmaBuildNetwork.particles.EffectsUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.NoPush;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

@SuppressWarnings("unused")
public class PlayerListener implements Listener {

    private final Main plugin = Main.getPlugin(Main.class);

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setCollidable(false); // todo test
        NoPush.setCantPush(player);
        ActionBar actionBar = new ActionBar(player);
        actionBar.runTaskTimer(plugin, 1L, 6L);
        for (Player e : Bukkit.getOnlinePlayers()) {
            if (CommandVanish.getVanishedPlayers().contains(player.getUniqueId()) && !CommandVanish.getVanishedPlayers().contains(e.getUniqueId()))
                e.hidePlayer(plugin, player);
        }
        event.setJoinMessage(CommandVanish.getVanishedPlayers().contains(player.getUniqueId()) ? null : Utils.colorize("&7[&a&l+&7] &b" + player.getName() + " &7joined the server."));
        Bukkit.getScheduler().runTaskLater(plugin, () -> Utils.message(player, "&aWelcome to the server &b " + player.getName()), 1L);
        if (!player.hasPlayedBefore()) {
            giveRespawnItems(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        GuiCreator.getOpenInventories().remove(p.getUniqueId());
        Combat.remove(p.getUniqueId());
        event.setQuitMessage(CommandVanish.getVanishedPlayers().contains(p.getUniqueId()) ? null : Utils.colorize("&7[&c&l-&7] &b" + p.getName() + " &7left the server."));
        EffectsUtils d = new EffectsUtils(p);
        if (EffectsUtils.getTrails().containsKey(p.getUniqueId()))
            d.endTask();
        if (CommandVanish.getVanishedPlayers().contains(p.getUniqueId())) {
            ConfigManager.getData().set("vanished-players", CommandVanish.getVanishedPlayers());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (PlayerCommand.getFrozenPlayers().contains(p.getUniqueId()) && (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockY() != event.getFrom().getBlockY() || event.getTo().getBlockZ() != event.getFrom().getBlockZ())) {
            event.setCancelled(true);
        } else if (EffectsUtils.getTrails().containsKey(p.getUniqueId())) {
            EffectsUtils effects = new EffectsUtils(p);
            if (EffectsUtils.getTrails().get(p.getUniqueId()) == EffectsUtils.Trails.WALKTRAIL) {
                effects.startWalkTrail();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getVehicle().getType() == EntityType.HORSE)) return;
        event.setCancelled(false);
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (!(event.getVehicle().getType() == EntityType.BOAT)) return;
        event.getVehicle().remove();
        ItemStack item = new ItemStack(getBoat(((Boat) event.getVehicle()).getWoodType()));
        event.getExited().getLocation().getWorld().dropItem(event.getExited().getLocation(), item);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Horse) event.setCancelled(false); // override worldguard
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (CommandVanish.getVanishedPlayers().contains(event.getEntity().getUniqueId()) &&
                !plugin.getConfig().getBoolean("pickup_items_during_vanish")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (CommandVanish.getVanishedPlayers().contains(player.getUniqueId()) && plugin.getConfig().getBoolean("disable_hunger_in_vanish")) {
            if (event.getFoodLevel() < player.getFoodLevel()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (plugin.getConfig().getBoolean("teleport_to_spawn_on.respawn")) {
            event.setRespawnLocation(Spawn.getLocation());
            event.getPlayer().setBedSpawnLocation(Spawn.getLocation());
        }
        giveRespawnItems(event.getPlayer());
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (event.getDamager() instanceof Player && !WEAPONS.contains(((Player) event.getDamager()).getInventory().getItemInMainHand().getType())) {
            event.setDamage(0); // todo cancel??
        } else if (event.getDamager() instanceof Player) {
            Combat.pvp(player, (Player) event.getDamager());
        } else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
            Combat.pvp(player, (Player) ((Arrow) event.getDamager()).getShooter());
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity)
            ((LivingEntity) event.getEntity()).setCollidable(false); // todo remove?
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
        if (CommandVanish.getVanishedPlayers().contains(event.getPlayer().getUniqueId()))
        event.setCancelled(true);
    }

    private static void giveRespawnItems(Player player) {
        PlayerInventory inv = player.getInventory();
        Utils.tryAddItemToInventory(player, 0, new ItemStack(Material.APPLE, 16));
        Utils.tryAddItemToInventory(player, 1, new ItemStack(Material.WOODEN_AXE));
    }

    private Material getBoat(TreeSpecies ts) {
        switch (ts) {
            case REDWOOD:
                return Material.SPRUCE_BOAT;
            case BIRCH:
                return Material.BIRCH_BOAT;
            case JUNGLE:
                return Material.JUNGLE_BOAT;
            case ACACIA:
                return Material.ACACIA_BOAT;
            case DARK_OAK:
                return Material.DARK_OAK_BOAT;
        }
        return Material.OAK_BOAT;
    }

    private static final Set<Material> WEAPONS = EnumSet.of(
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD
    );
}