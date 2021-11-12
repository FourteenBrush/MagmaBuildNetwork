package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.luckperms.api.model.group.GroupManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class PrefixGui extends GuiCreator {

    private final GroupManager groupManager = plugin.getApi().getGroupManager();
    private final Set<String> groups;
    private final Map<String, String> prefixes; //group, prefix

    public PrefixGui() {
        super("Prefix", 6);
        groups = new HashSet<>();
        prefixes = new HashMap<>();
        List<String> prefix = getPlayerGroups(player);
        prefix.forEach(group -> {
            if (prefix.isEmpty()) {
                PlayerUtils.message(player, "no groups found!");
                return;
            }
            int slot = inv.firstEmpty();
            String str = loadPrefixes().get(prefix.get(slot));
            setItem(slot, createItem(Material.OAK_SIGN, Utils.colorize(str), null), player -> {
                plugin.getApi().getUserManager().getUser(player.getUniqueId()).setPrimaryGroup(prefix.get(slot));
                plugin.getApi().getUserManager().savePlayerData(player.getUniqueId(), player.getName());
                player.closeInventory();
                plugin.getChat().setPlayerPrefix(player, str);
                PlayerUtils.message(player, "&aChanged prefix!");
            });
        });
    }

    private Map<String, String> loadPrefixes() {
        groups.clear();
        prefixes.clear();
        groupManager.getLoadedGroups().forEach(group -> groups.add(group.getName()));
        groups.forEach(group -> {
            String prefix = groupManager.getGroup(group).getCachedData().getMetaData().getPrefix();
            if (prefix == null || prefix.equals("null") || prefix.isEmpty())
                prefix = "&7";
            prefixes.put(group, prefix);
        });
        return prefixes;
    }

    private List<String> getPlayerGroups(Player player) {
        List<String> groups = new ArrayList<>();
        groupManager.getLoadedGroups().forEach(group -> { // check if group nonnull
            if (player.hasPermission("group." + group))
                groups.add(group.getName());
        });
        return groups;
    }
}