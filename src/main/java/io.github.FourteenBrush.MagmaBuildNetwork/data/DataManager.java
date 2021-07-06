package io.github.FourteenBrush.MagmaBuildNetwork.data;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class DataManager {

    private final Main plugin = Main.getInstance();
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public DataManager() {
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        dataConfig = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultStream = plugin.getResource("config.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataConfig.setDefaults(defaultConfig);
        }
    }
    public FileConfiguration getConfig() {
        if (dataConfig == null) {
            reloadConfig();
        }
        return dataConfig;
    }
    public void saveConfig() {
        if (dataConfig == null || configFile == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        }
        catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config file to " + this.configFile, e);
        }
    }
    public void saveDefaultConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        if (!this.configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
    }
}
