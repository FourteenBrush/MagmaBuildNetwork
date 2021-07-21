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

    @Override
    protected boolean execute(@NotNull String[] args) {

        final boolean nightvision = ConfigManager.getConfigConfig().getBoolean("nightvision_during_vanish");
        if (args.length == 1) {
            if (!Utils.isPlayerOnline(p, args[0])) {
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            vanishPlayer(target, nightvision);
            Utils.message(p, "§aSuccessfully vanished " + target.getName());
            return true;
        } else if (args.length < 1) {
            vanishPlayer(p, nightvision);
        }
        return true;
    }

    private static void vanishPlayer(Player p, boolean nightvision) {
        if (vanishedPlayers.remove(p)) {
            // vanished -> unvanish
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showPlayer(plugin, p);
            }
            setBossBar(p, true);
            if (p.getGameMode() != GameMode.CREATIVE) {
                p.setAllowFlight(false);
            }
            p.setInvulnerable(false);
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            Utils.message(p, "§aYou became visible again");
        } else {
            // not vanished -> vanish
            vanishedPlayers.add(p.getUniqueId());
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!getVanishedPlayers().contains(player))
                    player.hidePlayer(plugin, p);
            }
            setBossBar(p, false);
            p.setAllowFlight(true);
            p.setInvulnerable(true);
            if (nightvision) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 200000, 1));
            }
            Utils.message(p, "§aYou have been vanished");
        }
    }

    private static void setBossBar(Player p, boolean remove) {
        NamespacedKey key = new NamespacedKey(plugin, "mbnbossbar");
        BossBar bar = Bukkit.createBossBar(key, "Vanished", BarColor.BLUE, BarStyle.SEGMENTED_20);
        bar.setColor(BarColor.BLUE);
        if (remove)
            bar.removePlayer(p);
        else
            bar.addPlayer(p);
    }

    public static Set<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }
}
