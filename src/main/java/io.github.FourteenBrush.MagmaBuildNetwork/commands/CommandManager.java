package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.NPC;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.UUID;

public abstract class CommandManager {

    private static final Main plugin = Main.getInstance();
    private static final FileConfiguration dataFile = ConfigManager.getDataConfig();

    @SuppressWarnings("unchecked")
    public static void onEnable() {
        if (dataFile.contains("safe_chests") && dataFile.getConfigurationSection("safe_chests").getKeys(false).size() > 0) {
            CommandSafechest.loadInventories();
        }
        if (dataFile.contains("vanished_players")) {
            CommandVanish.load((List<UUID>) dataFile.getList("vanished_players"));
        }
        if (plugin.getConfig().contains("npc_data")) {
            NPC.loadNPCIntoWorld();
        }
    }

    public static void onDisable() {
        if (!CommandSafechest.getMenus().isEmpty()) {
            CommandSafechest.saveInventories();
        }
        if (!CommandVanish.getVanishedPlayers().isEmpty()) {
            CommandVanish.save();
        }
    }
}
