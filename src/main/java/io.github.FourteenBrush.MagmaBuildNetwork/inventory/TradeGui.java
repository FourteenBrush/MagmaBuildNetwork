package io.github.FourteenBrush.MagmaBuildNetwork.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TradeGui implements InventoryHolder {

    private Inventory inv;

    public TradeGui() {
        inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "Trade");
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inv;
    }

    public void createInventory() {
        ItemStack item = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        int[] slots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 17, 18, 22, 26, 27, 31, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
        for (int i : slots) {
            inv.setItem(i, item);
        }
    }


}
