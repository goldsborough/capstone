package capstone.gui;

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

        int place = 1;

        for (Highscore.Entry entry : highscore)
        {
            _addEntry(place++, entry);
        }

        add(new ButtonSlot(
                false,
                ButtonSlot.Kind.DONE,
                ButtonSlot.Kind.EXIT
        ));
    }

    private void _addEntry(int place, Highscore.Entry entry)
    {
        Panel slot = super._newSlot();

        add(slot, new Label(place + ":"));

        add(slot, new Label(Double.toString(entry.time())));

        add(slot, new Label(entry.players().toString()));

        add(slot);
    }
}
