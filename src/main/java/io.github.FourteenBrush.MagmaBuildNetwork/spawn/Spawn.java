package io.github.FourteenBrush.MagmaBuildNetwork.spawn;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn {

    private static final Main plugin = Main.getInstance();

    public static void setLocation(Location l) {
        plugin.getConfig().set("spawn.world", l.getWorld().getName());
        plugin.getConfig().set("spawn.x", l.getX());
        plugin.getConfig().set("spawn.y", l.getY());
        plugin.getConfig().set("spawn.z", l.getZ());
        plugin.getConfig().set("spawn.yaw", l.getYaw());
        plugin.getConfig().set("spawn.pitch", l.getPitch());
    }

    public static Location getLocation() {
        String worldName = plugin.getConfig().getString("spawn.world");
        if (worldName == null) {
            return null;
        }
        World world = Bukkit.getServer().getWorld(worldName);
        double x = plugin.getConfig().getDouble("spawn.x");
        double y = plugin.getConfig().getDouble("spawn.y");
        double z = plugin.getConfig().getDouble("spawn.z");
        float yaw = plugin.getConfig().getInt("spawn.yaw");
        float pitch = plugin.getConfig().getInt("spawn.pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void teleportPlayer(CommandSender sender, Player p, boolean message) {
        Location l = getLocation();
        if (l == null) {
            Utils.logWarning("Spawn is not set yet!");
            Utils.message(sender, "§cSpawn is not set yet!");
        } else {
            if (!l.getChunk().isLoaded()) {
                l.getChunk().load();
            }
            p.teleport(l);
            if (message) {
                Utils.message(sender, "§aYou have been teleported to spawn!");
            }
            if (!p.getName().equalsIgnoreCase(sender.getName())) {
                Utils.message(sender, ConfigManager.getMessagesConfig().getString("messages.spawn.teleported_other_player")
                        .replaceAll("%player%", sender.getName()));
            }
        }
    }
}
