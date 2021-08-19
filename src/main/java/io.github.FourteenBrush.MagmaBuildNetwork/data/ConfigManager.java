package io.github.FourteenBrush.MagmaBuildNetwork.data;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private static final Main plugin = Main.getInstance();
    private static FileConfiguration config, data, messages, homes;
    private static File configFile, dataFile, messagesFile, homesFile;

    public static void createFiles() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("data.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(dataFile);

        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);

        homesFile = new File(plugin.getDataFolder(), "homes.yml");
        if (!homesFile.exists()) {
            plugin.saveResource("homes.yml", false);
        }
        homes = YamlConfiguration.loadConfiguration(homesFile);
    }

    public static void saveConfig() {
        try {
            config.save(configFile);
            data.save(dataFile);
            messages.save(messagesFile);
            homes.save(homesFile);
        } catch (IOException e) {
            Utils.logError("Could not save data to config file");
            e.printStackTrace();
        }
    }

    public static void saveConfig(FileType file) {
        try {
            switch (file) {
                case CONFIG:
                    config.save(configFile);
                    break;
                case DATA:
                    data.save(dataFile);
                    break;
                case MESSAGES:
                    messages.save(messagesFile);
                    break;
                case HOMES:
                    homes.save(homesFile);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getConfig() {
        if (config == null) {
            createFiles();
        }
        return config;
    }

    public static FileConfiguration getDataConfig() {
        if (data == null) {
            createFiles();
        }
        return data;
    }

    public static FileConfiguration getMessagesConfig() {
        if (messages == null) {
            createFiles();
        }
        return messages;
    }

    public static FileConfiguration getHomes() {
        if (homes == null) {
            createFiles();
        }
        return homes;
    }

    public enum FileType {
        CONFIG, DATA, MESSAGES, HOMES
    }
}