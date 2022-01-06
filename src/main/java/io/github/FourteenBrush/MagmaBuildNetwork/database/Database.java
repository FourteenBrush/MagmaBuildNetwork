package io.github.FourteenBrush.MagmaBuildNetwork.database;

import com.zaxxer.hikari.HikariDataSource;
import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {

    private final MBNPlugin plugin;
    private final HikariDataSource datasource;

    public Database(MBNPlugin plugin) {
        this.plugin = plugin;
        datasource = new HikariDataSource();
        datasource.setMaximumPoolSize(8);
        // datasource.setJdbcUrl("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        FileConfiguration configuration = plugin.getConfig();
        String name = configuration.getString("mysql.name");
        datasource.addDataSourceProperty("databaseName", name);
        datasource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        datasource.addDataSourceProperty("port", 3306);
        datasource.setUsername(configuration.getString("mysql.username"));
        datasource.setPassword(configuration.getString("mysql.password"));
        String url = "jdbc:mysql://" + configuration.getString("mysql.host") + ":" +
                configuration.getString("mysql.port") + "/" + name + "?useSSL=false";
        datasource.addDataSourceProperty("url", url);
        insertQueryAsync("CREATE DATABASE IF NOT EXISTS " + name);
    }

    public void insertQuerySync(String sql) {
        try (Connection conn = datasource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            Logger.ERROR.log("Failed to execute db query: ");
            e.printStackTrace();
        }
    }

    public void insertQueryAsync(String sql) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> insertQuerySync(sql));
    }



    public void closeConnection() {
        if (!datasource.isClosed()) {
            datasource.close();
            Logger.INFO.log("Closed datasource!");
        } else {
            Logger.WARNING.log("Attempted to close a datasource which was already closed!");
        }
    }

    public Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }
}
