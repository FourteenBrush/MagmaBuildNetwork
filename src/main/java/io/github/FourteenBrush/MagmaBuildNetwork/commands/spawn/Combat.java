package io.github.FourteenBrush.MagmaBuildNetwork.commands.spawn;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.FourteenBrush.MagmaBuildNetwork.library.LibraryProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Combat extends LibraryProvider implements Listener {

    private static final Cache<UUID, Long> PVP_LIST = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();

    private static void pvp(Player player) {
        PVP_LIST.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public static void pvp(Player player, Player player1) {
        pvp(player);
        pvp(player1);
    }

    public static Cache<UUID, Long> getPvpList() {
        return PVP_LIST;
    }
}
