package io.github.FourteenBrush.MagmaBuildNetwork.player;

import io.github.FourteenBrush.MagmaBuildNetwork.player.profiles.ChatProfile;
import io.github.FourteenBrush.MagmaBuildNetwork.player.profiles.StatisticsProfile;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.UUID;

public class User {
    private final UUID uuid;
    private final BukkitRunnable actionbar;
    private final ChatProfile chatProfile;
    private final StatisticsProfile statisticsProfile;

    public User(UUID uuid, ChatProfile chatProfile, StatisticsProfile statisticsProfile) {
        this.uuid = uuid;
        this.actionbar = new Actionbar();
        this.chatProfile = chatProfile;
        this.statisticsProfile = statisticsProfile;
    }

    public UUID getUuid() {
        return uuid;
    }

    public ChatProfile getChatProfile() {
        return chatProfile;
    }

    public StatisticsProfile getStatisticsProfile() {
        return statisticsProfile;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void sendMessage(String message) {
        PlayerUtils.message(getPlayer(), message);
    }

    public void savePlayerData() {
        //: todo
    }

    public void logoutSafely() {
        actionbar.cancel();
        savePlayerData();
    }

    private class Actionbar extends BukkitRunnable {
        private final Player player;
        private final DecimalFormat df;

        public Actionbar() {
            this.player = getPlayer();
            df = new DecimalFormat("#.##");
        }

        @Override
        public void run() {
            String message = Utils.colorize("&a&lHP&r " + df.format(player.getHealth() * 5) + " / 100");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }
}
