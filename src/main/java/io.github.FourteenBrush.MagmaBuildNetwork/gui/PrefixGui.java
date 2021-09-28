package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.dependencies.LP;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Material;

import java.util.List;

public class PrefixGui extends GuiCreator {

    public PrefixGui() {
        super("Prefix", 6);
        LP.getPlayerGroups(player).forEach(group -> {
            List<String> prefix = LP.getPlayerGroups(player);
            if (prefix.isEmpty()) return;
            int slot = inv.firstEmpty();
            String str = LP.loadPrefixes().get(prefix.get(slot));
            setItem(slot, createItem(Material.OAK_SIGN, Utils.colorize(str), null), player -> {
                Main.getApi().getUserManager().getUser(player.getUniqueId()).setPrimaryGroup(LP.getPlayerGroups(player).get(slot));
                player.closeInventory();
                Main.getChat().setPlayerPrefix(player, str);
                Utils.message(player, "&aChanged prefix!");
            });
        });
    }
}