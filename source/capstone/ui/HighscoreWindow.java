package capstone.ui;

import capstone.data.Highscore;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;

import java.io.File;
import java.io.IOException;

/**
 * A Window for selecting a level and showing a HighscoreWidget
 * for that level (the HighscoreWidget shows the entries of the
 * Highscore object associated with the file).
 */
public class HighscoreWindow extends Widget
{
    /**
     * Constructs a new HighscoreWindow with a button to select
     * the level or be "Done".
     */
    public HighscoreWindow()
    {
        super("Highscores");

        addSpace(0, 2);

        add(new Button("Select Level", this::_selectLevel));

        addSpace(0, 2);

        add(new ButtonSlot(ButtonSlot.Kind.DONE));
    }

    /**
     * Selects a highscore by loading the file and opening
     * a HighscoreWidget to display the entries in the Highscore.
     * MVC 4 life :)))
     */
    private void _selectLevel()
    {
        File file = _openFileDialog("resources/highscores", "Level");

        if (file == null) return;

        try
        {
            Highscore highscore = new Highscore(file);

            HighscoreWidget widget = new HighscoreWidget(highscore);

            getOwner().showWindow(widget, GUIScreen.Position.CENTER);
        }

        catch (IOException e) { _showIOErrorBox(); }
    }
}
