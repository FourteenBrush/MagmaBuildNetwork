package io.github.FourteenBrush.MagmaBuildNetwork.spawn;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.MessagesUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Spawn {

    public static void setup() {
        if (getLocation() == null)
            setLocation(Main.getPlugin(Main.class).getServer().getWorlds().get(0).getSpawnLocation());
    }

    public static void setLocation(Location l) {
        ConfigManager.getData().set("spawn", l);
        ConfigManager.saveConfig(ConfigManager.FileType.DATA);
    }

    public static Location getLocation() {
        return (Location) ConfigManager.getData().get("spawn");
    }

    private static void teleportPlayer(Player sender, Player target) {
        Location l = getLocation();
        if (l == null) {
            Utils.logWarning("Spawn is not set yet!");
            Utils.message(sender, "§cSpawn is not set yet!");
        } else {
            if (!l.getChunk().isLoaded()) {
                l.getChunk().load();
            }
            target.teleport(l);
            if (!sender.getName().equalsIgnoreCase(target.getName())) {
                Utils.message(target, MessagesUtils.messageTeleportedOtherPlayer().replaceAll("%player%", sender.getName()));
                Utils.message(sender, "§aSuccessfully teleported " + target.getName() + " to spawn");
            }
        }
    }

    public static void spawn(Player sender, Player target) {
        if (Combat.getPvpList().containsKey(target.getUniqueId())) {
            Utils.message(sender, MessagesUtils.messageDisableSpawnCommandInCombat());
        } else {
            teleportPlayer(sender, target);
        }
    }
}
