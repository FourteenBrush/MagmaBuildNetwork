package io.github.FourteenBrush.MagmaBuildNetwork;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class DataManager {

    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public DataManager() {
        //saves/initializes config
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (this.configFile == null) {
            this.configFile = new File(Main.getInstance().getDataFolder(), "config.yml");
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = Main.getInstance().getResource("config.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }
    public FileConfiguration getConfig() {
        if (this.dataConfig == null) {
            reloadConfig();
        }
        return this.dataConfig;
    }
    public void saveConfig() {
        if (dataConfig == null || this.configFile == null) {
            return;
        }
        try {
            this.getConfig().save(this.configFile);
        }
        catch (IOException e) {
            Main.getInstance().getLogger().log(Level.SEVERE, "Could not save config file to " + this.configFile, e);
        }
    }
    public void saveDefaultConfig() {
        if (this.configFile == null) {
            this.configFile = new File(Main.getInstance().getDataFolder(), "config.yml");
        }
        if (!this.configFile.exists()) {
            Main.getInstance().saveResource("config.yml", false);
        }
    }
}
