package io.github.FourteenBrush.MagmaBuildNetwork.library;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Long> cooldowns;

    public CooldownManager() {
        cooldowns = new HashMap<>();
    }

    public void setCooldown(UUID uuid, long time) {
        if (time < 1) {
            cooldowns.remove(uuid);
        } else {
            cooldowns.put(uuid, time);
        }
    }

    public long getCooldown(UUID uuid) {
        return cooldowns.getOrDefault(uuid, 0L);
    }
}
