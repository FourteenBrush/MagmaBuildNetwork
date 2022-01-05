package io.github.FourteenBrush.MagmaBuildNetwork.user;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.user.profiles.StatisticsProfile;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Logger;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class UserManager {

    private final MBNPlugin plugin;
    private final Map<UUID, User> userCache;
    private final ForkJoinPool forkJoinPool;

    public UserManager(MBNPlugin plugin) {
        this.plugin = plugin;
        userCache = new HashMap<>();
        forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 2,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                (t, e) -> e.printStackTrace(),
                false);
    }

    public void startup() {
        plugin.getDatabase().insertQueryAsync("CREATE TABLE IF NOT EXISTS users " +
                "(uuid varchar(40)," +
                "playtime int," +
                "level int," +
                "last_update bigint," +
                "first_join bigint," +
                "primary key(uuid))"
        );
        Bukkit.getScheduler().runTaskTimer(plugin, () -> userCache.values().forEach(user -> {
            saveUser(user, true);
            if (user.getPlayer() == null) {
                userCache.remove(user.getUuid());
            }
        }), 20, 20 * 60 * 5); // run every five minutes
    }

    /**
     * Gets an user from the database, users should have been created the first time they enter the server
     * so failing to return an object shouldn't be possible
     * @param uuid the uuid of the user to search for
     * @return an user object
     * @throws IllegalStateException if the user cannot be found, which shouldn't happen in a normal situation
     * @see #createUser(UUID) should avoid any errors being thrown
     */
    public User getUser(UUID uuid) {
        User user = userCache.get(uuid);
        if (user != null) return user;

    }

    public User getUserAsync(UUID uuid) {
        CompletableFuture.supplyAsync(() -> getUserFromDatabase(uuid), forkJoinPool).whenComplete((u, t) ->
                userCache.put(uuid, u));
        return userCache.get(uuid);
    }

    private User getUserFromDatabase(UUID uuid) {
        String sql = "SELECT * FROM users WHERE uuid=?";
        try (Connection conn = plugin.getDatabase().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StatisticsProfile sp = new StatisticsProfile(uuid, rs.getInt("playtime"),
                        rs.getInt("level"), rs.getLong("first_join"));
                User user = new User(uuid, sp);
                userCache.put(user.getUuid(), user);
                return user;
            }
        } catch (SQLException e) {
            Logger.ERROR.log("Failed to get user from the database");
            e.printStackTrace();
        }
        throw new IllegalStateException("User not found on the database, user should have been created on first join");
    }

    /**
     * Creates a new user and adds it to the cache
     * @param uuid the uuid of the user to create
     */
    public void createUser(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO users (uuid, playtime, level, last_update, first_join) values(?,?,?,?,?)";
            try (Connection conn = plugin.getDatabase().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, uuid.toString());
                ps.setInt(2, 0);
                ps.setInt(3, 0);
                long now = System.currentTimeMillis();
                ps.setLong(4, now);
                ps.setLong(5, now);
                ps.execute();
                Logger.INFO.log("Created a new user on the database");
            } catch (SQLException e) {
                Logger.ERROR.log("Failed to create a new user on the database");
                e.printStackTrace();
            }
        });
    }

    /**
     * Saves the user to the database but still keeps it in cache
     * @param user the user to save to the database
     */
    public void saveUser(User user, boolean async) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> saveUserToDatabase(user));
        } else {
            saveUserToDatabase(user);
        }
    }

    private void saveUserToDatabase(User user) {
        String sql = "UPDATE users SET uuid=?, playtime=?, level=?, last_update=? WHERE uuid=?";
        try (Connection conn = plugin.getDatabase().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUuid().toString());
            ps.setInt(2, user.getStatisticsProfile().getPlaytime());
            ps.setInt(3, user.getStatisticsProfile().getLevel());
            long now = System.currentTimeMillis();
            ps.setLong(4, now);
            ps.setLong(5, now);
            ps.setString(6, user.getUuid().toString());
            if (ps.executeUpdate() == 0) {
                Logger.WARNING.log("Tried to update user " + user.getUuid().toString() + " , but no rows were affected");
            }
        } catch (SQLException e ) {
            Logger.ERROR.log("Failed to save user to the database");
            e.printStackTrace();
        }
    }

    // must be called on plugin disable
    public void shutdown()  {
        userCache.values().forEach(user -> {
            user.logoutSafely();
            saveUserToDatabase(user); // sync
        });
    }

    public void putInCache(User user) {
        userCache.putIfAbsent(user.getUuid(), user);
    }

    public void removeFromCacheAndSave(User user) {
        userCache.remove(user.getUuid());
        saveUser(user, true);
    }
}
