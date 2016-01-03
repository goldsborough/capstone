/**
 * Created by petergoldsborough on 12/26/15.
 */

package capstone;

import capstone.data.Highscore;
import capstone.data.Profile;
import capstone.data.Theme;
import capstone.element.Player;
import capstone.ui.InputKey;
import capstone.ui.Level;
import capstone.ui.LevelWindow;
import capstone.ui.ProfileWindow;
import capstone.utility.StopWatch;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.TerminalAppearance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Game
{
    public Game()
    {
        this(1);
    }

    public Game(double frameRate)
    {
        this.frameRate(frameRate);

        _screen = _createScreen();
        _screen.startScreen();

        _gui = new GUIScreen(_screen, "Labyrinth");

        _watch = new StopWatch();
    }

    public void play() throws IOException
    {
        start();

        loop();

        stop();
    }

    public void start() throws IOException
    {
        assert(_screen != null);

        //_gui.showWindow(new WelcomeWindow(), GUIScreen.Position.CENTER);

        //_getProfiles();

        //_getLevel();

        _profiles = new ArrayList<>();

        _profiles.add(new Profile(new File("resources/profiles/peter.profile")));

        _level = new Level(
                new File("resources/layouts/test.layout"),
                new Theme(new File("resources/themes/default.theme")),
                _profiles
        );

        _level.screen(_screen);
    }

    public void loop()
    {
        while (true);

        /*
        _watch.start();

        StopWatch timer = new StopWatch(_framePeriod);

        Map<String, Player.Direction> directions = new HashMap<>();

        while (! _level.done())
        {
            _getInput(directions);

            if (timer.timedOut())
            {
                _level.update(directions);

                timer.reset();
            }
        }

        _watch.stop();
        */
    }

    public void stop()
    {
        //_storeHighscore();

        _screen.stopScreen();
    }

    public void frameRate(double frameRate)
    {
        _framePeriod = (long) (1000 / frameRate);
    }

    public double frameRate()
    {
        return (1.0 / _framePeriod) * 1000;
    }

    private Screen _createScreen()
    {
        TerminalAppearance style = TerminalAppearance.DEFAULT_APPEARANCE.withFont(
                TerminalAppearance.DEFAULT_NORMAL_FONT.deriveFont((float)16)
        );

        _terminal = TerminalFacade.createSwingTerminal(style);

        return new Screen(_terminal);
    }

    private void _getProfiles()
    {
        ProfileWindow profileWindow = new ProfileWindow();

        _gui.showWindow(profileWindow, GUIScreen.Position.CENTER);

        _profiles = profileWindow.profiles();

        assert(_profiles != null);

        _fillKeyMap(_profiles);
    }

    private void _fillKeyMap(ArrayList<Profile> profiles)
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

    private void _getLevel()
    {
        LevelWindow levelWindow = new LevelWindow(_profiles);

        _gui.showWindow(levelWindow, GUIScreen.Position.CENTER);

        _level = levelWindow.level();

        assert(_level != null);
    }

    private void _storeHighscore()
    {
        Highscore highscore = _getHighscore();

        try
        {
            highscore.store();
        }

        catch (IOException e)
        {
            System.out.println("I/O Error writing highscore.");
        }
    }

    private Highscore _getHighscore()
    {
        Highscore highscore = null;

        File file = new File("resources/highscores", _level.name());

        if (file.exists())
        {
            try
            {
                highscore = new Highscore(file);
                highscore.put(_watch.seconds(), _profiles);
            }

            catch(IOException e)
            {
                System.out.println("I/O Error reading highscore.");
            }
        }

        else highscore = new Highscore(
                _level.name(),
                _watch.seconds(),
                _profiles
        );

        return highscore;
    }

    private void _getInput(Map<String, Player.Direction> directions)
    {
        Key key = _screen.readInput();

        if (key != null)
        {
            Profile profile = _keyMap.get(key);

            Player.Direction direction = profile.direction(key);

            directions.put(profile.id(), direction);
        }
    }

    private long _framePeriod;

    private StopWatch _watch;

    private Level _level;

    private final GUIScreen _gui;

    private final Screen _screen;

    private Terminal _terminal;

    private Map<Key, Profile> _keyMap;

    private ArrayList<Profile> _profiles;
}