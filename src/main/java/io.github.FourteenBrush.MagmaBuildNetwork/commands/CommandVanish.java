package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CommandVanish extends BaseCommand {

    private static final Main plugin = Main.getInstance();
    private static final Set<UUID> vanishedPlayers = new HashSet<>();
    private static final BossBar bar = Bukkit.createBossBar(new NamespacedKey(plugin, "mbnbossbar"), "Vanished", BarColor.BLUE, BarStyle.SOLID);

    @Override
    protected boolean execute(@NotNull String[] args) {

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (!Utils.isPlayerOnline(p, target)) {
                return true;
            }
            vanish(target, !vanishedPlayers.contains(target.getUniqueId()));
            Utils.message(p, "§aSuccessfully vanished " + target.getName());
            return true;
        } else if (args.length < 1) {
            vanish(p, !vanishedPlayers.contains(p.getUniqueId()));
        }
        return true;
    }

    private static void vanishPlayer(Player p) {
        if (vanishedPlayers.remove(p.getUniqueId())) {
            // vanished -> unvanish
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showPlayer(plugin, p);
            }
            setBossBar(p, true);
            p.setAllowFlight(p.getGameMode() == GameMode.CREATIVE);
            p.setInvulnerable(false);
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            Utils.message(p, "§aYou became visible again");
        } else {
            // not vanished -> vanish
            vanishedPlayers.add(p.getUniqueId());
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!getVanishedPlayers().contains(player.getUniqueId()))
                    player.hidePlayer(plugin, p);
            }
            setBossBar(p, false);
            p.setAllowFlight(true);
            p.setInvulnerable(true);
            if (plugin.getConfig().getBoolean("nightvision_during_vanish")) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 200000, 1));
            }
            Utils.message(p, "§aYou have been vanished");
        }
    }
    // todo remove after test

    private void vanish(Player player, boolean vanish) {
        if (vanish) {
            vanishedPlayers.add(player.getUniqueId());
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (!getVanishedPlayers().contains(pl.getUniqueId()))
                    pl.hidePlayer(plugin, player);
            }
            if (plugin.getConfig().getBoolean("nightvision_during_vanish")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 200000, 1));
            }
            player.setAllowFlight(true);
            Utils.message(player, "§aYou have been vanished");
        } else {
            vanishedPlayers.remove(player.getUniqueId());
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.showPlayer(plugin, player);
            }
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE);
            Utils.message(player, "§aYou became visible again");
        }
        setBossBar(player, !vanish);
        player.setInvulnerable(vanish);
    }

    private static void setBossBar(Player p, boolean remove) {
        if (remove) {
            bar.removePlayer(p);
            bar.setVisible(false);
        } else {
            bar.addPlayer(p);
            bar.setVisible(true);
        }
    }

    public static Set<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }
}
