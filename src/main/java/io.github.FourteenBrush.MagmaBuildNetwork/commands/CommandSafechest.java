package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandSafechest extends BaseCommand {

    private static final Map<UUID, ItemStack[]> menus = new HashMap<>();

    @Override
    protected boolean execute(@NotNull String[] args) {

        if (isConsole) messageNoConsole();

        Inventory inv = Bukkit.createInventory(p, 45, p.getName() + "'s safechest");

        if (menus.containsKey(p.getUniqueId())) {
            inv.setContents(menus.get(p.getUniqueId()));
        }
        p.openInventory(inv);
        return true;
    }

    public static void saveInventories() {
        for (Map.Entry<UUID, ItemStack[]> entry : CommandSafechest.getMenus().entrySet()) {
            ConfigManager.getDataConfig().set("safe_chests." + entry.getKey(), entry.getValue());
        }
        ConfigManager.saveConfig(ConfigManager.FileType.DATA);
    }

    public static void loadInventories() {
        ConfigManager.getDataConfig().getConfigurationSection("safe_chests").getKeys(false).forEach(key -> {
            @SuppressWarnings("unchecked")
            ItemStack[] content = ((List<ItemStack>) ConfigManager.getDataConfig().get("safe_chests." + key)).toArray(new ItemStack[0]);
            menus.put(UUID.fromString(key), content);
        });
    }

    public static Map<UUID, ItemStack[]> getMenus() {
        return menus;
    }
}
