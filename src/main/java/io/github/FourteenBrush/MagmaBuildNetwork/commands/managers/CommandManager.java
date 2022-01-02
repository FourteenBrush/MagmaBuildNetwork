package io.github.FourteenBrush.MagmaBuildNetwork.commands.managers;

import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.config.InventorySerialisation;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.SafechestGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CommandManager {

    private final ConfigManager configManager;
    private final FileConfiguration dataFile;

    public CommandManager(ConfigManager configManager) {
        this.configManager = configManager;
        dataFile = configManager.getData();
    }

    public void startup() {
        // load safe chests
        if (Utils.isValidConfigurationSection(dataFile, "safe-chests")) {
            dataFile.getConfigurationSection("safe-chests").getKeys(false).forEach(key -> {
                ItemStack[] content = InventorySerialisation.itemStackArrayFromBase64(dataFile.getString("safe-chests." + key));
                SafechestGui.getMenus().put(UUID.fromString(key), content);
            });
        }
    }

    public void shutdown() {
        // add safe chests
        SafechestGui.getMenus().forEach((key, value) -> {
            String data = InventorySerialisation.itemStackArrayToBase64(value);
            dataFile.set("safe-chests." + key, data);
        });
        // save file
        configManager.saveConfig(ConfigManager.FileType.DATA);
    }
}
