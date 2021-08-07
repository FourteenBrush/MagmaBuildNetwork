package io.github.FourteenBrush.MagmaBuildNetwork.database;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public abstract class DatabaseHandler {

    private static final Main plugin = Main.getInstance();

    public DatabaseHandler() {}

    public void createTable() {
        PreparedStatement ps;
        try {
            ps = plugin.mySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS magmabuildnetwork " +
                    "(name VARCHAR(50),uuid VARCHAR(20), onlineTime INT(20),PRIMARY KEY uuid)");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayer(Player player) {
        try {
            UUID uuid = player.getUniqueId();
            if (!exists(uuid)) {
                PreparedStatement ps2 = plugin.mySQL.getConnection().prepareStatement("INSERT IGNORE INFO magmabuildnetwork " +
                        "(name,uuid) VALUES (?,?)");
                ps2.setString(1, player.getName());
                ps2.setString(2, uuid.toString());
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void emptyTable() {
        PreparedStatement ps = null;
        try {
            ps = plugin.mySQL.getConnection().prepareStatement("TRUNCATE magmabuildnetwork");
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean exists(UUID uuid) {
        PreparedStatement ps = null;
        try {
            ps = plugin.mySQL.getConnection().prepareStatement("SELECT * FROM magmabuildnetwork " +
                    "WHERE uuid=?");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Statements getStatement(String query) {
        String trimmedQuery = query.trim();
        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("SELECT"))
            return Statements.SELECT;
        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("INSERT"))
            return Statements.INSERT;
        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("UPDATE"))
            return Statements.UPDATE;
        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("DELETE"))
            return Statements.DELETE;
        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("CREATE"))
            return Statements.CREATE;
        if (trimmedQuery.substring(0, 5).equalsIgnoreCase("ALTER"))
            return Statements.ALTER;
        if (trimmedQuery.substring(0, 4).equalsIgnoreCase("DROP"))
            return Statements.DROP;
        if (trimmedQuery.substring(0, 8).equalsIgnoreCase("TRUNCATE"))
            return Statements.TRUNCATE;
        if (trimmedQuery.substring(0, 6).equalsIgnoreCase("RENAME"))
            return Statements.RENAME;
        if (trimmedQuery.substring(0, 2).equalsIgnoreCase("DO"))
            return Statements.DO;
        if (trimmedQuery.substring(0, 7).equalsIgnoreCase("REPLACE"))
            return Statements.REPLACE;
        if (trimmedQuery.substring(0, 4).equalsIgnoreCase("LOAD"))
            return Statements.LOAD;
        if (trimmedQuery.substring(0, 7).equalsIgnoreCase("HANDLER"))
            return Statements.HANDLER;
        if (trimmedQuery.substring(0, 4).equalsIgnoreCase("CALL"))
            return Statements.CALL;
        return Statements.SELECT;
    }

    public enum Statements {
        SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, CREATE, ALTER, DROP, TRUNCATE, RENAME;
    }
}
