package de.orian.toggleprefix.database;

import com.zaxxer.hikari.HikariDataSource;
import de.orian.toggleprefix.config.ConfigManager;
import de.orian.toggleprefix.prefix.Prefix;
import de.orian.toggleprefix.utils.Formatter;
import de.orian.toggleprefix.utils.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MySQL {

    private static MySQL instance;

    private Sender sender = Sender.getInstance();

    private HikariDataSource ds;
    private final String prefixTable = "tp_prefixes";
    private final String playerTable = "tp_players";
    private final String playerHasPrefixTable = "tp_playerhasprefix";
    private final String groupHasPrefixTable = "tp_grouphasprefix";

    public MySQL() {
        instance = this;

        sender.console("Preparing database connection...");
        setupDataSource();
        sender.consoleSuccess("Connection is ready!");
        sender.consoleSuccess("Setting up tables...");
        if (setupTables()) sender.consoleSuccess("Set up tables!");
        else sender.consoleError("Connection to database failed.");
    }

    private void setupDataSource() {
        ds =  new HikariDataSource();

        HashMap<String, String> data = ConfigManager.getInstance().getDatabase();

        String url = "jdbc:mysql://" + data.get("host") + ":" + data.get("port") + "/" + data.get("name") +
                "?useJDBCCompliantTimezoneShift=true" +
                "&useLegacyDatetimeCode=false" +
                "&serverTimezone=Europe/Berlin" +
                "&autoReconnect=true";

        ds.setMaximumPoolSize(15);
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setJdbcUrl(url);
        ds.addDataSourceProperty("user", data.get("user"));
        ds.addDataSourceProperty("password", data.get("password"));
        ds.setAutoCommit(true);
        ds.setLeakDetectionThreshold(5000);
    }

    public static MySQL getInstance() {
        return instance;
    }

    //Close pool
    public void close() {
        if (ds != null) ds.close();
    }

    //Cleanup connection
    public void cleanup(Connection c, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean setupTables() {
        Connection c = null;
        PreparedStatement stmt = null;
        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + prefixTable + "(" +
                            "name varchar(64) PRIMARY KEY NOT NULL," +
                            "display varchar(64) NOT NULL," +
                            "chat varchar(128)," +
                            "tablist varchar(128)," +
                            "item varchar(128) NOT NULL," +
                            "priority varchar(3) NOT NULL" +
                        ");";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);
            stmt.execute();

            cleanup(c, stmt, null);
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            cleanup(c, stmt, null);
            return false;
        }

        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + playerTable + "(" +
                            "id BINARY(16) PRIMARY KEY NOT NULL," +
                            "prefix varchar(64) NOT NULL," +
                            "display_name varchar(1024)" +
                        ");";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);
            stmt.execute();

            cleanup(c, stmt, null);
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            cleanup(c, stmt, null);
            return false;
        }

        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + playerHasPrefixTable + "(" +
                            "id BINARY(16) NOT NULL," +
                            "prefix varchar(64) NOT NULL," +
                            "PRIMARY KEY(id, prefix)" +
                        ");";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);
            stmt.execute();

            cleanup(c, stmt, null);
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            cleanup(c, stmt, null);
            return false;
        }

        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + groupHasPrefixTable + "(" +
                            "lpgroup varchar(128) NOT NULL," +
                            "prefix varchar(64) NOT NULL," +
                            "PRIMARY KEY(lpgroup, prefix)" +
                        ");";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);
            stmt.execute();

            cleanup(c, stmt, null);
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            cleanup(c, stmt, null);
            return false;
        }

        try {
            String sql = "INSERT IGNORE INTO " + prefixTable + "(name,display,chat,tablist,item,priority) " +
                    "VALUES('default','Player','&7Player - &f%name% &7>>','&7Player | &f%name%','GREEN_STAINED_GLASS_PANE','100')";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);
            stmt.execute();
            cleanup(c, stmt, null);
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            cleanup(c, stmt, null);
            return false;
        }
        return true;
    }

    public List<String> getTeams() {
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT DISTINCT priority FROM " + prefixTable;
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);
            rs = stmt.executeQuery();

            List<String> teams = new ArrayList<>();

            while(rs.next()) {
                teams.add(Formatter.convertToLetters(rs.getString("priority")));
            }

            cleanup(c, stmt, rs);
            return teams;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        cleanup(c, stmt, rs);
        return null;
    }

    public boolean addPrefix(Prefix prefix) {
        Connection c = null;
        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO " + prefixTable + "(name,display,chat,tablist,item,priority) VALUES(?,?,?,?,?,?)";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            stmt.setString(1, prefix.name);
            stmt.setString(2, prefix.name);
            stmt.setString(3, prefix.chat);
            stmt.setString(4, prefix.chat);
            stmt.setString(5, prefix.item);
            stmt.setString(6, prefix.priority);

            stmt.execute();

            cleanup(c, stmt, null);
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            cleanup(c, stmt, null);
            return false;
        }
    }

    public boolean removePrefix(String name) {
        Connection c = null;
        PreparedStatement stmt = null;
        try {
            String sql = "DELETE FROM " + prefixTable + " WHERE name=?";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            stmt.setString(1, name);

            stmt.execute();

            cleanup(c, stmt, null);
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            cleanup(c, stmt, null);
            return false;
        }
    }

    public boolean updatePrefix(String name, String column, String value) {
        Connection c = null;
        PreparedStatement stmt = null;
        try {
            String sql = "UPDATE " + prefixTable + " SET " + column + "=? WHERE name=?";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            stmt.setString(1, value);
            stmt.setString(2, name);

            stmt.execute();

            cleanup(c, stmt, null);
            return true;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            cleanup(c, stmt, null);
            return false;
        }
    }

    public boolean prefixExists(String name) {
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        boolean exists = false;

        try {
            String sql = "SELECT * FROM " + prefixTable + " WHERE name=?";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            stmt.setString(1, name);

            rs = stmt.executeQuery();

            if (rs.next()) exists = true;
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        cleanup(c, stmt, rs);
        return exists;
    }
    public List<String> getPrefixes() {
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<String> prefixes = new ArrayList<>();

        try {
            String sql = "SELECT name FROM " + prefixTable;
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            rs = stmt.executeQuery();

            while (rs.next()) {
                prefixes.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        cleanup(c, stmt, rs);
        return prefixes;
    }

    public boolean addPrefixToGroup(String group, String prefix) {
        Connection c = null;
        PreparedStatement stmt = null;

        try {
            String sql = "INSERT IGNORE " + groupHasPrefixTable + " VALUES(?,?)";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            stmt.setString(1, group);
            stmt.setString(2, prefix);

            stmt.execute();

            cleanup(c, stmt, null);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            cleanup(c, stmt, null);
            return false;
        }
    }

    public boolean addPrefixToPlayer(Player player, String prefix) {
        Connection c = null;
        PreparedStatement stmt = null;

        try {
            String sql = "INSERT IGNORE " + playerHasPrefixTable + " VALUES(?,?)";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            stmt.setBytes(1, Formatter.UUIDtoBytes(player.getUniqueId()));
            stmt.setString(2, prefix);

            stmt.execute();

            cleanup(c, stmt, null);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            cleanup(c, stmt, null);
            return false;
        }
    }

    public boolean addPlayer(Player player) {
        Connection c = null;
        PreparedStatement stmt = null;

        try {
            String sql = "INSERT IGNORE " + playerTable + " VALUES(?,?,?)";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            stmt.setBytes(1, Formatter.UUIDtoBytes(player.getUniqueId()));
            stmt.setString(2, "default");
            stmt.setString(3, null);

            stmt.execute();

            cleanup(c, stmt, null);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            cleanup(c, stmt, null);
            return false;
        }
    }

    public Prefix getPlayerPrefix(Player player) {
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        Prefix prefix = null;

        try {
            String sql = "SELECT * FROM " + prefixTable + " WHERE name=(SELECT prefix FROM " + playerTable + " WHERE id=?)";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            stmt.setBytes(1, Formatter.UUIDtoBytes(player.getUniqueId()));

            rs = stmt.executeQuery();

            if (rs.next()) {
                prefix = new Prefix(
                        rs.getString("name"),
                        rs.getString("display"),
                        rs.getString("chat"),
                        rs.getString("tablist"),
                        rs.getString("item"),
                        rs.getString("priority")
                );
            }
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        cleanup(c, stmt, rs);
        return prefix;
    }

    public boolean setPlayerPrefix(Player player, String prefix) {
        Connection c = null;
        PreparedStatement stmt = null;

        try {
            String sql = "UPDATE " + playerTable + " SET prefix=? WHERE id=?";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            stmt.setString(1, prefix);
            stmt.setBytes(2, Formatter.UUIDtoBytes(player.getUniqueId()));

            stmt.execute();

            cleanup(c, stmt, null);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            cleanup(c, stmt, null);
            return false;
        }
    }

    public List<Prefix> getPlayerPrefixes(Player player) {
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Prefix> prefixes = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + playerHasPrefixTable + "," + prefixTable + " WHERE " + playerHasPrefixTable + ".prefix=" + prefixTable + ".name AND " + playerHasPrefixTable + ".id=?";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            stmt.setBytes(1, Formatter.UUIDtoBytes(player.getUniqueId()));

            rs = stmt.executeQuery();

            while (rs.next()) {
                prefixes.add(new Prefix(
                    rs.getString("name"),
                    rs.getString("display"),
                    rs.getString("chat"),
                    rs.getString("tablist"),
                    rs.getString("item"),
                    rs.getString("priority")
                ));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        cleanup(c, stmt, rs);
        return prefixes;
    }

    public List<Prefix> getGroupPrefixes(String group) {
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Prefix> prefixes = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + groupHasPrefixTable + "," + prefixTable + " WHERE " + groupHasPrefixTable + ".prefix=" + prefixTable + ".name AND " + groupHasPrefixTable + ".lpgroup=?";
            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            stmt.setString(1,group);

            rs = stmt.executeQuery();

            while (rs.next()) {
                prefixes.add(new Prefix(
                        rs.getString("name"),
                        rs.getString("display"),
                        rs.getString("chat"),
                        rs.getString("tablist"),
                        rs.getString("item"),
                        rs.getString("priority")
                ));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        cleanup(c, stmt, rs);
        return prefixes;
    }

    public boolean setPlayerDisplayName(Player player, String displayName) {
        Connection c = null;
        PreparedStatement stmt = null;

        try {
            String sql = "UPDATE " + playerTable + " SET display_name=? WHERE id=?";

            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            stmt.setString(1, displayName);
            stmt.setBytes(2, Formatter.UUIDtoBytes(player.getUniqueId()));

            stmt.execute();

            cleanup(c, stmt, null);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            cleanup(c, stmt, null);
            return false;
        }
    }

    public String getPlayerDisplayName(Player player) {
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String name = null;

        try {
            String sql = "SELECT display_name FROM " + playerTable + " WHERE id=?";

            c = ds.getConnection();
            stmt = c.prepareStatement(sql);

            stmt.setBytes(1, Formatter.UUIDtoBytes(player.getUniqueId()));

            rs = stmt.executeQuery();

            if (rs.next()) {
                name = rs.getString("display_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cleanup(c, stmt, rs);

        return name;
    }

}
