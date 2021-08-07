package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandTrade;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class TradeGui extends GuiCreator {

    private boolean clickedStatus = false;

    public TradeGui() {
        super("Trade", 6);
        for (int i : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 17, 18, 22, 26, 27, 31, 35,
                36, 40, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53}) {
            setItem(i, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
        }
        setItem(37, createItem(Material.RED_WOOL, "§cClick to change to ready", Arrays.asList("Click here to change your status", "to ready. When both players have done this",
                "the trade will be accepted")), player -> {
            clickedStatus = !clickedStatus;
            setItem(37, createItem(Material.LIME_WOOL, "§2Ready!", null));
            setItem(39, createItem(Material.LIME_DYE, "§fStatus: §aready", null));
        });
        setItem(38, createItem(Material.BARRIER, "§cClick here to exit the trade", null), player -> {
                    CommandTrade.cancel();
                    CommandTrade.getTraders().forEach(p -> {
                        p.closeInventory();
                        Utils.message(p, "§cThe trade has been cancelled!");
                    });
                });
            setItem(39, createItem(Material.LIGHT_GRAY_DYE, "§fStatus: §cnot ready", null));
            setItem(41, createItem(Material.RED_DYE, "§cYour opponent is not ready", null));
    }
}
