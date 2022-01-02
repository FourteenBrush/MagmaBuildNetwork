package io.github.FourteenBrush.MagmaBuildNetwork.player;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
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
        plugin.getDatabase().insertQueryAsync("CREATE TABLE IF NOT EXISTS players (" +
                "uuid varchar(40)," +
                "playtime int," +
                "level int," +
                "first_join bigint" +
                ")"
        );
        Bukkit.getScheduler().runTaskTimer(plugin, () -> userCache.values().forEach(user -> {
            user.savePlayerData();
            if (user.getPlayer() == null) {
                userCache.remove(user.getUuid());
            }
        }), 20, 20 * 60 * 5); // run every five minutes
    }

    public User getUser(UUID uuid) {
        User user = userCache.get(uuid);
        if (user != null) return user;
        String sql = "SELECT * FROM playerdata WHERE uuid=?";
        try (Connection conn = plugin.getDatabase().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            user = new User(uuid, plugin);
            // user.setStatisticsProfile()
            return user;
        } catch (SQLException e) {
            Logger.ERROR.log("Failed to get user from database");
            e.printStackTrace();
        }
        return null;
    }

    public void createUser() {

    }

    public void saveUser()

    public void shutdown() {
        userCache.values().forEach(User::logoutSafely);
    }
}
