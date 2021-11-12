package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import com.google.common.collect.Lists;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandLock extends AbstractCommand {

    private static final String[] HELP_MESSAGE = Utils.colorize(
      "&e------------ &7[&eLock Command&7] &e------------",
            "&7Below is a list of all home commands:",
            "  &6/lock set &7- &6Sets a lock on the right-clicked block",
            "  &6/lock remove &7- &6Removes the lock from the block you right-click on",
            "  &6/lock cancel &7- &6Cancels a pending lock",
            "  &6/lock help &7- &6Shows this message"
    );
    private static final Set<UUID> bypassingLock = new HashSet<>();
    private static final Map<UUID, Integer> peopleWantingLock = new HashMap<>();

    public CommandLock() {
        super("lock", Permission.BASIC, true);
    }
    // 1 as integer means removing the lock

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "set":
                    peopleWantingLock.put(executor.getUniqueId(), 0);
                    PlayerUtils.message(executor, "&aRight click a block to lock it!\nOr type /lock cancel to cancel");
                    break;
                case "cancel":
                    if (peopleWantingLock.remove(executor.getUniqueId(), 0) || peopleWantingLock.remove(executor.getUniqueId(), 1))
                        PlayerUtils.message(executor, "&aCancelled!");
                    else PlayerUtils.message(executor, "&cNothing to cancel!");
                    break;
                case "remove":
                    getPlayersWantingLock().put(executor.getUniqueId(), 1);
                    PlayerUtils.message(executor, "&aRight click a block to remove the lock!\nOr type &3/lock cancel&a to cancel");
                    break;
                case "bypass":
                    if (Permission.ADMIN.has(executor, true)) {
                        if (bypassingLock.remove(executor.getUniqueId())) {
                            PlayerUtils.message(executor, "&6Not longer bypassing locks!");
                        } else {
                            bypassingLock.add(executor.getUniqueId());
                            PlayerUtils.message(executor, "&aNow bypassing locks!");
                        }
                    }
                    break;
            }
        } else if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            PlayerUtils.message(executor,HELP_MESSAGE);
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
            if (Permission.ADMIN.has(executor)) l.add("bypass");
            return StringUtil.copyPartialMatches(args[0], l, new ArrayList<>());
        }
        return super.tabComplete();
    }
}
