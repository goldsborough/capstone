/**
 * Created by petergoldsborough on 12/26/15.
 */

package capstone;

import capstone.data.Highscore;
import capstone.data.Profile;
import capstone.gui.Level;
import capstone.gui.LevelWindow;
import capstone.gui.ProfileWindow;
import capstone.gui.WelcomeWindow;
import capstone.utility.StopWatch;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.TerminalAppearance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
    }

    public void play()
    {
        start();

        loop();

        stop();
    }

    public void start()
    {
        assert(_screen != null);

        Window window = new WelcomeWindow();

        _gui.showWindow(window, GUIScreen.Position.CENTER);

        _getProfiles();

        _getLevel();
    }

    public void loop()
    {
        _watch.start();



        _watch.stop();
    }

    public void stop()
    {
        _storeHighscore();

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
                highscore.putProfiles(_watch.seconds(), _profiles);
            }

            catch(IOException e)
            {
                System.out.println("I/O Error reading highscore.");
            }
        }

        else
        {
            HashMap<Double, ArrayList<Profile>> map = new HashMap<>();

            map.put(_watch.seconds(), _profiles);

            highscore = new Highscore(_level.name(), map);
        }

        return highscore;
    }

    private long _framePeriod;

    private StopWatch _watch;

    private Level _level;

    private final GUIScreen _gui;

    private final Screen _screen;

    private Terminal _terminal;

    private ArrayList<Profile> _profiles;
}