package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CommandVanish extends BaseCommand {

    private static final Main plugin = Main.getInstance();
    private static final Set<Player> vanishedPlayers = new HashSet<>();

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
        if (vanishedPlayers.contains(p)) {
            // vanished -> unvanish
            vanishedPlayers.remove(p);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showPlayer(plugin, p);
            }
            if (p.getGameMode() != GameMode.CREATIVE) {
                p.setAllowFlight(false);
            }
            p.setInvulnerable(false);
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            Utils.message(p, "§aYou became visible again");
        } else {
            // not vanished -> vanish
            vanishedPlayers.add(p);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!getVanishedPlayers().contains(player))
                    player.hidePlayer(plugin, p);
            }
            p.setAllowFlight(true);
            p.setInvulnerable(true);
            if (nightvision) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 200000, 1));
            }
            Utils.message(p, "§aYou have been vanished");
        }
    }

    public static Set<Player> getVanishedPlayers() {
        return vanishedPlayers;
    }
}
