package io.github.FourteenBrush.MagmaBuildNetwork.library;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Combat extends LibraryProvider implements Listener {

    private static final Map<UUID, BukkitTask> pvp = new HashMap<>();

    public static void remove(UUID uuid) {
        if (pvp.containsKey(uuid)) {
            if (pvp.get(uuid).isSync())
                (pvp.get(uuid)).cancel();
            pvp.remove(uuid);
        }
    }

    private static void pvp(Player player) {
        UUID uuid = player.getUniqueId();
        if (pvp.containsKey(uuid) && pvp.get(uuid).isSync())
            pvp.get(uuid).cancel();
        pvp.put(uuid, (new BukkitRunnable() {
            public void run() {
                pvp.remove(uuid);
            }
        }).runTaskLater(plugin, 600L));
    }

    public static void pvp(Player player, Player player1) {
        pvp(player);
        pvp(player1);
    }

    public static Map<UUID, BukkitTask> getPvpList() {
        return pvp;
    }
}
