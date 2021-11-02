package io.github.FourteenBrush.MagmaBuildNetwork.database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends Database {

    private final String host;
    private final String user;
    private final String password;
    private final String databaseName;
    private final int port;

    public MySQL(String host, String user, String password, String databaseName, int port) {
        super("com.mysql.jdbc.Driver", DatabaseType.MYSQL);
        this.host = host;
        this.user = user;
        this.password = password;
        this.databaseName = databaseName;
        this.port = port;
    }

    @Override
    public void reactivateConnection() throws SQLException {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
        setConnection(DriverManager.getConnection(url, user, password));
    }
}
