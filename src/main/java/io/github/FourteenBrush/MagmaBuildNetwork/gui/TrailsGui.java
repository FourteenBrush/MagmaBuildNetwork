package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.library.Effects;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TrailsGui extends GuiCreator{

    private final Effects effects;

    public TrailsGui(Player player) {
        super("Trails", 1);
        effects = new Effects(player);
        setItem(0, createItem(Material.TOTEM_OF_UNDYING, "&eTotem Trail", null), p -> {
            if (checkTrail(player))
                effects.startTotem();
        });
        setItem(1, createItem(Material.CAMPFIRE, "&cFire Trail", null), p -> {
            if (checkTrail(player))
                Effects.getTrails().put(player.getUniqueId(), Effects.Trails.WALKTRAIL);
        });
        setItem(2, createItem(Material.FIREWORK_ROCKET, "&bFireworks trail", null), p -> {
            if (checkTrail(player))
                effects.startFireworksTrail();
        });
        setItem(8, createItem(Material.BARRIER, "&cDisable Trails", null), this::checkTrail);
    }

    private boolean checkTrail(Player player) {
        if (Effects.getTrails().containsKey(player.getUniqueId())) {
            effects.endTask();
            player.closeInventory();
            return false;
        } else {
            player.closeInventory();
            return true;
        }
    }
}
