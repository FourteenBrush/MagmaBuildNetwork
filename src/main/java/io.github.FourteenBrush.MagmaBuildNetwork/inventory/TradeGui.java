package io.github.FourteenBrush.MagmaBuildNetwork.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class TradeGui extends GUI implements InventoryHolder {

    private Inventory inv;

    public TradeGui() {
        inv = Bukkit.createInventory(this, 54, ChatColor.DARK_GREEN + "Trade");
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inv;
    }

    public Inventory createInv() {
        // WHITE STAINED GLASS
        ItemStack item = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        int[] slots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 17, 18, 22, 26, 27, 31, 35,
                36, 40, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
        for (int i : slots) {
            inv.setItem(i, item);
        }
        // RED DYE
        item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Your opponent is NOT ready");
        item.setItemMeta(meta);
        inv.setItem(41, item);
        // LIME WOOL
        item = new ItemStack(Material.LIME_WOOL);
        meta.setDisplayName(ChatColor.DARK_GREEN + "Change to ready");
        List<String> lore = Arrays.asList("Click here to change your status", "to ready. When both players have done this",
                "the trade will be accepted");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(37, item);
        // BARRIER
        item = new ItemStack(Material.BARRIER);
        meta.setDisplayName(ChatColor.RED + "Click here to exit the trade");
        item.setItemMeta(meta);
        inv.setItem(38, item);
        // GRAY DYE
        item = new ItemStack(Material.GRAY_DYE);
        meta.setDisplayName("Status: §cnot ready");
        item.setItemMeta(meta);
        inv.setItem(39, item);

        return inv;
    }


}
