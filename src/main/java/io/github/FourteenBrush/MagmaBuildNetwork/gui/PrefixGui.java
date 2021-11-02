package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.dependencies.LP;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Material;

import java.util.List;

public class PrefixGui extends GuiCreator {

    public PrefixGui() {
        super("Prefix", 6);
        List<String> prefix = LP.getPlayerGroups(player);
        prefix.forEach(group -> {
            if (prefix.isEmpty()) {
                PlayerUtils.message(player, "no groups found!");
                return;
            }
            int slot = inv.firstEmpty();
            String str = LP.loadPrefixes().get(prefix.get(slot));
            setItem(slot, createItem(Material.OAK_SIGN, Utils.colorize(str), null), player -> {
                plugin.getApi().getUserManager().getUser(player.getUniqueId()).setPrimaryGroup(prefix.get(slot));
                plugin.getApi().getUserManager().savePlayerData(player.getUniqueId(), player.getName());
                player.closeInventory();
                plugin.getChat().setPlayerPrefix(player, str);
                PlayerUtils.message(player, "&aChanged prefix!");
            });
        });
    }
}