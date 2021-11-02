package io.github.FourteenBrush.MagmaBuildNetwork.database;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.sql.SQLException;

public class DatabaseFactory {

    private static Database database;
    private static final Main plugin = Main.getPlugin(Main.class);

    public static void startup() {
        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean("use-mysql")) {
            database = new MySQL(config.getString("mysql.host"), config.getString("mysql.user"),
                    config.getString("mysql.password"), config.getString("mysql.database"),
                    config.getInt("mysql.port"));
        } else {
            database = new SQLite(new File(plugin.getDataFolder(), "Database.db"));
        }
        if (database.testConnection()) {
            Utils.logInfo("Database connection was successful!");
            createTables();
        } else {
            Utils.logWarning("Could not connect to the database!");
        }
    }

    private static void createTables() {
        try {
            String bansTable = database.getDatabaseType() == Database.DatabaseType.MYSQL ?
                    "CREATE TABLE IF NOT EXISTS mbn_bans (" +
                            "ID INTEGER PRIMARY KEY AUTO_INCREMENT," +
                            "uuid varchar(64) NOT NULL," +
                            "reason varchar(100) NOT NULL," +
                            "banned_by varchar(64) NOT NULL," +
                            "expires DATETIME DEFAULT NULL)"
                    : "CREATE TABLE IF NOT EXISTS mbn_bans (" +
                    "ID integer primary key AUTOINCREMENT," +
                    "uuid varchar(64) not NULL," +
                    "reason varchar(100) not NULL," +
                    "banned_by varchar(64) not NULL," +
                    "expires datetime default NULL);";
            database.executeStatement(bansTable);
        } catch (SQLException e) {
            Utils.logError("Failed to create database!");
        }
    }

    public static Database getDatabase() {
        return database;
    }
}
