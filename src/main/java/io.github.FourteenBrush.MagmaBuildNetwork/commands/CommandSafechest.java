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

        if (isConsole) return true;

        if (menus.containsKey(p.getUniqueId())) {
            Inventory inv = Bukkit.createInventory(p, 54, p.getName() + "'s safechest");
            inv.setContents(menus.get(p.getUniqueId()));
            p.openInventory(inv);
            return true;
        }
        Inventory inv = Bukkit.createInventory(p, 54, p.getName() + "'s safechest");
        p.openInventory(inv);
        return true;
    }

    public static void saveInventories() {
        for (Map.Entry<UUID, ItemStack[]> entry : CommandSafechest.getMenus().entrySet()) {
            ConfigManager.getDataConfig().set("safe_chests." + entry.getKey(), entry.getValue());
        }
        ConfigManager.saveConfig();
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
