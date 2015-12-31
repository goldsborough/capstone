package capstone.gui;

import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel;

/**
 * Created by petergoldsborough on 12/30/15.
 */
public class ButtonSlot extends Widget
{
    public enum Kind
    {
        START,
        OK,
        DONE,
        CANCEL,
        EXIT
    }

    public ButtonSlot()
    {
        this(true);
    }

    public ButtonSlot(Button... buttons)
    {
        this(true, buttons);
    }

    public ButtonSlot(boolean vertical)
    {
        super("", vertical ? Panel.Orientation.VERTICAL : Panel.Orientation.HORISONTAL);

        _vertical = vertical;

        _alignment = _determineAlignment();
    }

    public ButtonSlot(boolean vertical, Button... buttons)
    {
        super("", vertical ? Panel.Orientation.VERTICAL : Panel.Orientation.HORISONTAL);

        _vertical = vertical;

        _alignment = _determineAlignment();

        for (Button button : buttons) add(button);
    }

    public ButtonSlot(Kind... buttons)
    {
        this(true, buttons);
    }

    public ButtonSlot(boolean vertical, Kind... buttons)
    {
        super("", vertical ? Panel.Orientation.VERTICAL : Panel.Orientation.HORISONTAL);

        _vertical = vertical;

        _alignment = _determineAlignment();

        for (Kind kind : buttons) add(kind);
    }

    @Override public void add(Component component)
    {
        add(component, _alignment);

        if (_vertical) addSpace(0, 2);

        else addSpace(1, 0);
    }

    public void add(Kind kind)
    {
        switch (kind)
        {
            case START: add(new Button("Start", () -> getWindow().close())); break;

            case OK: add(new Button("OK", () -> getWindow().close())); break;

            case DONE: add(new Button("Done", () -> getWindow().close())); break;

            case CANCEL: add(new Button("Cancel", () -> getWindow().close())); break;

            case EXIT: add(new Button("Exit", () -> System.exit(0))); break;
        }
    }

    private Alignment _determineAlignment()
    {
        if (_vertical) return Alignment.CENTER;

        return Alignment.LEFT_CENTER;
    }

    private boolean _vertical;

    private Alignment _alignment;
}
