package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.*;
import org.bukkit.Bukkit;
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

public class CommandVanish extends CommandHandler implements IConsoleCommand {

    private static final CommandVanish INSTANCE = new CommandVanish();
    private static final Set<UUID> vanishedPlayers = new HashSet<>();
    private final BossBar bar;

    public CommandVanish() {
        super("vanish", Permission.MODERATOR, true);
        bar = Bukkit.createBossBar("Vanished", BarColor.BLUE, BarStyle.SOLID);
        bar.setProgress(1.0);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length == 0) {
            if (denyConsole()) return true;
            vanish(executor, !vanishedPlayers.remove(executor.getUniqueId()));
        } else if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "list": return getPlayers();
                case "fakequit":
                    if (denyConsole()) return true;
                    return fakeQuit();
                default:
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (!PlayerUtils.checkPlayerOnline(sender, target)) return true;
                    vanish(target, !vanishedPlayers.remove(target.getUniqueId()));
                    if (executor != target)
                        sender.sendMessage(Lang.VANISHED_OTHER_PLAYER.get(target.getName()));
            }
        }
        return true;
    }

    public static CommandVanish getInstance() {
        return INSTANCE;
    }

    public void vanish(Player player, boolean vanish) {
        if (vanish) {
            vanishedPlayers.add(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(pl -> {
                if (!vanishedPlayers.contains(pl.getUniqueId()))
                    pl.hidePlayer(plugin, player);
                if (pl != executor && Permission.MODERATOR.has(pl))
                    pl.sendMessage(Lang.VANISH_ANNOUNCE.get(player.getName()));
            });
            if (plugin.getConfig().getBoolean("nightvision-in-vanish"))
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 2000000, 1, false, false, false));
        } else {
            Bukkit.getOnlinePlayers().forEach(pl -> {
                pl.showPlayer(plugin, player);
                if (pl != executor && Permission.MODERATOR.has(pl))
                    pl.sendMessage(Lang.VANISH_BACK_VISIBLE_ANNOUNCE.get(player.getName()));
            });
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            FileConfiguration dataFile = plugin.getConfigManager().getData();
            List<String> list = dataFile.getStringList("vanished-players");
            if (!list.isEmpty()) {
                dataFile.getStringList("vanished-players").remove(player.getUniqueId().toString());
                plugin.getConfigManager().saveConfig(ConfigManager.FileType.DATA);
            }

        }
        player.setInvulnerable(vanish);
        CommandFly.getInstance().fly(executor, vanish, false);
        setBossBar(player, !vanish);
        player.sendMessage(vanish ? Lang.VANISH_ENABLED.get() : Lang.VANISH_DISABLED.get());
    }

    private void setBossBar(Player player, boolean remove) {
        if (remove) bar.removePlayer(player);
        else bar.addPlayer(player);
        bar.setVisible(!remove);
    }

    private boolean getPlayers() {
        if (vanishedPlayers.isEmpty()) {
            sender.sendMessage(Lang.VANISH_NO_VANISHED_PLAYERS.get());
        } else {
            StringBuilder builder = new StringBuilder();
            vanishedPlayers.forEach(uuid -> {
                if (builder.length() > 0)
                    builder.append(", ");
                builder.append(Bukkit.getPlayer(uuid).getName());
            });
            PlayerUtils.message(sender, "&6Vanished: " + builder);
        }
        return true;
    }

    private boolean fakeQuit() {
        if (vanishedPlayers.contains(executor.getUniqueId())) {
            executor.sendMessage(Lang.VANISH_ALREADY_VANISHED_FOR_QUIT.get());
        } else {
            vanish(executor, true);
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player != executor && Permission.MODERATOR.has(player)) {
                    player.sendMessage(Lang.VANISH_ANNOUNCE.get(executor.getName()));
                } else player.sendMessage(Lang.LEAVE_MESSAGE.get(executor.getName()));
            });
            executor.sendMessage(Lang.VANISH_ENABLED.get());
        }
        return true;
    }

    public static Set<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }


    @Override
    public List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("list", "fakequit"), new ArrayList<>());
        }
        return super.tabComplete(args);
    }
}
