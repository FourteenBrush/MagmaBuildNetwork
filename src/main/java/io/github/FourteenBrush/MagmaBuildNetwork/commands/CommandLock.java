package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import com.google.common.collect.Lists;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandLock extends CommandHandler {

    private final String[] helpMessage = Utils.colorize(
      "&e------------ &7[&eLock Command&7] &e------------",
            "&7Below is a list of all home commands:",
            "  &6/lock set &7- &6Sets a lock on the right-clicked block",
            "  &6/lock remove &7- &6Removes the lock from the block you right-click on",
            "  &6/lock cancel &7- &6Cancels a pending lock",
            "  &6/lock help &7- &6Shows this message"
    );
    private static final Set<UUID> bypassingLock = new HashSet<>();
    private static final Map<UUID, Integer> playersWantingLock = new HashMap<>();
    // 1 as integer means removing the lock

    public CommandLock() {
        super("lock", Permission.BASIC, true);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "set": return setLock();
                case "remove": return removeLock();
                case "cancel": return cancelLock();
                case "bypass": return bypassLock();
                default: executor.sendMessage(helpMessage);
            }
        } else {
            executor.sendMessage(helpMessage);
        }
        return true;
    }

    private boolean setLock() {
        playersWantingLock.put(executor.getUniqueId(), 0);
        executor.sendMessage(Lang.LOCK_PLACE_LOCK.get());
        return true;
    }

    private boolean removeLock() {
        playersWantingLock.put(executor.getUniqueId(), 1);
        executor.sendMessage(Lang.LOCK_REMOVE_LOCK.get());
        return true;
    }

    private boolean cancelLock() {
        if (playersWantingLock.remove(executor.getUniqueId(), 0) || playersWantingLock.remove(executor.getUniqueId(), 1))
            executor.sendMessage(Lang.LOCK_CANCELLED.get());
        else executor.sendMessage(Lang.LOCK_NOTHING_TO_CANCEL.get());
        return true;
    }

    private boolean bypassLock() {
        if (Permission.ADMIN.has(executor, true)) {
            if (bypassingLock.remove(executor.getUniqueId())) {
                executor.sendMessage(Lang.LOCK_NOT_LONGER_BYPASSING.get());
            } else {
                bypassingLock.add(executor.getUniqueId());
                executor.sendMessage(Lang.LOCK_BYPASSING.get());
            }
        }
        return true;
    }

    public static Map<UUID, Integer> getPlayersWantingLock() {
        return playersWantingLock;
    }

    public static Set<UUID> getPlayersBypassingLock() {
        return bypassingLock;
    }


    @Override
    public List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            List<String> l = Lists.newArrayList("set", "remove", "cancel", "help");
            if (Permission.ADMIN.has(executor)) l.add("bypass");
            return StringUtil.copyPartialMatches(args[0], l, new ArrayList<>());
        }
        return super.tabComplete(args);
    }
}
