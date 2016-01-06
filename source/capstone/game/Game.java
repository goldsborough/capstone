/**
 * Created by petergoldsborough on 12/26/15.
 */

package capstone.game;

import capstone.data.Highscore;
import capstone.data.Profile;
import capstone.element.Direction;
import capstone.ui.InputKey;
import capstone.ui.LevelWindow;
import capstone.ui.MenuWindow;
import capstone.ui.ProfileWindow;
import capstone.ui.WelcomeWindow;
import capstone.utility.StopWatch;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Main entrance point of the program, manages dispatchment of the
 * levels (the actual UI) and acts as the layer between the
 * environment/system and the game by mapping key-presses to player
 * and handing them over to the Level class, which handles the
 * game logic.
 *
 * The Game class is also responsible for managing the frame-rate, which is
 * the basic unit of time used by the system. The frame-rate corresponds to
 * how often the game is updated per second, i.e. how often accumulated
 * user-presses are transferred to the level to move the players. It also
 * controls at what rate dynamic obstacles move (which, however, is also
 * influenced by the difficulty of the game).
 *
 * @author Peter Goldsborough
 *
 */
public class Game
{
    /**
     * Defaults the frame-rate to 10 Hz, i.e. 10 updates per second.
     */
    public Game()
    {
        this(10);
    }

    /**
     *
     * Constructs the Game with the given frameRate
     *
     * @param frameRate How often to update the game per second (in Hertz).
     *
     */
    public Game(double frameRate)
    {
        this.frameRate(frameRate);

        _screen = _createScreen();
        _screen.startScreen();

        this.gui(new GUIScreen(_screen, "Labyrinth"));

        _profiles = new ArrayList<>();
    }


    /**
     *
     * Executes the full cycle of setting up the game, looping until
     * the game is lost or won, and then storing the result and
     * stopping the screen.
     *
     * @throws IOException for various file I/O.
     *
     */

    public void play() throws IOException
    {
        setup();

        loop();

        stop();
    }

    /**
     * Sets up the game for playing by opening the various setup windows,
     * such as for selecting profiles, themes, levels, session etc.
     */
    public void setup()
    {
        assert(_screen != null);

        // Important if setup() is called by Menu
        _profiles.clear();
        _level = null;

        // Loop the UI so we can always cancel operations and
        // go back to the previous window, e.g. so you can cancel
        // selecting a level and come back to the welcome screen.
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

    /**
     * Main event-loop. Basically accumulates key-presses and maps the to
     * the respective players (if possible) and once a frame has timed-out
     * sends those key-presses to the game for processing.
     */
    public void loop()
    {
        Map<String, Direction> directions = new HashMap<>();

        // For frame time-outs.
        _timer = new StopWatch(_framePeriod, true);

        // Time the whole game for storing the high-score.
        _watch = new StopWatch(true);

        while (! _level.isDone())
        {
            // Get key-presses.
            _handleInput(directions);

            if (_timer.timedOut())
            {
                _level.update(directions);

                _timer.reset();

                directions.clear();
            }
        }

        _endGame();
    }

    /**
     * Stops the screen.
     */
    public void stop()
    {
        _screen.stopScreen();
    }

    /**
     * Utility method to setup the level and restart the watch.
     * Used by the menu to go back to the start.
     */
    public void backToStart()
    {
        setup();

        _watch = new StopWatch(true);
    }


    /**
     *
     * Saves the level at its current state.
     *
     * @throws IOException in case of file I/O badness.
     */
    public void save() throws IOException
    {
        _level.store();
    }

    /**
     * @return The GUIScreen of the game.
     */
    public GUIScreen gui()
    {
        return _gui;
    }

    /**
     * @param gui The GUIScreen of the game.
     */
    public void gui(GUIScreen gui)
    {
        assert(gui != null);

        _gui = gui;

        _screen = _gui.getScreen();
    }

    /**
     * @return The currently-executed game.
     */
    public Level level()
    {
        return _level;
    }

    /**
     * @param level The currently-executed game.
     */
    public void level(Level level)
    {
        assert(level != null);

        _level = level;

        if (_watch.isRunning()) _watch.reset();
    }

    /**
     * @return The profiles registered in the game.
     */
    public List<Profile> profiles()
    {
        return Collections.unmodifiableList(_profiles);
    }


    /**
     * @param frameRate How often to refresh player
     *                  and obstacle movements per second
     */
    public void frameRate(double frameRate)
    {
        // Convert from frequency to period
        _framePeriod = (long) (1000 / frameRate);

        // Reset the timer for the main loop.
        if (_timer != null) _timer.timeout(_framePeriod);
    }

    /**
     * @return The current framerate of the game.
     */
    public double frameRate()
    {
        return (1.0 / _framePeriod) * 1000;
    }

    /**
     * Ends the game by showing a result message-box (Success/Lost)
     * and stores the highscore if the game was won. Then goes right
     * back to the start screen.
     */
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

    /**
     * @return A new Screen (between Terminal and GUIScreen) for the game.
     */
    private Screen _createScreen()
    {
        Terminal terminal = TerminalFacade.createTerminal();

        return new Screen(terminal);
    }

    /**
     *
     * Manages retrieving the profiles for the game by showing the relevant
     * UI window. Finally fills the keymap for mapping key-presses to profiles.
     *
     * @return A list or registered profiles.
     */
    private List<Profile> _getProfiles()
    {
        ProfileWindow profileWindow = new ProfileWindow(_profiles);

        _gui.showWindow(profileWindow, GUIScreen.Position.CENTER);

        List<Profile> profiles = profileWindow.profiles();

        // Map keys to profiles
        _fillKeyMap(profiles);

        return profiles;
    }

    /**
     *
     * Fills the _keyMap field with each key of every player mapped to their
     * profile, such that you can lookup a player's profile in O(1) from a
     * new keypress.
     *
     * @param profiles The profiles collected previously
     */
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

    /**
     *
     * Loads a level from the LevelWindow UI.
     *
     * @return The level to be executed.
     */
    private Level _getLevel()
    {
        LevelWindow levelWindow = new LevelWindow(new ArrayList<>(_profiles));

        _gui.showWindow(levelWindow, GUIScreen.Position.CENTER);

        return levelWindow.level();
    }

    /**
     * Stores a new entry for the players in the highscore associated with
     * the game. Creates a new highscore object/file if none exists yet for
     * this level, else adds it to the old one.
     *
     * Note that only successes are stored.
     *
     * @param time The elapsed time for the last game.
     */
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

    /**
     * Helper method of _storeHighscore to retrieve a new highscore object
     * (does what _storeHighscore says).
     *
     * @param time The elapsed time for the last game.
     *
     * @return A Highscore object, either newly created or from an
     *         existing file.
     */
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

    /**
     *
     * Manages keypress input.
     *
     * The escape key opens the menu, the backspace key is a shortcut to the
     * legend. Other keys may be user keys or simply noise.
     *
     * @param directions The Map from ids to directions to add a potential
     *                   player key-press too.
     */
    private void _handleInput(Map<String, Direction> directions)
    {
        Key key = _screen.readInput();

        if (key == null) return;

        // Show full menu
        if (key.getKind() == Key.Kind.Escape)
        {
            new MenuWindow(this).show();
        }

        // Show only the legend
        else if (key.getKind() == Key.Kind.Backspace)
        {
            new MenuWindow(this).showLegend();
        }

        // See if it maps to a profile
        else _processKey(key, directions);
    }

    /**
     *
     * Attempts to match a key to a player.
     *
     * @param raw A lanterna key object. "raw" because it is used to construct
     *            a superior InputKey object (e.g. better equals support).
     *
     *
     * @param directions The map from ids to directions.
     */
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

    private StopWatch _timer;

    private Screen _screen;

    private Map<InputKey, Profile> _keyMap;
}