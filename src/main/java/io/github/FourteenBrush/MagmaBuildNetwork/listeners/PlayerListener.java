package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandFly;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandSpawn;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandVanish;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.SimpleCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.GuiCreator;
import io.github.FourteenBrush.MagmaBuildNetwork.user.User;
import io.github.FourteenBrush.MagmaBuildNetwork.user.UserManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Keys;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.ServerMode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final MBNPlugin plugin;
    private final UserManager userManager;
    private final Set<Material> WEAPONS = EnumSet.of(
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD
    );

    public PlayerListener(MBNPlugin plugin) {
        this.plugin = plugin;
        userManager = plugin.getUserManager();
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        ServerMode serverMode = ServerMode.of(plugin.getConfig().getString("server-mode"));
        if (serverMode != null && !event.getPlayer().hasPermission(serverMode.getPermission())) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, serverMode.getMessage());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> PlayerUtils.message(player, "&aWelcome to the server &b " + player.getName()), 10L);
        if (!player.hasPlayedBefore()) {
            giveRespawnItems(player);
            userManager.createUser(player.getUniqueId());
        } else if (player.getPersistentDataContainer().getOrDefault(Keys.VANISHED, PersistentDataType.BYTE, (byte)0) == 1) {
            CommandVanish.getInstance().vanish(player, true);
            event.setJoinMessage(null);
        } else event.setJoinMessage(Lang.JOIN_MESSAGE.get(player.getName()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        User user = userManager.getUser(uuid);
        user.logoutSafely();
        userManager.removeFromCacheAndSave(user);
        GuiCreator.getOpenInventories().remove(uuid);
        if (CommandVanish.getVanishedPlayers().remove(uuid)) {
            event.setQuitMessage(null);
            event.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
        } else event.setQuitMessage(Lang.LEAVE_MESSAGE.get(event.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (SimpleCommand.isPlayerFrozen(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        // todo
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
            event.getPlayer().setBedSpawnLocation(CommandSpawn.getInstance().getLocation());
            event.setRespawnLocation(CommandSpawn.getInstance().getLocation());
        }
        giveRespawnItems(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player && projectile.getShooter() != event.getEntity()) {
                CommandSpawn.getInstance().pvp(((Player) projectile.getShooter()).getUniqueId());
            }
        } else if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            if (WEAPONS.contains(damager.getInventory().getItemInMainHand().getType())) {
                CommandSpawn.getInstance().pvp(damager.getUniqueId());
            } else {
                event.setDamage(0);
            }
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

    private void giveRespawnItems(Player player) {
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