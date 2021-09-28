package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.PlayerCommand;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class StatsGui extends GuiCreator {

    public StatsGui(Player player) {
        super("Stats", 6);
        setItem(10, createItem(Material.STONE_PICKAXE, PlayerCommand.getTotalMinedBlocks(player) + " blocks mined", null));
        setItem(13, createItem(Material.STONE_SWORD, player.getStatistic(Statistic.PLAYER_KILLS) + " kills", null));
        setItem(16, createItem(Material.WITHER_SKELETON_SKULL, "&7" + player.getStatistic(Statistic.DEATHS) + " deaths", null));
        setItem(28, createItem(Material.OAK_BOAT, player.getStatistic(Statistic.BOAT_ONE_CM) / 1000 + " blocks travelled with boat", null));
        setItem(31, createItem(Material.IRON_AXE, PlayerCommand.getTotalChoppedTrees(player) + " logs chopped", null));
    }
}
