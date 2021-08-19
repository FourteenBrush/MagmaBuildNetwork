package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.dependencies.LP;
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
        Player player = event.getPlayer();
        player.setDisplayName(LP.loadPrefixes().get(
                Main.getApi().getUserManager().getUser(player.getUniqueId()).getPrimaryGroup())
                .replaceAll("&", "§") + player.getDisplayName());
        player.setDisplayName(Main.getChat().getPlayerPrefix(player) + " " + player.getName());
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster) {
            Player p = event.getEntity().getKiller();
            if (p == null) return; // if mobs died of a natural dead return
            Random random = new Random();
            int amount = random.nextInt(10) + 10;
            Main.getEco().depositPlayer(p, amount);
            Utils.message(p, "§a+§b" + amount + " §3coins");
        }
    }
}