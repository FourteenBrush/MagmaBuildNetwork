package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandVanish extends AbstractCommand implements ConsoleCommand {

    private static final Set<UUID> vanishedPlayers = new HashSet<>();
    private final BossBar bar;

    public CommandVanish() {
        super("vanish", Permission.ADMIN, false);
        bar = Bukkit.createBossBar(new NamespacedKey(plugin, "vanished"), "Vanished", BarColor.BLUE, BarStyle.SEGMENTED_10);
        bar.setProgress(1.0);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        Player target = args.length == 1 ? Bukkit.getPlayer(args[0]) : executor;
        if (!PlayerUtils.checkPlayerOnline(sender, target, false)) return true;
        vanish(target, !vanishedPlayers.remove(target.getUniqueId()));
        if (executor != target) PlayerUtils.message(sender, Lang.VANISHED_OTHER_PLAYER.get(target.getName()));
        return true;
    }

    public void vanish(Player player, boolean vanish) {
        if (vanish) {
            vanishedPlayers.add(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(pl -> {
                if (!vanishedPlayers.contains(pl.getUniqueId())) pl.hidePlayer(plugin, player);
            });
            if (plugin.getConfig().getBoolean("nightvision-in-vanish"))
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 2000000, 1, false, false, false));
        } else {
            Bukkit.getOnlinePlayers().forEach(pl -> pl.showPlayer(plugin, player));
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            FileConfiguration dataFile = plugin.getConfigManager().getData();
            if (Utils.isValidConfigurationSection(dataFile, "vanished-players")) {
                dataFile.getStringList("vanished-players").remove(player.getUniqueId().toString());
            }
        }
        player.setInvulnerable(vanish);
        Instances.COMMAND_FLY.fly(executor, vanish, false);
        setBossBar(player, !vanish);
        PlayerUtils.message(player, vanish ? Lang.VANISH_ENABLED.get() : Lang.VANISH_DISABLED.get());
    }

    private void setBossBar(Player player, boolean remove) {
        if (remove) bar.removePlayer(player);
        else bar.addPlayer(player);
        bar.setVisible(!remove);
    }

    public static Set<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }
}
