package io.github.FourteenBrush.MagmaBuildNetwork.dependencies;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.luckperms.api.model.group.Group;
import org.bukkit.entity.Player;

import java.util.*;

public class LP {

    private static final ArrayList<String> groups = new ArrayList<>();
    private static final Map<String, String> prefixes = new HashMap<>(); //group, prefix
    private static boolean enabled = Utils.isPluginEnabled("LuckPerms");

    private static void loadGroups() {
        if (!enabled) return;
        groups.clear();
        Set<Group> groupsSet = Main.getApi().getGroupManager().getLoadedGroups();
        for (Group group : groupsSet) {
            groups.add(group.getName());
        }
    }

    public static Map<String, String> loadPrefixes() {
        if (!enabled) return null;
        loadGroups();
        prefixes.clear();
        for (String groupName : groups) {
            String prefix = Main.getApi().getGroupManager().getGroup(groupName).getCachedData().getMetaData().getPrefix();
            if (prefix == null || prefix.equalsIgnoreCase("null")) {
                prefix = "&7";
            }
            prefixes.put(groupName, prefix);
        }
        return prefixes;
    }

    public static boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }

    public static List<String> getPlayerGroups(Player player) {
        if (!enabled) return null;
        List<String> groups = new ArrayList<>();
        for (Group g : Main.getApi().getGroupManager().getLoadedGroups()) {
            if (player.hasPermission("group." + g)) {
                groups.add(g.getName());
            }
        }
        return groups;
    }
}
