package capstone.ui;

import capstone.data.Profile;
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
 * Created by petergoldsborough on 01/05/16.
 */
public class MenuWindow extends Widget
{
    public MenuWindow(Game game)
    {
        super("Menu", Panel.Orientation.VERTICAL);

        this.game(game);

        add(new Button("Continue", this::_continue));

        add(new Button("Show Legend", this::_showLegend));

        add(new Button("Highscores", this::_showHighscores));

        add(new Button("Load Game", this::_loadGame));

        add(new Button("Save", this::_saveLevel));

        add(new Button("Save and Exit", this::_saveAndExit));

        add(new Button("Save and Back to Start", this::_saveAndBackToStart));

        add(new Button("Back to Start", this::_backToStart));

        add(new Button("Exit", () -> System.exit(0)));
    }

    public void show()
    {
        _game.gui().showWindow(this, GUIScreen.Position.CENTER);

        _game.level().redraw();
    }

    public void showLegend()
    {
        _game.gui().showWindow(
                new LegendWidget(_game.level().theme()),
                GUIScreen.Position.CENTER
        );

        _game.level().redraw();
    }

    public Game game()
    {
        return _game;
    }

    public void game(Game game)
    {
        assert(game != null);

        _game = game;
    }

    @Override public void add(Component component)
    {
        super.add(component, Alignment.LEFT_CENTER);
    }

    private void _continue()
    {
        super.close();

        _game.level().redraw();
    }

    private void _showLegend()
    {
        LegendWidget legend = new LegendWidget(_game.level().theme());

        _game.gui().showWindow(legend, GUIScreen.Position.CENTER);
    }

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

    private void _showHighscores()
    {
        HighscoreWindow window = new HighscoreWindow();

        _game.gui().showWindow(window, GUIScreen.Position.CENTER);
    }

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

    private void _backToStart()
    {
        super.close();

        _game.backToStart();
    }

    private void _saveAndBackToStart()
    {
        _saveLevel();

        _backToStart();
    }

    private void _saveAndExit()
    {
        _saveLevel();

        System.exit(0);
    }

    private Game _game;
}
