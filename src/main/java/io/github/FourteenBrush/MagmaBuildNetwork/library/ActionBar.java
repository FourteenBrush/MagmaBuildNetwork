package io.github.FourteenBrush.MagmaBuildNetwork.library;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class ActionBar extends BukkitRunnable {

    private static final Map<UUID, BukkitRunnable> actionbars = new HashMap<>();
    private final Player player;

    public ActionBar(Player player) {
        this.player = player;
        actionbars.put(player.getUniqueId(), this);
    }

    @Override
    public void run() {
        DecimalFormat df = new DecimalFormat("#.#");
        String message = Utils.colorize("&a&lHP&r " + df.format(player.getHealth() * 5) + " / 100");
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public static Map<UUID, BukkitRunnable> getActionbars() {
        return actionbars;
    }
}
