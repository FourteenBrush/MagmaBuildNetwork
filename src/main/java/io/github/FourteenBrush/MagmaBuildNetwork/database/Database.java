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
            Utils.logWarning("Driver not found! " + driver);
        }
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public boolean testConnection() {
        try {
            getConnection();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed())
            reactivateConnection();
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed())
            connection.close();
    }

    public void closeStatement(Statement statement) throws SQLException {
        if (statement != null)
            statement.close();
    }

    public void closeResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet != null)
            resultSet.close();
    }

    public Statement getStatement() throws SQLException {
        return getConnection().createStatement();
    }

    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        return getConnection().prepareStatement(query);
    }

    public PreparedStatement getPreparedStatement(String query, int options) throws SQLException {
        return getConnection().prepareStatement(query, options);
    }

    public void executeStatement(String query) throws SQLException {
        Statement statement = getStatement();
        statement.execute(query);
        closeStatement(statement);
    }

    public abstract void reactivateConnection() throws SQLException;

    public enum DatabaseType {
        MYSQL, SQLITE
    }
}
