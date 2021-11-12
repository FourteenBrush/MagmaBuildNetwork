package io.github.FourteenBrush.MagmaBuildNetwork.library;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Combat extends LibraryProvider implements Listener {

    private static final Map<UUID, BukkitTask> PVP_LIST = new HashMap<>();

    public static void remove(UUID uuid) {
        if (PVP_LIST.containsKey(uuid)) {
            if (PVP_LIST.get(uuid).isSync())
                (PVP_LIST.get(uuid)).cancel();
            PVP_LIST.remove(uuid);
        }
    }

    private static void pvp(Player player) {
        UUID uuid = player.getUniqueId();
        if (PVP_LIST.containsKey(uuid) && PVP_LIST.get(uuid).isSync())
            PVP_LIST.get(uuid).cancel();
        PVP_LIST.put(uuid, (new BukkitRunnable() {
            public void run() {
                PVP_LIST.remove(uuid);
            }
        }).runTaskLater(plugin, 600L));
    }

    public static void pvp(Player player, Player player1) {
        pvp(player);
        pvp(player1);
    }

    public static Map<UUID, BukkitTask> getPvpList() {
        return PVP_LIST;
    }
}
