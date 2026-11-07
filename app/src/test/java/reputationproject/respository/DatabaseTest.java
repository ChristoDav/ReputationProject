package reputationproject.respository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import reputationproject.model.Page;
import reputationproject.model.World;

class DatabaseTest {
    @TempDir
    Path folder;

    private Path path;

    private Database database;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        path = folder.resolve("nested").resolve("test.db");
        database = new Database(path);
    }

    @AfterEach
    void tearDown() throws SQLException {
        database.close();
    }

    @Test
    void savedStateIsReadBackAfterReopening() throws SQLException, IOException {
        World world = database.createWorld("Eldoria");
        database.saveSetting("theme", "DARK");
        database.recordPageVisit(world, Page.SETTINGS);
        database.close();

        database = new Database(path);

        assertEquals("DARK", database.getSetting("theme"));
        assertEquals("Eldoria", database.getWorlds().get(0).getName());
        assertEquals(List.of(Page.SETTINGS), database.getRecentPages(world, 5));
    }

    @Test
    void savingASettingTwiceKeepsTheLatestValue() throws SQLException {
        database.saveSetting("theme", "DARK");
        database.saveSetting("theme", "LIGHT");

        assertEquals("LIGHT", database.getSetting("theme"));
    }

    @Test
    void worldNamesAreUniqueIgnoringCase() throws SQLException {
        database.createWorld("Eldoria");

        assertThrows(SQLException.class, () -> database.createWorld("ELDORIA"));
    }

    @Test
    void revisitingAPageMovesItToTheTop() throws SQLException {
        World world = database.createWorld("Eldoria");
        database.recordPageVisit(world, Page.ACTORS);
        database.recordPageVisit(world, Page.SETTINGS);
        database.recordPageVisit(world, Page.ACTORS);

        assertEquals(List.of(Page.ACTORS, Page.SETTINGS), database.getRecentPages(world, 5));
        assertEquals(List.of(Page.ACTORS), database.getRecentPages(world, 1));
    }

    @Test
    void recentPagesAreKeptPerWorld() throws SQLException {
        World first = database.createWorld("Eldoria");
        World second = database.createWorld("Karsus");
        database.recordPageVisit(first, Page.SETTINGS);

        assertTrue(database.getRecentPages(second, 5).isEmpty());
    }

    @Test
    void wipeRemovesEverything() throws SQLException {
        World world = database.createWorld("Eldoria");
        database.saveSetting("theme", "DARK");
        database.recordPageVisit(world, Page.SETTINGS);

        database.wipe();

        assertTrue(database.getWorlds().isEmpty());
        assertNull(database.getSetting("theme"));
        assertTrue(database.getRecentPages(world, 5).isEmpty());
    }

    @Test
    void exportedContentsCanBeImportedAfterAWipe() throws SQLException, IOException {
        World world = database.createWorld("Eldoria");
        database.saveSetting("theme", "DARK");
        database.recordPageVisit(world, Page.SETTINGS);
        Path export = folder.resolve("export.db");

        database.exportTo(export);
        database.wipe();
        database.importFrom(export);

        assertEquals("Eldoria", database.getWorlds().get(0).getName());
        assertEquals("DARK", database.getSetting("theme"));
        assertEquals(List.of(Page.SETTINGS), database.getRecentPages(world, 5));
    }

    @Test
    void exportOverwritesAnExistingFile() throws SQLException, IOException {
        database.createWorld("Eldoria");
        Path export = folder.resolve("export.db");
        Files.writeString(export, "old contents");

        database.exportTo(export);
        database.wipe();
        database.importFrom(export);

        assertEquals(1, database.getWorlds().size());
        assertFalse(Files.exists(folder.resolve("export.db.tmp")));
    }

    @Test
    void importReplacesTheCurrentContents() throws SQLException, IOException {
        database.createWorld("Eldoria");
        Path export = folder.resolve("export.db");
        database.exportTo(export);
        database.wipe();
        database.createWorld("Karsus");

        database.importFrom(export);

        assertEquals(1, database.getWorlds().size());
        assertEquals("Eldoria", database.getWorlds().get(0).getName());
    }

    @Test
    void importingAnInvalidFileKeepsTheCurrentContents() throws SQLException, IOException {
        database.createWorld("Eldoria");
        Path invalid = folder.resolve("invalid.db");
        Files.writeString(invalid, "this is not a database, just some text that is long enough to be read");

        assertThrows(SQLException.class, () -> database.importFrom(invalid));

        assertEquals("Eldoria", database.getWorlds().get(0).getName());
    }

    @Test
    void importingAnUnrelatedDatabaseKeepsTheCurrentContents() throws SQLException, IOException {
        database.createWorld("Eldoria");
        Path unrelated = folder.resolve("unrelated.db");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + unrelated);
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE something_else (id INTEGER)");
        }

        assertThrows(SQLException.class, () -> database.importFrom(unrelated));

        assertEquals("Eldoria", database.getWorlds().get(0).getName());

        // The failed import must leave the connection usable
        database.createWorld("Karsus");
        assertEquals(2, database.getWorlds().size());
    }
}
