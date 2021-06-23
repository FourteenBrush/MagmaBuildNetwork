package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUI {

    private static Inventory INV;

    public void register() {
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
