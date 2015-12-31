package capstone.gui;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.component.ActionListBox;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by petergoldsborough on 12/30/15.
 */
public class ListWidget<T> extends Widget
{
    public ListWidget()
    {
        this(true);
    }

    public ListWidget(boolean showCancelButton)
    {
        super(Panel.Orientation.VERTICAL);

        _cancelShown = showCancelButton;

        this.list(new ActionListBox());
    }

    @SafeVarargs public ListWidget(Action action, T... options)
    {
        this(true, action, options);
    }

    @SafeVarargs public ListWidget(boolean showCancelButton, Action action, T... options)
    {
        this(action, new ArrayList<>(Arrays.asList(options)), showCancelButton);
    }

    public ListWidget(ArrayList<T> options, ArrayList<Action> actions)
    {
        this(options, actions, true);
    }

    public ListWidget(ArrayList<T> options, ArrayList<Action> actions, boolean showCancelButton)
    {
        super(Panel.Orientation.VERTICAL);

        assert(options != null);
        assert(actions != null);

        _cancelShown = showCancelButton;

        this.list(new ActionListBox());

        for (int i = 0; i < options.size(); ++i)
        {
            add(options.get(i), actions.get(i));
        }
    }

    public ListWidget(Action action, Collection<T> options)
    {
        this(action, options, true);
    }

    public ListWidget(Action action, Collection<T> options, boolean showCancelButton)
    {
        super(Panel.Orientation.VERTICAL);

        assert(options != null);

        _cancelShown = showCancelButton;

        this.list(new ActionListBox());

        options.forEach((T item) -> add(item, action));
    }


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

    public String text()
    {
        return _selected.toString();
    }

    public T item()
    {
        return _selected;
    }

    public void reset()
    {
        _selected = null;
    }

    public ActionListBox list()
    {
        return _list;
    }

    public void list(ActionListBox list)
    {
        assert(list != null);

        _panel.removeAllComponents();

        _list = list;

        super.add(list);

        if (_cancelShown) addCancelButton();
    }

    public int size()
    {
        return _list.getNrOfItems();
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

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
