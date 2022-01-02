package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.CooldownManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommandSpawn extends CommandHandler {

    private static final CommandSpawn INSTANCE = new CommandSpawn();
    private Location location;
    private final Map<UUID, Integer> uses;
    private final CooldownManager cm;
    private final Cache<UUID, Boolean> pvpList;

    public CommandSpawn() {
        super("spawn", Permission.BASIC, true);
        uses = new HashMap<>();
        cm = new CooldownManager();
        pvpList = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();
        setup();
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        UUID uuid = executor.getUniqueId();
        if (args.length == 0) {
            // handle cooldown
            if (!CommandMagmabuildnetwork.isBypassing(uuid) && uses.merge(uuid, 1, Integer::sum) > 3) {
                long timeLeft = System.currentTimeMillis() - cm.getCooldown(uuid);
                if (TimeUnit.MILLISECONDS.toDays(timeLeft) < 1) {
                    executor.sendMessage(Lang.COMMAND_COOLDOWN.get(Utils.millisToReadable(TimeUnit.DAYS.toMillis(1) - timeLeft)));
                    return true;
                }
            }
            spawn(executor, executor);
            // make sure bypassed uses don't count as an use
            if (!CommandMagmabuildnetwork.isBypassing(uuid))
                cm.setCooldown(uuid, System.currentTimeMillis());
        } else if (args.length < 2 && args[0].equalsIgnoreCase("set")) {
            if (!Permission.ADMIN.has(executor, true)) return true;
            Location loc = executor.getLocation();
            setLocation(loc);
            executor.getWorld().setSpawnLocation(loc);
            executor.sendMessage(Lang.SPAWN_SET.get());
        } else {
            // /spawn someRandomDude will show the no-permission message if you aren't a moderator
            Player target = Bukkit.getPlayer(args[0]);
            if (executor != target && !Permission.MODERATOR.has(executor, true)) return true;
            if (!PlayerUtils.checkPlayerOnline(executor, target)) return true;
            spawn(executor, target);
        }
        return true;
    }

    public static CommandSpawn getInstance() {
        return INSTANCE;
    }

    public void setup() {
        location = (Location) plugin.getConfigManager().getData().get("spawn");
        if (location == null) {
            location = plugin.getServer().getWorlds().get(0).getSpawnLocation();
            plugin.getConfigManager().getData().set("spawn", location);
            plugin.getConfigManager().saveConfig(ConfigManager.FileType.DATA);
        }
    }

    public Location getLocation() {
        if (location == null) {
            setup();
        }
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        plugin.getConfigManager().getData().set("spawn", location);
        plugin.getConfigManager().saveConfig(ConfigManager.FileType.DATA);
    }

    public void spawn(Player executor, Player target) {
        if (pvpList.getIfPresent(executor.getUniqueId()) != null) {
            executor.sendMessage(Lang.SPAWN_DISABLED_IN_COMBAT.get());
        } else {
            Location location = getLocation();
            // todo wait until chunk is loaded
            target.teleport(location);
            if (executor != target) {
                executor.sendMessage(Lang.SPAWN_TELEPORTED_OTHER_PLAYER.get(target.getName()));
                target.sendMessage(Lang.SPAWN_TELEPORTED_BY_OTHER_PLAYER.get(executor.getName()));
            } else {
                executor.sendMessage(Lang.SPAWN_TELEPORTED.get());
            }
        }
    }

    public void pvp(UUID uuid) {
        pvpList.put(uuid, true);
    }

    @Override
    public List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1 && Permission.MODERATOR.has(executor)) {
            return StringUtil.copyPartialMatches(args[0], Collections.singletonList("set"), new ArrayList<>());
        }
        return super.tabComplete(args);
    }
}
