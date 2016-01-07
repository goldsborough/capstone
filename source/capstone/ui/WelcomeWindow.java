package capstone.ui;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel;

/**
 * The Window at startup of the whole game.
 *
 * Shows a button to start, to show a HighscoreWindow or to exit.
 */
public class WelcomeWindow extends Widget
{
    /**
     * Constructs a new WelcomeWindow with all its buttons.
     */
    public WelcomeWindow()
    {
        super("Welcome!", Panel.Orientation.HORISONTAL);

        addSpace(1, 3);

        add(new ButtonSlot(
                false, // not vertical
                new Button("Start", super::close),
                new Button("Highscores", this::_showHighscores),
                new Button("Exit", () -> System.exit(0))
        ));

        addSpace(1, 3);
    }

    /**
     * Shows a new HighscoreWindow.
     */
    private void _showHighscores()
    {
        getOwner().showWindow(
                new HighscoreWindow(),
                GUIScreen.Position.CENTER
        );
    }
}
