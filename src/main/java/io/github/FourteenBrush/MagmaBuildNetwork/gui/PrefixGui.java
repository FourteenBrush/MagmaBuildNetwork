package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.dependencies.LP;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class PrefixGui extends GuiCreator {

    public PrefixGui(Player p) {
        super("Prefix", 6);
        if (LP.getPlayerGroups(p) != null) {
            for (int i = 0; i < LP.getPlayerGroups(p).size(); i++) {
                List<String> prefix = LP.getPlayerGroups(p);
                if (!(prefix == null || prefix.isEmpty())) {
                    int finalI = i;
                    setItem(0, createItem(Material.OAK_SIGN, Utils.colorize(LP.loadPrefixes().get(prefix.get(i))) , null), player
                            -> { Main.getApi().getUserManager().getUser(p.getUniqueId()).setPrimaryGroup(LP.getPlayerGroups(p).get(finalI));
                            player.closeInventory();
                    });
                }
            }
        }
    }
}