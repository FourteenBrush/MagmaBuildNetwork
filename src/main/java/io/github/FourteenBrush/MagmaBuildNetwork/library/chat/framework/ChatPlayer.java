package io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatPlayer {

    private final UUID uuid;
    private Channel mainChannel;
    private final Main plugin;
    private final List<Channel> channels;
    private final List<Channel> bannedChannels;

    public ChatPlayer(Main plugin, UUID uuid) {
        this.uuid = uuid;
        this.plugin = plugin;
        channels = new ArrayList<>();
        bannedChannels = new ArrayList<>();
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Channel getMainChannel() {
        return mainChannel;
    }

    public void setMainChannel(Channel mainChannel) {
        this.mainChannel = mainChannel;
    }

    public void addChannel(Channel channel) {
        forceAddChannel(channel);
    }

    public void forceAddChannel(Channel channel) {
        channel.forceAddPlayer(this);
        if (!channels.contains(channel))
            channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        if (channel.equals(mainChannel)) {
            mainChannel = null;
        }
        channel.removePlayer(this);
        channels.remove(channel);
    }

    public void banFromChannel(Channel channel) {
        removeChannel(channel);
        bannedChannels.add(channel);
    }

    public boolean isBannedFromChannel(Channel channel) {
        return bannedChannels.contains(channel);
    }

    public void unbanFromChannel(Channel channel) {
        bannedChannels.remove(channel);
    }

    public boolean isInChannel(Channel channel) {
        return channels.contains(channel);
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void sendMessage(String message) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.isOnline())
            PlayerUtils.message(offlinePlayer.getPlayer(), message);
    }

    public void savePlayerData() {
        File file = new File(plugin.getDataFolder() + File.separator + "players" + File.separator + uuid + ".yml");
        if (createFileIfNotExists(file)) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<String> list = new ArrayList<>();
        channels.forEach(channel -> list.add(channel.getName()));
        yaml.set("channel", mainChannel.getName());
        yaml.set("channels", list);
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPlayerData() {
        File file = new File(plugin.getDataFolder() + File.separator + "players" + File.separator + uuid + ".yml");
        if (createFileIfNotExists(file)) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        if (yaml.contains("channel")) {
            Channel channel = plugin.getChannelManager().getChannel(yaml.getString("channel"));
            if (channel != null)
                mainChannel = channel;
        }
        if (yaml.contains("channels") && !yaml.getStringList("channels").isEmpty()) {
            yaml.getStringList("channels").forEach(name -> {
                Channel channel = plugin.getChannelManager().getChannel(name);
                if (channel != null)
                    forceAddChannel(channel);
            });
        }
    }

   private boolean createFileIfNotExists(File file) {
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        }
        return false;
   }
}
