package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;

public class VaultListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.setDisplayName(Main.getChat().getPlayerPrefix(p) + " " + p.getName());
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster) {
            Player p = event.getEntity().getKiller();
            if (p == null) return; // if mobs died of a natural dead return
            int amount = new Random().nextInt(10) + 10;
            Main.eco.depositPlayer(p, amount);
            Utils.message(p, "§2§l+ $" + amount);
        }
    }
}
