package io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.library.ActionBar;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class User {

    private final UUID uuid;
    private Channel mainChannel;
    private final MBNPlugin plugin;
    private final Set<Channel> channels;
    private final Set<Channel> bannedChannels;

    public User(UUID uuid, MBNPlugin plugin) {
        this.uuid = uuid;
        this.plugin = plugin;
        channels = new HashSet<>();
        bannedChannels = new HashSet<>();
        Player player = getPlayer();
        Team team = getTeam(player.getScoreboard(), "mbn");
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.addEntry(player.getName());
        loadPlayerData();
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
        channel.addPlayer(this);
        channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        if (channel == mainChannel) {
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

    public Set<Channel> getChannels() {
        return channels;
    }

    public void sendMessage(String message) {
        Player player = getPlayer();
        if (player != null)
            PlayerUtils.message(player, message);
    }

    public void setPrefix() {
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Vault")) return;
        Player player = getPlayer();
        Team team = getTeam(player.getScoreboard(), player.getName());
        team.addEntry(player.getName());
        team.setPrefix(plugin.getChat().getPlayerPrefix(player));
    }

    private Team getTeam(Scoreboard scoreboard, String name) {
        return scoreboard.getTeam(name) == null ? scoreboard.registerNewTeam("mbn") : scoreboard.getTeam(name);
    }

    public void sendActionbar() {
        BukkitRunnable runnable = new ActionBar(getPlayer());
        runnable.runTaskTimerAsynchronously(plugin, 1L, 3L);
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
                    addChannel(channel);
            });
        }
    }

    public void savePlayerData() {
        File file = new File(plugin.getDataFolder() + File.separator + "players" + File.separator + uuid + ".yml");
        if (createFileIfNotExists(file)) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<String> list = new ArrayList<>();
        yaml.set("playername", getPlayer().getName());
        channels.forEach(channel -> list.add(channel.getName()));
        if (mainChannel != null)
            yaml.set("channel", mainChannel.getName());
        yaml.set("channels", list);
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   private boolean createFileIfNotExists(File file) {
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            Utils.logError("Could not create files");
        if (!file.exists()) {
            try {
                return !file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        }
        return false;
   }
}
