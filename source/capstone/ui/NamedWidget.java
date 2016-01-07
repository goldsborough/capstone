package capstone.ui;

import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;

/**
 * A Widget that is a slot with a Label and another Widget.
 * The Label is supposed to give the Widget a "name", therefore
 * this class is called NamedWidget. The Label is placed to the
 * left of the Widget.
 *
 * @param <W> The Widget to give a name.
 */
public class NamedWidget<W extends Widget> extends Widget
{

    /**
     *
     * Constructs a new NamedWidget with the given name and Widget.
     *
     * @param name The name to give the Widget, i.e. what is displayed
     *             in the label to the left of the Widget.
     *
     * @param widget The Widget to give a name and to put to the right
     *               of the Label.
     */
    public NamedWidget(String name, W widget)
    {
        super(name, Panel.Orientation.HORISONTAL);

        _label = _createLabel();

        this.name(name);
        this.widget(widget);
    }

    /**
     * @return The Widget that is given a name by this NamedWidget.
     */
    public W widget()
    {
        return _widget;
    }

    /**
     *
     * Sets the Widget that is given a name by
     * this NamedWidget to a new Widget.
     *
     * @param widget The new Widget that will be
     *               given a name by the NamedWidget.
     */
    public void widget(W widget)
    {
        assert(widget != null);

        if (_widget != null)
        {
            _panel.removeComponent(_widget.hook());
        }

        _widget = widget;

        super.add(_widget.hook(), Component.Alignment.RIGHT_CENTER);
    }

    /**
     *
     * The name is the content of the Label.
     *
     * @return The name of the NamedWidget.
     */
    public String name()
    {
        return _label.getText();
    }

    /**
     *
     * Sets the name for the NamedWidget.
     *
     * The name is the content of the Label.
     *
     * @param name The new name for the NamedWidget.
     */
    public void name(String name)
    {
        assert(name != null);
        assert(_label != null);

        _label.setText(name);
    }

    /**
     * @return A Label with a LEFT_CENTER aligment.
     */
    protected Label _createLabel()
    {
        Label label = new Label();

        add(label, Component.Alignment.LEFT_CENTER);

        return label;
    }

    protected Label _label;

    protected W _widget;
}
