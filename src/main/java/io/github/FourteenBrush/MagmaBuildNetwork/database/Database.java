package io.github.FourteenBrush.MagmaBuildNetwork.database;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;

import java.sql.*;

public abstract class Database {

    private Connection connection;
    private final DatabaseType databaseType;

    public Database(String driver, DatabaseType databaseType) {
        this.databaseType = databaseType;
        try {
            Object obj = Class.forName(driver).newInstance();
            if (!(obj instanceof Driver)) {
                Utils.logWarning("Database driver is not an instance of the Driver class");
            } else {
                DriverManager.registerDriver((Driver) obj);
            }
        } catch (Exception e) {
            Utils.logWarning("Database driver not found! " + driver);
        }
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed())
            reactivateConnection();
        return connection;
    }

    public boolean testConnection() {
        try {
            getConnection();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed())
            connection.close();
    }

    public abstract void reactivateConnection() throws SQLException;

    public enum DatabaseType {
        MYSQL, SQLITE
    }
}
