package capstone.ui;

import capstone.data.Profile;
import capstone.data.Theme;
import capstone.game.Game;
import capstone.game.Level;
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.dialog.MessageBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The window to display the in-game menu. Contains actions to
 *
 * 1. Continue the game.
 * 2. Show the legend.
 * 3. Change the theme of the level.
 * 4. Show the highscore-window.
 * 5. Load a new game.
 * 6. Save the game.
 * 7. Saving and exiting the game.
 * 8. Saving and going back to the welcome window (the "start").
 * 9. Going back to the start without saving.
 * 10. Exiting the whole game (std::exit).
 *
 */
public class MenuWindow extends Widget
{
    /**
     *
     * Constructs a MenuWindow for a Game.
     *
     * @param game The game to operate on.
     *
     */
    public MenuWindow(Game game)
    {
        super("Menu", Panel.Orientation.VERTICAL);

        this.game(game);

        add(new Button("Continue", this::_continue));

        add(new Button("Show Legend", this::_showLegend));

        add(new Button("Highscores", this::_showHighscores));

        add(new Button("Change Theme", this::_changeTheme));

        add(new Button("Load Game", this::_loadGame));

        add(new Button("Save", this::_saveLevel));

        add(new Button("Save and Exit", this::_saveAndExit));

        add(new Button("Save and Back to Start", this::_saveAndBackToStart));

        add(new Button("Back to Start", this::_backToStart));

        add(new Button("Exit", () -> System.exit(0)));
    }

    /**
     * Shows the whole menu on the game's screen.
     */
    public void show()
    {
        _game.gui().showWindow(this, GUIScreen.Position.CENTER);

        _game.level().redraw();
    }

    /**
     * Shows only the legend on the game's screen.
     */
    public void showLegend()
    {
        _game.gui().showWindow(
                new LegendWidget(_game.level().theme()),
                GUIScreen.Position.CENTER
        );

        _game.level().redraw();
    }

    /**
     * @return The game held by the MenuWindow.
     */
    public Game game()
    {
        return _game;
    }

    /**
     *
     * Sets the game the MenuWindow operates on.
     *
     * @param game The new game object.
     */
    public void game(Game game)
    {
        assert(game != null);

        _game = game;
    }

    /**
     *
     * Adds components on the left instead of entirely-centered
     * (as does the default Widget method) by default.
     *
     * @param component The component to add.
     */
    @Override public void add(Component component)
    {
        super.add(component, Alignment.LEFT_CENTER);
    }

    /**
     * Performs the operation for the "Continue" button.
     *
     * Just closes the Menu.
     */
    private void _continue()
    {
        super.close();

        _game.level().redraw();
    }

    /**
     * Performs the operation for the "Show Legend" button.
     *
     * Shows the LegendWidget.
     */
    private void _showLegend()
    {
        LegendWidget legend = new LegendWidget(_game.level().theme());

        _game.gui().showWindow(legend, GUIScreen.Position.CENTER);
    }

    /**
     * Performs the operation for the "Save Level" button.
     *
     * Saves the level and returns to the menu. Also shows a small
     * MessageBox to indicate successful saving.
     */
    private void _saveLevel()
    {
        try
        {
            _game.save();

            MessageBox.showMessageBox(
                    _game.gui(),
                    "",
                    "\nSaved!"
            );
        }

        catch (IOException e)
        {
            Widget.showIOErrorBox(_game.gui());
        }
    }

    /**
     * Performs the operation for the "Show Highscores" button.
     *
     * Opens a new HighscoreWindow.
     */
    private void _showHighscores()
    {
        HighscoreWindow window = new HighscoreWindow();

        _game.gui().showWindow(window, GUIScreen.Position.CENTER);
    }

    /**
     * Performs the operation for the "Change Theme" button.
     *
     * Opens a new ThemeWindow.
     */
    private void _changeTheme()
    {
        ThemeWindow window = new ThemeWindow();

        _game.gui().showWindow(window, GUIScreen.Position.CENTER);

        Theme theme = window.theme();

        if (theme != null) _game.level().theme(theme);
    }

    /**
     * Performs the operation for the "Load Game" button.
     *
     * Opens a new LevelWindow.
     */
    private void _loadGame()
    {
        List<Profile> profiles = new ArrayList<>(_game.profiles());

        LevelWindow window = new LevelWindow(profiles);

        _game.gui().showWindow(window, GUIScreen.Position.CENTER);

        Level level = window.level();

        if (level != null)
        {
            _game.level(level);

            super.close();
        }
    }

    /**
     * Performs the operation for the "(Save and) Back To Start" button.
     *
     * Closes the window and calls the Game class' backToStart method,
     * which shows the WelcomeWindow.
     */
    private void _backToStart()
    {
        super.close();

        _game.backToStart();
    }

    /**
     * Performs the operation for the "Save And Back To Start" button.
     *
     * Combines the "Save" and "Back To Start" buttons.
     */
    private void _saveAndBackToStart()
    {
        _saveLevel();

        _backToStart();
    }

    /**
     * Performs the operation for the "Exit" button.
     *
     * Combines the "Save" and "Exit" buttons.
     */
    private void _saveAndExit()
    {
        _saveLevel();

        System.exit(0);
    }

    private Game _game;
}
