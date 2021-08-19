package io.github.FourteenBrush.MagmaBuildNetwork.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private String hostname = "localhost";
    private String port = "3306";
    private String database = "minecraft";
    private String username = "root";
    private String password = "";

    private Connection connection;

    public boolean isConnected() {
        return connection != null;
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if (!isConnected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" +
                    hostname + ":" + port + "/" + database + "?useSSL=false", username, password);
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}