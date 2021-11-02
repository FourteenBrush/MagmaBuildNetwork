package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CommandFly extends AbstractCommand {

    private static final Set<UUID> flyingPlayers = new HashSet<>();

    public CommandFly() {
        super("fly", Permission.ADMIN, false);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        Player target = args.length == 1 ? Bukkit.getPlayer(args[0]) : executor;
        if (!PlayerUtils.checkPlayerOnline(executor, target, false)) return true;
        fly(target, !flyingPlayers.remove(target.getUniqueId()), true);
        return true;
    }

    public void fly(Player player, boolean fly, boolean message) {
        if (fly) flyingPlayers.add(player.getUniqueId());
        player.setAllowFlight(fly || player.getGameMode() == GameMode.CREATIVE);
        if (message) PlayerUtils.message(executor, fly ? "&aSet fly mode enabled" : "&aSet fly mode &cdisabled");
    }
}
