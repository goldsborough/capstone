package capstone.ui;

import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;

/**
 * Created by petergoldsborough on 12/30/15.
 */
public class NamedWidget<W extends Widget> extends Widget
{
    public NamedWidget(String title, W widget)
    {
        super(title, Panel.Orientation.HORISONTAL);

        _label = _createLabel();

        this.title(title);
        this.widget(widget);
    }

    public W widget()
    {
        return _widget;
    }

    public void widget(W widget)
    {
        if (_widget != null)
        {
            _panel.removeComponent(_widget.hook());
        }

        _widget = widget;

        super.add(_widget.hook(), Component.Alignment.RIGHT_CENTER);
    }

    public String title()
    {
        return _label.getText();
    }

    public void title(String title)
    {
        assert(title != null);
        assert(_label != null);

        _label.setText(title);
    }

    protected Label _createLabel()
    {
        Label label = new Label();

        add(label, Component.Alignment.LEFT_CENTER);

        return label;
    }

    protected Label _label;

    protected W _widget;
}
