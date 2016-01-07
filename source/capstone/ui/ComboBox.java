package capstone.ui;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A Widget containing a Button that opens a ListWidget.
 */
public class ComboBox<T> extends Widget
{
    /**
     * Constructs a new ComboBox and sets
     * the displayed initial string to "...".
     */
    public ComboBox()
    {
        this("...");
    }

    /**
     *
     * Constructs a new ComboBox and sets the
     * displayed initial string to the one passed.
     *
     * @param display The string shown before a selection is made.
     */
    public ComboBox(String display)
    {
        assert(display != null);

        _button = _createButton(display);

        _listWidget = new ListWidget<>();
    }

    /**
     *
     * Constructs a new ComboBox with the default "..."
     * string as the initially displayed one, and adds
     * all the options to the ListWidget shown.
     *
     * @param options An array of options to add to the ComboBox.
     */
    @SafeVarargs public ComboBox(T... options)
    {
        this(new ArrayList<>(Arrays.asList(options)));
    }

    /**
     *
     * Constructs a new ComboBox with the default "..."
     * string as the initially displayed one, and adds
     * all the options to the ListWidget shown.
     *
     * @param options A collection of options to add to the ComboBox.
     */
    public ComboBox(Collection<T> options)
    {
        this("...", options);
    }

    /**
     *
     * Constructs a new ComboBox with the string passed
     * as the initially displayed one, and adds
     * all the options to the ListWidget shown.
     *
     * @param display The string shown before a selection is made.
     *
     * @param options A collection of options to add to the ComboBox.
     */
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

    /**
     *
     * Constructs a new ComboBox with the string passed as the initially
     * displayed one, and sets the ListWidget passed as the internally
     * used ListWidget, displayed when the ComboBox's button is pressed.
     *
     * @param display The string shown before a selection is made.
     *
     * @param listWidget The ListWidget to be used internally.
     */
    public ComboBox(String display, ListWidget<T> listWidget)
    {
        _button = _createButton(display);

        this.listWidget(listWidget);
    }


    /**
     *
     * Adds an option to the ComboBox's ListWidget.
     *
     * @param option The option to add to the ListWidget.
     */
    public void add(T option)
    {
        _listWidget.add(option, () -> _button.setText(option.toString()));
    }

    /**
     * @return The number of elements in the ListWidget.
     */
    public int size()
    {
        return _listWidget.size();
    }

    /**
     * @return True if the ListWidget is empty, else false.
     */
    public boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * @return True if an item has been selected yet, else false.
     */
    public boolean hasSelection()
    {
        return item() != null;
    }

    /**
     * @return The item in selection, which may be
     *         null if none has been selected yet.
     *
     * @see ComboBox#text
     */
    public T item()
    {
        return _listWidget.item();
    }

    /**
     * @return The text of the ListWidget, i.e. a
     *         string representation of the item().
     *
     * @see ComboBox#item
     */
    public String text()
    {
        return _listWidget.text();
    }

    /**
     *
     * Sets the text of the ComboBox's button. This
     * text may be reset when a new item is selected.
     *
     * @param text The new text for the ComboBox's Button.
     */
    public void text(String text)
    {
        assert(text != null);

        _button.setText(text);
    }

    /**
     * @return The Button of the ComboBox.
     */
    public Button button()
    {
        return _button;
    }

    /**
     * @return The ListWidget of the ComboBox.
     */
    public ListWidget listWidget()
    {
        return _listWidget;
    }

    /**
     *
     * Sets the ListWidget of the ComboBox.
     *
     * @param listWidget The new ListWidget of the ComboBox.
     */
    public void listWidget(ListWidget<T> listWidget)
    {
        assert(listWidget != null);

        _listWidget = listWidget;

        _listWidget.close();
    }

    /**
     *
     * Creates a Button with the given title, that, when
     * pressed, shows the ListWidget, and returns that Button.
     *
     * @param title The title to give the Button.
     *
     * @return The created Button.
     */
    private Button _createButton(String title)
    {
        Button button = new Button(title, this::_showList);

        super.add(button);

        return button;
    }

    /**
     * Shows the ListWidget centered on the Screen.
     */
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
