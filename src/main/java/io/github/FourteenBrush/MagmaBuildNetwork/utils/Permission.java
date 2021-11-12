package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import org.bukkit.entity.Player;

public enum Permission {

    BASIC("MagmaBuildNetwork.basic"),
    TRAILS("MagmaBuildNetwork.trails"),
    SAFECHEST("MagmaBuildNetwork.safechest"),
    MAINTENANCE_BYPASS("MagmaBuildNetwork.maintenance-bypass"),
    MODERATOR("MagmaBuildNetwork.moderator"),
    ADMIN("MagmaBuildNetwork.admin");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public boolean has(Player player) {
        return player.hasPermission(permission);
    }

    public boolean has(Player player, boolean notifyFalse) {
        if (has(player)) return true;
        else if (notifyFalse) PlayerUtils.message(player, Lang.NO_PERMISSION.get());
        return false;
    }
}
