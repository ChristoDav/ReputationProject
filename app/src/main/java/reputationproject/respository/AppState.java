package reputationproject.respository;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import reputationproject.model.Page;
import reputationproject.model.Resolution;
import reputationproject.model.Theme;
import reputationproject.model.World;
import reputationproject.model.actor.Actor;

public class AppState {
    private static final String RESOLUTION_SETTING = "resolution";
    private static final String THEME_SETTING = "theme";
    private static final int RECENT_PAGE_LIMIT = 5;

    private static AppState instance;

    private Database database;

    private ArrayList<Actor> actors;
    private ArrayList<World> worlds;
    private ArrayList<Page> recentPages;
    private World currentWorld;
    private Resolution resolution;
    private Theme theme;

    private AppState() {
        actors = new ArrayList<>();
        worlds = new ArrayList<>();
        recentPages = new ArrayList<>();
    }

    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    public void load(Path path) throws SQLException, IOException {
        database = new Database(path);
        reload();
    }

    public void close() throws SQLException {
        if (database != null) {
            database.close();
        }
    }

    public ArrayList<Actor> getActors() {
        return actors;
    }

    // Settings

    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(Resolution resolution) throws SQLException {
        database.saveSetting(RESOLUTION_SETTING, resolution.name());
        this.resolution = resolution;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) throws SQLException {
        database.saveSetting(THEME_SETTING, theme.name());
        this.theme = theme;
    }

    // Worlds

    public ArrayList<World> getWorlds() {
        return worlds;
    }

    public World getCurrentWorld() {
        return currentWorld;
    }

    public boolean hasWorld(String name) {
        for (World world : worlds) {
            if (world.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void createWorld(String name) throws SQLException {
        setCurrentWorld(database.createWorld(name));
    }

    public void openWorld(World world) throws SQLException {
        database.markWorldOpened(world);
        setCurrentWorld(world);
    }

    public void closeWorld() {
        currentWorld = null;
        recentPages = new ArrayList<>();
    }

    // Recent pages

    public ArrayList<Page> getRecentPages() {
        return recentPages;
    }

    public void recordPageVisit(Page page) throws SQLException {
        if (page == Page.SETTINGS) return;
        database.recordPageVisit(currentWorld, page);
        recentPages = database.getRecentPages(currentWorld, RECENT_PAGE_LIMIT);
    }

    // Import, export and wipe functions

    public void exportDatabase(Path destination) throws SQLException, IOException {
        database.exportTo(destination);
    }

    public void importDatabase(Path source) throws SQLException {
        database.importFrom(source);
        reload();
    }

    public void wipeDatabase() throws SQLException {
        database.wipe();
        reload();
    }

    // Utility functions

    private void setCurrentWorld(World world) throws SQLException {
        currentWorld = world;
        worlds = database.getWorlds();
        recentPages = database.getRecentPages(world, RECENT_PAGE_LIMIT);
    }

    private void reload() throws SQLException {
        resolution = Resolution.fromName(database.getSetting(RESOLUTION_SETTING));
        theme = Theme.fromName(database.getSetting(THEME_SETTING));
        worlds = database.getWorlds();
        closeWorld();
    }
}
