package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Effects {

    private static final Main plugin = Main.getInstance();
    private int taskID;
    private final Player player;

    private static final Map<UUID, Integer> TRAILS = new HashMap<>();
    private final UUID uuid;

    public Effects(Player player, UUID uuid) {
        this.player = player;
        this.uuid = uuid;
    }

    public void startTotem() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            double var = 0;
            Location loc, first, second;

            @Override
            public void run() {
                if (!hasID()) {
                    setID(taskID);
                }

                var += Math.PI / 16;

                loc = player.getLocation();
                first = loc.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                second = loc.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1, Math.sin(var + Math.PI));

                player.getWorld().spawnParticle(Particle.TOTEM, first, 0);
                player.getWorld().spawnParticle(Particle.TOTEM, second, 0);
            }
        }, 0, 1);
    }

    public void setID(int id) {
        TRAILS.put(uuid, id);
    }

    public int getID() {
        return TRAILS.get(uuid);
    }

    public boolean hasID() {
        return TRAILS.containsKey(uuid);
    }

    public void removeID() {
        TRAILS.remove(uuid);
    }

    public void endTask() {
        if (getID() == 1)
            return;
        Bukkit.getScheduler().cancelTask(getID());
    }

    public static boolean hasWalkTrail(UUID uuid) {
        return TRAILS.containsKey(uuid) && TRAILS.get(uuid) == 1;
        // ID 1 = walk trail
    }
}
