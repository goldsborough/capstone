package capstone.utility;

import capstone.data.Data;
import capstone.data.Profile;
import capstone.data.Theme;
import capstone.element.Element;
import capstone.element.Player;
import capstone.game.Level;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Takes care of all deserialization and serialization for levels.
 * These methods were previously contained in the Level class.
 * Refactoring is a beautiful thing.
 */
public class LevelBuilder extends Data
{
    /**
     *
     * Constructs a LevelBuilder from a Level. You can call store() right after.
     *
     * For example: new LevelBuilder(level).store();
     *
     * @param level The level to construct this LevelBuilder from.
     */
    public LevelBuilder(Level level)
    {
        assert(level != null);

        this.players(level.players())
            .name(level.name())
            .grid(level.grid())
            .page(level.currentPage())
            .theme(level.theme())
            .levelSize(level.size())
            .hidden(level.hidden())
            .difficulty(level.difficulty())
            .totalKeys(level.totalKeys());
    }

    /**
     *
     * Constructs the LevelBuilder from a file containing properties,
     * a list of profiles and a GUI screen.
     *
     * @param file The file containing the properties to deserialize.
     *
     * @param profiles The profiles for the level.
     *
     * @param gui The GUI screen on which the level will be rendered.
     *
     * @throws IOException for I/O badness.
     *
     */
    public LevelBuilder(File file,
                        List<Profile> profiles,
                        GUIScreen gui) throws IOException
    {
        this(Data._load(file), profiles, gui);
    }

    /**
     *
     * Constructs the LevelBuilder from session properties,
     * a list of profiles and a GUI screen.
     *
     * @param session The properties to deserialize.
     *
     * @param profiles The profiles for the level.
     *
     * @param gui The GUI screen on which the level will be rendered.
     *
     */
    public LevelBuilder(Properties session,
                        List<Profile> profiles,
                        GUIScreen gui)
    {
        assert(session != null);
        assert(profiles != null);
        assert(gui != null);
        assert(! profiles.isEmpty());

        assert(session.containsKey("Name"));

        _players = new ArrayList<>();

        _hidden = new ArrayList<>();

        this.gui(gui);

        Map<String, Profile> remaining = _map(profiles);

        Collection<Element> elements = deserialize(session, remaining);

        _grid = _setupGrid(elements, remaining.size());

        _placeAtEntrances(remaining.values());

        _page = _fetchPage();

        _makeIDMap();
    }

    /**
     *
     * Constructs the LevelBuilder from a file containing properties,
     * a list of profiles and a GUI screen. Also sets the difficulty
     * parameter.
     *
     * @param difficulty The difficulty of the Level,
     *                   from the Level.Difficulty enum.
     *
     * @param file The file containing the properties to deserialize.
     *
     * @param theme The theme for the level.
     *
     * @param profiles The profiles for the level.
     *
     * @param gui The GUI screen on which the level will be rendered.
     *
     * @throws IOException for I/O badness.
     *
     */
    public LevelBuilder(Level.Difficulty difficulty,
                        File file,
                        Theme theme,
                        List<Profile> profiles,
                        GUIScreen gui) throws IOException
    {
        this(
                difficulty,
                Data._getName(file),
                Data._load(file),
                theme,
                profiles,
                gui
        );
    }

    /**
     *
     * Constructs the LevelBuilder from a file containing properties,
     * a list of profiles and a GUI screen. Also sets the difficulty
     * parameter.
     *
     * @param difficulty The difficulty of the Level,
     *                   from the Level.Difficulty enum.
     *
     * @param name The naem of the level.
     *
     * @param layout The layout of the game, in a properties file.
     *
     * @param theme The Theme for the level.
     *
     * @param profiles The profiles for the level.
     *
     * @param gui The GUI screen on which the level will be rendered.
     *
     */
    public LevelBuilder(Level.Difficulty difficulty,
                        String name,
                        Properties layout,
                        Theme theme,
                        List<Profile> profiles,
                        GUIScreen gui)
    {
        assert(difficulty != null);
        assert(layout != null);
        assert(theme != null);
        assert(profiles != null);
        assert(gui != null);
        assert(! profiles.isEmpty());

        _players = new ArrayList<>();

        _hidden = new ArrayList<>();

        this.gui(gui);

        _difficulty = difficulty;

        _theme = theme;

        _name = name;

        Collection<Element> elements = deserialize(
                layout,
                new HashMap<>()
        );

        _grid = _setupGrid(elements, profiles.size());

        _placeAtEntrances(profiles);

        _page = _fetchPage();

        _makeIDMap();
    }


    /**
     * @return The theme of the level.
     */
    public Theme theme()
    {
        return _theme;
    }

    /**
     *
     * Sets the theme for the level.
     *
     * @param theme The theme that will be given to the level.
     *
     * @return The same LevelBuilder instance.
     */
    public LevelBuilder theme(Theme theme)
    {
        assert(theme != null);

        _theme = theme;

        return this;
    }

    /**
     * @return The name of the level.
     */
    public String name()
    {
        return _name;
    }

    /**
     *
     * Sets the name for the level.
     *
     * @param name The name that will be given to the level.
     *
     * @return The same LevelBuilder instance.
     */
    public LevelBuilder name(String name)
    {
        assert(name != null);

        _name = name;

        return this;
    }

    /**
     * @return The size of the level.
     */
    public LevelSize levelSize()
    {
        return _levelSize;
    }

    /**
     *
     * Sets the size that will be given to the level.
     *
     * @param levelSize A LevelSize instance for the level.
     *
     * @return The same LevelBuilder instance.
     */
    public LevelBuilder levelSize(LevelSize levelSize)
    {
        assert(levelSize != null);

        _levelSize = levelSize;

        return this;
    }

    /**
     * @return The hidden profiles that could not be put at an entrance.
     */
    public List<Profile> hidden()
    {
        return _hidden;
    }

    /**
     *
     * Sets the hidden profiles of the LevelBuilder.
     *
     * @param hidden The hidden profiles.
     *
     * @return The same LevelBuilder instance.
     */
    public LevelBuilder hidden(List<Profile> hidden)
    {
        assert(hidden != null);

        _hidden = hidden;

        return this;
    }

    /**
     * @return The map from ids to players.
     */
    public Map<String, Player> IDMap()
    {
        return _IDMap;
    }

    /**
     * @return The active (non-hidden) players of the level.
     */
    public List<Player> players()
    {
        return _players;
    }

    /**
     *
     * Sets the active (non-hidden) players for the level.
     *
     * @param players A list of active players.
     *
     * @return The same LevelBuilder instance.
     */
    public LevelBuilder players(List<Player> players)
    {
        assert(players != null);

        _players = players;

        return this;
    }

    /**
     * @return The PageGrid of the level.
     */
    public PageGrid grid()
    {
        return _grid;
    }

    /**
     *
     * Sets the PageGrid for the level.
     *
     * @param grid An instance of the PageGrid class.
     *
     * @return The same LevelBuilder instance.
     */
    public LevelBuilder grid(PageGrid grid)
    {
        assert(grid != null);

        _grid = grid;

        return this;
    }

    /**
     * @return The current page in the grid.
     */
    public Page page()
    {
        return _page;
    }

    /**
     *
     * Sets the current page of the level.
     *
     * @param page The current page of the level.
     *
     * @return The same LevelBuilder instance.
     */
    public LevelBuilder page(Page page)
    {
        assert(page != null);

        _page = page;

        return this;
    }

    /**
     * @return The total number of keys in the game.
     */
    public int totalKeys()
    {
        return _totalKeys;
    }

    /**
     *
     * Sets the total number of keys in the level.
     *
     * @param totalKeys The total number of keys in the level.
     *
     * @return The same LevelBuilder instance.
     */
    public LevelBuilder totalKeys(int totalKeys)
    {
        _totalKeys = totalKeys;

        return this;
    }

    /**
     * @return The gui screen used to render the game on.
     */
    public GUIScreen gui()
    {
        return _gui;
    }

    /**
     *
     * Sets the GUI on which the level is rendered.
     *
     * @param gui The GUI on which the level is rendered.
     *
     * @return The same LevelBuilder instance.
     */
    public LevelBuilder gui(GUIScreen gui)
    {
        assert(gui != null);

        _gui = gui;

        return this;
    }

    /**
     * @return The difficulty of the level.
     */
    public Level.Difficulty difficulty()
    {
        return _difficulty;
    }

    /**
     *
     * Sets the difficulty of the level.
     *
     * @param difficulty The difficulty of the level.
     *
     * @return The same LevelBuilder instance.
     */
    public LevelBuilder difficulty(Level.Difficulty difficulty)
    {
        _difficulty = difficulty;

        return this;
    }


    /**
     * Always need the profiles.
     */
    @Override public void deserialize(Properties serialization)
    {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * Deserializes a Level.
     *
     * @param serialization The properties containing the level data.
     *
     * @param remaining The remaining profiles to try to find in the data.
     *
     * @return A Collection of Elements.
     */
    public Collection<Element> deserialize(Properties serialization,
                                           Map<String, Profile> remaining)
    {
        if (serialization.containsKey("Name"))
        {
            _name = Data.pop(serialization, "Name");
        }

        _difficulty = _deserializeDifficulty(serialization);

        // Difference for loading a session vs a layout
        if (_theme == null) _loadTheme(serialization);

        else serialization.remove("Theme");

        _levelSize = new LevelSize(
                Integer.parseInt(Data.pop(serialization, "Width")),
                Integer.parseInt(Data.pop(serialization, "Height"))
        );

        return _deserializeElements(serialization, remaining);
    }

    /**
     *
     * Stores the level as a session in resources/sessions.
     *
     * @throws IOException for I/O badness.
     */
    @Override public void store() throws IOException
    {
        super.store(new File("resources/sessions"));
    }

    /**
     *
     * Serializes the level.
     *
     * @return A Properties object containing the data.
     */
    public Properties serialize()
    {
        Properties properties = new Properties();

        assert(_name != null);

        properties.setProperty("Name", _name);

        properties.setProperty("Width", Integer.toString(_levelSize.getColumns()));
        properties.setProperty("Height", Integer.toString(_levelSize.getRows()));

        properties.setProperty("Difficulty", _difficulty.toString());

        properties.setProperty("Theme", _theme.fileName());

        _serializeGrid(properties);

        _serializePlayers(properties);

        return properties;
    }

    /**
     * @return The filename for the level session,
     *         which is name-of-level_date_time.session.
     */
    @Override public String fileName()
    {
        assert(_name != null);

        return String.format(
                "%1$s_%2$tF_%2$tT.session",
                _name,
                new Date()
        );
    }

    /**
     *
     * Helper method of serialize() to serialize the PageGrid.
     *
     * @param properties The properties to serialize into.
     */
    private void _serializeGrid(Properties properties)
    {
        for (Page page : _grid.pages())
        {
            for (Element element : page)
            {
                properties.setProperty(
                        element.point().toStringPlain(),
                        Integer.toString(element.kind().code())
                );
            }
        }
    }

    /**
     *
     * Helper method of serialize() to serialize the players.
     *
     * @param properties The properties to serialize into.
     */
    private void _serializePlayers(Properties properties)
    {
        for (Player player : _players)
        {
            properties.setProperty(
                    String.format("id:%1$s", player.id()),
                    player.point().toStringPlain()
            );
        }

        for (Profile profile : _hidden)
        {
            properties.setProperty(
                    String.format("id:%1$s", profile.id()),
                    "hidden"
            );
        }
    }

    /**
     * Creates the IDMap by mapping ids to players.
     */
    private void _makeIDMap()
    {
        _IDMap = new HashMap<>();

        _players.forEach(player -> _IDMap.put(player.id(), player));
    }

    /**
     *
     * Attempts to deserialize the difficulty, sets it to MEDIUM if not found.
     *
     * @param serialization The Properties object to deserialize from.
     *
     * @return The difficulty.
     */
    private Level.Difficulty _deserializeDifficulty(Properties serialization)
    {
        if (_difficulty != null) return _difficulty;

        if (serialization.containsKey("Difficulty"))
        {
            return Level.Difficulty.valueOf(
                    Data.pop(serialization, "Difficulty")
            );
        }

        else return Level.Difficulty.MEDIUM;
    }

    /**
     *
     * Handles deserialization of elements.
     *
     * Side effect: _players are modified.
     *
     * @param serialization The properties to deserialize from.
     *
     * @param remaining The remaining profiles.
     *
     * @return A Collection of Elements.
     *
     */
    private Collection<Element>
    _deserializeElements(Properties serialization,
                         Map<String, Profile> remaining)
    {
        _totalKeys = 0;

        // First need the players, so that we know
        // how many rows to reserve for the status bar.
        // Players are stored as id:<id>=x,y|hidden
        // so that (1) numeric ids don't clash with codes and
        // (2), players standing on elements don't cause bad
        // serialization and (3) players can be hidden.
        for (String key : serialization.stringPropertyNames())
        {
            if (key.startsWith("id:"))
            {
                String value = serialization.getProperty(key);

                _deserializePlayer(key, value, remaining);

                // so that we're only left with game-elements
                serialization.remove(key);
            }
        }

        Collection<Element> elements = new ArrayList<>();

        for (String key : serialization.stringPropertyNames())
        {
            String value = serialization.getProperty(key);

            Element element = _deserializeGameElement(key, value);

            elements.add(element);
        }

        return elements;
    }

    /**
     *
     * Deserializes a player. The player is added to _players and removed
     * from the remaining map. At the end, there will be only players that
     * haven't been loaded from the session. Some will be put at entrances,
     * the rest will be hidden.
     *
     * @param key The key of the property.
     *
     * @param value The value of the property.
     *
     * @param remaining The non-mapped set of profiles.
     */
    private void _deserializePlayer(String key,
                                    String value,
                                    Map<String, Profile> remaining)
    {
        // without the 'id:'
        String id = key.substring(3);

        Profile profile = remaining.remove(id);

        if (profile == null) return;

        if (value.equals("hidden")) _hidden.add(profile);

        else
        {
            Point point = new Point(value);

            _players.add(new Player(point, profile));
        }
    }

    /**
     *
     * Deserializes a non-player element (a "game element").
     *
     * @param key The key of the property.
     *
     * @param value The value of the property.
     *
     * @return The Element that was deserialized.
     */
    private Element _deserializeGameElement(String key, String value)
    {
        Point point = new Point(key);

        int code = Integer.parseInt(value);

        // Get the Element kind from the code with which the elements
        // are stored (e.g. 0 = wall)
        Element.Kind kind = Element.Kind.fromCode(code);

        if (kind == Element.Kind.KEY) ++_totalKeys;

        // Call the factory method
        return Element.Create(kind, point, _theme);
    }

    /**
     *
     * Handles loading the theme associated with
     * the level stored in the session.
     *
     * Sets the _theme field.
     *
     * @param serialization The properties to deserialize from.
     */
    private void _loadTheme(Properties serialization)
    {
        File file = new File(
                "resources/themes",
                Data.pop(serialization, "Theme")
        );

        try
        {
            _theme = new Theme(file);
        }

        catch (IOException e) { System.out.println("Error reading theme!"); }
    }

    /**
     *
     * Takes a collection of profiles and attempts to place them at
     * the entrances of the level. If an entrance is found, a Player
     * is constructed with the point of the entrance and the profile.
     *
     * Profiles that were not matched to entrances are put into the
     * _hidden collection, so that they can be revealed by the Level
     * when the players at the entrances leave their entrance.
     *
     * @param profiles The profiles
     */
    private void _placeAtEntrances(Collection<Profile> profiles)
    {
        assert(profiles != null);

        Iterator<Profile> iterator = profiles.iterator();

        // Iterate over the grid, look for
        // the entrances on each page
        for (Page page : _grid.pages())
        {
            for (Element entrance : page.entrances())
            {
                // Precedence for the profiles that were not hidden
                // when the session was stored, or that were just
                // newly registered for the Game and passed to the
                // constructor of LevelBuilder.
                if (! profiles.isEmpty())
                {
                    Point point = entrance.point();

                    _players.add(new Player(point, iterator.next()));

                    iterator.remove();
                }

                // Then check if there is space for the players that
                // were hidden when the session was stored, e.g. because
                // a player that was active during the level was now
                // not registered for the Game.
                else if (! _hidden.isEmpty())
                {
                    Point point = entrance.point();

                    Profile profile = _hidden.remove(_hidden.size() - 1);

                    _players.add(new Player(point, profile));
                }

                else break;
            }
        }

        _hidden.addAll(profiles);
    }

    /**
     *
     * Constructs the PageGrid.
     *
     * @param elements The elements for the PageGrid.
     *
     * @param numberOfPlayers The number of players in the game, to determine
     *                        how much space to leave for the status bar.
     *
     * @return The created PageGrid.
     */
    private PageGrid _setupGrid(Collection<Element> elements,
                                int numberOfPlayers)
    {
        // _pageSize gets us the size for the pages, i.e. the
        // terminalSize with space for the players and the level status
        TerminalSize terminalSize = _pageSize(numberOfPlayers);

        return new PageGrid(_levelSize, terminalSize, elements);
    }

    /**
     *
     * Handle fetching the relevant initial page.
     *
     * That page is either the page *one of* (random) the players is on,
     * or else the page at (0, 0) if there are no players.
     *
     * @return The initial Page.
     */
    private Page _fetchPage()
    {
        if (! _players.isEmpty())
        {
            return _grid.fetchPageOf(_players.get(0));
        }

        // The currentPage() of a newly constructed Grid is
        // the page at (0, 0)
        else return _grid.currentPage();
    }

    /**
     *
     * Calculates the TerminalSize that the PageGrid should know of,
     * i.e. the size of one page on the screen. That size is the regular
     * TerminalSize, minus some rows for the level status and status for
     * all the players.
     *
     * @param remaining The number of remaining players.
     *
     * @return The proper TerminalSize, with space left for the StatusBar.
     */
    private TerminalSize _pageSize(int remaining)
    {
        TerminalSize size = _gui.getScreen().getTerminalSize();

        int columns = size.getColumns();

        int rows = size.getRows();

        // Level meta-information
        rows -= 1;

        // Active players that were mapped during deserialization.
        rows -= _players.size();

        // Hidden players that were mapped during deserialization.
        rows -= _hidden.size();

        // Players that weren't mapped (this method has to be
        // called before those remaining players were placed at
        // entrances or hidden)
        rows -= remaining;

        return new TerminalSize(columns, rows);
    }

    /**
     *
     * Maps a list profiles to a Map from the player IDs to the profiles.
     *
     * @param profiles A list of profiles.
     *
     * @return The mapping from id to profile.
     */
    private Map<String, Profile> _map(List<Profile> profiles)
    {
        Map<String, Profile> map = new HashMap<>();

        profiles.forEach(profile -> map.put(profile.id(), profile));

        return map;
    }

    private Theme _theme;

    private String _name;

    private LevelSize _levelSize;

    private List<Profile> _hidden;

    private Map<String, Player> _IDMap;

    private List<Player> _players;

    private PageGrid _grid;

    private Page _page;

    private int _totalKeys;

    private GUIScreen _gui;

    private Level.Difficulty _difficulty;
}
