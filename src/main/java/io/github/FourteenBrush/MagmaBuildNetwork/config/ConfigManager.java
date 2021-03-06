package io.github.FourteenBrush.MagmaBuildNetwork.config;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final MBNPlugin plugin;
    private FileConfiguration data, homes, lang;
    private File dataFile, homesFile, langFile;

    public ConfigManager(MBNPlugin plugin) {
        this.plugin = plugin;
    }

    public void startup() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            Logger.ERROR.log("Could not create files!");
        } else {
            plugin.saveDefaultConfig();

            langFile = new File(plugin.getDataFolder(), "lang.yml");
            if (!langFile.exists()) plugin.saveResource("lang.yml", false);
            lang = YamlConfiguration.loadConfiguration(langFile);

            try {
                dataFile = new File(plugin.getDataFolder(), "data.yml");
                if (!dataFile.exists()) dataFile.createNewFile();
                data = YamlConfiguration.loadConfiguration(dataFile);

                homesFile = new File(plugin.getDataFolder(), "homes.yml");
                if (!homesFile.exists()) homesFile.createNewFile();
                homes = YamlConfiguration.loadConfiguration(homesFile);
            } catch (IOException e) {
                Logger.ERROR.log("Something went wrong when creating config files: ");
                e.printStackTrace();
            }
        }
    }

    public boolean createNewFileSucceeded(File file) {
        if (!file.exists()) {
            file.mkdirs();
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void saveConfig(FileType file) {
        try {
            switch (file) {
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
            Logger.ERROR.log("Could not save data to file " + file.name().toLowerCase());
            e.printStackTrace();
        }
    }

    public FileConfiguration getData() {
        if (data == null)
            startup();
        return data;
    }

    public FileConfiguration getHomes() {
        if (homes == null)
            startup();
        return homes;
    }

    public FileConfiguration getLang() {
        if (lang == null)
            startup();
        return lang;
    }

    public enum FileType {
        DATA, HOMES, LANG
    }
}