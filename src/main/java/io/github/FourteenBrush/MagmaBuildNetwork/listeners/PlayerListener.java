package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandFly;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.spawn.CommandSpawn;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandVanish;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.SimpleCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.GuiCreator;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.spawn.Combat;
import io.github.FourteenBrush.MagmaBuildNetwork.library.ActionBar;
import io.github.FourteenBrush.MagmaBuildNetwork.library.Effects;
import io.github.FourteenBrush.MagmaBuildNetwork.library.NoPush;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerListener implements Listener {

    private final MBNPlugin plugin;
    private static final Set<Material> WEAPONS = EnumSet.of(
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD
    );

    public PlayerListener(MBNPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        NoPush.setCantPush(player);
        if (plugin.getCommandManager().checkVanishedPlayer()) {
            Bukkit.getOnlinePlayers().forEach(pl -> {
                if (!CommandVanish.getVanishedPlayers().contains(pl.getUniqueId()))
                    pl.hidePlayer(plugin, player);
                if (pl != player && Permission.MODERATOR.has(pl))
                    PlayerUtils.message(pl, "&e" + player.getName() + " has joined vanished.");
            });
            PlayerUtils.message(player, "&eYou have joined vanished.");
            event.setJoinMessage(null);
        } else event.setJoinMessage(Lang.JOIN_MESSAGE.get(player.getName()));
        Bukkit.getScheduler().runTaskLater(plugin, () -> PlayerUtils.message(player, "&aWelcome to the server &b " + player.getName()), 10L);
        new ActionBar(player).runTaskTimerAsynchronously(plugin, 1L, 3L);
        if (!player.hasPlayedBefore())
            giveRespawnItems(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        plugin.removeUserFromCache(uuid);
        GuiCreator.getOpenInventories().remove(uuid);
        BukkitRunnable r = ActionBar.getActionbars().remove(player.getUniqueId());
        if (r != null)
            r.cancel();
        if (CommandVanish.getVanishedPlayers().remove(uuid)) {
            CommandVanish.getInstance().vanish(player, false);
            event.setQuitMessage(null);
            plugin.getConfigManager().getData().set("vanished-players", CommandVanish.getVanishedPlayers().stream().map(UUID::toString).collect(Collectors.toList()));
            plugin.getConfigManager().saveConfig(ConfigManager.FileType.LANG);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        } else event.setQuitMessage(Lang.LEAVE_MESSAGE.get(player.getName()));
        if (Effects.getTrails().containsKey(uuid)) {
            new Effects(player).endTask();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (SimpleCommand.isPlayerFrozen(player.getUniqueId())) {
            event.setCancelled(true);
        } else if (Effects.getTrails().containsKey(player.getUniqueId()) && Effects.getTrails().get(player.getUniqueId()) == Effects.Trails.WALKTRAIL) {
            Effects effects = new Effects(player);
            effects.startWalkTrail();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getVehicle().getType() == EntityType.HORSE) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (vehicle.getType() == EntityType.BOAT) {
            vehicle.remove();
            ItemStack drop = new ItemStack(getBoat(((Boat) vehicle).getWoodType()));
            event.getExited().getLocation().getWorld().dropItem(event.getExited().getLocation(), drop);
        }
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Horse)
            event.setCancelled(false);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (CommandVanish.getVanishedPlayers().contains(event.getEntity().getUniqueId())
            && !plugin.getConfig().getBoolean("pickup-items-in-vanish")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (CommandVanish.getVanishedPlayers().contains(player.getUniqueId())
            && plugin.getConfig().getBoolean("disable-hunger-in-vanish")
            && event.getFoodLevel() < player.getFoodLevel()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (plugin.getConfig().getBoolean("teleport-to-spawn-on.respawn")) {
            event.getPlayer().setBedSpawnLocation(CommandSpawn.getLocation());
            event.setRespawnLocation(CommandSpawn.getLocation());
        }
        giveRespawnItems(event.getPlayer());
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (event.getDamager() instanceof Player) {
            if (!WEAPONS.contains(((Player) event.getDamager()).getInventory().getItemInMainHand().getType())) {
                event.setDamage(0);
            } else {
                Combat.pvp(player, (Player) event.getDamager());
            }
        } else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
            Combat.pvp(player, (Player) ((Arrow) event.getDamager()).getShooter());
        }
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (CommandFly.getFlyingPlayers().contains(player.getUniqueId())
            && event.getNewGameMode() == GameMode.SURVIVAL) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent event) {
        if (CommandVanish.getVanishedPlayers().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    private static void giveRespawnItems(Player player) {
        PlayerUtils.addOrDropFor(player, 0, new ItemStack(Material.APPLE, 16));
        PlayerUtils.addOrDropFor(player, 1, new ItemStack(Material.WOODEN_PICKAXE));
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
}