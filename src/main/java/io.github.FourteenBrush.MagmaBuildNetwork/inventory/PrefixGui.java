package io.github.FourteenBrush.MagmaBuildNetwork.inventory;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class PrefixGui implements GUI, InventoryHolder {

    private final Inventory inv;
    private final Player p;

    public PrefixGui(Player p) {
        inv = Bukkit.createInventory(this, 54, "§cPrefix");
        this.p = p;
    }

    @NotNull
    public Inventory getInventory() {
        return inv;
    }

    @Override
    public Inventory createInv() {

        ItemStack item = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = item.getItemMeta();
        for (int i = 0; i < LP.getPlayerGroups(p).toArray().length; i++) {
            meta.setDisplayName(LP.loadPrefixes(p).get(LP.getPlayerGroups(p).get(i)));
            item.setItemMeta(meta);
            setItem(inv, item, i);
        }
        return inv;
    }
}
