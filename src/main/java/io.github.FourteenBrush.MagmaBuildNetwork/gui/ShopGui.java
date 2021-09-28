package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class ShopGui extends GuiCreator {

    private final Inventory root;

    public ShopGui() {
        super("Shop", 2);
        createShop();
        root = inv;
    }

    private void createShop() {
        setItem(0, new ItemStack(Material.WOODEN_AXE), player -> openMenu(Material.WOODEN_AXE, 100, false));
        setItem(1, new ItemStack(Material.WOODEN_PICKAXE), player -> openMenu(Material.WOODEN_PICKAXE, 100, false));
        setItem(2, new ItemStack(Material.WOODEN_HOE), player -> openMenu(Material.WOODEN_HOE, 100, false));
        setItem(3, new ItemStack(Material.WOODEN_SHOVEL), player -> openMenu(Material.WOODEN_SHOVEL, 100, false));
        setItem(4, new ItemStack(Material.COBBLESTONE), player -> openMenu(Material.COBBLESTONE, 0.5, true));
        setItem(5, new ItemStack(Material.OAK_BOAT), player -> openMenu(Material.DIRT, 0.5, true));
        setItem(6, new ItemStack(Material.DIRT), player -> openMenu(Material.OAK_BOAT, 250, false));
        setItem(7, new ItemStack(Material.APPLE), player -> openMenu(Material.APPLE, 50, false));
        setItem(8, new ItemStack(Material.WRITABLE_BOOK), player -> openMenu(Material.WRITABLE_BOOK, 50, false));
    }

    private void openMenu(Material material, double price, boolean sell) {
        inv = Bukkit.createInventory(null, 45);
        clearActions();
        ItemStack item = new ItemStack(material, 1);
        setItem(13, item);
        setItem(22, createItem(Material.BOOK, "Price: " + price + " dollars", null));
        setItem(29, createItem(sell ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE, sell ? "§aSell" : "§aBuy", null), player -> sell(sell, material, item.getAmount(), price));
        setItem(40, createItem(Material.ARROW, "Back", null), player -> {
            clearActions();
            createShop();
            inv = root;
            open(player);
        });
        if (sell) {
            setItem(0, createItem(material, "§a+§r1", null), player -> update(item, item.getAmount() + 1));
            setItem(9, createItem(material, 10, "§a+§r10", null), player -> update(item, item.getAmount() + 10));
            setItem(18, createItem(material, 32, "§a+§r32", null), player -> update(item, item.getAmount() + 32));
            setItem(27, createItem(material, 64, "§a+§r64", null), player -> update(item, item.getAmount() + 63));
            setItem(8, createItem(material, "§c-§r1", null), player -> update(item, item.getAmount() - 1));
            setItem(17, createItem(material, 10, "§c-§r10", null), player -> update(item, item.getAmount() - 10));
            setItem(26, createItem(material, 32, "§c-§r32", null), player -> update(item, item.getAmount() - 32));
            setItem(35, createItem(material, 64, "§c-§r64", null), player -> update(item, item.getAmount() - 63));
        }
        open(player);
    }

    private void sell(boolean sell, Material material, int amount, double price) {
        ItemStack item = new ItemStack(material, amount);
        if (sell) {
            if (player.getInventory().containsAtLeast(item, amount)) {
                player.getInventory().removeItem(item);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 6, 1);
                Main.getEco().depositPlayer(player, amount * price);
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 6, 1);
                return;
            }
        } else if (Main.getEco().has(player, amount * price)) {
            Main.getEco().withdrawPlayer(player, amount * price);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 6, 1);
            Utils.giveOrDropFor(player, item);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 6, 1);
            return;
        }
        DecimalFormat df = new DecimalFormat("##.#");
        Utils.message(player, Utils.colorize((sell ? "&a+&b " : "&c-&b ") + df.format(amount * price) + " &3coins"));
    }

    private void update(ItemStack item, int amount) {
        if (amount > 0 && item.getAmount() + amount < 66)
            item.setAmount(amount);
        setItem(13, item);
    }

    private void clearActions() {
        for (int i = 0; i < inv.getSize(); i++) {
            if (getActions().get(i) != null)
                getActions().remove(i);
        }
    }
}
