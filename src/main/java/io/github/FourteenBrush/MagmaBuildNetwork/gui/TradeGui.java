package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandTrade;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class TradeGui extends GuiCreator {

    public TradeGui() {
        super("Trade", 6);
        ItemStack divider = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        int[] slots = new int[] { 0,1,2,3,4,5,6,7,8,9,13,17,18,22,26,27,31,35,36,40,42,43,44,45,46,47,48,49,50,51,52,53 };
        for (int i: slots) {
            setItem(i, divider);
        }
        setItem(37, createItem(Material.RED_WOOL, "&cClick to change to ready!", Arrays.asList(
                "Click here to change your status", "to ready. When both players have done this",
                "the trade will be accepted"
        )), player -> {
            setItem(37, createItem(Material.LIME_WOOL, "&2Ready!", null));
            setItem(39, createItem(Material.LIME_DYE, "&fStatus: &aReady", null));
        });
        setItem(38, createItem(Material.BARRIER, "&cClick here to exit the trade", null), player -> {
            CommandTrade.getInstance().cancelTrade(player.getName());
        });
        setItem(39, createItem(Material.LIGHT_GRAY_DYE, "&fStatus: &cnot ready", null));
        setItem(41, createItem(Material.RED_DYE, "&cYour opponent is not ready", null));
    }
}
