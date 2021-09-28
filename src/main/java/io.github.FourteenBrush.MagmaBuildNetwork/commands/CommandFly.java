package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.MessagesUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
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
        super("fly", false);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (isConsole) return MessagesUtils.noConsole(sender);
        Player target = executor;
        if (args.length == 1) target = Bukkit.getPlayer(args[0]);
        if (!Utils.isPlayerOnline(executor, target)) return true;
        fly(target, !flyingPlayers.contains(target.getUniqueId()), true);
        return true;
    }

    public void fly(Player player, boolean fly, boolean message) {
        if (fly) flyingPlayers.add(player.getUniqueId());
        else flyingPlayers.remove(player.getUniqueId());
        player.setAllowFlight(fly || player.getGameMode() == GameMode.CREATIVE);
        if (message) Utils.message(executor, fly ? "&aSet fly mode enabled" : "&cSet fly mode disabled");
    }
}
