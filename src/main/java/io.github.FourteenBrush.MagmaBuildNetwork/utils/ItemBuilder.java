package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta itemMeta;
    private final ArrayList<String> lore;

    public ItemBuilder(Material material) {
        item = new ItemStack((material == null) ? Material.BARRIER : material);
        itemMeta = item.getItemMeta();
        lore = new ArrayList<>();
    }

    public void setDisplayName(String name) {
        itemMeta.setDisplayName(name);
    }

    public void setLore(String... loreList) {
        Collections.addAll(lore, loreList);
        itemMeta.setLore(lore);
    }

    public void setLore(List<String> loreList) {
        lore.addAll(loreList);
        itemMeta.setLore(lore);
    }

    public void addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
    }

    public void setItemFlags(ItemFlag... itemFlags) {
        itemMeta.addItemFlags(itemFlags);
    }

    public void setGlowing() {
        addEnchantment(Enchantment.ARROW_FIRE, 0, true);
        setItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public void setUnbreakable() {
        itemMeta.setUnbreakable(true);
    }

    public ItemStack build(Material material, String name, List<String> lore) {
        if (material == null) {
            itemMeta.setDisplayName(Utils.colorize("&cInvalid Material"));
            itemMeta.setLore(Utils.colorize("&cThis item is invalid", "&7Please pick another material to use", "&7that is supported by your server version"));
            return getItem();
        }
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        return getItem();
    }

    private ItemStack getItem() {
        item.setItemMeta(itemMeta);
        return item;
    }
}
