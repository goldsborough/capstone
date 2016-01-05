package capstone.ui;

import capstone.data.Profile;
import capstone.data.Theme;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LevelWindow extends Widget
{
    public LevelWindow(List<Profile> profiles)
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

        add(new Button("New Game", this::_getLevel));

        addSpace(0, 2);

        add(new Button("Resume Game", this::_getSession));
    }

    private void _getLevel()
    {
        File file = _openFileDialog("resources/layouts", "Layout");

        if (file == null) return;

        Theme theme = _getTheme();

        if (theme == null) return;

        try
        {
            _level = new Level(file, theme, _profiles, getOwner());

            this.close();
        }

        catch(IOException e) { _showIOErrorBox(); }
    }

    private Theme _getTheme()
    {
        ThemeWindow window = new ThemeWindow();

        getOwner().showWindow(window, GUIScreen.Position.CENTER);

        return window.theme();
    }


    private void _getSession()
    {
        File file = _openFileDialog("resources/sessions", "Session");

        if (file == null) return;

        try
        {
            _level = new Level(file, _profiles, getOwner());

            this.close();
        }

        catch(IOException e) { _showIOErrorBox(); }
    }

    private List<Profile> _profiles;

    private Level _level;
}
