package capstone.ui;

import capstone.data.Representation;
import capstone.data.Theme;
import capstone.element.Element;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;

import java.util.Map;

/**
 * A Widget to display the Legend, sourced from a Theme object.
 */
public class LegendWidget extends Widget
{
    /**
     *
     * Constructs a new LegendWidget from a Theme.
     *
     * @param theme The Theme containing the Kind-Representation
     *              mapping this Legend should visualize.
     */
    public LegendWidget(Theme theme)
    {
        super("Legend", Panel.Orientation.VERTICAL);

        assert(theme != null);

        addSpace(0, 1);

        // Add all the entries. The order is random for better or for worse.
        for (Map.Entry<Element.Kind, Representation> entry : theme)
        {
            _addSlot(entry.getKey(), entry.getValue());
        }

        addSpace(0, 1);

        add(new ButtonSlot(false, ButtonSlot.Kind.DONE));
    }

    /**
     *
     * Adds a new slot to the Legend, i.e. an entry
     * consisting of a Kind a Representation.
     *
     * @param kind The Kind for the entry.
     *
     * @param representation The Representation for the entry.
     */
    private void _addSlot(Element.Kind kind, Representation representation)
    {
        Panel slot = _newSlot();

        String label = String.format("%s:", kind.toLowerString());

        add(slot, new Label(label), Alignment.LEFT_CENTER);

        Label character = new Label(
            Character.toString(representation.character()),
            representation.foreground()
        );

        add(slot, character, Alignment.RIGHT_CENTER);

        add(slot);
    }
}
