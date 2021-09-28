package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import com.google.common.collect.Lists;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.MessagesUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandLock extends AbstractCommand {

    private static final Set<UUID> bypassingLock = new HashSet<>();
    private static final Map<UUID, Integer> peopleWantingLock = new HashMap<>();

    public CommandLock() {
        super("lock", true);
    }
    // 1 as integer means removing the lock

    @Override
    public boolean execute(@NotNull String[] args) {

        if (isConsole) return MessagesUtils.noConsole(sender);

        if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
            getPlayersWantingLock().put(executor.getUniqueId(), 0);
            Utils.message(executor, "&aRight click a block to lock it!\nOr type /lock cancel to cancel");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("cancel")) {
            if (getPlayersWantingLock().remove(executor.getUniqueId(), 0) || getPlayersWantingLock().remove(executor.getUniqueId(), 1)) {
                Utils.message(executor, "&aCancelled!");
            } else {
                Utils.message(executor, "&cNothing to cancel!");
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
            getPlayersWantingLock().put(executor.getUniqueId(), 1);
            Utils.message(executor, "&aRight click a block to remove the lock!\nOr type /lock cancel to cancel");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("bypass")) {
            if (Utils.isAuthorized(executor, "admin")) {
                if (bypassingLock.remove(executor.getUniqueId())) {
                    Utils.message(executor, "&6Not longer bypassing locks!");
                } else {
                    bypassingLock.add(executor.getUniqueId());
                    Utils.message(executor, "&aNow bypassing locks!");
                }
            }
        } else if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Utils.message(executor, "&f--- &9Lock command &f---",
                    "&9/lock &7set - &fsets a lock on the block you right-click on",
                    "&9/lock &7remove - &fremoves the lock from the block you right-click on",
                    "&9/lock &7cancel - &fcancels the lock creation",
                    "&9/lock &7help - &fshows this message");
        }
        return true;
    }

    public static Map<UUID, Integer> getPlayersWantingLock() {
        return peopleWantingLock;
    }

    public static Set<UUID> getPlayersBypassingLock() {
        return bypassingLock;
    }

    @Override
    protected List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            List<String> l = Lists.newArrayList("set", "remove", "cancel", "help");
            if (Utils.isAuthorized(executor, "admin")) l.add("bypass");
            return StringUtil.copyPartialMatches(args[0], l, new ArrayList<>());
        }
        return null;
    }
}
