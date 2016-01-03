

/**
 * Created by petergoldsborough on 12/26/15.
 */

package capstone.ui;

import capstone.data.Data;
import capstone.data.Profile;
import capstone.data.Representation;
import capstone.data.Theme;
import capstone.element.Element;
import capstone.element.MysteryBox;
import capstone.element.Player;
import capstone.utility.Page;
import capstone.utility.PageGrid;
import capstone.utility.Point;
import com.googlecode.lanterna.gui.GUIScreen;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Level extends Data
{
    public Level(File file, List<Profile> profiles) throws IOException
    {
        assert(file != null);
        assert(profiles != null);
        assert(! profiles.isEmpty());

        Properties session = new Properties();

        session.load(new BufferedInputStream(new FileInputStream(file)));

        _setup(session, profiles);
    }

    public Level(Properties session, List<Profile> profiles)
    {
        assert(session != null);
        assert(profiles != null);
        assert(! profiles.isEmpty());

        _setup(session, profiles);
    }

    public Level(File file,
                 Theme theme,
                 List<Profile> profiles) throws IOException
    {
        assert(file != null);
        assert(theme != null);
        assert(profiles != null);
        assert(! profiles.isEmpty());

        Properties layout = new Properties();

        layout.load(new BufferedInputStream(new FileInputStream(file)));

        _setup(layout, theme, profiles);
    }

    public Level(Properties layout, Theme theme, List<Profile> profiles)
    {
        assert(layout != null);
        assert(theme != null);
        assert(profiles != null);
        assert(! profiles.isEmpty());

        _setup(layout, theme, profiles);
    }

    public void update(Map<String, Player.Direction> directions)
    {
        _page.update();

        for (Map.Entry<String, Player.Direction> entry : directions.entrySet())
        {
            Player player = _IDMap.get(entry.getKey());

            assert(player != null);

            _evaluate(player.move(entry.getValue()));
        }
    }

    public GUIScreen screen()
    {
        return _screen;
    }

    public void screen(GUIScreen screen)
    {
        _screen = screen;
    }

    public String name()
    {
        return _name;
    }

    public boolean done()
    {
        return true;
    }

    public Collection<Element> elements()
    {
        ArrayList<Element> elements = new ArrayList<>(
                _page.elements()
        );

        elements.addAll(players());

        return elements;
    }

    public Theme theme()
    {
        return _theme;
    }

    public void Theme(Theme theme)
    {
        assert(theme != null);

        _theme = theme;

        // Redraw
    }

    public Collection<Player> players()
    {
        return _players.values();
    }

    public Collection<Point> playerPositions()
    {
        return _players.keySet();
    }

    public Map<Point, Player> playerMap()
    {
        return _players;
    }

    @Override public void deserialize(Properties serialization)
    {
        throw new UnsupportedOperationException();
    }

    public void deserialize(Properties serialization,
                            Map<String, Profile> remaining)
    {
        if (serialization.containsKey("Name"))
        {
            _name = Data.pop(serialization, "Name");
        }

        if (_theme == null) _loadTheme(serialization);

        else serialization.remove("Theme");

        _width = Integer.parseInt(Data.pop(serialization, "Width"));
        _height = Integer.parseInt(Data.pop(serialization, "Height"));

        for (String key : serialization.stringPropertyNames())
        {
            _makeElement(
                    key,
                    serialization.getProperty(key),
                    remaining
            );
        }
    }

    @Override public Properties serialize()
    {
        return serialize(true);
    }

    public Properties serialize(boolean withTheme)
    {
        Properties properties = new Properties();

        properties.setProperty("Width", Integer.toString(_width));
        properties.setProperty("Height", Integer.toString(_height));

        if (withTheme) properties.setProperty("Theme", _theme.fileName());

        for (Element element : elements())
        {
            properties.setProperty(
                    element.point().toStringPlain(),
                    Integer.toString(element.kind().code())
            );
        }

        return properties;
    }

    @Override public String fileName()
    {
        return null;
    }

    private void _setup(Properties session, List<Profile> profiles)
    {
        _players = new HashMap<>();

        Map<String, Profile> remaining = new HashMap<>();

        profiles.forEach(profile -> remaining.put(profile.id(), profile));

        deserialize(session, remaining);

        _placeNearOthers(new ArrayList<>(remaining.values()));

        _makeIDMap();
    }

    private void _setup(Properties layout,
                        Theme theme,
                        List<Profile> profiles)
    {
        _theme = theme;

        _players = new HashMap<>();

        // None will be found in the deserialization
        deserialize(layout, new HashMap<>());

        _placeAtEntrances(profiles);

        _makeIDMap();
    }
    
    private void _makeIDMap()
    {
        _IDMap = new HashMap<>();

        _players.values().forEach(player -> _IDMap.put(player.id(), player));
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

    private void _makeElement(String key, String value, Map<String, Profile> remaining)
    {
        Point point = new Point(key);

        Element.Kind kind = Element.Kind.fromCode(Integer.parseInt(value));

        Representation repr = _theme.representation(kind);

        /*
        switch(value)
        {
            case "0": _walls.put(point, new Wall(point, repr)); break;

            case "1": _entrances.put(point, new Entrance(point, repr)); break;

            case "2": _exits.put(point, new Exit(point, repr)); break;

            case "3": _keys.put(point, new Key(point, repr)); break;

            case "4": _staticObstacles.put(point, new StaticObstacle(point, repr)); break;

            case "5": _dynamicObstacles.put(point, new DynamicObstacle(point, repr)); break;

            case "6": _mysteryBoxes.put(point, new MysteryBox(point, repr)); break;

            default:
            {
                assert(remaining.containsKey(value));

                Profile profile = remaining.get(value);

                if (profile != null)
                {
                    _players.put(point, new Player(point, profile));

                    remaining.remove(value);
                }
            }
        }
        */
    }

    private void _placeNearOthers(List<Profile> remaining)
    {
        int last = remaining.size() - 1;


    }

    private void _placeAtEntrances(List<Profile> profiles)
    {
        assert(profiles != null);

        /*
        int last = profiles.size() - 1;

        for (Entrance free : _entrances.values())
        {
            if (profiles.isEmpty()) break;

            // Remove at end for greater efficiency with ArrayList
            Profile profile = profiles.get(last);

            _players.put(
                    free.point(),
                    new Player(free.point(), profile)
            );

            profiles.remove(last--);
        }
        */

        _hidden = profiles;
    }

    private void _evaluate(Player player)
    {
        Element element = _page.at(player.point());

        if (element == null) return;

        switch (element.kind())
        {
            case WALL:
                player.goBack();
                return;

            case ENTRANCE:
                player.goBack();
                return;

            case EXIT:
            {
                if (_keysCollected == _totalKeys) _page.remove(element);

                else player.goBack();

                return;
            }

            case KEY:
            {
                _page.remove(element);
                ++_keysCollected;

                return;
            }

            case STATIC_OBSTACLE:
                player.injure();
                return;

            case DYNAMIC_OBSTACLE:
                player.injure();
                return;

            case MYSTERY_BOX:
                _handleMysteryBox((MysteryBox) element, player);
                return;
        }

        Player other = _players.get(player.point());

        // Avoid collision (should first make moves, then check this)
        if (other != null && ! other.equals(player)) player.goBack();
    }

    private void _handleMysteryBox(MysteryBox mysteryBox, Player player)
    {
        mysteryBox.reveal(_screen);

        switch (mysteryBox.event())
        {
            case EMPTY:
                return;

            case HEAL:
                player.heal();

            case INJURE:
                player.injure();

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

            case UNLOCK_DOOR:
                break;
        }
    }

    private Theme _theme;

    private String _name;

    private int _width;

    private int _height;

    private int _keysCollected;

    private int _totalKeys;

    private List<Profile> _hidden;

    private GUIScreen _screen;

    private Map<String, Player> _IDMap;

    private Map<Point, Player> _players;

    private PageGrid _grid;

    private Page _page;
}