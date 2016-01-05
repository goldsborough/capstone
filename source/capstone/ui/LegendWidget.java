package capstone.ui;

import capstone.data.Representation;
import capstone.data.Theme;
import capstone.element.Element;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;

import java.util.Map;

/**
 * Created by petergoldsborough on 01/05/16.
 */
public class LegendWidget extends Widget
{
    public LegendWidget(Theme theme)
    {
        super("Legend", Panel.Orientation.VERTICAL);

        addSpace(0, 1);

        for (Map.Entry<Element.Kind, Representation> entry : theme)
        {
            _addSlot(entry.getKey(), entry.getValue());
        }

        addSpace(0, 1);

        add(new ButtonSlot(false, ButtonSlot.Kind.DONE));
    }

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
