package capstone.gui;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by petergoldsborough on 12/30/15.
 */
public class ComboBox<T> extends Widget
{
    public ComboBox()
    {
        this("...");
    }

    public ComboBox(String display)
    {
        assert(display != null);

        _button = _createButton(display);

        _listWidget = new ListWidget<>();
    }

    @SafeVarargs
    public ComboBox(T... options)
    {
        this(new ArrayList<>(Arrays.asList(options)));
    }

    public ComboBox(ArrayList<T> options)
    {
        this("...", options.subList(1, options.size() - 1));
    }

    public ComboBox(String display, Collection<T> options)
    {
        _button = _createButton(display);

        this.listWidget(new ListWidget<>());

        options.forEach(this::add);
    }

    public ComboBox(ListWidget<T> listWidget)
    {
        this("...", listWidget);
    }

    public ComboBox(String display, ListWidget<T> listWidget)
    {
        _button = _createButton(display);

        this.listWidget(listWidget);
    }


    public void add(T option)
    {
        _listWidget.add(option, () -> _button.setText(option.toString()));
    }

    public int size()
    {
        return _listWidget.size();
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

    public boolean isNull()
    {
        return item() == null;
    }

    public T item()
    {
        return _listWidget.item();
    }

    public String text()
    {
        return _listWidget.text();
    }

    public void text(String text)
    {
        assert(text != null);

        _button.setText(text);
    }

    public Button button()
    {
        return _button;
    }

    public ListWidget listWidget()
    {
        return _listWidget;
    }

    public void listWidget(ListWidget<T> listWidget)
    {
        assert(listWidget != null);

        _listWidget = listWidget;

        _listWidget.close();
    }

    private Button _createButton(String title)
    {
        Button button = new Button(title, this::_showList);

        super.add(button);

        return button;
    }

    private void _showList()
    {
        _button.getParent().getWindow().getOwner().showWindow(
                _listWidget,
                GUIScreen.Position.CENTER
        );
    }

    private Button _button;

    private ListWidget<T> _listWidget;
}
