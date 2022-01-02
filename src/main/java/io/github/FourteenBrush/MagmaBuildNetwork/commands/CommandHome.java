package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.CooldownManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommandHome extends CommandHandler {

    private final String[] helpMessage = Utils.colorize(
            "&e------------ &7[&eHome Command&7] &e------------",
            "&7Below is a list of all home commands:",
            "  &6/home create <name> &7- &6Creates a new home",
            "  &6/home remove <name> &7- &6Removes the home with that name",
            "  &6/home list &7- &6Shows a list of all your homes",
            "  &6/home teleport <name> &7- &6Teleports you to the home with that name",
            "  &6/home help &7- &6Shows this message"
    );
    private final FileConfiguration homes;
    private final Map<UUID, Integer> uses;
    private final CooldownManager cm;
    private int homesLimit;

    public CommandHome() {
        super("home", Permission.BASIC, true);
        homes = plugin.getConfigManager().getHomes();
        cm = new CooldownManager();
        uses = new HashMap<>();
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "set":
                case "create":
                    homesLimit = Permission.ADMIN.has(executor) ? 5 : 2;
                    return setHome(args[1], executor.getLocation());
                case "teleport":
                case "tp": return teleport(args[1]);
                case "remove":
                case "delete": return removeHome(args[1]);
                default: executor.sendMessage(helpMessage);
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            return getHomes();
        } else executor.sendMessage(helpMessage);
        return true;
    }

    private boolean setHome(String name, Location location) {
        if (hasHome(name)) {
            executor.sendMessage(Lang.HOME_ALREADY_EXISTS.get());
        } else if (hasLimitReached()) {
            executor.sendMessage(Lang.HOME_LIMIT_REACHED.get());
        } else {
            homes.set("homes." + executor.getUniqueId() + "." + name + ".location", location);
            plugin.getConfigManager().saveConfig(ConfigManager.FileType.HOMES);
            executor.sendMessage(Lang.HOME_CREATED_SUCCESS.get(name));
        }
        return true;
    }

    private boolean removeHome(String name) {
        if (hasHome(name)) {
            homes.set("homes." + executor.getUniqueId() + "." + name, null);
            plugin.getConfigManager().saveConfig(ConfigManager.FileType.HOMES);
            executor.sendMessage(Lang.HOME_REMOVED_SUCCESS.get(name));
        } else messageNoHomesFound();
        return true;
    }

    private boolean getHomes() {
        if (hasHome()) {
            String path = "homes." + executor.getUniqueId();
            Set<String> homes = this.homes.getConfigurationSection(path).getKeys(false);
            PlayerUtils.message(executor, "&e------------ &7[&eHomes&7] &e------------", "&7Below is a list of all your homes:");
            homes.forEach(home -> {
                Location loc = (Location) this.homes.get(path + "." + home + ".location");
                PlayerUtils.message(executor, String.format("&6  %s: [x: %s, y: %s, z: %s]", home, (int) loc.getX(), (int) loc.getY(), (int) loc.getZ()));
            });
        } else PlayerUtils.message(executor, new ComponentBuilder()
                .append(Utils.colorize("&cYou have no homes, click"))
                .append(Utils.colorize(" &6here "))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to create a home")))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/home create NAME"))
                .append(Utils.colorize("&cto create a home at your current position"))
                .event((HoverEvent) null)
                .event((ClickEvent) null)
                .create());
        return true;
    }

    private Set<String> getHomesList() {
        return homes.getConfigurationSection("homes." + executor.getUniqueId()).getKeys(false);
    }

    private boolean teleport(String name) {
        UUID uuid = executor.getUniqueId();
        if (!CommandMagmabuildnetwork.isBypassing(uuid) && uses.merge(uuid, 1, Integer::sum) > 2) {
            long timeLeft = System.currentTimeMillis() - cm.getCooldown(uuid);
            if (TimeUnit.MILLISECONDS.toDays(timeLeft) < 1) {
                executor.sendMessage(Lang.COMMAND_COOLDOWN.get(Utils.millisToReadable(TimeUnit.DAYS.toMillis(1) - timeLeft)));
                return true;
            }
        }
        Location loc = getLocation(name);
        if (loc == null) return true;
        if (!loc.getChunk().isLoaded()) {
            loc.getChunk().load();
        }
        executor.teleport(loc);
        // makes sure bypassed uses don't count as an use
        if (!CommandMagmabuildnetwork.isBypassing(uuid))
            cm.setCooldown(uuid, System.currentTimeMillis());
        executor.sendMessage(Lang.HOME_TELEPORTED.get(name));
        return true;
    }

    private boolean hasHome(String name) {
        return Utils.isValidConfigurationSection(homes, "homes." + executor.getUniqueId() + "." + name);
    }

    private boolean hasHome() {
        return Utils.isValidConfigurationSection(homes, "homes." + executor.getUniqueId());
    }

    private boolean hasLimitReached() {
        return hasHome() && homes.getConfigurationSection("homes." + executor.getUniqueId()).getKeys(false).size() >= homesLimit;
    }

    @Nullable
    private Location getLocation(String name) {
        if (hasHome(name)) return (Location) homes.get("homes." + executor.getUniqueId() + "." + name + ".location");
        else messageNoHomesFound();
        return null;
    }

    private void messageNoHomesFound() {
        PlayerUtils.message(executor, new ComponentBuilder()
                .append(Utils.colorize("&cNo homes found, click"))
                .append(Utils.colorize(" &6&lhere "))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to see your homes!")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home list"))
                .append(Utils.colorize("&cto see all your homes"))
                .event((HoverEvent) null)
                .event((ClickEvent) null)
                .create()
        );
    }

    @Override
    public List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("create", "remove", "list", "teleport", "help"), new ArrayList<>());
        } else if (args.length == 2 && hasHome()) {
            switch (args[0].toLowerCase()) {
                case "remove":
                case "delete":
                case "teleport":
                case "tp":
                    return StringUtil.copyPartialMatches(args[1], getHomesList(), new ArrayList<>());
            }
        }
        return super.tabComplete(args);
    }
}
