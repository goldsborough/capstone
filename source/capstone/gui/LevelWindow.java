package capstone.gui;

import capstone.data.Profile;
import capstone.data.Theme;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.dialog.FileDialog;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class LevelWindow extends Widget
{
    public LevelWindow(Collection<Profile> profiles)
    {
        super("Level Selection");

        assert(profiles != null);

        _profiles = profiles;

        _createActionButtons();

        addSpace(0, 2);

        _createBottomButtons(true);
    }

    public Level level()
    {
        return _level;
    }

    private void _createActionButtons()
    {
        addSpace(0, 2);

        _createNewGameButton();

        addSpace(0, 2);

        _createResumeGameButton();
    }

    private void _createNewGameButton()
    {
        Button button = new Button("New Game", this::_getLevel);

        add(button);
    }

    private void _getLevel()
    {
        File file = _openFileDialog("resources/layouts", "Layout");

        if (file == null) return;

        Theme theme = _getTheme();

        if (theme == null) return;

        try
        {
            _level = new Level(file, theme, _profiles);
        }

        catch(IOException e) { _showIOErrorBox(); }

        this.close();
    }

    private Theme _getTheme()
    {
        ThemeWindow window = new ThemeWindow();

        getOwner().showWindow(window, GUIScreen.Position.CENTER);

        return window.theme();
    }

    private void _createResumeGameButton()
    {
        Button button = new Button("Resume Game", this::_getSession);

        add(button);
    }

    private void _getSession()
    {
        File file = _openFileDialog("resources/sessions", "Session");

        if (file == null) return;

        try
        {
            _level = new Level(file, _profiles);
        }

        catch(IOException e) { _showIOErrorBox(); }
    }

    private Collection<Profile> _profiles;

    private Level _level;
}
