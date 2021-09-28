package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.particles.EffectsUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TrailsGui extends GuiCreator{

    private final EffectsUtils effects;

    public TrailsGui(Player player) {
        super("trails", 1);
        effects = new EffectsUtils(player);
        setItem(0, createItem(Material.TOTEM_OF_UNDYING, "&eTotem Trail", null), p -> {
            checkTrail(p);
            effects.startTotem();
        });
        setItem(1, createItem(Material.CAMPFIRE, "&cFire Trail", null), p -> {
            checkTrail(p);
            EffectsUtils.getTrails().put(player.getUniqueId(), EffectsUtils.Trails.WALKTRAIL);
        });
        setItem(2, createItem(Material.FIREWORK_ROCKET, "&bFireworks trail", null), p -> {
            checkTrail(p);
            effects.startFireworksTrail();
        });
        setItem(8, createItem(Material.BARRIER, "&cDisable Trails", null), this::checkTrail);
    }

    private void checkTrail(Player player) {
        if (EffectsUtils.getTrails().containsKey(player.getUniqueId()))
            effects.endTask();
        player.closeInventory();
    }
}
