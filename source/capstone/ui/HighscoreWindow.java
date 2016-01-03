package capstone.ui;

import capstone.data.Highscore;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;

import java.io.File;
import java.io.IOException;

/**
 * Created by petergoldsborough on 12/31/15.
 */
public class HighscoreWindow extends Widget
{
    public HighscoreWindow()
    {
        super("Highscores");

        addSpace(0, 2);

        add(new Button("Select Level", this::_selectLevel));

        addSpace(0, 2);

        add(new Button("Done", super::close));
    }

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
