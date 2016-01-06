/**
 * Created by petergoldsborough on 12/26/15.
 */

package capstone.game;

import capstone.data.Highscore;
import capstone.data.Profile;
import capstone.data.Theme;
import capstone.element.Direction;
import capstone.element.Player;
import capstone.ui.LegendWidget;
import capstone.ui.LevelWindow;
import capstone.ui.MenuWindow;
import capstone.ui.InputKey;
import capstone.game.Level;
import capstone.ui.ProfileWindow;
import capstone.ui.WelcomeWindow;
import capstone.utility.LevelBuilder;
import capstone.utility.StopWatch;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.TerminalAppearance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Game
{
    public Game()
    {
        this(10);
    }

    public Game(double frameRate)
    {
        this.frameRate(frameRate);

        _screen = _createScreen();
        _screen.startScreen();

        this.gui(new GUIScreen(_screen, "Labyrinth"));

        _profiles = new ArrayList<>();
    }

    public void play() throws IOException
    {
        setup();

        loop();

        stop();
    }

    public void setup()
    {
        assert(_screen != null);

        // Important if setup() is called by Menu
        _profiles.clear();
        _level = null;

        while (_profiles.isEmpty())
        {
            _gui.showWindow(new WelcomeWindow(), GUIScreen.Position.CENTER);

            while (_level == null)
            {
                _profiles.addAll(_getProfiles());

                if (_profiles.isEmpty()) break;

                _level = _getLevel();
            }
        }
    }

    public void loop()
    {
        Map<String, Direction> directions = new HashMap<>();

        StopWatch timer = new StopWatch(_framePeriod, true);

        _watch = new StopWatch(true);

        while (! _level.isDone())
        {
            _handleInput(directions);

            if (timer.timedOut())
            {
                _level.update(directions);

                timer.reset();

                directions.clear();
            }
        }

        _endGame();
    }

    public void stop()
    {
        _screen.stopScreen();
    }

    public void backToStart()
    {
        setup();

        _watch = new StopWatch(true);
    }

    public void save() throws IOException
    {
        _level.store();
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

    public Level level()
    {
        return _level;
    }

    public void level(Level level)
    {
        assert(level != null);

        _level = level;

        if (_watch.isRunning()) _watch.reset();
    }

    public List<Profile> profiles()
    {
        return Collections.unmodifiableList(_profiles);
    }


    public void frameRate(double frameRate)
    {
        _framePeriod = (long) (1000 / frameRate);
    }

    public double frameRate()
    {
        return (1.0 / _framePeriod) * 1000;
    }

    private void _endGame()
    {
        _watch.stop();

        if (_level.hasWon()) _storeHighscore(_watch.seconds());

        String message = String.format(
                "\n%1$s\n\nTime: %2$.3f s",
                _level.hasWon() ? "Success!" : "Sorry, you lost.",
                _watch.seconds()
        );

        MessageBox.showMessageBox(_gui, "Result", message);

        setup();
        loop();
    }

    private Screen _createScreen()
    {
        TerminalAppearance style = TerminalAppearance.DEFAULT_APPEARANCE.withFont(
                TerminalAppearance.DEFAULT_NORMAL_FONT.deriveFont((float)16)
        );

        Terminal terminal = TerminalFacade.createSwingTerminal(style);

        return new Screen(terminal);
    }

    private List<Profile> _getProfiles()
    {
        ProfileWindow profileWindow = new ProfileWindow(_profiles);

        _gui.showWindow(profileWindow, GUIScreen.Position.CENTER);

        List<Profile> profiles = profileWindow.profiles();
/*
        List<Profile> profiles = null;

        try
        {
            profiles = new ArrayList<>();

            profiles.add(new Profile(new File("resources/profiles/peter.profile")));
        }

        catch (IOException e)
        {
            System.out.println("I/O Error");
            System.exit(1);
        }
        */

        _fillKeyMap(profiles);

        return profiles;
    }

    private void _fillKeyMap(Collection<Profile> profiles)
    {
        if (_keyMap == null) _keyMap = new HashMap<>();

        else _keyMap.clear();

        for (Profile profile : profiles)
        {
            for (InputKey key : profile.keyMap().keys())
            {
                _keyMap.put(key, profile);
            }
        }
    }

    private Level _getLevel()
    {
        LevelWindow levelWindow = new LevelWindow(new ArrayList<>(_profiles));

        _gui.showWindow(levelWindow, GUIScreen.Position.CENTER);

        return levelWindow.level();
/*
        Level level = null;

        try
        {
            level = new Level(new LevelBuilder(
                    new File("resources/layouts/test.layout"),
                    new Theme(new File("resources/themes/default.theme")),
                    new ArrayList<>(_profiles),
                    _gui
            ));
        }

        catch (IOException e)
        {
            System.out.println("I/O Error");
            System.exit(1);
        }
*/
    }

    private void _storeHighscore(double time)
    {
        Highscore highscore = _getHighscore(time);

        try
        {
            highscore.store();
        }

        catch (IOException e)
        {
            System.out.println("I/O Error writing highscore.");
        }
    }

    private Highscore _getHighscore(double time)
    {
        Highscore highscore = null;

        File file = new File(
            "resources/highscores",
            String.format("%1$s.highscore", _level.name())
        );

        if (file.exists())
        {
            try
            {
                highscore = new Highscore(file);
                highscore.put(time, _profiles);
            }

            catch(IOException e)
            {
                System.out.println("I/O Error reading highscore.");
            }
        }

        else highscore = new Highscore(_level.name(), time, _profiles);

        return highscore;
    }

    private void _handleInput(Map<String, Direction> directions)
    {
        Key key = _screen.readInput();

        if (key == null) return;

        if (key.getKind() == Key.Kind.Escape)
        {
            new MenuWindow(this).show();
        }

        else if (key.getKind() == Key.Kind.Backspace)
        {
            new MenuWindow(this).showLegend();
        }

        else _processKey(key, directions);
    }

    private void _processKey(Key raw, Map<String, Direction> directions)
    {
        assert(raw != null);

        InputKey key = InputKey.fromKey(raw);

        Profile profile = _keyMap.get(key);

        if (profile == null) return;

        Direction direction = profile.direction(key);

        directions.put(profile.id(), direction);
    }

    private Level _level;

    private GUIScreen _gui;

    private List<Profile> _profiles;

    private long _framePeriod;

    private StopWatch _watch;

    private Screen _screen;

    private Map<InputKey, Profile> _keyMap;
}