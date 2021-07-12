package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.spawn.Spawn;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandSpawn extends BaseCommand {


    @Override
    protected boolean execute(@NotNull String[] args) {

        if (isConsole) return true;

        Location loc = p.getLocation();
        Spawn.setLocation(loc);
        p.getWorld().setSpawnLocation((int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
        Utils.message(p, "§aSpawn successfully set!");
        return true;
    }

    @Override
    protected @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (Utils.isAuthorized(p, "admin"))
            arguments.add("set");
        return StringUtil.copyPartialMatches(args[0], arguments, new ArrayList<>());
    }
}
