package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import com.google.common.collect.ImmutableMap;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.*;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SimpleCommand implements CommandExecutor {

    private static final Set<UUID> frozenPlayers = new HashSet<>();
    private Player executor;
    private final Map<String, Permission> commands = ImmutableMap.of(
            "safechest", Permission.SAFECHEST,
            "stats", Permission.BASIC,
            "trails", Permission.TRAILS,
            "shop", Permission.BASIC,
            "prefix", Permission.ADMIN // todo change this to Permission.Basic after it works correctly
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            PlayerUtils.message(sender, Lang.NO_CONSOLE.get());
            return true;
        }
        executor = (Player) sender;
        if (!commands.getOrDefault(cmd.getName().toLowerCase(), Permission.ADMIN).has(executor, true)) {
            return true;
        }
        switch(cmd.getName().toLowerCase()) {
            case "safechest":
                return safechest();
            case "ignite":
                return ignite(args);
            case "heal":
                return heal(args);
            case "freeze":
                return freeze(args);
            case "stats":
                return stats();
            case "trails":
                return trails();
            case "shop":
                return shop();
            case "prefix":
                return prefix();
        }
        return true;
    }

    private boolean safechest() {
        new SafechestGui(executor).open(executor);
        return true;
    }

    private boolean ignite(String[] args) {
        Player target = args.length == 1 ? Bukkit.getPlayer(args[0]) : executor;
        if (target != executor && !PlayerUtils.checkPlayerOnline(executor, target, false)) return true;
        target.setFireTicks(500);
        PlayerUtils.message(executor, "&aIgnited&6 " + target.getName());
        return true;
    }

    private boolean heal(String[] args) {
        Player target = args.length == 1 ? Bukkit.getPlayer(args[0]) : executor;
        if (target != executor && !PlayerUtils.checkPlayerOnline(executor, target, false)) return true;
        target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
        target.setFoodLevel(20);
        target.setSaturation(10);
        target.setExhaustion(0);
        target.setFireTicks(0);
        PlayerUtils.message(target, "&aHealed &6" + target.getName());
        return true;
    }

    private boolean freeze(String[] args) {
        Player target = args.length == 1 ? Bukkit.getPlayer(args[0]) : executor;
        if (target != executor && !PlayerUtils.checkPlayerOnline(executor, target, false)) return true;
        if (!frozenPlayers.remove(target.getUniqueId())) {
            frozenPlayers.add(target.getUniqueId());
            PlayerUtils.message(executor, "&aPlayer&6 " + target.getName() + " &afrozen");
            return true;
        }
        PlayerUtils.message(executor, "&aPlayer&6 " + target.getName() + " &aunfrozen");
        return true;
    }

    private boolean stats() {
        new StatsGui(executor).open(executor);
        return true;
    }

    private boolean prefix() {
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            new PrefixGui().open(executor);
        }
        return true;
    }

    private boolean trails() {
        new TrailsGui(executor).open(executor);
        return true;
    }

    private boolean shop() {
        new ShopGui().open(executor);
        return true;
    }

    public static boolean isPlayerFrozen(UUID uuid) {
        return frozenPlayers.contains(uuid);
    }
}