package capstone.ui;

import capstone.data.Profile;
import capstone.data.Theme;
import capstone.game.Level;
import capstone.utility.LevelBuilder;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Window to select a level, either a new one or from an old session.
 */
public class LevelWindow extends Widget
{
    /**
     *
     * Constructs a LevelWindow instance. Needs the profiles because
     * the Level constructor needs them.
     *
     * @param profiles The profiles to construct new levels with.
     */
    public LevelWindow(List<Profile> profiles)
    {
        super("Level Selection");

        assert(profiles != null);

        _profiles = profiles;

        _createActionButtons();

        addSpace(0, 2);

        // Cancel and Exit buttons at bottom
        add(new ButtonSlot(ButtonSlot.Kind.CANCEL, ButtonSlot.Kind.EXIT));
    }

    /**
     *
     * May be null if no level was selected yet or the user cancelled.
     *
     * @return The retrieve level, if any.
     */
    public Level level()
    {
        return _level;
    }

    private void _createActionButtons()
    {
        addSpace(0, 2);

        // For creating a new session
        add(new Button("New Game", this::_getNewLevel));

        addSpace(0, 2);

        // For resuming an old session
        add(new Button("Resume Game", this::_getSession));
    }

    /**
     * Sets up a new Level (as opposed to a session) by opening
     * a file dialog. Then goes on to open the theme-selection window
     * and difficulty widget.
     */
    private void _getNewLevel()
    {
        // Get the file of the layout
        File file = _openFileDialog("resources/layouts", "Layout");

        // Cancelled?
        if (file == null) return;

        Level.Difficulty difficulty;

        Theme theme;

        // Loop between the theme and difficulty widgets
        // so you can cancel selecting a difficulty
        // (brings you back to choosing a theme) and cancel
        // selecting a theme (brings you back to the levelWindow)
        do
        {
            theme = _getTheme();

            if (theme == null) return;

            difficulty = _getDifficulty();
        }

        while (difficulty == null);

        try
        {
            _level = new Level(new LevelBuilder(
                    difficulty,
                    file,
                    theme,
                    _profiles,
                    getOwner()
            ));

            this.close();
        }

        catch(IOException e) { _showIOErrorBox(); }
    }

    /**
     *
     * Opens a ThemeWindow for selecting a theme.
     *
     * @return The selected theme, if any. Null if cancelled.
     */
    private Theme _getTheme()
    {
        ThemeWindow window = new ThemeWindow();

        getOwner().showWindow(window, GUIScreen.Position.CENTER);

        return window.theme();
    }

    /**
     *
     * Opens a DifficultyWidget for selecting a difficulty.
     *
     * @return The selected difficulty, if any. Null if cancelled.
     */
    private Level.Difficulty _getDifficulty()
    {
        DifficultyWidget widget = new DifficultyWidget();

        getOwner().showWindow(widget, GUIScreen.Position.CENTER);

        return widget.difficulty();
    }

    /**
     * Opens the a file dialog for the sessions and tries to select one.
     */
    private void _getSession()
    {
        File file = _openFileDialog("resources/sessions", "Session");

        if (file == null) return;

        try
        {
            _level = new Level(new LevelBuilder(
                    file,
                    _profiles,
                    getOwner()
            ));

            this.close();
        }

        catch(IOException e) { _showIOErrorBox(); }
    }

    private List<Profile> _profiles;

    private Level _level;
}
