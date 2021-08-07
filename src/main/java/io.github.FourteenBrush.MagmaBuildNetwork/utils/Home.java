package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Home {

    private final String name;
    private final Location location;
    private final UUID homeID;
    private final FileConfiguration homesFile = ConfigManager.getHomes();

    public Home(String name, Location location) {
        this.name = name;
        this.location = location;
        homeID = UUID.randomUUID();
    }

    public void createHome(Player player) { // on disable
        String data = "homes." + player.getUniqueId() + "." + name;
        homesFile.set(data + ".playerName", player.getName());
        homesFile.set(data + ".location", location);
        ConfigManager.saveConfig("homes");
    }

    public void removeHome(Player player, String name) {
        if (hasHome(player, name)) {
            homesFile.set("homes." + player.getUniqueId() + "." + name, null);
            ConfigManager.saveConfig("homes");
            Utils.message(player, "§aSuccessfully removed the home " + name);
            return;
        }
        Utils.message(player, new String[]{"§cHome not found!", "§cYou can use /home list to see all your homes"});
    }

    public void getHomes(Player player) {
        if (hasHome(player)) {
            List<String> homes = new ArrayList<>(homesFile.getConfigurationSection("homes." + player.getUniqueId()).getKeys(false));
            Utils.message(player, "§f--- §9Homes §f---");
            homes.forEach(home -> {
                Utils.message(player, home);
                Utils.message(player, homesFile.getConfigurationSection("homes." + player.getUniqueId() + "." + home).getValues(true).toString());
            });
            return;
        }
        Utils.message(player, "§cYou have no homes");
    }

    private boolean hasHome(Player player, String name) {
        return homesFile.contains("homes." + player.getUniqueId() + "." + name);
    }

    private boolean hasHome(Player player) {
        return homesFile.contains("homes." + player.getUniqueId());
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getHomeID() {
        return homeID;
    }
}
