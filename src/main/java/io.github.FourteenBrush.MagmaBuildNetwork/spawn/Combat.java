package io.github.FourteenBrush.MagmaBuildNetwork.spawn;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandVanish;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class Combat implements Listener {

    private static final Main plugin = Main.getInstance();
    private static final HashMap<UUID, BukkitTask> pvp = new HashMap<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && CommandVanish.getVanishedPlayers().contains((event.getEntity()).getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        if (!(event.getEntity() instanceof Player) || !plugin.getConfig().getBoolean("disable_spawn_command_in_pvp"))
            return;
        Player p = (Player) event.getEntity();

        if (event.getDamager() instanceof Player) {
            pvp(p, (Player) event.getDamager());
        } else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
            pvp(p, (Player)((Arrow) event.getDamager()).getShooter());
        }
    }

    public static boolean containsKey(Player p) {
        return pvp.containsKey(p.getUniqueId());
    }

    public static void remove(UUID uuid) {
        if (pvp.containsKey(uuid)) {
            if (pvp.get(uuid).isSync())
                (pvp.get(uuid)).cancel();
            pvp.remove(uuid);
        }
    }

    private void pvp(final Player p) {
        UUID uuid = p.getUniqueId();
        if (pvp.containsKey(uuid) && pvp.get(uuid).isSync())
            pvp.get(uuid).cancel();
        pvp.put(uuid, (new BukkitRunnable() {
            public void run() {
                pvp.remove(uuid);
            }
        }).runTaskLater(plugin, 160L));
    }

    private void pvp(Player player, Player player1) {
        pvp(player);
        pvp(player1);
    }
}
