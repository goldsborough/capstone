package capstone.ui;

import capstone.data.Highscore;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;

/**
 * A Widget to display the entries of the highscore for a level, i.e. the
 * view for the Highscore class in the MVC model.
 */
public class HighscoreWidget extends Widget
{
    /**
     *
     * Constructs a new HighscoreWidget from the given Highscore object.
     *
     * @param highscore The Highscore object to create this HighscoreWidget for.
     */
    public HighscoreWidget(Highscore highscore)
    {
        super(highscore.level());

        addSpace(0, 1);

        int ranking = 1;

        for (Highscore.Entry entry : highscore)
        {
            _addEntry(ranking++, entry);
        }

        addSpace(0, 1);

        add(new ButtonSlot(ButtonSlot.Kind.DONE));
    }

    /**
     *
     * Adds a new entry-view to the widget.
     *
     * @param ranking The ranking of this entry int he highscore.
     *
     * @param entry The entry to add a view for.
     */
    private void _addEntry(int ranking, Highscore.Entry entry)
    {
        // Create a new slot
        Panel slot = super._newSlot();

        // The format is ranking: time players
        // e.g. 1: 1.533s peter, borat

        add(slot, new Label(ranking + ":"));

        add(slot, new Label(String.format("%1$.3fs", entry.time())));

        String players = entry.players().toString();

        // Without the square brackets of the list representation
        add(slot, new Label(players.substring(1, players.length() - 1)));

        add(slot);
    }
}
