package reputationproject.respository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import reputationproject.model.Page;
import reputationproject.model.World;

public class Database {
    public static final Path DEFAULT_PATH = Path.of(System.getProperty("user.home"), ".reputationproject",
            "reputation.db");

    private Connection connection;

    public Database(Path path) throws SQLException, IOException {
        Files.createDirectories(path.toAbsolutePath().getParent());
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS settings (
                        key TEXT PRIMARY KEY,
                        value TEXT NOT NULL
                    )""");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS worlds (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE COLLATE NOCASE,
                        last_opened INTEGER NOT NULL
                    )""");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS recent_pages (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        world_id INTEGER NOT NULL REFERENCES worlds(id) ON DELETE CASCADE,
                        page TEXT NOT NULL,
                        UNIQUE (world_id, page)
                    )""");
        }
    }

    // Settings

    public String getSetting(String key) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT value FROM settings WHERE key = ?")) {
            statement.setString(1, key);
            ResultSet result = statement.executeQuery();
            return result.next() ? result.getString("value") : null;
        }
    }

    public void saveSetting(String key, String value) throws SQLException {
        try (PreparedStatement statement = connection
                .prepareStatement("INSERT OR REPLACE INTO settings (key, value) VALUES (?, ?)")) {
            statement.setString(1, key);
            statement.setString(2, value);
            statement.executeUpdate();
        }
    }

    // Worlds

    public ArrayList<World> getWorlds() throws SQLException {
        ArrayList<World> worlds = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement
                    .executeQuery("SELECT id, name FROM worlds ORDER BY last_opened DESC, id DESC");
            while (result.next()) {
                worlds.add(new World(result.getInt("id"), result.getString("name")));
            }
        }
        return worlds;
    }

    public World createWorld(String name) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO worlds (name, last_opened) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setLong(2, System.currentTimeMillis());
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            return new World(keys.getInt(1), name);
        }
    }

    public void markWorldOpened(World world) throws SQLException {
        try (PreparedStatement statement = connection
                .prepareStatement("UPDATE worlds SET last_opened = ? WHERE id = ?")) {
            statement.setLong(1, System.currentTimeMillis());
            statement.setInt(2, world.getId());
            statement.executeUpdate();
        }
    }

    // Recent pages

    public ArrayList<Page> getRecentPages(World world, int limit) throws SQLException {
        ArrayList<Page> pages = new ArrayList<>();
        try (PreparedStatement statement = connection
                .prepareStatement("SELECT page FROM recent_pages WHERE world_id = ? ORDER BY id DESC")) {
            statement.setInt(1, world.getId());
            ResultSet result = statement.executeQuery();
            while (result.next() && pages.size() < limit) {
                // Imported databases may name pages this version doesn't have
                for (Page page : Page.values()) {
                    if (page.name().equals(result.getString("page"))) {
                        pages.add(page);
                    }
                }
            }
        }
        return pages;
    }

    public void recordPageVisit(World world, Page page) throws SQLException {
        // REPLACE gives the row a new id, moving the page to the top of the list
        try (PreparedStatement statement = connection
                .prepareStatement("INSERT OR REPLACE INTO recent_pages (world_id, page) VALUES (?, ?)")) {
            statement.setInt(1, world.getId());
            statement.setString(2, page.name());
            statement.executeUpdate();
        }
    }

    // Import, export and wipe functions

    public void exportTo(Path destination) throws SQLException, IOException {
        // VACUUM INTO refuses to overwrite, so write beside the destination and move it over
        Path temporary = destination.resolveSibling(destination.getFileName() + ".tmp");
        Files.deleteIfExists(temporary);
        try (PreparedStatement statement = connection.prepareStatement("VACUUM INTO ?")) {
            statement.setString(1, temporary.toString());
            statement.executeUpdate();
            Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    public void importFrom(Path source) throws SQLException {
        try (PreparedStatement attach = connection.prepareStatement("ATTACH DATABASE ? AS imported")) {
            attach.setString(1, source.toString());
            attach.executeUpdate();
        }
        try (Statement statement = connection.createStatement()) {
            // One transaction, so an invalid file leaves the current contents untouched
            connection.setAutoCommit(false);
            clearTables(statement);
            statement.executeUpdate("INSERT INTO main.settings (key, value) SELECT key, value FROM imported.settings");
            statement.executeUpdate("""
                    INSERT INTO main.worlds (id, name, last_opened)
                    SELECT id, name, last_opened FROM imported.worlds""");
            statement.executeUpdate("""
                    INSERT INTO main.recent_pages (id, world_id, page)
                    SELECT id, world_id, page FROM imported.recent_pages""");
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
            try (Statement detach = connection.createStatement()) {
                detach.execute("DETACH DATABASE imported");
            }
        }
    }

    public void wipe() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            clearTables(statement);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void close() throws SQLException {
        connection.close();
    }

    // Utility functions

    private void clearTables(Statement statement) throws SQLException {
        statement.executeUpdate("DELETE FROM main.recent_pages");
        statement.executeUpdate("DELETE FROM main.worlds");
        statement.executeUpdate("DELETE FROM main.settings");
    }
}
