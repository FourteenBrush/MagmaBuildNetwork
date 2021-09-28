package io.github.FourteenBrush.MagmaBuildNetwork.particles;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EffectsUtils {

    private static final Main plugin = Main.getPlugin(Main.class);
    private static final Map<UUID, Trails> trails = new HashMap<>();
    private final Player player;
    private boolean cancel;

    public EffectsUtils(Player player) {
        this.player = player;
    }

    public void startTotem() {
        trails.put(player.getUniqueId(), Trails.TOTEMTRAIL);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (cancel) this.cancel();
                double var = 0;
                Location loc, first, second;

                var += Math.PI / 16;

                loc = player.getLocation();
                first = loc.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                second = loc.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1, Math.sin(var + Math.PI));

                player.getWorld().spawnParticle(Particle.TOTEM, first, 0);
                player.getWorld().spawnParticle(Particle.TOTEM, second, 0);
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 2L);
    }

    public void startWalkTrail() {
        Random r = ThreadLocalRandom.current();
        for (int i = 0; i < 5; i++) {
            player.getWorld().spawnParticle(Particle.CRIT_MAGIC, player.getLocation().add(
                    r.nextDouble() * 0.5, r.nextDouble() * 0.5, r.nextDouble() * .5), 0);
        }
        for (int i = 0; i < 5; i++) {
            player.getWorld().spawnParticle(Particle.CRIT_MAGIC, player.getLocation().add(
                    -1 * (r.nextDouble() * 0.5), r.nextDouble() * 0.5, (r.nextDouble() * .5) * -1), 0);
        }
    }

    public void startFireworksTrail() {
        trails.put(player.getUniqueId(), Trails.FIREWORKSTRAIL);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (cancel) this.cancel();
                player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 3, 0, 0.5, 0, 0L, 5L);
            }
        }.runTaskTimerAsynchronously(plugin, 1L, 5L);
    }

    public void startLavaPopTrail() {
        Location loc = player.getLocation().add(0.0D, 0.5D, 0.0D);
        for (int i = 0; i < 15; i++) {
            float x = (float) Math.random();
            float z = (float) Math.random();
            player.getWorld().spawnParticle(Particle.LAVA, loc, (int) x, -0.5F, z, 1.0F, 1);
        }
    }

    public static Map<UUID, Trails> getTrails() {
        return trails;
    }

    public void endTask() {
        cancel = true;
        trails.remove(player.getUniqueId());
    }

    public enum Trails {
        WALKTRAIL, FIREWORKSTRAIL, LAVAPOPTRAIL, TOTEMTRAIL
    }
}
