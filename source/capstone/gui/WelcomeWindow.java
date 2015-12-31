package capstone.gui;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel;

/**
 * Created by petergoldsborough on 12/29/15.
 */
public class WelcomeWindow extends Widget
{
    public WelcomeWindow()
    {
        super("Welcome!", Panel.Orientation.HORISONTAL);

        addSpace(1, 3);

        add(new ButtonSlot(
                false,
                new Button("Start", super::close),
                new Button("Highscores", this::_showHighscores),
                new Button("Exit", () -> System.exit(0))
        ));

        addSpace(1, 3);
    }

    private void _showHighscores()
    {
        getOwner().showWindow(
                new HighscoreWindow(),
                GUIScreen.Position.CENTER
        );
    }
}
