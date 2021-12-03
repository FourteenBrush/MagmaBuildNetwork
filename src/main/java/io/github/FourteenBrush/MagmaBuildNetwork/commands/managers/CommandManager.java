package io.github.FourteenBrush.MagmaBuildNetwork.commands.managers;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.*;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.spawn.CommandSpawn;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.config.InventorySerialisation;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.SafechestGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandManager {

    private final MBNPlugin plugin;
    private final FileConfiguration dataFile;

    public CommandManager(MBNPlugin plugin) {
        this.plugin = plugin;
        dataFile = plugin.getConfigManager().getData();
    }

    public void startup() {
        // load safe chests
        if (Utils.isValidConfigurationSection(dataFile, "safe-chests")) {
            dataFile.getConfigurationSection("safe-chests").getKeys(false).forEach(key -> {
                ItemStack[] content = InventorySerialisation.itemStackArrayFromBase64(dataFile.getString("safe-chests." + key));
                SafechestGui.getMenus().put(UUID.fromString(key), content);
            });
        }
        // setup spawn
        CommandSpawn.setup();
    }

    public void shutdown() {
        // add vanished players
        dataFile.set("vanished-players", CommandVanish.getVanishedPlayers().stream().map(UUID::toString).collect(Collectors.toList()));
        // add safe chests
        SafechestGui.getMenus().forEach((key, value) -> {
            String data = InventorySerialisation.itemStackArrayToBase64(value);
            dataFile.set("safe-chests." + key, data);
        });
        // save file
        plugin.getConfigManager().saveConfig(ConfigManager.FileType.DATA);
    }

    public boolean checkVanishedPlayer() {
        List<String> list = dataFile.getStringList("vanished-players");
        if (list.isEmpty()) return false;
        list.forEach(string -> {
            Player player = Bukkit.getPlayer(UUID.fromString(string));
            if (player != null)
                CommandVanish.getInstance().vanish(player, true);
        });
        return true;
    }
}
