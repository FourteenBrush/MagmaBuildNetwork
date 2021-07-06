package io.github.FourteenBrush.MagmaBuildNetwork.inventory;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.PlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class StatsGui  implements GUI, InventoryHolder {

    private final Inventory inv;
    private Player p;

    public StatsGui() {
        inv = Bukkit.createInventory(this, 54, ChatColor.RED + "Stats");
    }

    @NotNull
    public Inventory getInventory() {
        return inv;
    }

    @Override
    public Inventory createInv() {
        ItemStack item = new ItemStack(Material.STONE_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(PlayerCommand.getTotalMinedBlocks(p) + " blocks mined");
        item.setItemMeta(meta);
        inv.setItem(10, item);

        item = new ItemStack(Material.STONE_SWORD);
        meta.setDisplayName(p.getStatistic(Statistic.PLAYER_KILLS) + " kills");
        item.setItemMeta(meta);
        inv.setItem(13, item);

        item = new ItemStack(Material.WITHER_SKELETON_SKULL);
        meta.setDisplayName(p.getStatistic(Statistic.DEATHS) + " deaths");
        item.setItemMeta(meta);
        inv.setItem(16, item);

        item = new ItemStack(Material.OAK_BOAT);
        meta.setDisplayName((p.getStatistic(Statistic.BOAT_ONE_CM) / 5000) + " blocks travelled with boat");
        item.setItemMeta(meta);
        inv.setItem(28, item);

        return inv;
    }


    public void setPlayer(Player p) {
        this.p = p;
    }
}
