package io.github.FourteenBrush.MagmaBuildNetwork.commands.managers;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.*;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.config.InventorySerialisation;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.SafechestGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Instances;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandManager {

    private final Main plugin;
    private final FileConfiguration dataFile;

    public CommandManager() {
        plugin = Main.getPlugin(Main.class);
        dataFile = plugin.getConfigManager().getData();
        plugin.getCommand("ban").setExecutor(new CommandBan());
        plugin.getCommand("debug").setExecutor(new CommandDebug());
        plugin.getCommand("fly").setExecutor(Instances.COMMAND_FLY);
        plugin.getCommand("home").setExecutor(new CommandHome());
        plugin.getCommand("lock").setExecutor(new CommandLock());
        plugin.getCommand("magmabuildnetwork").setExecutor(new CommandMagmabuildnetwork());
        plugin.getCommand("spawn").setExecutor(new CommandSpawn());
        plugin.getCommand("trade").setExecutor(Instances.COMMAND_TRADE);
        plugin.getCommand("vanish").setExecutor(Instances.COMMAND_VANISH);
        plugin.getCommand("maintenance").setExecutor(new CommandMaintenance());
    }

    public void startup() {
        // load safe chests
        if (Utils.isValidConfigurationSection(dataFile, "safe-chests")) {
            dataFile.getConfigurationSection("safe-chests").getKeys(false).forEach(key -> {
                ItemStack[] content = InventorySerialisation.itemStackArrayFromBase64("safe-chests" + key);
                SafechestGui.getMenus().put(UUID.fromString(key), content);
            });
        }
        // setup spawn
        CommandSpawn.setup();
    }

    public void shutdown() {
        // add safe chests
        // dataFile.set("safe-chests", SafechestGui.getMenus().values().stream().map(InventorySerialisation::itemStackArrayToBase64).collect(Collectors.toList()));
        SafechestGui.getMenus().forEach((key, value) -> dataFile.set("safe-chests." + key, InventorySerialisation.itemStackArrayToBase64(value)));
        // add vanished players
        dataFile.set("vanished-players", CommandVanish.getVanishedPlayers().stream().map(UUID::toString).collect(Collectors.toList()));
        // save file
        plugin.getConfigManager().saveConfig(ConfigManager.FileType.DATA);
    }

    public boolean checkVanishedPlayer() {
        if (Utils.isValidConfigurationSection(dataFile, "vanished-players")) {
            dataFile.getConfigurationSection("vanished-players").getKeys(false).forEach(key ->
                    Instances.COMMAND_VANISH.vanish(Bukkit.getPlayer(UUID.fromString(key)), true));
            return true;
        }
        return false;
    }
}
