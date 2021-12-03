package io.github.FourteenBrush.MagmaBuildNetwork.database;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseFactory {

    private static Database database;

    public static void startup(MBNPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean("use-mysql")) {
            database = new MySQL(config.getString("mysql.host"), config.getString("mysql.user"),
                    config.getString("mysql.password"), config.getString("mysql.database"),
                    config.getInt("mysql.port"));
        } else {
            database = new SQLite(new File(plugin.getDataFolder(), "Database.db"));
        }
        if (database.testConnection()) {
            try (Connection conn = database.getConnection();
                 Statement statement = conn.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS tblbans ( ID INTEGER PRIMARY KEY " + (database.getDatabaseType() == Database.DatabaseType.MYSQL ? "AUTO_INCREMENT" : "AUTOINCREMENT") + ", uuid varchar(36) NOT NULL, reason varchar(100) NOT NULL, banned_by varchar(50) NOT NULL, expires " + (database.getDatabaseType() == Database.DatabaseType.MYSQL ? "DATETIME" : "datetime") + " DEFAULT NULL);");
            } catch (SQLException e) {
                Utils.logError("Failed to create tables!");
                e.printStackTrace();
            }
        } else {
            Utils.logWarning("Could not connect to the database!");
        }
    }

    public static Database getDatabase() {
        return database;
    }
}
