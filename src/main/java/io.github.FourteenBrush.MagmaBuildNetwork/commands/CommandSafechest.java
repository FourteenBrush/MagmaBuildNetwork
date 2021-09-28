package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.MessagesUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandSafechest extends AbstractCommand {

    private static final Map<UUID, ItemStack[]> menus = new HashMap<>();

    public CommandSafechest() {
        super("safechest", false);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (isConsole) return MessagesUtils.noConsole(sender);
        Inventory inv = Bukkit.createInventory(executor, 45, executor.getName() + "'s safechest");
        if (menus.containsKey(executor.getUniqueId())) {
            inv.setContents(menus.get(executor.getUniqueId()));
        }
        executor.openInventory(inv);
        return true;
    }

    public static void save() {
        for (Map.Entry<UUID, ItemStack[]> entry : CommandSafechest.getMenus().entrySet()) {
            ConfigManager.getData().set("safe_chests." + entry.getKey(), entry.getValue());
        }
        ConfigManager.saveConfig(ConfigManager.FileType.DATA);
    }

    public static void load() {
        ConfigManager.getData().getConfigurationSection("safe-chests").getKeys(false).forEach(key -> {
            @SuppressWarnings("unchecked")
            ItemStack[] content = ((List<ItemStack>) ConfigManager.getData().get("safe-chests." + key)).toArray(new ItemStack[0]);
            menus.put(UUID.fromString(key), content);
        });
    }

    public static Map<UUID, ItemStack[]> getMenus() {
        return menus;
    }
}
