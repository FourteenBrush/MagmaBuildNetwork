package io.github.FourteenBrush.MagmaBuildNetwork;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Debug {

    public static Inventory createInv() {

        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "" + ChatColor.BOLD + "Sellect Team");

        ItemStack item = new ItemStack(Material.BLUE_CONCRETE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.DARK_BLUE + "BLUE TEAM");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "Click to join team!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(0, item);

        item.setType(Material.RED_CONCRETE);
        meta.setDisplayName(ChatColor.DARK_RED + "RED TEAM");
        item.setItemMeta(meta);
        inv.setItem(1, item);

        item.setType(Material.LIME_CONCRETE);
        meta.setDisplayName(ChatColor.GREEN + "LIME TEAM");
        item.setItemMeta(meta);
        inv.setItem(2, item);

        item.setType(Material.ORANGE_CONCRETE);
        meta.setDisplayName(ChatColor.GOLD + "ORANGE TEAM");
        item.setItemMeta(meta);
        inv.setItem(3, item);

        item.setType(Material.PURPLE_CONCRETE);
        meta.setDisplayName(ChatColor.DARK_PURPLE + "PURPLE TEAM");
        item.setItemMeta(meta);
        inv.setItem(4, item);

        item.setType(Material.CYAN_CONCRETE);
        meta.setDisplayName(ChatColor.BLUE + "CYAN TEAM");
        item.setItemMeta(meta);
        inv.setItem(5, item);

        item.setType(Material.BLACK_CONCRETE);
        meta.setDisplayName(ChatColor.DARK_GRAY + "BLACK TEAM");
        item.setItemMeta(meta);
        inv.setItem(6, item);

        item.setType(Material.BARRIER);
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Close Menue");
        lore.clear();
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(8, item);


        return inv;
    }
}
