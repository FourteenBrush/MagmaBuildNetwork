package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Home;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class CommandManager {

    public static void onEnable() {
        final FileConfiguration dataFile = ConfigManager.getData();
        if (dataFile.isConfigurationSection("safe-chests")) {
            CommandSafechest.load();
        }
        if (dataFile.contains("vanished-players")) {
            new CommandVanish().load(dataFile.getStringList("vanished-players"));
        }
    }

    public static void onDisable() {
        if (!CommandSafechest.getMenus().isEmpty()) {
            CommandSafechest.save();
        }
        if (!CommandVanish.getVanishedPlayers().isEmpty()) {
            CommandVanish.save();
        }
        if (!Home.getBuffer().isEmpty())
            for (Home h: Home.getBuffer()) {
                h.savePlayerHomes(h.getOwner());
            }
    }
}
