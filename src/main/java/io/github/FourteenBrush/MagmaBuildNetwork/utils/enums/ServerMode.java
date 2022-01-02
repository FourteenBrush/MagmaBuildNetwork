package io.github.FourteenBrush.MagmaBuildNetwork.utils.enums;

public enum ServerMode {

    FATAL_ERROR("magmabuildnetwork.servermode.developer", "The server is locked by a fatal error, contact staff ASAP"),
    MAINTENANCE("mamgabuildnetwork.servermode.maintenance", "The server is currently undergoing maintenance"),
    RESTARTING("magmabuildnetwork.servermode.restarting", "The server is restarting"),
    EARLYBETA("magmabuildnetwork.servermode.earlybeta", "The server is in early beta"),
    BETA("magmabuildnetwork.servermode.beta", "The server is in beta mode"),
    RELEASED("magmabuildnetwork.servermode.released", "You don't have permission to join this server, check if you're whitelisted?");

    private final String message;
    private final String permission;

    ServerMode(String permisson, String message) {
        this.permission = permisson;
        this.message = message;
    }

    public String getPermission() {
        return permission;
    }

    public String getMessage() {
        return message;
    }

    public static ServerMode of(String mode) {
        for (ServerMode serverMode : values()) {
            if (serverMode.name().equalsIgnoreCase(mode)) {
                return serverMode;
            }
        }
        return null;
    }
}
