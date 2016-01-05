package capstone.ui;

import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.Container;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.dialog.FileDialog;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.layout.HorisontalLayout;
import com.googlecode.lanterna.gui.layout.VerticalLayout;
import com.googlecode.lanterna.gui.listener.ComponentListener;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.File;

/**
 * Created by petergoldsborough on 12/30/15.
 */
public class Widget extends Window implements Component
{
    public static void showIOErrorBox(GUIScreen owner)
    {
        MessageBox.showMessageBox(
                owner,
                "I/O Error",
                "Could not perform I/O Operation :("
        );
    }

    public Widget()
    {
        this("", Panel.Orientation.VERTICAL);
    }

    public Widget(String title)
    {
        this(title, Panel.Orientation.VERTICAL);
    }

    public Widget(Panel.Orientation orientation)
    {
        this("", orientation);
    }

    public Widget(String title, Panel.Orientation orientation)
    {
        super(title);

        super.setDrawShadow(false);

        _panel = _createPanel(orientation);

        super.addComponent(_panel);
    }

    // What to add to as component
    public Panel hook()
    {
        return _panel;
    }

    @Override public Container getParent()
    {
        return hook().getParent();
    }

    @Override public Window getWindow()
    {
        return hook().getWindow();
    }

    @Override public void addComponentListener(ComponentListener componentListener)
    {
        hook().addComponentListener(componentListener);;
    }

    @Override public void removeComponentListener(ComponentListener componentListener)
    {
        hook().removeComponentListener(componentListener);
    }

    @Override public void repaint(TextGraphics textGraphics)
    {
        hook().repaint(textGraphics);
    }

    @Override public void setVisible(boolean b)
    {
        hook().setVisible(b);
    }

    @Override public boolean isVisible()
    {
        return hook().isVisible();
    }

    @Override public boolean isScrollable()
    {
        return hook().isScrollable();
    }

    @Override public TerminalSize getPreferredSize()
    {
        return hook().getPreferredSize();
    }

    @Override public TerminalSize getMinimumSize()
    {
        return hook().getMinimumSize();
    }

    @Override public void setPreferredSize(TerminalSize terminalSize)
    {
        hook().setPreferredSize(terminalSize);
    }

    @Override public void setAlignment(Alignment alignment)
    {
        hook().setAlignment(alignment);
    }

    @Override public Alignment getAlignment()
    {
        return hook().getAlignment();
    }

    @Override public Component addBorder(Border border, String s)
    {
        return hook().addBorder(border, s);
    }

    public void add(Widget widget,
                    Component.Alignment alignment)
    {
        add(_panel, widget, alignment);
    }

    public void add(Widget widget)
    {
        add(widget.hook(), Component.Alignment.CENTER);
    }

    public void add(Component component,
                    Component.Alignment alignment)
    {
        add(_panel, component, alignment);
    }


    public void add(Component component)
    {
        add(component, Component.Alignment.CENTER);
    }


    public void addSpace(int width, int height)
    {
        addSpace(_panel, width, height);
    }

    protected void add(Panel panel,
                       Widget widget,
                       Component.Alignment alignment)
    {
        add(panel, widget.hook(), alignment);
    }

    protected void add(Panel panel, Widget widget)
    {
        add(panel, widget.hook(), Alignment.CENTER);
    }

    protected void add(Panel panel,
                       Component component,
                       Component.Alignment alignment)
    {
        component.setAlignment(alignment);

        panel.addComponent(
                component,
                HorisontalLayout.GROWS_HORIZONTALLY,
                HorisontalLayout.GROWS_VERTICALLY
        );
    }

    protected void add(Panel panel, Component component)
    {
        add(panel, component, Alignment.CENTER);
    }

    protected void addSpace(Panel panel, int width, int height)
    {
        panel.addComponent(new EmptySpace(width, height));
    }


    protected Panel _createPanel(Panel.Orientation orientation)
    {
        Panel panel = new Panel(new Border.Invisible(), orientation);

        if (orientation == Panel.Orientation.HORISONTAL)
        {
            panel.setLayoutManager(new HorisontalLayout());
        }

        else panel.setLayoutManager(new VerticalLayout());

        return panel;
    }

    protected Panel _newSlot()
    {
        Panel slot = new Panel(
                new Border.Invisible(),
                Panel.Orientation.HORISONTAL
        );

        slot.setLayoutManager(new HorisontalLayout());

        return slot;
    }

    protected void _createBottomButtons(boolean vertical)
    {
        add(new ButtonSlot(
                vertical,
                ButtonSlot.Kind.CANCEL,
                ButtonSlot.Kind.EXIT
        ));
    }

    protected File _openFileDialog(String path, String what)
    {
        return FileDialog.showOpenFileDialog(
                super.getOwner(),
                new File(path),
                "Choose " + what
        );
    }

    protected void _showIOErrorBox()
    {
        Widget.showIOErrorBox(getOwner());
    }


    protected Panel _panel;
}