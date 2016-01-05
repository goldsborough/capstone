package capstone.ui;

import capstone.data.Data;
import capstone.data.Profile;
import capstone.data.Theme;
import capstone.element.Element;
import capstone.element.MysteryBox;
import capstone.element.Player;
import capstone.utility.LevelSize;
import capstone.utility.Page;
import capstone.utility.PageGrid;
import capstone.utility.Point;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Level extends Data
{
    public Level(File file,
                 List<Profile> profiles,
                 GUIScreen gui) throws IOException
    {
        assert(file != null);
        assert(profiles != null);
        assert(gui != null);
        assert(! profiles.isEmpty());

        Properties session = new Properties();

        session.load(new BufferedInputStream(new FileInputStream(file)));

        _setup(session, profiles, gui);
    }

    public Level(Properties session,
                 List<Profile> profiles,
                 GUIScreen gui)
    {
        assert(session != null);
        assert(profiles != null);
        assert(gui != null);
        assert(! profiles.isEmpty());

        _setup(session, profiles, gui);
    }

    public Level(File file,
                 Theme theme,
                 List<Profile> profiles,
                 GUIScreen gui) throws IOException
    {
        assert(file != null);
        assert(theme != null);
        assert(profiles != null);
        assert(gui != null);
        assert(! profiles.isEmpty());

        Properties layout = new Properties();

        layout.load(new BufferedInputStream(new FileInputStream(file)));

        String filename = file.getName();

        String name = filename.substring(0, filename.lastIndexOf('.'));

        _setup(name, layout, theme, profiles, gui);
    }

    public Level(String name,
                 Properties layout,
                 Theme theme,
                 List<Profile> profiles,
                 GUIScreen gui)
    {
        assert(layout != null);
        assert(theme != null);
        assert(profiles != null);
        assert(gui != null);
        assert(! profiles.isEmpty());

        _setup(name, layout, theme, profiles, gui);
    }

    public void update(Map<String, Player.Direction> directions)
    {
        _checkResize();

        _page.update(_screen);

        Page old = _page;

        _movePlayers(directions);

        _evaluatePlayers();

        if (_page != old)
        {
            _screen.clear();
            _page.render(_screen);
        }

        _statusBar.draw();

        _screen.refresh();
    }

    public String name()
    {
        return _name;
    }

    public boolean isDone()
    {
        return hasWon() || hasLost();
    }

    public boolean hasWon()
    {
        return _won;
    }

    public boolean hasLost()
    {
        return _players.isEmpty() && _hidden.isEmpty();
    }

    public void redraw()
    {
        _clear(_screen);

        _page.render(_screen);

        for (Player player : _players)
        {
            player.render(_screen, _page.region());
        }

        _statusBar.draw();

        _screen.refresh();
    }

    public void resize()
    {
        _grid.resize(_screen.getTerminalSize());

        _screen.refresh();
    }

    public Theme theme()
    {
        return _theme;
    }

    public void Theme(Theme theme)
    {
        assert(theme != null);

        _theme = theme;

        _grid.pages().forEach(page -> page.render(_screen));

        _screen.refresh();
    }

    public Collection<Player> players()
    {
        Collection<Player> players = new ArrayList<>(_players);

        players.addAll(_deadPlayers);

        return players;
    }

    public Collection<Profile> hidden()
    {
        return Collections.unmodifiableCollection(_hidden);
    }

    public TerminalSize pageSize()
    {
        TerminalSize size = _screen.getTerminalSize();

        int columns = size.getColumns();

        int rows = size.getRows();

        rows -= 1; // level information

        rows -= _players.size();

        rows -= _deadPlayers.size();

        rows -= _hidden.size();

        return new TerminalSize(columns, rows);
    }

    public int keysCollected()
    {
        return _keysCollected;
    }

    public int totalKeys()
    {
        return _totalKeys;
    }

    public PageGrid grid()
    {
        return _grid;
    }

    public Page currentPage()
    {
        return _page;
    }

    public GUIScreen gui()
    {
        return _gui;
    }

    public void gui(GUIScreen gui)
    {
        assert(gui != null);

        _gui = gui;

        _screen = _gui.getScreen();
    }

    public Screen screen()
    {
        return _screen;
    }

    public LevelSize size()
    {
        return _size;
    }

    @Override public void store() throws  IOException
    {
        super.store(new File("resources/sessions"));
    }

    @Override public void deserialize(Properties serialization)
    {
        deserialize(serialization, new HashMap<>());
    }

    public void deserialize(Properties serialization,
                            Map<String, Profile> remaining)
    {
        if (serialization.containsKey("Name"))
        {
            _name = Data.pop(serialization, "Name");
        }

        // Difference for loading a session vs a layout
        if (_theme == null) _loadTheme(serialization);

        else serialization.remove("Theme");

        _size = new LevelSize(
                Integer.parseInt(Data.pop(serialization, "Width")),
                Integer.parseInt(Data.pop(serialization, "Height"))
        );

        _deserializeElements(serialization, remaining);

        PageGrid.Location pageWithEntrance =
                _grid.locationOf(Element.Kind.ENTRANCE);

        assert(pageWithEntrance != null);

        // Fetch page with an entrance
        _page = _grid.fetch(pageWithEntrance.index());
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

        properties.setProperty("Width", Integer.toString(_size.getColumns()));
        properties.setProperty("Height", Integer.toString(_size.getRows()));

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



        // hidden players



        return properties;
    }

    @Override public String fileName()
    {
        assert(_name != null);

        return String.format("%1$s.session", _name);
    }

    private void _setup(Properties session,
                        List<Profile> profiles,
                        GUIScreen gui)
    {
        assert(session.containsKey("Name"));

        _commonSetup(gui);

        Map<String, Profile> remaining = new HashMap<>();

        profiles.forEach(profile -> remaining.put(profile.id(), profile));

        deserialize(session, remaining);

        _grid.resize(pageSize());

        _placeAtFreePoints(remaining.values());

        _makeIDMap();

        redraw();
    }

    private void _setup(String name,
                        Properties layout,
                        Theme theme,
                        List<Profile> profiles,
                        GUIScreen gui)
    {
        _commonSetup(gui);

        _theme = theme;

        _name = name;

        deserialize(layout);

        _grid.resize(pageSize());

        _placeAtEntrances(profiles);

        _makeIDMap();

        redraw();
    }

    private void _commonSetup(GUIScreen gui)
    {
        _statusBar = new StatusBar(this);

        _players = new ArrayList<>();

        _deadPlayers = new ArrayList<>();

        this.gui(gui);
    }
    
    private void _makeIDMap()
    {
        _IDMap = new HashMap<>();

        _players.forEach(player -> _IDMap.put(player.id(), player));
    }

    private void _deserializeElements(Properties serialization,
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

        _grid = new PageGrid(_size, _screen.getTerminalSize());

        // Players are stored as id:<id>=x,y
        // so that (1) numeric ids don't clash with codes and
        // 2, players standing on elements don't cause bad serialization

        for (String key : serialization.stringPropertyNames())
        {
            String value = serialization.getProperty(key);

            _deserializeGameElement(key, value);
        }

        _page = _grid.currentPage();
    }

    private void _deserializePlayer(String key,
                                    String value,
                                    Map<String, Profile> remaining)
    {
        String id = key.substring(3);

        Point point = new Point(value);

        _players.add(new Player(point, remaining.remove(id)));
    }

    private void _deserializeGameElement(String key, String value)
    {
        Point point = new Point(key);

        int code = Integer.parseInt(value);

        Element.Kind kind = Element.Kind.fromCode(code);

        if (kind == Element.Kind.KEY) ++_totalKeys;

        _grid.add(Element.Create(kind, point, _theme));
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

    private void _placeAtFreePoints(Collection<Profile> profiles)
    {
        assert(profiles != null);

        if (profiles.isEmpty()) return;

        Iterator<Profile> iterator = profiles.iterator();

        Set<Point> taken = new HashSet<>();

        for (int space = 0; space < _page.amountOfFreeSpace(); ++space)
        {
            Point free;

            // players not contained in page
            do free = _page.freePoint();

            while (free != null && taken.contains(free));

            if (free == null) break;

            // Remove at end for greater efficiency with ArrayList
            Profile profile = iterator.next();

            _players.add(new Player(free, profile));

            taken.add(free);

            iterator.remove();

            if (profiles.isEmpty()) break;
        }

        _hidden = profiles;
    }

    private void _placeAtEntrances(Collection<Profile> profiles)
    {
        assert(profiles != null);

        Iterator<Profile> iterator = profiles.iterator();

        for (Element entrance : _page.entrances())
        {
            if (profiles.isEmpty()) break;

            _players.add(new Player(entrance.point(), iterator.next()));

            iterator.remove();
        }
    }

    private void _movePlayers(Map<String, Player.Direction> directions)
    {
        for (Map.Entry<String, Player.Direction> entry : directions.entrySet())
        {
            Player player = _IDMap.get(entry.getKey());

            assert(player != null);

            _move(player, entry.getValue());
        }
    }

    private void _move(Player player, Player.Direction direction)
    {
        player.unrender(_screen, _page.region());

        // See if the player stood on something, e.g. entrance
        Element element = _page.at(player.point());

        if (element != null) element.render(_screen, _page.region());

        _page = _grid.follow(player.move(direction));
    }

    private void _evaluatePlayers()
    {
        for (int i = 0; i < _players.size(); )
        {
            Player player = _players.get(i);

            _evaluate(player);

            if (player.isAlive())
            {
                ++i;

                player.render(_screen, _page.region());
            }
        }
    }

    private void _evaluate(Player player)
    {
        assert(player != null);

        Element element = _page.at(player.point());

        if (element == null) return;

        switch (element.kind())
        {
            case WALL:
                player.goBack();
                break;

            case ENTRANCE:
                if (player.canGoBack()) player.goBack();
                break;

            case EXIT:
            {
                if (_keysCollected == _totalKeys) _won = true;

                else player.goBack();

                break;
            }

            case KEY:
            {
                element.unrender(_screen, _page.region());
                _page.remove(element);

                ++_keysCollected;

                break;
            }

            case STATIC_OBSTACLE:
            case DYNAMIC_OBSTACLE:
            {
                player.injure();

                if (player.isDead()) _kill(player);

                break;
            }

            case MYSTERY_BOX:
                _handleMysteryBox((MysteryBox) element, player);
                break;

            default:
            {
                for (Player other : _players)
                {
                    if (! player.equals(other) &&
                            player.point().equals(other.point()))
                    {
                        player.goBack();

                        break;
                    }
                }
            }
        }

        _page = _grid.follow(player);
    }

    private void _handleMysteryBox(MysteryBox mysteryBox, Player player)
    {
        mysteryBox.reveal(_gui, _page.region());

        switch (mysteryBox.event())
        {
            case EMPTY:
                return;

            case HEAL:
                player.heal();

            case INJURE:
            {
                player.injure();

                if (player.isDead()) _kill(player);

                break;
            }

            case NEW_KEY:
            {
                _grid.generate(Element.Kind.KEY, _theme);

                ++_totalKeys;

                break;
            }

            case NEW_STATIC_OBSTACLE:
                _grid.generate(Element.Kind.STATIC_OBSTACLE, _theme);
                break;

            case NEW_DYNAMIC_OBSTACLE:
                _grid.generate(Element.Kind.DYNAMIC_OBSTACLE, _theme);
                break;

            case NEW_MYSTERY_BOX:
                _grid.generate(Element.Kind.MYSTERY_BOX, _theme);
                break;

            case NEW_WALL:
                _grid.generate(Element.Kind.WALL, _theme);
                break;

            case REMOVE_DYNAMIC_OBSTACLE:
                _grid.remove(Element.Kind.DYNAMIC_OBSTACLE);
                break;

            case REMOVE_STATIC_OBSTACLE:
                _grid.remove(Element.Kind.STATIC_OBSTACLE);
                break;

            case LOSE_KEY:
            {
                _grid.generate(Element.Kind.KEY, _theme);

                --_keysCollected;

                return;
            }
        }
    }

    private void _kill(Player player)
    {
        assert(_players.contains(player));
        assert(_IDMap.containsKey(player.id()));
        assert(player.isDead());

        _players.remove(player);

        _deadPlayers.add(player);

        _IDMap.remove(player.id());
    }

    private void _checkResize()
    {
        if (! _screen.resizePending()) return;

        _screen.refresh();

        _clear(_screen);

        _grid.resize(pageSize());

        _page = _grid.fetchPageOf(_players.get(0));

        _page.render(_screen);
    }

    private void _clear(Screen screen)
    {
        ScreenWriter writer = new ScreenWriter(screen);

        writer.fillScreen(' ');
    }

    private Theme _theme;

    private String _name;

    private LevelSize _size;

    private int _keysCollected;

    private int _totalKeys;

    private Collection<Profile> _hidden;

    private GUIScreen _gui;

    private Screen _screen;

    private Map<String, Player> _IDMap;

    private List<Player> _players;

    private List<Player> _deadPlayers;

    private PageGrid _grid;

    private Page _page;

    private boolean _won;

    private StatusBar _statusBar;
}