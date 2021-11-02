package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandSpawn;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandVanish;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.SimpleCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.GuiCreator;
import io.github.FourteenBrush.MagmaBuildNetwork.library.Combat;
import io.github.FourteenBrush.MagmaBuildNetwork.library.ActionBar;
import io.github.FourteenBrush.MagmaBuildNetwork.library.Effects;
import io.github.FourteenBrush.MagmaBuildNetwork.library.NoPush;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerListener implements Listener {

    private final Main plugin = Main.getPlugin(Main.class);
    private static final Set<Material> WEAPONS = EnumSet.of(
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD
    );

    // todo
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.KICK_BANNED) {
            // event.setKickMessage(MessagesUtils.messageBanned(CommandBan.getBanReason()));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        NoPush.setCantPush(player);
        plugin.getCommandManager().checkVanishedPlayer();
        if (plugin.getCommandManager().checkVanishedPlayer()) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (!CommandVanish.getVanishedPlayers().contains(p.getUniqueId()))
                    p.hidePlayer(plugin, player);
            });
            event.setJoinMessage(null);
        } else event.setJoinMessage(Utils.colorize("&7[&a&l+&7] &b" + player.getName() + " &7joined the server."));
        Bukkit.getScheduler().runTaskLater(plugin, () -> PlayerUtils.message(player, "&aWelcome to the server &b " + player.getName()), 100L);
        new ActionBar(player).runTaskTimerAsynchronously(plugin, 1L, 6L);
        if (!player.hasPlayedBefore())
            giveRespawnItems(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        GuiCreator.getOpenInventories().remove(uuid);
        Combat.remove(uuid);
        event.setQuitMessage(CommandVanish.getVanishedPlayers().contains(uuid) ? null : Utils.colorize("&7[&c&l-&7] &b" + player.getName() + " &7left the server."));
        Effects d = new Effects(player);
        if (Effects.getTrails().containsKey(uuid))
            d.endTask();
        if (CommandVanish.getVanishedPlayers().contains(uuid)) {
            plugin.getConfigManager().getData().set("vanished-players", CommandVanish.getVanishedPlayers().stream().map(UUID::toString).collect(Collectors.toList()));
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (SimpleCommand.isPlayerFrozen(uuid) && (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockY() != event.getFrom().getBlockY() || event.getTo().getBlockZ() != event.getFrom().getBlockZ())) {
            event.setCancelled(true);
        } else if (Effects.getTrails().containsKey(uuid) && Effects.getTrails().get(uuid) == Effects.Trails.WALKTRAIL) {
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Horse)
            event.setCancelled(false); // override worldguard
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
            event.setRespawnLocation(CommandSpawn.getLocation());
            event.getPlayer().setBedSpawnLocation(CommandSpawn.getLocation());
        }
        giveRespawnItems(event.getPlayer());
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (event.getDamager() instanceof Player) {
            if (!WEAPONS.contains(((Player) event.getDamager()).getInventory().getItemInMainHand().getType())) {
                event.setDamage(0.0);
            } else {
                Combat.pvp(player, (Player) event.getDamager());
            }
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
        if (event.getNewGameMode() == GameMode.SURVIVAL
            && CommandVanish.getVanishedPlayers().contains(player.getUniqueId())) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent event) {
        if (CommandVanish.getVanishedPlayers().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    private static void giveRespawnItems(Player player) {
        PlayerUtils.addAtOrDrop(player, 0, new ItemStack(Material.APPLE, 16));
        PlayerUtils.addAtOrDrop(player, 1, new ItemStack(Material.WOODEN_AXE));
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