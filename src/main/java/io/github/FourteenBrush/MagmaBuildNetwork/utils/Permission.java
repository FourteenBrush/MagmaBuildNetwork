package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import org.bukkit.entity.Player;

public enum Permission {

    BASIC("magmabuildnetwork.basic"),
    TRAILS("magmabuildnetwork.trails"),
    SAFECHEST("magmabuildnetwork.safechest"),
    MAINTENANCE_BYPASS("magmabuildnetwork.maintenance-bypass"),
    CHANNELS_JOIN("magmabuildnetwork.channels.join."),
    CHANNELS_MODERATE("magmabuildnetwork.channels.moderate."),
    CHANNELS_AUTOJOIN("magmabuildnetwork.channels.autojoin."),
    CHANNELS_SETMAIN("magmabuildnetwork.channels.setmain."),
    CHANNELS_SEE_ALL("magmabuildnetwork.channels.see-all"),
    MODERATOR("magmabuildnetwork.moderator"),
    ADMIN("magmabuildnetwork.admin");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String get() {
        return permission;
    }

    public boolean has(Player player) {
        return player.hasPermission(permission);
    }

    public boolean has(Player player, boolean notifyFalse) {
        if (has(player)) return true;
        else if (notifyFalse) player.sendMessage(Lang.NO_PERMISSION.get());
        return false;
    }

    public boolean has(Player player, boolean notifyFalse, String... subNodes) {
        for (String str : subNodes) {
            if (player.hasPermission(permission + str)) return true;
        }
        if (notifyFalse) player.sendMessage(Lang.NO_PERMISSION.get());
        return false;
    }
}
