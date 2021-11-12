package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.library.CooldownManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommandHome extends AbstractCommand {

    private static final String[] HELP_MESSAGE = Utils.colorize(
            "&e------------ &7[&eHome Command&7] &e------------",
            "&7Below is a list of all home commands:",
            "  &6/home create <name> &7- &6Creates a new home",
            "  &6/home remove <name> &7- &6Removes the home with that name",
            "  &6/home list &7- &6Shows a list of all your homes",
            "  &6/home teleport <name> &7- &6Teleports you to the home with that name",
            "  &6/home help &7- &6Shows this message"
    );
    private final FileConfiguration homesFile = plugin.getConfigManager().getHomes();
    private final Map<UUID, Integer> uses;
    private final CooldownManager cm;
    private int homesLimit;

    public CommandHome() {
        super("home", Permission.BASIC, true);
        cm = new CooldownManager();
        uses = new HashMap<>();
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length > 1) {
            switch (args[0].toLowerCase()) {
                case "set":
                case "create":
                    homesLimit = Permission.ADMIN.has(executor) ? 5 : 2;
                    return setHome(args[1], executor.getLocation());
                case "teleport":
                case "tp":
                    return teleport(executor, args[1]);
                case "remove":
                case "delete":
                    return removeHome(args[1]);
            }
        } else if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            PlayerUtils.message(executor, HELP_MESSAGE);
        } else if (args[0].equalsIgnoreCase("list")) {
            return getHomes();
        }
        return true;
    }

    private boolean setHome(String name, Location location) {
        if (hasHome(name)) {
            PlayerUtils.message(executor, Lang.HOME_ALREADY_EXISTS.get());
        } else if (hasLimitReached()) {
            PlayerUtils.message(executor, Lang.HOME_LIMIT_REACHED.get());
        } else {
            homesFile.set("homes." + executor.getUniqueId() + "." + name + ".playerName", executor.getName());
            homesFile.set("homes." + executor.getUniqueId() + "." + name + ".location", location);
            plugin.getConfigManager().saveConfig(ConfigManager.FileType.HOMES);
            PlayerUtils.message(executor, Lang.HOME_CREATED_SUCCESS.get(name));
        }
        return true;
    }

    private boolean removeHome(String name) {
        if (hasHome(name)) {
            homesFile.set("homes." + executor.getUniqueId() + "." + name, null);
            plugin.getConfigManager().saveConfig(ConfigManager.FileType.HOMES);
            PlayerUtils.message(executor, Lang.HOME_REMOVED_SUCCESS.get(name));
        } else messageNoHomesFound();
        return true;
    }

    private boolean getHomes() {
        if (hasHome()) {
            String path = "homes." + executor.getUniqueId();
            Set<String> homes = new HashSet<>(homesFile.getConfigurationSection(path).getKeys(false));
            PlayerUtils.message(executor, "&f--- &9Homes &f---");
            homes.forEach(home -> {
                Location loc = (Location) homesFile.get(path + "." + home + ".location");
                PlayerUtils.message(executor, "&f" + home, String.format(" x: %s, y: %s, z: %s", (int) loc.getX(), (int) loc.getY(), (int) loc.getZ()));
            });
        } else PlayerUtils.message(executor, new ComponentBuilder()
                .append(Utils.colorize("&cYou have no homes, click"))
                .append(Utils.colorize(" &6here "))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to create a home")))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/home create NAME_HERE"))
                .append(Utils.colorize("&cto create a home at your current position"))
                .event((HoverEvent) null)
                .event((ClickEvent) null)
                .create());
        return true;
    }

    private Set<String> getHomesList() {
        return homesFile.getConfigurationSection("homes." + executor.getUniqueId()).getKeys(false);
    }

    private boolean teleport(Player player, String name) {
        UUID uuid = player.getUniqueId();
        if (!CommandMagmabuildnetwork.isBypassing(uuid) && uses.merge(uuid, 1, Integer::sum) > 2) {
            long timeLeft = System.currentTimeMillis() - cm.getCooldown(uuid);
            if (TimeUnit.MILLISECONDS.toDays(timeLeft) < 1) {
                PlayerUtils.message(player, "&cPlease wait&e " + Utils.millisToReadable(TimeUnit.DAYS.toMillis(1) - timeLeft) + " &cbefore resuing this command!");
                return false;
            }
        }
        Location loc = getLocation(name);
        if (loc != null) {
            if (!loc.getChunk().isLoaded())
                loc.getChunk().load();
            player.teleport(loc);
            // makes sure bypassed uses don't count as an use
            if (!CommandMagmabuildnetwork.isBypassing(uuid))
                cm.setCooldown(uuid, System.currentTimeMillis());
            PlayerUtils.message(player,"&aTeleported to&6 " + name);
        }
        return true;
    }

    private boolean hasHome(String name) {
        return Utils.isValidConfigurationSection(homesFile, "homes." + executor.getUniqueId() + "." + name);
    }

    private boolean hasHome() {
        return Utils.isValidConfigurationSection(homesFile, "homes." + executor.getUniqueId());
    }

    private boolean hasLimitReached() {
        return hasHome() && homesFile.getConfigurationSection("homes." + executor.getUniqueId()).getKeys(false).size() >= homesLimit;
    }

    @Nullable
    private Location getLocation(String name) {
        if (hasHome(name)) return (Location) homesFile.get("homes." + executor.getUniqueId() + "." + name + ".location");
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
    protected List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("create", "remove", "list", "teleport", "help"), new ArrayList<>());
        } else if (args.length == 2 && hasHome()) {
            switch(args[0].toLowerCase()) {
                case "remove":
                case "delete":
                case "teleport":
                case "tp":
                    return StringUtil.copyPartialMatches(args[1], getHomesList(), new ArrayList<>());
            }
        }
        return super.tabComplete();
    }
}
