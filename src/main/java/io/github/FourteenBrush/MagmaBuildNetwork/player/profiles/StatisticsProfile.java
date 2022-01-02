package io.github.FourteenBrush.MagmaBuildNetwork.player.profiles;

import org.apache.commons.lang.Validate;

import java.util.UUID;

public class StatisticsProfile {
    private final UUID uuid;
    private int playtime;
    private int level;
    private long lastUpdate;
    private final long firstJoin;

    public StatisticsProfile(UUID uuid) {
        this(uuid, 0, 0, System.currentTimeMillis());
    }

    /**
     * Constructs a new statistics profile, which belongs to an user
     * @param uuid the uuid of the user this profile belongs to
     * @param playTime the playtime of the user in minutes
     * @param level the level of the user
     * @param firstJoin the time in millis of the first join (compared with {@link System#currentTimeMillis()})
     */
    public StatisticsProfile(UUID uuid, int playTime, int level, long firstJoin) {
        this.uuid = uuid;
        this.playtime = playTime;
        this.level = level;
        this.firstJoin = firstJoin;
        lastUpdate = System.currentTimeMillis();
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getPlaytime() {
        return playtime;
    }

    public int getLevel() {
        return level;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public long getFirstJoin() {
        return firstJoin;
    }

    public void setPlaytime(int playtime) {
        Validate.isTrue(playtime >= 0, "Playtime must be bigger than or equals to 0");
        this.playtime = playtime;
    }

    public void updatePlaytime(int playtime) {
        Validate.isTrue(playtime > 0, "You can only update the playtime with a number bigger than 0");
        this.playtime += playtime;
    }

    public void setLevel(int level) {
        Validate.isTrue(level >= 0, "Level must be bigger than or equals to 0");
        this.level = level;
    }

    public void setLastUpdate(long lastUpdate) {
        Validate.isTrue(lastUpdate > 0, "Last update time must be bigger than 0");
        this.lastUpdate = lastUpdate;
    }
}
