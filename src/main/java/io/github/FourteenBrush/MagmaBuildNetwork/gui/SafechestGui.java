package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SafechestGui extends GuiCreator {

    private static final Map<UUID, ItemStack[]> menus = new HashMap<>();

    public SafechestGui(Player player) {
        super(player.getName() + "'s safechest", 5);
        if (menus.containsKey(player.getUniqueId())) {
            inv.setContents(menus.get(player.getUniqueId()));
        }
    }

    public static Map<UUID, ItemStack[]> getMenus() {
        return menus;
    }
}
