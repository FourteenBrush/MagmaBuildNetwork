package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
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
        setItem(0, Material.WOODEN_AXE, event -> openMenu(Material.WOODEN_AXE, 100, false));
        setItem(1, Material.WOODEN_PICKAXE, event -> openMenu(Material.WOODEN_PICKAXE, 100, false));
        setItem(2, Material.WOODEN_HOE, event -> openMenu(Material.WOODEN_HOE, 100, false));
        setItem(3, Material.WOODEN_SHOVEL, event -> openMenu(Material.WOODEN_SHOVEL, 100, false));
        setItem(4, Material.COBBLESTONE, event -> openMenu(Material.COBBLESTONE, 0.5, true));
        setItem(5, Material.OAK_BOAT, event -> openMenu(Material.DIRT, 0.5, true));
        setItem(6, Material.DIRT, event -> openMenu(Material.OAK_BOAT, 250, false));
        setItem(7, Material.APPLE, event -> openMenu(Material.APPLE, 50, false));
        setItem(8, Material.WRITABLE_BOOK, event -> openMenu(Material.WRITABLE_BOOK, 50, false));
    }

    private void openMenu(Material material, double price, boolean sell) {
        inv = Bukkit.createInventory(this, 45);
        ItemStack item = new ItemStack(material);
        clearActions();
        setItem(13, item);
        setItem(22, createItem(Material.BOOK, "Price: " + price + " dollars"));
        setItem(29, createItem(sell ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE, sell ? "&aSell" : "&aBuy"),
                event -> sell(sell, material, item.getAmount(), price));
        setItem(40, createItem(Material.ARROW, "Back"), event -> {
            clearActions();
            createShop();
            inv = root;
            open((Player) event.getWhoClicked());
        });
        if (sell) {
            setItem(0, createItem(material, "&a+&r1"), event -> update(item, item.getAmount() + 1));
            setItem(9, createItem(material, 10, "&a+&r10"), event -> update(item, item.getAmount() + 10));
            setItem(18, createItem(material, 32, "&a+&r32"), event -> update(item, item.getAmount() + 32));
            setItem(27, createItem(material, 64, "&a+&r64"), event -> update(item, item.getAmount() + 63));
            setItem(8, createItem(material, "&c-&r1"), event -> update(item, item.getAmount() - 1));
            setItem(17, createItem(material, 10, "&c-&r10"), event -> update(item, item.getAmount() - 10));
            setItem(26, createItem(material, 32, "&c-&r32"), event -> update(item, item.getAmount() - 32));
            setItem(35, createItem(material, 64, "&c-&r64"), event -> update(item, item.getAmount() - 63));
        }
        open(player);
    }

    private void sell(boolean sell, Material material, int amount, double price) {
        ItemStack item = new ItemStack(material, amount);
        if (sell) {
            if (player.getInventory().containsAtLeast(item, amount)) {
                player.getInventory().removeItem(item);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 6, 1);
                plugin.getEco().depositPlayer(player, amount * price);
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 6, 1);
                return;
            }
        } else if (plugin.getEco().has(player, amount * price)) {
            plugin.getEco().withdrawPlayer(player, amount * price);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 6, 1);
            PlayerUtils.giveOrDropFor(player, item);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 6, 1);
            return;
        }
        DecimalFormat df = new DecimalFormat("##.#");
        PlayerUtils.message(player, (sell ? "&a+&b " : "&c-&b ") + df.format(amount * price) + " &3coins");
    }

    private void update(ItemStack item, int amount) {
        if (amount > 0 && item.getAmount() + amount < 66)
            item.setAmount(amount);
        setItem(13, item);
    }

    private void clearActions() {
        for (int i = 0; i < inv.getSize(); i++)
            getActions().remove(i);
    }
}
