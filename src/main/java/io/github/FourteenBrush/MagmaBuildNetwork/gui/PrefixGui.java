package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.dependencies.LP;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class PrefixGui extends GuiCreator {

    public PrefixGui(Player p) {
        super("Prefix", 6);
        for (int i = 0; i < LP.getPlayerGroups(p).toArray().length; i++) {
            List<String> prefix = LP.getPlayerGroups(p);
            if (!(prefix == null || prefix.isEmpty())) {
                setItem(0, createItem(Material.OAK_SIGN, LP.loadPrefixes(p).get(prefix.get(i)), null));
                // todo change prefix on click
            }
        }
    }
}