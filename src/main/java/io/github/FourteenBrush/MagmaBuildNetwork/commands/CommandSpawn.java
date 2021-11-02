package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.library.Combat;
import io.github.FourteenBrush.MagmaBuildNetwork.library.CooldownManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommandSpawn extends AbstractCommand {

    private static Location location;
    private final Map<UUID, Integer> uses;
    private final CooldownManager cm;

    public CommandSpawn() {
        super("spawn", Permission.BASIC, true);
        uses = new HashMap<>();
        cm = new CooldownManager();
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        UUID uuid = executor.getUniqueId();
        if (args.length < 1) {
            if (!CommandMagmabuildnetwork.isBypassing(uuid) && uses.merge(uuid, 1, Integer::sum) > 3) {
                long timeLeft = System.currentTimeMillis() - cm.getCooldown(uuid);
                if (TimeUnit.MILLISECONDS.toDays(timeLeft) < 1) {
                    PlayerUtils.message(executor, "&cPlease wait&e " + Utils.millisToReadable(TimeUnit.DAYS.toMillis(1) - timeLeft) + " &cbefore resuing this command!");
                    return true;
                }
            }
            spawn(executor, executor);
            // makes sure bypassed uses don't count as an use
            if (!CommandMagmabuildnetwork.isBypassing(uuid))
                cm.setCooldown(uuid, System.currentTimeMillis());
        } else if (args.length < 2 && args[0].equalsIgnoreCase("set")) {
            if (Permission.ADMIN.has(executor, true)) {
                Location loc = executor.getLocation();
                setLocation(loc);
                executor.getWorld().setSpawnLocation(loc);
                PlayerUtils.message(executor, "&aSpawn successfully set!");
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (!PlayerUtils.checkPlayerOnline(executor, target, false)) return true;
            spawn(executor, target);
        }
        return true;
    }

    public static void setup() {
        location = (Location) plugin.getConfigManager().getData().get("spawn");
        if (location == null) {
            location = plugin.getServer().getWorlds().get(0).getSpawnLocation();
            plugin.getConfigManager().getData().set("spawn", location);
            plugin.getConfigManager().saveConfig(ConfigManager.FileType.DATA);
        }
    }

    public static Location getLocation() {
        if (location == null)
            setup();
        return location;
    }

    public static void setLocation(Location location) {
        CommandSpawn.location = location;
        plugin.getConfigManager().getData().set("spawn", location);
        plugin.getConfigManager().saveConfig(ConfigManager.FileType.DATA);
    }

    public static void spawn(Player executor, Player target) {
        if (Combat.getPvpList().containsKey(target.getUniqueId())) {
            PlayerUtils.message(executor, Lang.SPAWN_DISABLED_IN_COMBAT.get());
        } else {
            teleport(executor, target);
        }
    }

    private static void teleport(Player executor, Player target) {
        Location location = getLocation();
        if (!location.getChunk().isLoaded())
            location.getChunk().load();
        target.teleport(location);
        if (!executor.getName().equals(target.getName())) {
            PlayerUtils.message(executor, Lang.SPAWN_TELEPORTED_OTHER_PLAYER_SUCCESS.get(target.getName()));
            PlayerUtils.message(target, Lang.SPAWN_TELEPORTED_BY_OTHER_PLAYER.get(executor.getName()));
        }
    }

    @Override
    protected List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1 && Permission.ADMIN.has(executor)) {
            return StringUtil.copyPartialMatches(args[0], Collections.singletonList("set"), new ArrayList<>());
        }
        return super.tabComplete();
    }
}
