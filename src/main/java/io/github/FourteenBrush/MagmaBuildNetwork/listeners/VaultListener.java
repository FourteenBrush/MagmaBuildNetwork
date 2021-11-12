package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;

public class VaultListener implements Listener {

    private final Main plugin;

    public VaultListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setDisplayName(Utils.colorize(plugin.getChat().getPlayerPrefix(player) + " " + player.getName()));
        player.setCustomNameVisible(true);
        Utils.logDebug(plugin.getChat().getPlayerPrefix(player) + " " + player.getName()); // todo
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster) {
            Player player = event.getEntity().getKiller();
            if (player == null) return;
            int amount = new Random().nextInt(10) + 10;
            plugin.getEco().depositPlayer(player, amount);
            PlayerUtils.message(player, "&a+&b" + amount + " &3coins");
        }
    }
}
