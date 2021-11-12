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
import org.bukkit.util.StringUtil;
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
        if (args.length == 0) {
            if (isConsoleSender) {
                PlayerUtils.message(sender, Lang.NO_CONSOLE.get());
                return true;
            }
            vanish(executor, !vanishedPlayers.remove(executor.getUniqueId()));
        } else if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "list":
                    StringBuilder builder = new StringBuilder();
                    vanishedPlayers.forEach(uuid -> builder.append(Bukkit.getPlayer(uuid).getName()).append(", "));
                    PlayerUtils.message(executor, "&e" + builder.toString());
                case "fakequit":
                    if (vanishedPlayers.contains(executor.getUniqueId())) {
                        PlayerUtils.message(executor, "&cAlready vanished!");
                    } else {
                        
                    }
                default:
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (!PlayerUtils.checkPlayerOnline(sender, target, true)) return true;
                    vanish(target, !vanishedPlayers.remove(target.getUniqueId()));
            }
        }
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
                if (!vanishedPlayers.contains(pl.getUniqueId()))
                    pl.hidePlayer(plugin, player);
                if (pl != executor && Permission.MODERATOR.has(pl))
                    PlayerUtils.message(pl, Lang.VANISH_ANNOUNCE.get(player.getName()));
            });
            if (plugin.getConfig().getBoolean("nightvision-in-vanish"))
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 2000000, 1, false, false, false));
        } else {
            Bukkit.getOnlinePlayers().forEach(pl -> {
                pl.showPlayer(plugin, player);
                if (pl != executor && Permission.MODERATOR.has(pl))
                    PlayerUtils.message(pl, "&e" + player.getName() + " has become visible.");
            });
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            FileConfiguration dataFile = plugin.getConfigManager().getData();
            if (Utils.isValidConfigurationSection(dataFile, "vanished-players"))
                dataFile.getStringList("vanished-players").remove(player.getUniqueId().toString());
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

    @Override
    protected List<String> tabComplete(@NotNull String... args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("list", "fakequit"), new ArrayList<>());
        }
        return super.tabComplete(args);
    }
}
