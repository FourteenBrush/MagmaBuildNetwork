package io.github.FourteenBrush.MagmaBuildNetwork.player;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.player.profiles.StatisticsProfile;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Logger;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final MBNPlugin plugin;
    private final Map<UUID, User> userCache;

    public UserManager(MBNPlugin plugin) {
        this.plugin = plugin;
        userCache = new HashMap<>();
    }

    public void startup() {
        plugin.getDatabase().insertQueryAsync("CREATE TABLE IF NOT EXISTS users " +
                "(uuid varchar(40)," +
                "playtime int," +
                "level int," +
                "last_update bigint," +
                "first_join bigint," +
                "primary key(uuid) )"
        );
        Bukkit.getScheduler().runTaskTimer(plugin, () -> userCache.values().forEach(user -> {
            if (user.getPlayer() == null) {
                userCache.remove(user.getUuid());
            }
        }), 20, 20 * 60 * 5); // run every five minutes
    }

    /**
     * @see #createUser(UUID, boolean)
     * @param uuid the uuid of the user to search for
     * @return an user object
     */
    public User getUser(UUID uuid) {
        return getUser(uuid, true);
    }

    /**
     * Gets an user from the database, users should have been created the first time they enter the server
     * so failing to return an object shouldn't be possible
     * <strong>this is called on the server thread so you might need to use {@link Bukkit#getScheduler()}</strong>
     * @param uuid the uuid of the user to search for
     * @param async whether or not to execute this database query async
     * @return an user object
     * @throws IllegalStateException if the user cannot be found, which shouldn't happen
     * @see #createUser(UUID, boolean) should avoid any errors being thrown
     */
    public User getUser(UUID uuid, boolean async) {
        User user = userCache.get(uuid);
        if (user != null) return user;
        String sql = "SELECT * FROM players WHERE uuid=? LIMIT 1";
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> getUser(uuid, false));
        }
        try (Connection conn = plugin.getDatabase().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            StatisticsProfile sp = new StatisticsProfile(uuid, rs.getInt("playtime"),
                    rs.getInt("level"), rs.getLong("first_join"));
            user = new User(uuid, sp);
            userCache.put(user.getUuid(), user);
            return user;
        } catch (SQLException e) {
            Logger.ERROR.log("Failed to get user from the database");
            e.printStackTrace();
        }
        throw new IllegalStateException("User not found on the database, user should have been created on first join");
    }

    /**
     * @see #createUser(UUID, boolean)
     * @param uuid the uuid of the user to create
     */
    public void createUser(UUID uuid) {
        createUser(uuid, true);
    }

    /**
     * Creates an new user with default values and saves it to the database
     * @param uuid the uuid of the user to create
     * @param async whether or not to execute this database query async
     */
    public void createUser(UUID uuid, boolean async) {
        String sql = "INSERT INTO users (uuid, playtime, level, last_update, first_join) values(?,?,?,?,?)";
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> createUser(uuid, false));
        }
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
    }

    /**
     * @see #saveUser(User, boolean)
     * @param user the user to save to the database
     */
    public void saveUser(User user) {
        saveUser(user, true);
    }

    /**
     * Saves an user to the database
     * @param user the user to save
     * @param async whether or not to execute this database query async
     */
    public void saveUser(User user, boolean async) {
        String sql = "UPDATE users SET uuid=?, playtime=?, level=?, last_update=? WHERE uuid=?";
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> saveUser(user, false));
        }
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

    // must be called from onDisable to avoid invalid user data
    public void shutdown() {
        userCache.values().forEach(User::logoutSafely);
    }

    public void removeFromCache(UUID uuid) {
        userCache.remove(uuid);
    }
}
