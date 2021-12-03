package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandMaintenance extends CommandHandler implements IConsoleCommand {

    private boolean maintenanceEnabled;

    public CommandMaintenance() {
        super("maintenance", Permission.ADMIN, false);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("enable")) {
                if (maintenanceEnabled) {
                    sender.sendMessage(Lang.MAINTENANCE_ALREADY_ENABLED.get());
                } else {
                    maintenance(true);
                    sender.sendMessage(Lang.MAINTENANCE_ENABLED.get());
                }
            } else if (args[0].equalsIgnoreCase("disable")) {
                if (!maintenanceEnabled) {
                    sender.sendMessage(Lang.MAINTENANCE_ALREADY_DISABLED.get());
                } else {
                    maintenance(false);
                    sender.sendMessage(Lang.MAINTENANCE_DISABLED.get());
                }
            }
        }
        return true;
    }

    private void maintenance(boolean enable) {
        if (enable) {
            startCountdown();
        }
        plugin.getConfig().set("maintenance.enabled", enable);
        maintenanceEnabled = enable;
    }

    private void startCountdown() {
        final int countdown = plugin.getConfig().getInt("maintenance.countdown");
        List<String> announceList = plugin.getConfig().getStringList("maintenance.announce");
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String str : announceList) {
                   if (countdown == Integer.parseInt(ChatColor.stripColor(str))) {
                       Bukkit.getOnlinePlayers().forEach(player ->
                               player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 2L, 2L));
                   }
                }
            }
        }.runTaskTimer(plugin, 1L, 20L);
    }

    public boolean isMaintenanceEnabled() {
        return maintenanceEnabled;
    }
}
