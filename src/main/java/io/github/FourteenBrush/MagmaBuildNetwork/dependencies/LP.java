package io.github.FourteenBrush.MagmaBuildNetwork.dependencies;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import net.luckperms.api.model.group.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class LP {

    private static final Main plugin = Main.getPlugin(Main.class);
    private static final GroupManager groupManager = plugin.getApi().getGroupManager();
    private static final Set<String> groups = new HashSet<>();
    private static final Map<String, String> prefixes = new HashMap<>(); //group, prefix

    public static Map<String, String> loadPrefixes() {
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

    public static List<String> getPlayerGroups(Player player) {
        List<String> groups = new ArrayList<>();
        groupManager.getLoadedGroups().forEach(group -> { // check if group nonnull
            if (player.hasPermission("group." + group))
                groups.add(group.getName());
        });
        return groups;
    }
}
