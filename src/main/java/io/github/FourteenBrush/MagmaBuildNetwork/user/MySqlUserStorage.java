package io.github.FourteenBrush.MagmaBuildNetwork.user;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.database.Database;
import io.github.FourteenBrush.MagmaBuildNetwork.user.profiles.StatisticsProfile;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Storage;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Logger;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MySqlUserStorage implements Storage<User, UUID> {
    private final MBNPlugin plugin;
    private final Database database;
    private final Map<UUID, User> users;


    public MySqlUserStorage(MBNPlugin plugin) {
        this.plugin = plugin;
        database = plugin.getDatabase();
        users = new HashMap<>();
    }

    @Override
    public void startup() {
        database.insertQuerySync(
                "CREATE TABLE IF NOT EXISTS users " +
                "(uuid varchar(40)," +
                "playtime int," +
                "level int," +
                "last_update bigint," +
                "first_join bigint," +
                "primary key(uuid))");
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Iterator<User> iterator = users.values().iterator(); iterator.hasNext();) {
                User user = iterator.next();
                save(user);
                if (user.getPlayer() == null) {
                    iterator.remove();
                }
            }
        }, 20, 20 * 60 * 5);
    }

    @Override
    public void shutdown() {
        users.values().forEach(this::save);
    }

    @Override
    public void load(UUID id) {
        String sql = "SELECT * FROM users WHERE uuid=?";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StatisticsProfile sp = new StatisticsProfile(id, rs.getInt("playtime"),
                        rs.getInt("level"), rs.getLong("first_join"));
                User user = new User(id, sp);
                users.put(id, user);
            } else {
                throw new IllegalStateException("User " + id + " not found on the database, user should've been created on join");
            }
        } catch (SQLException e) {
            Logger.ERROR.log("Failed to load user from the database");
            e.printStackTrace();
        }
    }

    @Override
    public void save(User data) {
        String sql = "UPDATE users SET playtime=?, level=?, last_update=?, WHERE uuid=?";
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            StatisticsProfile sp = data.getStatisticsProfile();
            ps.setInt(1, sp.getPlaytime());
            ps.setInt(2, sp.getLevel());
            ps.setLong(3, System.currentTimeMillis());
            ps.setString(4, data.getUuid().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            Logger.ERROR.log("Failed to save user to database");
            e.printStackTrace();
        }
    }
}
