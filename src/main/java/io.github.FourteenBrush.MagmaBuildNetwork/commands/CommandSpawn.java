package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.spawn.Spawn;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.CooldownManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.MessagesUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommandSpawn extends AbstractCommand {

    private final Map<UUID, Integer> uses;
    private final CooldownManager cm;

    public CommandSpawn() {
        super("spawn", true);
        uses = new HashMap<>();
        cm = new CooldownManager();
    }

    @Override
    public boolean execute(@NotNull String[] args) {

        if (isConsole) return MessagesUtils.noPermission(sender);
        UUID uuid = executor.getUniqueId();
        if (args.length < 1) {
            if (!CommandMagmabuildnetwork.getBypassingPlayers().contains(uuid)) {
                uses.putIfAbsent(uuid, 0);
                if (uses.get(uuid) < 4) {
                    uses.put(uuid, uses.get(uuid) + 1);
                } else if (TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - cm.getCooldown(uuid)) < 1) {
                    Utils.message(executor, "&cPlease wait&e " + Utils.millisToReadable(TimeUnit.DAYS.toMillis(1) - (System.currentTimeMillis() - cm.getCooldown(uuid))) + " &cbefore reusing this command!");
                    return true;
                }
            }
            Spawn.spawn(executor, executor);
            cm.setCooldown(uuid, System.currentTimeMillis());
        } else if (args.length < 2) {
            if (args[0].equalsIgnoreCase("set")) {
                if (!Utils.isAuthorized(executor, "admin")) MessagesUtils.noPermission(executor);
                Location loc = executor.getLocation();
                Spawn.setLocation(loc);
                executor.getWorld().setSpawnLocation(loc);
                Utils.message(executor, "&aSpawn successfully set!");
            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if (!Utils.isPlayerOnline(executor, target)) return true;
                Spawn.spawn(executor, target);
            }
        }
        return true;
    }

    @Override
    protected List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1 && Utils.isAuthorized(executor, "admin") && args[0].startsWith("s")) {
            return StringUtil.copyPartialMatches(args[0], Collections.singletonList("set"), new ArrayList<>());
        }
        return null;
    }
}
