package io.github.FourteenBrush.MagmaBuildNetwork.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUI implements InventoryHolder {

    private static Inventory INV;
    private InventoryHolder holder = this;

    public void registerTrails() {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "" + ChatColor.BOLD+ "Trails");
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
        setInventory(inv);
    }

    public void registerTrade() {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "Trade");
        ItemStack item = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);

        int[] values = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 17, 18, 22, 26, 27, 31, 35,
        36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
        for (int i : values) {
            inv.setItem(i, item);
        }
        setInventory(inv);
    }

    public Inventory getInventory() {
        return INV;
    }

    private void setInventory(Inventory inv) {
        INV = inv;
    }

    public void openInventory(Player player) {
        player.openInventory(INV);
    }
}
