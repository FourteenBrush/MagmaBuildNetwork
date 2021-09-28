package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandVanish extends AbstractCommand {

    private static final Set<UUID> vanishedPlayers = new HashSet<>();
    private static final BossBar bar = Bukkit.createBossBar(new NamespacedKey(plugin, "mbnbossbar"), "Vanished", BarColor.BLUE, BarStyle.SOLID);

    public CommandVanish() {
        super("vanish", false);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        Player target = args.length == 1 ? Bukkit.getPlayer(args[0]) : executor;
        if (!Utils.isPlayerOnline(sender, target)) return true;
        vanish(target, !vanishedPlayers.contains(target.getUniqueId()));
        if (executor != target) Utils.message(sender, "&aSuccessfully vanished " + target.getName());
        return true;
    }

    private void vanish(Player player, boolean vanish) {
        if (vanish) {
            vanishedPlayers.add(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(pl -> {
                if (!vanishedPlayers.contains(pl.getUniqueId())) pl.hidePlayer(plugin, player);
            });
            if (plugin.getConfig().getBoolean("nightvision_during_vanish"))
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 2000000, 1, false, false, false));
        } else {
            vanishedPlayers.remove(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(pl -> pl.showPlayer(plugin, player));
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
        player.setInvulnerable(vanish);
        new CommandFly().fly(executor, vanish, false);
        setBossBar(player, !vanish);
        Utils.message(player, vanish ? "&aYou have been vanished" : "&aYou became visible again");
    }

    private void setBossBar(Player p, boolean remove) {
        if (remove) bar.removePlayer(p);
        else bar.addPlayer(p);
        bar.setVisible(!remove);
    }

    public static Set<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public static void save() {
        ConfigManager.getData().set("vanished-players", vanishedPlayers.toString());
        ConfigManager.saveConfig(ConfigManager.FileType.DATA);
    }

    public void load(List<String> list) {
        list.forEach(s -> vanish(Bukkit.getPlayer(UUID.fromString(s)), true));
    }
}
