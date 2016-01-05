package capstone.ui;

import capstone.data.Highscore;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;

/**
 * Created by petergoldsborough on 12/31/15.
 */
public class HighscoreWidget extends Widget
{
    public HighscoreWidget(Highscore highscore)
    {
        super(highscore.level());

        addSpace(0, 1);

        int place = 1;

        for (Highscore.Entry entry : highscore)
        {
            _addEntry(place++, entry);
        }

        addSpace(0, 1);

        add(new ButtonSlot(ButtonSlot.Kind.DONE));
    }

    private void _addEntry(int place, Highscore.Entry entry)
    {
        Panel slot = super._newSlot();

        add(slot, new Label(place + ":"));

        add(slot, new Label(Double.toString(entry.time())));

        String players = entry.players().toString();

        // Without the square brackets of list representation
        add(slot, new Label(players.substring(1, players.length() - 1)));

        add(slot);
    }
}
