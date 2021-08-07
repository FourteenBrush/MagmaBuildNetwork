package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.spawn.Spawn;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandSpawn extends BaseCommand {


    @Override
    protected boolean execute(@NotNull String[] args) {

        if (isConsole) return true;

        if (args.length < 1) {
            Spawn.spawn(p);
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
            if (Utils.isAuthorized(p, "admin")) {
                Location loc = p.getLocation();
                Spawn.setLocation(loc);
                p.getWorld().setSpawnLocation((int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
                Utils.message(p, "§aSpawn successfully set!");
                return true;
            }
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (!Utils.isPlayerOnline(p, target)) return true;
            Spawn.spawn(target);
            Utils.message(p, "§aSuccessfully teleported " + target.getName() + " to spawn");
        }
        return true;
    }

    @Override
    protected @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            if (Utils.isAuthorized(p, "admin"))
                if (args[0].startsWith("s"))
                    arguments.add("set");
            return StringUtil.copyPartialMatches(args[0], arguments, new ArrayList<>());
        }
        return null;
    }
}
