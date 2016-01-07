package capstone.ui;

import capstone.data.Theme;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.dialog.FileDialog;

import java.io.File;
import java.io.IOException;

/**
 * The Window managing Themes. This Window is responsible for showing the
 * ThemeCreationWidget if the user wishes to create a new Theme, or else
 * for opening a FileDialog. It is expected to yield a Theme.
 */
public class ThemeWindow extends Widget
{
    /**
     * Constructs a new ThemeWindow.
     *
     * A ThemeWindow has an "Existing Theme" Button, a "New Theme" Button
     * and some utility buttons at the bottom (CANCEL and EXIT).
     */
    public ThemeWindow()
    {
        super("Theme Selection");

        addSpace(0, 2);

        add(new Button("Existing Theme", this::_getExistingTheme));

        addSpace(0, 2);

        add(new Button("New Theme", this::_getNewTheme));

        addSpace(0, 2);

        _createBottomButtons(true);
    }

    /**
     * @return The Theme collected, if any, else null.
     */
    public Theme theme()
    {
        return _theme;
    }

    /**
     * Handles creating a new Theme by opening the ThemeCreationWidget.
     */
    private void _getNewTheme()
    {
        ThemeCreationWidget creation = new ThemeCreationWidget();

        getOwner().showWindow(creation, GUIScreen.Position.CENTER);

        // May be null, of course.
        _theme = creation.theme();

        this.close();
    }

    /**
     * Handles getting an existing Theme from the resources/themes folder.
     */
    private void _getExistingTheme()
    {
        File file = FileDialog.showOpenFileDialog(
                super.getOwner(),
                new File("resources/themes"),
                "Choose Theme"
        );

        // If the user cancelled, just return to the window.
        if (file == null) return;

        try
        {
            _theme = new Theme(file);
        }

        catch (IOException e) { _showIOErrorBox(); }

        this.close();
    }

    private Theme _theme;
}
