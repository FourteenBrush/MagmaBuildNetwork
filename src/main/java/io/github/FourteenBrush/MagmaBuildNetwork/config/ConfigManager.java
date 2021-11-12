package io.github.FourteenBrush.MagmaBuildNetwork.config;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final Main plugin;
    private FileConfiguration config, data, homes, lang;
    private File configFile, dataFile, homesFile, langFile;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
    }

    public void startup() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            Utils.logError("Could not create files");
        } else {
            configFile = new File(plugin.getDataFolder(), "config.yml");
            if (!configFile.exists()) plugin.saveResource("config.yml", false);
            config = YamlConfiguration.loadConfiguration(configFile);

            dataFile = new File(plugin.getDataFolder(), "data.yml");
            if (!dataFile.exists()) plugin.saveResource("data.yml", false);
            data = YamlConfiguration.loadConfiguration(dataFile);

            homesFile = new File(plugin.getDataFolder(), "homes.yml");
            if (!homesFile.exists()) plugin.saveResource("homes.yml", false);
            homes = YamlConfiguration.loadConfiguration(homesFile);

            langFile = new File(plugin.getDataFolder(), "lang.yml");
            if (!langFile.exists()) plugin.saveResource("lang.yml", false);
            lang = YamlConfiguration.loadConfiguration(langFile);
        }
    }

    private FileConfiguration createAndLoad(File file, String name) {
        if (!file.exists()) file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) plugin.saveResource(name, false);
        return YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig(FileType file) {
        try {
            switch (file) {
                case CONFIG:
                    config.save(configFile);
                    break;
                case DATA:
                    data.save(dataFile);
                    break;
                case HOMES:
                    homes.save(homesFile);
                    break;
                case LANG:
                    lang.save(langFile);
                    break;
            }
        } catch (IOException e) {
            Utils.logError("Could not save data to file " + file.name());
            e.printStackTrace();
        }
    }

    public FileConfiguration getData() {
        if (data == null) startup();
        return data;
    }

    public FileConfiguration getHomes() {
        if (homes == null) startup();
        return homes;
    }

    public FileConfiguration getLang() {
        if (lang == null) startup();
        return lang;
    }

    public enum FileType {
        CONFIG, DATA, HOMES, LANG
    }
}