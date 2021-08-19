package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.Effects;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TrailsGui extends GuiCreator {

    private final Effects effects;

    public TrailsGui(Player player) {
        super("trails", 1);
        effects = new Effects(player, player.getUniqueId());
        setItem(3, createItem(Material.TOTEM_OF_UNDYING, "§eTotem Trail", null), p -> {
            checkTrail(p);
            effects.startTotem();
        });
        setItem(5, createItem(Material.CAMPFIRE, "§cFire Trail", null), p -> {
            checkTrail(p);
            effects.setID(1);
        });
        setItem(8, createItem(Material.BARRIER, "§cDisable Trails", null), this::checkTrail);
    }

    private void checkTrail(Player player) {
        if (effects.hasID()) {
            effects.endTask();
            effects.removeID();
        }
        player.closeInventory();
    }
}
