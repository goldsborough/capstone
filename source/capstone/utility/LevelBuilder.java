package capstone.utility;

import capstone.data.Data;
import capstone.data.Profile;
import capstone.data.Theme;
import capstone.element.Element;
import capstone.element.Entrance;
import capstone.element.Player;
import capstone.game.Level;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by petergoldsborough on 01/05/16.
 */
public class LevelBuilder extends Data
{
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
            .difficulty(level.difficulty());
    }

    public LevelBuilder(File file,
                        List<Profile> profiles,
                        GUIScreen gui) throws IOException
    {
        this(Data._load(file), profiles, gui);
    }

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


    public Theme theme()
    {
        return _theme;
    }

    public LevelBuilder theme(Theme theme)
    {
        assert(theme != null);

        _theme = theme;

        return this;
    }

    public String name()
    {
        return _name;
    }

    public LevelBuilder name(String name)
    {
        assert(name != null);

        _name = name;

        return this;
    }

    public LevelSize levelSize()
    {
        return _levelSize;
    }

    public LevelBuilder levelSize(LevelSize levelSize)
    {
        assert(levelSize != null);

        _levelSize = levelSize;

        return this;
    }

    public List<Profile> hidden()
    {
        return _hidden;
    }

    public LevelBuilder hidden(List<Profile> hidden)
    {
        assert(hidden != null);

        _hidden = hidden;

        return this;
    }

    public Map<String, Player> IDMap()
    {
        return _IDMap;
    }

    public List<Player> players()
    {
        return _players;
    }

    public LevelBuilder players(List<Player> players)
    {
        assert(players != null);

        _players = players;

        return this;
    }

    public PageGrid grid()
    {
        return _grid;
    }

    public LevelBuilder grid(PageGrid grid)
    {
        assert(grid != null);

        _grid = grid;

        return this;
    }

    public Page page()
    {
        return _page;
    }

    public LevelBuilder page(Page page)
    {
        assert(page != null);

        _page = page;

        return this;
    }

    public int totalKeys()
    {
        return _totalKeys;
    }

    public GUIScreen gui()
    {
        return _gui;
    }

    public LevelBuilder gui(GUIScreen gui)
    {
        assert(gui != null);

        _gui = gui;

        return this;
    }

    public Level.Difficulty difficulty()
    {
        return _difficulty;
    }

    public LevelBuilder difficulty(Level.Difficulty difficulty)
    {
        _difficulty = difficulty;

        return this;
    }

    @Override public void deserialize(Properties serialization)
    {
        throw new UnsupportedOperationException();
    }

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

    @Override public void store() throws IOException
    {
        super.store(new File("resources/sessions"));
    }

    @Override public Properties serialize()
    {
        return serialize(true);
    }

    public Properties serialize(boolean withTheme)
    {
        Properties properties = new Properties();

        assert(_name != null);

        properties.setProperty("Name", _name);

        properties.setProperty("Width", Integer.toString(_levelSize.getColumns()));
        properties.setProperty("Height", Integer.toString(_levelSize.getRows()));

        properties.setProperty("Difficulty", _difficulty.toString());

        if (withTheme) properties.setProperty("Theme", _theme.fileName());

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

        return properties;
    }

    @Override public String fileName()
    {
        assert(_name != null);

        return String.format(
                "%1$s_%2$tF_%2$tT.session",
                _name,
                new Date()
        );
    }

    private void _makeIDMap()
    {
        _IDMap = new HashMap<>();

        _players.forEach(player -> _IDMap.put(player.id(), player));
    }

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

    private Collection<Element>
    _deserializeElements(Properties serialization,
                         Map<String, Profile> remaining)
    {
        _totalKeys = 0;

        // First need the players, so that we know
        // how many rows to reserve for the status bar.
        for (String key : serialization.stringPropertyNames())
        {
            if (key.startsWith("id:"))
            {
                String value = serialization.getProperty(key);

                _deserializePlayer(key, value, remaining);

                serialization.remove(key);
            }
        }

        Collection<Element> elements = new ArrayList<>();

        // Players are stored as id:<id>=x,y
        // so that (1) numeric ids don't clash with codes and
        // 2, players standing on elements don't cause bad serialization

        for (String key : serialization.stringPropertyNames())
        {
            String value = serialization.getProperty(key);

            Element element = _deserializeGameElement(key, value);

            elements.add(element);
        }

        return elements;
    }

    private void _deserializePlayer(String key,
                                    String value,
                                    Map<String, Profile> remaining)
    {
        String id = key.substring(3);

        Profile profile = remaining.remove(id);

        if (value.equals("hidden")) _hidden.add(profile);

        else if (profile != null)
        {
            Point point = new Point(value);

            _players.add(new Player(point, profile));
        }
    }

    private Element _deserializeGameElement(String key, String value)
    {
        Point point = new Point(key);

        int code = Integer.parseInt(value);

        Element.Kind kind = Element.Kind.fromCode(code);

        if (kind == Element.Kind.KEY) ++_totalKeys;

        return Element.Create(kind, point, _theme);
    }

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

    private void _placeAtEntrances(Collection<Profile> profiles)
    {
        assert(profiles != null);

        Iterator<Profile> iterator = profiles.iterator();

        for (Page page : _grid.pages())
        {
            for (Element entrance : page.entrances())
            {
                if (! profiles.isEmpty())
                {
                    Point point = entrance.point();

                    _players.add(new Player(point, iterator.next()));

                    iterator.remove();
                }

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

    private PageGrid _setupGrid(Collection<Element> elements, int players)
    {
        TerminalSize terminalSize = _pageSize(players);

        return new PageGrid(_levelSize, terminalSize, elements);
    }

    private Page _fetchPage()
    {
        if (! _players.isEmpty())
        {
            return _grid.fetchPageOf(_players.get(0));
        }

        else return _grid.currentPage();
    }

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

        // Players that weren't mapped.
        rows -= remaining;

        return new TerminalSize(columns, rows);
    }

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
