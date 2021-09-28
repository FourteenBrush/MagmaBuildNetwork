package io.github.FourteenBrush.MagmaBuildNetwork.dependencies;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import net.luckperms.api.model.group.Group;
import org.bukkit.entity.Player;

import java.util.*;

public class LP {

    private static final Set<String> groups = new HashSet<>();
    private static final Map<String, String> prefixes = new HashMap<>(); //group, prefix

    private static void loadGroups() {
        groups.clear();
        for (Group group : Main.getApi().getGroupManager().getLoadedGroups()) {
            groups.add(group.getName());
        }
    }

    public static Map<String, String> loadPrefixes() {
        loadGroups();
        prefixes.clear();
        groups.forEach(group -> {
            String prefix = Main.getApi().getGroupManager().getGroup(group).getCachedData().getMetaData().getPrefix();
            if (prefix == null || prefix.equals("null") || prefix.isEmpty())
                prefix = "&7";
            prefixes.put(group, prefix);
        });
        return prefixes;
    }

    private static boolean isPlayerInGroup(Player player, Group group) {
        return player.hasPermission("group." + group);
    }

    public static List<String> getPlayerGroups(Player player) {
        List<String> groups = new ArrayList<>();
        Main.getApi().getGroupManager().getLoadedGroups().forEach(group -> {
            if (isPlayerInGroup(player, group))
                groups.add(group.getName());
        });
        return groups;
    }
}
