package capstone.ui;

import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel;

/**
 * A Widget containing a Panel to which Buttons or
 * optionally other Components or Widgets can be added.
 */
public class ButtonSlot extends Widget
{
    /**
     * Default Buttons.
     */
    public enum Kind
    {
        START,
        OK,
        DONE,
        CANCEL,
        EXIT
    }

    /**
     * Constructs an empty vertical ButtonSlot.
     */
    public ButtonSlot()
    {
        this(true);
    }

    /**
     *
     * Constructs a vertical ButtonSlot and adds the specified buttons.
     *
     * @param buttons An array of buttons to add to the ButtonSlot.
     */
    public ButtonSlot(Button... buttons)
    {
        this(true, buttons);
    }

    /**
     *
     * Constructs an empty ButtonSlot with the specified orientation.
     *
     * @param vertical Whether to have the ButtonSlot have a vertical
     *                 Panel layout as opposed to a horizontal one.
     */
    public ButtonSlot(boolean vertical)
    {
        super(
                "",
                vertical
                ? Panel.Orientation.VERTICAL
                : Panel.Orientation.HORISONTAL
        );

        _vertical = vertical;

        _alignment = _determineAlignment();
    }

    /**
     *
     * Creates a ButtonSlot with the specified
     * orientation and adds the specified Buttons.
     *
     * @param vertical Whether to have the ButtonSlot have a vertical
     *                 Panel layout as opposed to a horizontal one.
     *
     * @param buttons An array of buttons to add to the ButtonSlot.
     */
    public ButtonSlot(boolean vertical, Button... buttons)
    {
        super("", vertical ? Panel.Orientation.VERTICAL : Panel.Orientation.HORISONTAL);

        _vertical = vertical;

        _alignment = _determineAlignment();

        for (Button button : buttons) add(button);
    }

    /**
     *
     * Constructs a vertical ButtonSlot and adds
     * the specified kinds of buttons.
     *
     * @param buttons An array of buttons to add to the ButtonSlot.
     */
    public ButtonSlot(Kind... buttons)
    {
        this(true, buttons);
    }

    /**
     *
     * Creates a ButtonSlot with the specified
     * orientation and adds the specified kinds of Buttons.
     *
     * @param vertical Whether to have the ButtonSlot have a vertical
     *                 Panel layout as opposed to a horizontal one.
     *
     * @param buttons An array of buttons to add to the ButtonSlot.
     */
    public ButtonSlot(boolean vertical, Kind... buttons)
    {
        super("", vertical ? Panel.Orientation.VERTICAL : Panel.Orientation.HORISONTAL);

        _vertical = vertical;

        _alignment = _determineAlignment();

        for (Kind kind : buttons) add(kind);
    }

    /**
     *
     * Adds a component and also adds some spacing.
     *
     * Whether the spacing is horizontal or vertical
     * (i.e. below or to the right of the Widget)
     * depends on the orientation of the ButtonSlot.
     *
     * @param component The component to add.
     */
    @Override public void add(Component component)
    {
        add(component, _alignment);

        if (_vertical) addSpace(0, 2);

        else addSpace(1, 0);
    }

    /**
     *
     * Adds a widget and also adds some spacing.
     *
     * Whether the spacing is horizontal or vertical
     * (i.e. below or to the right of the Widget)
     * depends on the orientation of the ButtonSlot.
     *
     * @param widget The widget to add.
     */
    @Override public void add(Widget widget)
    {
        add(widget, _alignment);

        if (_vertical) addSpace(0, 2);

        else addSpace(1, 0);
    }

    /**
     *
     * Adds one of the kinds of default buttons
     * defined by the ButtonSlot.Kind enum
     *
     * @param kind The Kind of button to add.
     */
    public void add(Kind kind)
    {
        switch (kind)
        {
            case START:
                add(new Button("Start", this::_close));
                break;

            case OK:
                add(new Button("OK", this::_close));
                break;

            case DONE:
                add(new Button("Done", this::_close));
                break;

            case CANCEL:
                add(new Button("Cancel", this::_close));
                break;

            case EXIT:
                add(new Button("Exit", () -> System.exit(0)));
                break;
        }
    }

    /**
     * Closes the ButtonSlot.
     */
    private void _close()
    {
        getWindow().close();

        this.close();
    }

    /**
     *
     * Determines the alignment necessary given
     * the boolean specifying verticality.
     *
     * @return The proper alignment.
     */
    private Alignment _determineAlignment()
    {
        if (_vertical) return Alignment.CENTER;

        return Alignment.LEFT_CENTER;
    }

    private boolean _vertical;

    private Alignment _alignment;
}
