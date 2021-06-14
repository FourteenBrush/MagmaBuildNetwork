package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;

public class EconomyListener implements Listener {

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster) {
            Player player = event.getEntity().getKiller();
            if (player == null) // if mobs died of a natural dead return
                return;
            Random r = new Random();
            int amount = r.nextInt(10) + 10;
            Main.getInstance().eco.depositPlayer(player, amount);
            player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "+ $" + amount);
        }
    }
}
