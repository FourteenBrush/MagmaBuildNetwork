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
import java.util.List;
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

    private static void vanish(Player player, boolean vanish) {
        if (vanish) {
            vanishedPlayers.add(player.getUniqueId());
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (!getVanishedPlayers().contains(pl.getUniqueId()))
                    pl.hidePlayer(plugin, player);
            }
            if (plugin.getConfig().getBoolean("nightvision_during_vanish")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 2000000, 1, false, false, false));
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
        } else {
            bar.addPlayer(p);
        }
        bar.setVisible(!remove);
    }

    public static Set<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public static void save() {
        ConfigManager.getDataConfig().set("vanished_players", vanishedPlayers);
    }

    public static void load(List<UUID> list) {
        list.forEach((s) -> vanish(Bukkit.getPlayer(s), true));
    }
}
