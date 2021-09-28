package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private ItemStack item;
    private ItemMeta itemMeta;
    private List<String> lore;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
        itemMeta = item.getItemMeta();
        lore = new ArrayList<>();
    }

    protected ItemBuilder() {}

    public ItemBuilder setDisplayName(String name) {
        itemMeta.setDisplayName(Utils.colorize(name));
        return this;
    }

    public ItemBuilder setLore(List<String> loreList) {
        itemMeta.setLore(loreList);
        return this;
    }

    public ItemBuilder setItemFlags(ItemFlag... itemFlags) {
        itemMeta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder setUnbreakable() {
        itemMeta.setUnbreakable(true);
        return this;
    }

    public ItemBuilder setGlowing() {
        addEnchantment(Enchantment.ARROW_FIRE, 0, true)
        .setItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return this;
    }

    public ItemStack build() {
        setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
}
