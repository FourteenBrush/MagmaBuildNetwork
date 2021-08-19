package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Home;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommandHome extends BaseCommand {

    private int homesLimit;
    private final static FileConfiguration homesFile = ConfigManager.getHomes();
    private final static Map<UUID, Home> homes = new HashMap<>(); // todo

    @Override
    protected boolean execute(@NotNull String[] args) {

        if (isConsole) return true;

        if (args.length > 1 && args[0].equalsIgnoreCase("set")) {
            homesLimit = Utils.isAuthorized(p, "admin") ? 5 : 2;
            setHome(args[1], p.getLocation());
            return true;
        } else if (args.length > 1 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))) {
            removeHome(p, args[1]);
            return true;
        } else if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
            getHomes(p);
            return true;
        } else if (args.length > 1 && (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport"))) {
            teleport(p, args[1]);
            return true;
        } else if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Utils.message(p, new String[]{"§f--- §9Home command §f---",
                    "§9/home §7set - §fSets a new home",
                    "§9/home §7remove - §fremoves the provided home",
                    "§9/home §7list - §fshows a list of all your homes",
                    "§9/home §7tp - §fteleports you to your home",
                    "§9/home §7help - §fshows this message"});
        }
        return true;
    }

    private void setHome(String name, Location l) {
        if (hasHome(p, name)) {
            Utils.message(p, "§cYou already have a home with that name, please try again!");
            return;
        } else if (hasLimitReached(p)) {
            Utils.message(p, "§cYou have reached the maximum amount of homes, please delete one first!");
            return;
        }
        homes.put(p.getUniqueId(), new Home(name, p.getUniqueId(), p.getLocation()));
        String temp = name;
        name = "homes." + p.getUniqueId() + "." + name;
        homesFile.set(name + ".playerName", p.getName());
        homesFile.set(name + ".location", l);
        ConfigManager.saveConfig();
        Utils.message(p, String.format("§aSuccessfully set your new home! [%s]", temp));
    }

    private void removeHome(Player p, String name) {
        if (hasHome(p, name)) {
            homesFile.set("homes." + p.getUniqueId() + "." + name, null);
            ConfigManager.saveConfig();
            Utils.message(p, "§aRemoved " + name);
            return;
        }
        Utils.message(p, new String[]{"§cHome not found!", "§cYou can use /home list to see all your homes"});
    }

    private void getHomes(Player p) {
        if (hasAtleastOneHome(p)) {
            String path = "homes." + p.getUniqueId();
            List<String> homes = new ArrayList<>(homesFile.getConfigurationSection(path).getKeys(false));
            Utils.message(p, "§f--- §9Homes §f---");
            homes.forEach(home -> {
                Location loc = (Location) homesFile.get(path + "." + home + ".location");
                Utils.message(p, new String[] {"§f" + home, String.format("x: %s, y: %s, z: %s", (int) loc.getX(), (int) loc.getY(), (int) loc.getZ())});
            });
            return;
        }
        Utils.message(p, "§cYou have no homes");
    }


    private void teleport(Player p, String name) {
        Location loc = getLocation(p, name);
        if (loc != null) {
            if (!loc.getChunk().isLoaded()) {
                loc.getChunk().load();
            }
            p.teleport(loc);
            Utils.message(p,"§aTeleported to " + name);
        }
    }

    private boolean hasHome(Player p, String name) {
        return homesFile.contains("homes." + p.getUniqueId() + "." + name);
    }

    private boolean hasAtleastOneHome(Player p) {
        return homesFile.contains("homes." + p.getUniqueId()) &&
                homesFile.getConfigurationSection("homes." + p.getUniqueId()).getKeys(false).size() > 0;
    }

    private boolean hasLimitReached(Player player) {
        if (hasAtleastOneHome(p)) {
            return homesFile.getConfigurationSection("homes." + player.getUniqueId()).getKeys(false).size() >= homesLimit;
        }
        return false;
    }

    private @Nullable Location getLocation(Player p, String name) {
        Location loc = null;
        if (hasHome(p, name)) {
            loc = (Location) homesFile.get("homes." + p.getUniqueId() + "." + name + ".location");
        } else {
            Utils.message(p, new String[] {"§cHome not found!", "§cYou can use /home list to see all your homes"});
        }
        return loc;
    }

    @Override
    protected List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            arguments.addAll(Arrays.asList("set", "remove", "list", "tp", "help"));
            return StringUtil.copyPartialMatches(args[0], arguments, new ArrayList<>());
        }
        return new ArrayList<>();
    }
}
