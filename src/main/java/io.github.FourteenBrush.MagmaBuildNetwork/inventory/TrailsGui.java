package io.github.FourteenBrush.MagmaBuildNetwork.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrailsGui extends GUI implements InventoryHolder {

    private Inventory inv;

    public TrailsGui() {
        inv = Bukkit.createInventory(this, 9, ChatColor.GOLD + "" + ChatColor.BOLD +"Trails");
    }

    public Inventory createInv() {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Totem Trail");
        item.setItemMeta(meta);
        inv.setItem(3, item);

        item = new ItemStack(Material.CAMPFIRE);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Fire Trail");
        item.setItemMeta(meta);
        inv.setItem(5, item);

        item = new ItemStack(Material.BARRIER);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Disable Trails");
        item.setItemMeta(meta);
        inv.setItem(8, item);

        return inv;
    }
}
