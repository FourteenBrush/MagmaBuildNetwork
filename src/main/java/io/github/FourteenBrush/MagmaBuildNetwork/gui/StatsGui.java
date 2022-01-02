package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class StatsGui extends GuiCreator {

    public StatsGui(Player player) {
        super("Stats", 6);
        setItem(10, createItem(Material.STONE_PICKAXE, getTotalMinedStones(player) + " blocks mined"));
        setItem(13, createItem(Material.STONE_SWORD, player.getStatistic(Statistic.PLAYER_KILLS) + " kills"));
        setItem(16, createItem(Material.WITHER_SKELETON_SKULL, "&7" + player.getStatistic(Statistic.DEATHS) + " deaths"));
        setItem(28, createItem(Material.OAK_BOAT, player.getStatistic(Statistic.BOAT_ONE_CM) / 1000 + " blocks travelled with boat"));
        setItem(31, createItem(Material.IRON_AXE, getTotalChoppedLogs(player) + " logs chopped"));
    }

    private int getTotalMinedStones(Player player) {
        int total = 0;
        Material[] materials = { Material.STONE, Material.ANDESITE, Material.COBBLESTONE, Material.DIORITE, Material.GRANITE, Material.DIRT };
        for (Material m : materials) {
            total += player.getStatistic(Statistic.MINE_BLOCK, m);
        }
        return total;
    }

    private int getTotalChoppedLogs(Player player) {
        int total = 0;
        Material[] materials = { Material.OAK_LOG, Material.SPRUCE_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.BIRCH_LOG };
        for (Material m : materials) {
            total += player.getStatistic(Statistic.MINE_BLOCK, m);
        }
        return total;
    }
}
