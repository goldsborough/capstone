package capstone.ui;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.component.ActionListBox;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * A Widget to display a list of Items of generic type.
 *
 * Every list-item is associated with an action.
 */
public class ListWidget<T> extends Widget
{
    /**
     * Constructs the ListWidget with a CANCEL-button at the bottom.
     */
    public ListWidget()
    {
        this(true);
    }

    /**
     * Constructs the ListWidget with the given name
     * and a CANCEL-button at the bottom.
     *
     * @param title The name for the Widget (the Window).
     *
     */
    public ListWidget(String title)
    {
        this(title, true);
    }

    /**
     *
     * Constructs the ListWidget with the given name and
     * optionally a CANCEL-button at the bottom.
     *
     * @param title The name for the Widget (the Window).
     *
     * @param showCancelButton Whether to show a CANCEL-button at the bottom.
     */
    public ListWidget(String title, boolean showCancelButton)
    {
        super(title, Panel.Orientation.VERTICAL);

        _cancelShown = showCancelButton;

        this.list(new ActionListBox());
    }

    /**
     *
     * Constructs the ListWidget with an optional CANCEL-button at the bottom.
     *
     * @param showCancelButton Whether to show a CANCEL-button at the bottom.
     */
    public ListWidget(boolean showCancelButton)
    {
        super(Panel.Orientation.VERTICAL);

        _cancelShown = showCancelButton;

        this.list(new ActionListBox());
    }

    /**
     *
     * Constructs the ListWidget with the same Action for all options passed.
     *
     * No CANCEL-button is set initially. Add with the addCancelButton() method.
     *
     * @param action The one Action that should be performed for all options.
     *
     * @param options An array of elements the ListWidget
     *                should contain initially.
     *
     * @see ListWidget#addCancelButton
     */
    @SafeVarargs public ListWidget(Action action, T... options)
    {
        this(action, new ArrayList<>(Arrays.asList(options)));
    }

    /**
     *
     * Constructs the ListWidget with the given option -> action mapping.
     *
     * No CANCEL-button is set initially. Add with the addCancelButton() method.
     *
     * @param entries A mapping from options to actions.
     *
     * @see ListWidget#addCancelButton
     */
    public ListWidget(Map<T, Action> entries)
    {
        super(Panel.Orientation.VERTICAL);

        assert(entries != null);

        this.list(new ActionListBox());

        for (Map.Entry<T, Action> entry : entries.entrySet())
        {
            add(entry.getKey(), entry.getValue());
        }
    }


    /**
     *
     * Constructs the ListWidget with the same Action for all options passed.
     *
     * No CANCEL-button is set initially. Add with the addCancelButton() method.
     *
     * @param action The one Action that should be performed for all options.
     *
     * @param options A collection of elements the ListWidget
     *                should contain initially.
     *
     * @see ListWidget#addCancelButton
     */
    public ListWidget(Action action, Collection<T> options)
    {
        super(Panel.Orientation.VERTICAL);

        assert(options != null);

        this.list(new ActionListBox());

        options.forEach((T item) -> add(item, action));
    }

    /**
     *
     * Adds a new entry to the list.
     *
     * @param item The item to add.
     *
     * @param action The action to associate with that item.
     */
    public void add(T item, Action action)
    {
        assert(item != null);
        assert(action != null);
        assert(_list != null);

        _list.addAction(item.toString(), () -> {
            _selected = item;
            action.doAction();
            this.close();
        });

    }

    /**
     * @return The text representation of the current selection if there
     *         is a selection, else null.
     */
    public String text()
    {
        return _selected == null ? null : _selected.toString();
    }

    /**
     * @return The selected item. Null if no item has been selected yet.
     */
    public T item()
    {
        return _selected;
    }

    /**
     * Resets the selection.
     */
    public void reset()
    {
        _selected = null;
    }

    /**
     * @return The underlying ActionListBox.
     */
    public ActionListBox list()
    {
        return _list;
    }

    /**
     *
     * Sets the underlying ActionListBox.
     *
     * @param list The new ActionListBox for the ListWidget.
     */
    public void list(ActionListBox list)
    {
        assert(list != null);

        _panel.removeAllComponents();

        _list = list;

        super.addSpace(0, 1);

        super.add(list);

        super.addSpace(0, 1);

        if (_cancelShown) addCancelButton();
    }

    /**
     * @return The number of options in the list.
     */
    public int size()
    {
        return _list.getNrOfItems();
    }

    /**
     * @return True if there are no options in the list, else false.
     */
    public boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * Adds a CANCEL-button to the bottom of the list.
     */
    public void addCancelButton()
    {
        Button cancel = new Button("Cancel", super::close);

        super.add(cancel);

        _cancelShown = true;
    }

    protected ActionListBox _list;

    protected T _selected;

    protected boolean _cancelShown;
}
