package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class Home implements ConfigurationSerializable {

    private final String name;
    private final Location location;
    private final UUID owner;
    private static final FileConfiguration homesFile = ConfigManager.getHomes();
    private static final List<Home> buffer = new ArrayList<>();

    public Home(String name, UUID owner, Location location) {
        this.name = name;
        this.location = location;
        this.owner = owner;
        buffer.add(this);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        final Map<String, Object> data = new HashMap<>();
        data.put("name", this.name);
        data.put("owner", this.owner);
        data.put("location", this.location);
        return data;
    }

    public static Home deserialize(final Map<String, Object> map) {
        return new Home((String) map.get("name"), (UUID) map.get("owner"), (Location) map.get("location"));
    }

    public void savePlayerHomes(UUID uuid) {
        homesFile.set("homes", getHomes(uuid));
        ConfigManager.saveConfig(ConfigManager.FileType.HOMES);
    }

    @SuppressWarnings("unchecked")
    public List<Home> loadHomes() {
        return (List<Home>) homesFile.getList("homes");
    }

    private List<Home> getHomes(UUID uuid) {
        if (homesFile.getConfigurationSection("homes." + uuid).getKeys(false).size() > 0) {
            List<Home> homes = new ArrayList<>();
            homesFile.getConfigurationSection("homes." + uuid).getKeys(false).forEach(home -> {
                //homes.add();
            });
            return homes;
        }
        return null;
    }


    private boolean hasHome(Player player, String name) {
        return homesFile.contains("homes." + player.getUniqueId() + "." + name);
    }

    private boolean hasHome(Player player) {
        return homesFile.getConfigurationSection("homes." + player.getUniqueId()).getKeys(false).size() > 0;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getOwner() {
        return owner;
    }

}
