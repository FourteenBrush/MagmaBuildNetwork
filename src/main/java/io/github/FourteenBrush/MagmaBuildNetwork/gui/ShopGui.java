package io.github.FourteenBrush.MagmaBuildNetwork.gui;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopGui extends GuiCreator {

    public ShopGui() {
        super("Shop", 2);
        setItem(0, new ItemStack(Material.WOODEN_AXE));
        setItem(1, new ItemStack(Material.WOODEN_PICKAXE));
        setItem(2, new ItemStack(Material.WOODEN_HOE));
        setItem(3, new ItemStack(Material.WOODEN_SHOVEL));
        setItem(4, new ItemStack(Material.COBBLESTONE));
        setItem(5, new ItemStack(Material.OAK_BOAT));
        setItem(6, new ItemStack(Material.DIRT));
        setItem(7, new ItemStack(Material.APPLE));
        setItem(8, new ItemStack(Material.WRITABLE_BOOK));
    }
}
