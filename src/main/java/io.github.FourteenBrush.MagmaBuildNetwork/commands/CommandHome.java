package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.CooldownManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Home;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.MessagesUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommandHome extends AbstractCommand {

    private final static FileConfiguration homesFile = ConfigManager.getHomes();
    private final CooldownManager cm = new CooldownManager();
    private final Map<UUID, Integer> uses = new HashMap<>();
    private Home home;
    private int homesLimit;

    public CommandHome() {
        super("home", true);
    }

    @Override
    public boolean execute(@NotNull String[] args) {

        if (isConsole) return MessagesUtils.noConsole(sender);

        if (args.length > 1 && args[0].equalsIgnoreCase("create")) {
            homesLimit = Utils.isAuthorized(executor, "admin") ? 4 : 2;
            setHome(args[1], executor.getLocation());
            return true;
        } else if (args.length > 1 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))) {
            removeHome(executor, args[1]);
            return true;
        } else if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
            getHomes(executor);
            return true;
        } else if (args.length > 1 && (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport"))) {
            //teleport(p, args[1]);
            handle(executor, args[1]);
            return true;
        } else if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Utils.message(executor, "&f--- &9Home command &f---",
                    "&9/home &7set - &fSets a new home",
                    "&9/home &7remove - &fremoves the provided home",
                    "&9/home &7list - &fshows a list of all your homes",
                    "&9/home &7teleport - &fteleports you to your home",
                    "&9/home &7help - &fshows this message");
        }
        return true;
    }

    private void setHome(String name, Location l) {
        if (hasHome(executor, name)) {
            Utils.message(executor, "&cYou already have a home with that name, please choose another!");
            return;
        } else if (hasLimitReached(executor)) {
            Utils.message(executor, "&cYou have reached the maximum amount of homes, please delete one first!");
            return;
        }
        homesFile.set("homes." + executor.getUniqueId() + "." + name + ".playerName", executor.getName());
        homesFile.set("homes." + executor.getUniqueId() + "." + name + ".location", l);
        ConfigManager.saveConfig(ConfigManager.FileType.HOMES);
        this.home = new Home(name, executor.getUniqueId(), l);
        Utils.message(executor, String.format("&aSuccessfully set your new home! [ %s ]", home.getName()));
        Utils.logDebug(home.toString(), Home.getBuffer().toString());
    }

    private void removeHome(Player p, String name) {
        if (hasHome(p, name)) {
            homesFile.set("homes." + p.getUniqueId() + "." + name, null);
            ConfigManager.saveConfig(ConfigManager.FileType.HOMES);
            Utils.message(p, "&aRemoved " + name + " from your homes");
        } else Utils.messageSpigot(p, Utils.suggestCommandByClickableText("&cHome not found! \nYou can use &6/home list &cto see all your homes", "/home list"));
    }

    private void getHomes(Player p) {
        if (hasHome(p)) {
            String path = "homes." + p.getUniqueId();
            Set<String> homes = new HashSet<>(homesFile.getConfigurationSection(path).getKeys(false));
            Utils.message(p, "&f--- &9Homes &f---");
            homes.forEach(home -> {
                Location loc = (Location) homesFile.get(path + "." + home + ".location");
                Utils.message(p, "&f" + home, String.format(" x: %s, y: %s, z: %s", (int) loc.getX(), (int) loc.getY(), (int) loc.getZ()));
            });
        } else Utils.message(p, "&cYou have no homes");
    }

    private Set<String> getHomesList(Player p) {
        return homesFile.getConfigurationSection("homes." + p.getUniqueId()).getKeys(false);
    }

    private void teleport(Player p, String name) {
        UUID uuid = p.getUniqueId();
        if (!CommandMagmabuildnetwork.getBypassingPlayers().contains(uuid)) {
            uses.putIfAbsent(uuid, 0);
            if (uses.get(uuid) < 2) {
                uses.put(uuid, uses.get(uuid) + 1);
            } else if (TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - cm.getCooldown(uuid)) < 1) {
                Utils.message(p, "&cPlease wait&e " + Utils.millisToReadable(TimeUnit.DAYS.toMillis(1) - System.currentTimeMillis() - cm.getCooldown(uuid)) + " &cbefore reusing this command!");
                return;
            }
        }
        Location loc = getLocation(p, name);
        if (loc != null) {
            loc.getChunk().load();
            p.teleport(loc);
            cm.setCooldown(uuid, System.currentTimeMillis());
            Utils.message(p,"&aTeleported to " + name);
        }
    }

    private void testTeleport(Player player, String name) {
        UUID uuid = player.getUniqueId();
        if (uses.merge(uuid, 1, Integer::sum) > 2) {
            // check cooldown
            long timeLeft = System.currentTimeMillis() - cm.getCooldown(uuid);
            if (TimeUnit.MILLISECONDS.toDays(timeLeft) < 1) {
                Utils.message(player, "&cPlease wait&e " + Utils.millisToReadable(TimeUnit.DAYS.toMillis(1) - timeLeft) + " &cbefore resuing this command!");
                return;
            }
            // teleport
        }
    }

    private void handle(Player p, String name) {
        UUID uuid = p.getUniqueId();
        if (CommandMagmabuildnetwork.getBypassingPlayers().contains(uuid) || !uses.containsKey(uuid) ||
                (uses.containsKey(uuid) && uses.get(uuid) < 2)) {
            Location loc = getLocation(p, name);
            if (loc != null) {
                if (!loc.getChunk().isLoaded()) loc.getChunk().load();
                p.teleport(loc);
                cm.setCooldown(uuid, System.currentTimeMillis());
                uses.put(uuid, uses.containsKey(uuid) ? uses.get(uuid) + 1 : 1);
                Utils.message(p,"&aTeleported to " + name);
            }
        } else {
            long timeLeft = System.currentTimeMillis() - cm.getCooldown(uuid);
            if (TimeUnit.MILLISECONDS.toDays(timeLeft) < 1) {
                Utils.message(p, "&cPlease wait&e " + Utils.millisToReadable(TimeUnit.DAYS.toMillis(1) - timeLeft) + " &cbefore reusing this command!");
            }
        }
    }

    private boolean hasHome(Player p, String name) {
        return homesFile.contains("homes." + p.getUniqueId() + "." + name);
    }

    private boolean hasHome(Player p) {
        return homesFile.isConfigurationSection("homes." + p.getUniqueId()) && !homesFile.getConfigurationSection("homes." + p.getUniqueId()).getKeys(false).isEmpty();
    }

    private boolean hasLimitReached(Player player) {
        return hasHome(executor) && homesFile.getConfigurationSection("homes." + player.getUniqueId()).getKeys(false).size() >= homesLimit;
    }

    private @Nullable Location getLocation(Player p, String name) {
        if (hasHome(p, name)) return (Location) homesFile.get("homes." + p.getUniqueId() + "." + name + ".location");
        else Utils.suggestCommandByClickableText(executor, "&cHome not found! \nYou can use ", "&6/home list ", "&cto see all your homes", "/home list");
        //else Utils.messageSpigot(p, Utils.suggestCommandByClickableText("&cHome not found! \nYou can use &6/home list &cto see all your homes", "/home list"));
        return null;
    }

    @Override
    protected List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("create", "remove", "list", "teleport", "help"), new ArrayList<>());
        } else if (args.length == 2 && args[1].equalsIgnoreCase("remove") && hasHome(executor)) {
            return StringUtil.copyPartialMatches(args[0], getHomesList(executor), new ArrayList<>());
        }
        return null;
    }
}
