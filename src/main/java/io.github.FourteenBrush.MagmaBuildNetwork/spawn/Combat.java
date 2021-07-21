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

public class Combat implements Listener {

    private static final Main plugin = Main.getInstance();
    private static final HashMap<Player, BukkitTask> pvp = new HashMap<>();

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
            pvp(p);
            pvp((Player) event.getDamager());
        } else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
            pvp(p);
            pvp((Player)((Arrow) event.getDamager()).getShooter());
        }
    }

    public static boolean containsKey(Player p) {
        return pvp.containsKey(p);
    }

    public static void remove(Player p) {
        if (pvp.containsKey(p)) {
            if (pvp.get(p).isSync())
                (pvp.get(p)).cancel();
            pvp.remove(p);
        }
    }

    private void pvp(final Player p) {
        if (pvp.containsKey(p) && pvp.get(p).isSync())
            pvp.get(p).cancel();
        pvp.put(p, (new BukkitRunnable() {
            public void run() {
                pvp.remove(p);
            }
        }).runTaskLater(plugin, 160L));
    }
}
