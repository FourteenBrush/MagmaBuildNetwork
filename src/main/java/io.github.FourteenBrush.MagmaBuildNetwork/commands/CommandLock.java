package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommandLock extends BaseCommand {

    private static Set<UUID> bypassingLock = new HashSet<>();
    private static final Map<UUID, Integer> peopleWantingLock = new HashMap<>();
    // 1 as integer means removing the lock

    @Override
    protected boolean execute(@NotNull String[] args) {

        if (isConsole) return true;

        if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
            getPlayersWantingLock().put(p.getUniqueId(), 0);
            Utils.message(p, "§aRight click a block to lock it!\nOr type /lock cancel to cancel");

        } else if (args.length == 1 && args[0].equalsIgnoreCase("cancel")) {
            if (getPlayersWantingLock().remove(p.getUniqueId(), 0) || getPlayersWantingLock().remove(p.getUniqueId(), 1)) {
                Utils.message(p, "§aCancelled!");
            } else {
                Utils.message(p, "§cNothing to cancel!");
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
            getPlayersWantingLock().put(p.getUniqueId(), 1);
            Utils.message(p, "§aRight click a block to remove the lock!\nOr type /lock cancel to cancel");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("bypass")) {
           if (bypassingLock.contains(p.getUniqueId())) {
               bypassingLock.remove(p.getUniqueId());
               Utils.message(p, "§6Not longer bypassing locks!");
           } else {
               bypassingLock.add(p.getUniqueId());
               Utils.message(p, "§aNow bypassing locks!");
           }
        } else if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Utils.message(p, new String[] {"§f--- §9Lock command §f---",
                    "§9/lock §7set - §fsets a lock on the block you right-click on",
                    "§9/lock §7remove - §fremoves the lock from the block you right-click on",
                    "§9/lock §7cancel - §fcancels the lock creation",
                    "§9/lock §7help - §fshows this message"});
        }
        return true;
    }

    public static Map<UUID, Integer> getPlayersWantingLock() {
        return peopleWantingLock;
    }

    public static Set<UUID> getBypassingLock() {
        return bypassingLock;
    }

    @Override
    protected @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        arguments.add("set");
        arguments.add("remove");
        arguments.add("cancel");
        arguments.add("help");
        if (Utils.isAuthorized(p, "admin"))
            arguments.add("bypass");
        return StringUtil.copyPartialMatches(args[0], arguments, new ArrayList<>());
    }
}
