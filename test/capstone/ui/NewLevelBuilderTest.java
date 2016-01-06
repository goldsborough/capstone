package capstone.ui;

import capstone.utility.Page;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.screen.Screen;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import capstone.data.Profile;
import capstone.data.Representation;
import capstone.data.Theme;
import capstone.element.DynamicObstacle;
import capstone.element.Element;
import capstone.element.Entrance;
import capstone.element.Exit;
import capstone.element.Key;
import capstone.element.MysteryBox;
import capstone.element.Player;
import capstone.element.StaticObstacle;
import capstone.element.Wall;
import capstone.utility.KeyMap;
import capstone.utility.Point;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class NewLevelBuilderTest
{
    private static final int dimension = 5;

    private static Theme theme;

    private List<Profile> profiles;

    private Map<Point, Player> players;

    private Map<Point, Wall> walls;

    private Map<Point, Entrance> entrances;

    private Map<Point, Exit> exits;

    private Map<Point, Key> keys;

    private Map<Point, StaticObstacle> staticObstacles;

    private Map<Point, DynamicObstacle> dynamicObstacles;

    private Map<Point, MysteryBox> mysteryBoxes;

    private GUIScreen screen;


    public <T, U> void assertEquals(Collection<T> first, Collection<U> second)
    {
        assertTrue(first.containsAll(second));
        assertTrue(second.containsAll(first));
    }

    public Collection<Element> allElements()
    {
        Collection<Element> elements = new ArrayList<>();

        elements.addAll(players.values());

        elements.addAll(walls.values());

        elements.addAll(entrances.values());

        elements.addAll(exits.values());

        elements.addAll(keys.values());

        elements.addAll(staticObstacles.values());

        elements.addAll(dynamicObstacles.values());

        elements.addAll(mysteryBoxes.values());

        return elements;
    }

    public Properties getLayout()
    {
        Properties properties = new Properties();

        properties.setProperty("Width", "5");
        properties.setProperty("Height", "5");

        for (Element element : allElements())
        {
            if (element.kind() == Element.Kind.PLAYER) continue;

            properties.setProperty(
                    element.point().toStringPlain(),
                    Integer.toString(element.kind().code())
            );
        }

        return properties;
    }

    public Properties getSession()
    {
        Properties properties = getLayout();

        properties.setProperty("Theme", "default.theme");

        for (Player player : players.values())
        {
            properties.setProperty(
                    String.format("id:%s", player.id()),
                    player.point().toStringPlain()
            );
        }

        return properties;
    }

    public File getLayoutFile()
    {
        File file = new File("test.layout");

        file.deleteOnExit();

        Properties layout = getLayout();

        try
        {
            layout.store(
                    new BufferedOutputStream(new FileOutputStream(file)),
                    "Test Layout"
            );
        }

        catch (IOException e) { }

        return file;
    }

    public File getSessionFile()
    {
        File file = new File("test.session");

        file.deleteOnExit();

        Properties session = getSession();

        try
        {
            session.store(
                    new BufferedOutputStream(new FileOutputStream(file)),
                    "Test Session"
            );
        }

        catch (IOException e) { }

        return file;
    }

    public <E extends Element> void put(Map<Point, E> map,
                                        Point point,
                                        Element.Kind kind)
    {
        Element element = null;

        Representation representation = theme.representation(kind);

        switch (kind)
        {
            case WALL: element = new Wall(point, representation); break;

            case ENTRANCE: element = new Entrance(point, representation); break;

            case EXIT: element = new Exit(point, representation); break;

            case KEY: element = new Key(point, representation); break;

            case STATIC_OBSTACLE: element = new StaticObstacle(point, representation); break;

            case DYNAMIC_OBSTACLE: element = new DynamicObstacle(point, representation); break;

            case MYSTERY_BOX: element = new MysteryBox(point, representation); break;
        }

        map.put(point, (E)element);
    }

    @BeforeClass public static void setUpTheme()
    {
        theme = new Theme("TestTheme");

        for (Element.Kind kind : Element.Kind.kinds())
        {
            Representation representation = new Representation(
                    kind == Element.Kind.EXIT ? 'X' : kind.toString().charAt(0),
                    Terminal.Color.RED,
                    Terminal.Color.BLUE
            );

            theme.representation(kind, representation);
        }
    }

    @Before public void setUpProfilesAndPlayers()
    {
        Representation representation = new Representation(
                '',
                Terminal.Color.RED,
                Terminal.Color.BLUE
        );

        Profile profile = new Profile(
                "test",
                "Real Name",
                KeyMap.Arrows(),
                representation
        );

        profiles = new ArrayList<>();

        profiles.add(new Profile(profile));

        players = new HashMap<>();

        players.put(
                new Point(1, 0),
                new Player(new Point(1, 0), new Profile(profile))
        );

        profile.id("test2");
        profile.realName("Fake Name");
        profile.keyMap(KeyMap.WASD());

        profiles.add(new Profile(profile));

        players.put(
                new Point(3, 0),
                new Player(new Point(3, 0), profile)
        );
    }

    @Before public void setUpWalls()
    {
        walls = new HashMap<>();

        for (int i = 0; i < dimension; ++i)
        {
            put(walls, new Point(0, i), Element.Kind.WALL);

            if (i != 1 && i != 3)
            {
                put(walls, new Point(i, 0), Element.Kind.WALL);
            }

            put(walls, new Point(dimension - 1, i), Element.Kind.WALL);

            if (i != 1 && i != 3)
            {
                put(walls, new Point(i, dimension - 1), Element.Kind.WALL);
            }
        }
    }

    @Before public void setUpEntrances()
    {
        entrances = new HashMap<>();

        put(entrances, new Point(1, 0), Element.Kind.ENTRANCE);

        put(entrances, new Point(3, 0), Element.Kind.ENTRANCE);
    }

    @Before public void setUpExit()
    {
        exits = new HashMap<>();

        put(exits, new Point(1, dimension - 1), Element.Kind.EXIT);

        put(exits, new Point(3, dimension - 1), Element.Kind.EXIT);
    }

    @Before public void setUpKeys()
    {
        keys = new HashMap<>();

        put(keys, new Point(3, 3), Element.Kind.KEY);

        put(keys, new Point(3, 1), Element.Kind.KEY);
    }

    @Before public void setUpStaticObstacles()
    {
        staticObstacles = new HashMap<>();

        put(staticObstacles, new Point(2, 2), Element.Kind.STATIC_OBSTACLE);

        put(staticObstacles, new Point(2, 3), Element.Kind.STATIC_OBSTACLE);
    }

    @Before public void setUpDynamicObstacles()
    {
        dynamicObstacles = new HashMap<>();

        put(dynamicObstacles, new Point(3, 2), Element.Kind.DYNAMIC_OBSTACLE);

        put(dynamicObstacles, new Point(1, 2), Element.Kind.DYNAMIC_OBSTACLE);
    }

    @Before public void setUpMysteryBoxes()
    {
        mysteryBoxes = new HashMap<>();

        put(mysteryBoxes, new Point(1, 1), Element.Kind.MYSTERY_BOX);

        put(mysteryBoxes, new Point(1, 3), Element.Kind.MYSTERY_BOX);
    }

    @Before public void setUpScreen()
    {
        screen = new GUIScreen(new Screen(TerminalFacade.createTerminal()));
    }

    @Test public void testSessionConstructorConstructsWellFromFile() throws IOException
    {
        File file = getSessionFile();

        Level level = new Level(file, profiles, screen);

        assertEquals(level.players(), players.values());

        Page page = level.currentPage();

        assertEquals(page.walls(), walls.values());
        assertEquals(page.entrances(), entrances.values());
        assertEquals(page.exits(), exits.values());
        assertEquals(page.keys(), keys.values());
        assertEquals(page.staticObstacles(), staticObstacles.values());
        assertEquals(page.dynamicObstacles(), dynamicObstacles.values());
        assertEquals(page.mysteryBoxes(), mysteryBoxes.values());

        assert(file.delete());
    }

    @Test public void testSessionConstructorConstructsWellFromProperties()
    {
        Level level = new Level(getSession(), profiles, screen);

        Page page = level.currentPage();

        assertEquals(level.players(), players.values());

        assertEquals(level.currentPage().walls(), walls.values());
        assertEquals(page.entrances(), entrances.values());
        assertEquals(page.exits(), exits.values());
        assertEquals(page.keys(), keys.values());
        assertEquals(page.staticObstacles(), staticObstacles.values());
        assertEquals(page.dynamicObstacles(), dynamicObstacles.values());
        assertEquals(page.mysteryBoxes(), mysteryBoxes.values());
    }

    @Test public void testLayoutThemeConstructor()
    {
        Level level = new Level("Level", getLayout(), theme, profiles, screen);

        Page page = level.currentPage();

        assertEquals(level.players(), players.values());

        assertEquals(page.walls(), walls.values());
        assertEquals(page.entrances(), entrances.values());
        assertEquals(page.exits(), exits.values());
        assertEquals(page.keys(), keys.values());
        assertEquals(page.staticObstacles(), staticObstacles.values());
        assertEquals(page.dynamicObstacles(), dynamicObstacles.values());
        assertEquals(page.mysteryBoxes(), mysteryBoxes.values());
    }

    @Test(expected=AssertionError.class)
    public void testSessionConstructorConstructingFromFileThrowsForEmptyProfiles() throws IOException
    {
        new Level(getSessionFile(), new ArrayList<>(), screen);
    }

    @Test(expected=AssertionError.class)
    public void testSessionConstructorConstructingFromFileThrowsForNullProfiles() throws IOException
    {
        new Level(getSessionFile(), null, screen);
    }

    @Test(expected=AssertionError.class)
    public void testSessionConstructorConstructingFromFileThrowsForNullScreen() throws IOException
    {
        new Level(getSessionFile(), profiles, null);
    }

    @Test(expected=AssertionError.class)
    public void testSessionConstructorConstructingFromPropertiesThrowsForEmptyProfiles()
    {
        new Level(getSession(), new ArrayList<>(), screen);
    }

    @Test(expected=AssertionError.class)
    public void testSessionConstructorConstructingFromPropertiesThrowsForNullProfiles()
    {
        new Level(getSession(), null, screen);
    }

    @Test(expected=AssertionError.class)
    public void testSessionConstructorConstructingFromPropertiesThrowsForNullScreen()
    {
        new Level(getSession(), profiles, null);
    }

    @Test(expected=AssertionError.class)
    public void testLayoutThemeConstructorConstructingFromPropertiesThrowsForEmptyProfiles()
    {
        new Level("Level", getLayout(), theme, new ArrayList<>(), screen);
    }

    @Test(expected=AssertionError.class)
    public void testLayoutThemeConstructorConstructingFromPropertiesThrowsForNullTheme()
    {
        new Level("Level", getLayout(), null, profiles, screen);
    }

    @Test(expected=AssertionError.class)
    public void testLayoutThemeConstructorFromPropertiesThrowsForNullProfiles()
    {
        new Level("Level", getLayout(), theme, null, screen);
    }

    @Test(expected=AssertionError.class)
    public void testLayoutThemeConstructorFromPropertiesThrowsForNullScreen()
    {
        new Level("Level", getLayout(), theme, profiles, null);
    }

    @Test(expected=AssertionError.class)
    public void testLayoutThemeConstructorConstructingFromFileThrowsForEmptyProfiles() throws IOException
    {
        new Level(getLayoutFile(), theme, new ArrayList<>(), screen);
    }

    @Test(expected=AssertionError.class)
    public void testLayoutThemeConstructorConstructingFromFileThrowsForNullTheme() throws IOException
    {
        new Level(getLayoutFile(), null, profiles, screen);
    }

    @Test(expected=AssertionError.class)
    public void testLayoutThemeConstructorFromFileThrowsForNullProfiles() throws IOException
    {
        new Level(getLayoutFile(), theme, null, screen);
    }

    @Test(expected=AssertionError.class)
    public void testLayoutThemeConstructorFromFileThrowsForNullScreen() throws IOException
    {
        new Level(getLayoutFile(), theme, profiles, null);
    }
}