package io.github.FourteenBrush.MagmaBuildNetwork.database;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends Database {

    private final File database;

    public SQLite(File database) {
        super("org.sqlite.JDBC", DatabaseType.SQLITE);
        if (database.getParentFile().exists())
            database.getParentFile().mkdirs();
        this.database = database;
    }

    @Override
    public void reactivateConnection() throws SQLException {
        setConnection(DriverManager.getConnection("jdbc:sqlite://" + database.getAbsolutePath()));
    }
}
