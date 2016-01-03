package capstone.ui;

import capstone.data.Theme;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.dialog.FileDialog;

import java.io.File;
import java.io.IOException;

/**
 * Created by petergoldsborough on 12/30/15.
 */
public class ThemeWindow extends Widget
{
    public ThemeWindow()
    {
        super("Theme Selection");

        addSpace(0, 2);

        _createNewThemeButton();

        addSpace(0, 2);

        _createExistingThemeButton();

        addSpace(0, 2);

        _createBottomButtons(true);
    }

    public Theme theme()
    {
        return _theme;
    }

    private void _createNewThemeButton()
    {
        Button button = new Button("New Theme", this::_getNewTheme);

        add(button);
    }

    private void _getNewTheme()
    {
        ThemeCreationWindow creation = new ThemeCreationWindow();

        getOwner().showWindow(creation, GUIScreen.Position.CENTER);

        _theme = creation.theme();

        this.close();
    }

    private void _createExistingThemeButton()
    {
        Button button = new Button("Existing Theme", this::_getExistingTheme);

        add(button);
    }

    private void _getExistingTheme()
    {
        File file = FileDialog.showOpenFileDialog(
                super.getOwner(),
                new File("resources/themes"),
                "Choose Theme"
        );

        try
        {
            _theme = new Theme(file);
        }

        catch (IOException e) { _showIOErrorBox(); }

        this.close();
    }

    private Theme _theme;
}
