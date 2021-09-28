package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

public class ActionBar extends BukkitRunnable {

    private final Player player;

    public ActionBar(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        DecimalFormat df = new DecimalFormat("#.#");
        Utils.sendActionBar(player, "&a&lHP&r " + df.format(player.getHealth() * 5) + " / 100");
    }
}
