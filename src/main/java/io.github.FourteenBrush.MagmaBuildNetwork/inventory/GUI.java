package io.github.FourteenBrush.MagmaBuildNetwork.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface GUI  {

    Inventory getInventory();
    Inventory createInv();

    default void setItem(Inventory inv, ItemStack item, int index) {
        inv.setItem(index, item);
    }
}
