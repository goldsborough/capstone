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
 *
 * I have spent a large chunk of my life programming GUI applications with
 * PyQt in Python and Qt in C++ and in Qt, everything is a widget. A button
 * is a widget, but also a window containing a million buttons is a widget
 * and can be embedded in another widget. It makes no distinction between
 * what is a container/window and what is a component of such a container.
 * However, Lanterna is badly, badly designed and makes such a differentiation.
 * In Lanterna, there are windows holding components and there are components
 * in those windows. At the same time every window in Lanterna has a panel
 * and every panel is a component, so wait a minute ... isn't every window
 * then a component, because it has a panel that can in turn be embedded
 * in a window? Yes! But Lanterna people didn't spend a single thought on
 * the fact that people might want to create more components than the 10
 * they provide. They didn't think of the idea that if you need a label
 * and a button next to each other 100 times in your window, there exists
 * such a thing as object-oriented programming that's supposed to make it
 * easy for you to package those two items into a Widget and then embed that
 * widget wherever, rather than creating 100 labels and 100 buttons each god
 * damn time. So Lanterna provides the Window class. A window can be displayed
 * on the screen. It also has an *interface* for components. So we're good,
 * right? We just extend the Window and implement the Component interface
 * and we have our "Widget", right? NOOOOO because there's also an
 * AbstractComponent abstract class that you have to extend because only
 * those have a non-null parent. If you decompile their stupid jar you see
 * that they cast all components to AbstractComponents first. So basically
 * the component interface is pretty much useless. So the solution is to
 * subclass both Window and AbstratComponent, because every Widget is a Window
 * that can be displayed, but every Widget is also a Component because it has
 * a panel (the "hook") that we can hook on to, to embed the widget in another
 * Widget. But wait, this is Java. There's no multiple inheritance. Who ever
 * thought that was a good idea. Well it's a good idea because every class should
 * encapsulate only one concept and multiple inheritance often breaks that,
 * but Josh Bloch and his language designers didn't anticipate how horrible you
 * could make a library. So yeah. This class is pretty much ok, and it overcomes
 * the fact that a Widget can't 100% be a component (AbstractComponent business)
 * by adding overloads on the `add` methods that add the hook (the panel) rather
 * than the widget itself. So best only use classes derived from Widget for
 * everything. If you need to add a widget to a Window directly, add the hook()
 * which returns the panel.
 *
 */
public class Widget extends Window implements Component
{
    /**
     *
     * Utility message to show an I/O error box.
     *
     * @param owner The screen to display the message on.
     */
    public static void showIOErrorBox(GUIScreen owner)
    {
        MessageBox.showMessageBox(
                owner,
                "I/O Error",
                "Could not perform I/O Operation :("
        );
    }

    /**
     * Constructs a Widget with an empty title and vertical orientation.
     */
    public Widget()
    {
        this("", Panel.Orientation.VERTICAL);
    }

    /**
     *
     * Constructs a Widget with the given title and vertical orientation.
     *
     * @param title The title of the window.
     */
    public Widget(String title)
    {
        this(title, Panel.Orientation.VERTICAL);
    }

    /**
     *
     * Constructs a Widget with an empty title and the given orienation.
     *
     * @param orientation The orientation of the window (how components/widgets
     *                    are added to the panel)
     */
    public Widget(Panel.Orientation orientation)
    {
        this("", orientation);
    }

    /**
     *
     * Constructs a Widget with the given title and the given orienation.
     *
     * @param title The title of the window.
     *
     * @param orientation The orientation of the window (how components/widgets
     *                    are added to the panel)
     */

    public Widget(String title, Panel.Orientation orientation)
    {
        super(title);

        super.setDrawShadow(false);

        _panel = _createPanel(orientation);

        super.addComponent(_panel);
    }

    /**
     *
     * This right here is what makes every Window a Component and thus a Widget
     * both a Window and a Component.
     *
     * @return The panel of the Widget.
     */
    public Panel hook()
    {
        return _panel;
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public Container getParent()
    {
        return hook().getParent();
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public Window getWindow()
    {
        return hook().getWindow();
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public void addComponentListener(ComponentListener componentListener)
    {
        hook().addComponentListener(componentListener);;
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public void removeComponentListener(ComponentListener componentListener)
    {
        hook().removeComponentListener(componentListener);
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public void repaint(TextGraphics textGraphics)
    {
        hook().repaint(textGraphics);
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public void setVisible(boolean b)
    {
        hook().setVisible(b);
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public boolean isVisible()
    {
        return hook().isVisible();
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public boolean isScrollable()
    {
        return hook().isScrollable();
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public TerminalSize getPreferredSize()
    {
        return hook().getPreferredSize();
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public TerminalSize getMinimumSize()
    {
        return hook().getMinimumSize();
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public void setPreferredSize(TerminalSize terminalSize)
    {
        hook().setPreferredSize(terminalSize);
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public void setAlignment(Alignment alignment)
    {
        hook().setAlignment(alignment);
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public Alignment getAlignment()
    {
        return hook().getAlignment();
    }

    /**
     * Delegates the component call to the hook.
     */
    @Override public Component addBorder(Border border, String s)
    {
        return hook().addBorder(border, s);
    }


    /**
     *
     * Adds a Widget (its hook) to this Widget.
     *
     * @param widget The widget to add.
     *
     * @param alignment The alignment it should have in the panel.
     *
     */
    public void add(Widget widget,
                    Component.Alignment alignment)
    {
        add(_panel, widget, alignment);
    }

    /**
     *
     * Adds a Widget (its hook) to this Widget.
     *
     * @param widget The widget to add.
     *
     */
    public void add(Widget widget)
    {
        add(widget.hook(), Component.Alignment.CENTER);
    }

    /**
     *
     * Adds a component to the widget.
     *
     * @param component The component to add.
     *
     * @param alignment The alignment it should have in the panel.
     *
     */
    public void add(Component component,
                    Component.Alignment alignment)
    {
        add(_panel, component, alignment);
    }

    /**
     *
     * Adds a component to the widget.
     *
     * @param component The component to add.
     *
     */
    public void add(Component component)
    {
        add(component, Component.Alignment.CENTER);
    }

    /**
     *
     * Adss space to the panel.
     *
     * @param width The columns of space.
     *
     * @param height The rows of space.
     *
     */
    public void addSpace(int width, int height)
    {
        addSpace(_panel, width, height);
    }

    /**
     *
     * Removes a component from the widget.
     *
     * @param component The component to remove.
     *
     */
    public void remove(Component component)
    {
        assert(component != null);

        super.removeComponent(component);
    }

    /**
     *
     * Removes a panel from the widget and all components it contains.
     *
     * @param panel The panel to remove.
     *
     */
    public void remove(Panel panel)
    {
        assert(panel != null);

        panel.removeAllComponents();

        super.removeComponent(panel);
    }

    /**
     *
     * Removes a widget from the widget.
     *
     * @param widget The widget to remove.
     *
     */
    public void remove(Widget widget)
    {
        assert(widget != null);

        widget.hook().removeAllComponents();

        remove(widget.hook());
    }

    /**
     *
     * Adds the Widget with the given alignment to a Panel. Could be the
     * panel of the Widget or another Panel, e.g. a slot.
     *
     * Gotta love not having default arguments. Gotta love 2^N methods.
     *
     * @param panel The panel to add the button to.
     *
     * @param widget The widget to add.
     *
     * @param alignment The alignment of the widget.
     *
     */
    protected void add(Panel panel,
                       Widget widget,
                       Component.Alignment alignment)
    {
        add(panel, widget.hook(), alignment);
    }

    /**
     *
     * Adds the Widget with CENTER alignment to a Panel. Could be the
     * panel of the Widget or another Panel, e.g. a slot.
     *
     * @param panel The panel to add the button to.
     *
     * @param widget The widget to add.
     *
     */

    protected void add(Panel panel, Widget widget)
    {
        add(panel, widget.hook(), Alignment.CENTER);
    }

    /**
     *
     * Adds a Component with the given alignment to a Panel. Could be the
     * panel of the Widget or another Panel, e.g. a slot.
     *
     * @param panel The panel to add the button to.
     *
     * @param component The component to add.
     *
     * @param alignment The alignment of the widget.
     *
     */

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

    /**
     *
     * Adds a Component with CENTER alignment to a Panel. Could be the
     * panel of the Widget or another Panel, e.g. a slot.
     *
     * @param panel The panel to add the button to.
     *
     * @param component The component to add.
     *
     */

    protected void add(Panel panel, Component component)
    {
        add(panel, component, Alignment.CENTER);
    }

    /**
     *
     * Adds space to the panel with the specified width and height.
     *
     * @param panel The panel to add the space to.
     *
     * @param width How many columns of space to add.
     *
     * @param height How many rows of space to add.
     */
    protected void addSpace(Panel panel, int width, int height)
    {
        panel.addComponent(new EmptySpace(width, height));
    }


    /**
     *
     * Creates a new panel and sets its layout-manager.
     *
     * @param orientation The orientation of the panel (horizontal/vertical).
     *
     * @return The created Panel.
     *
     */
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

    /**
     *
     * Creates a new slot. A slot is a horizontal panel.
     *
     * @return The created slot.
     */

    protected Panel _newSlot()
    {
        Panel slot = new Panel(
                new Border.Invisible(),
                Panel.Orientation.HORISONTAL
        );

        slot.setLayoutManager(new HorisontalLayout());

        return slot;
    }

    /**
     *
     * Utility method to add the most common set
     * of buttons at the bottom of a widget.
     *
     * @param vertical Whether to make the button slot vertical.
     */
    protected void _createBottomButtons(boolean vertical)
    {
        add(new ButtonSlot(
                vertical,
                ButtonSlot.Kind.CANCEL,
                ButtonSlot.Kind.EXIT
        ));
    }

    /**
     *
     * Opens a file dialog for something.
     *
     * @param path The directory path to open.
     *
     * @param what What you're selecting. The title is "Choose" + what.
     *
     * @return A selected file, if any. Null if the operation was cancelled.
     */
    protected File _openFileDialog(String path, String what)
    {
        return FileDialog.showOpenFileDialog(
                super.getOwner(),
                new File(path),
                "Choose " + what
        );
    }

    /**
     *
     * Utiltity method to show an I/O error box with
     * for the Widget's owner screen.
     *
     */
    protected void _showIOErrorBox()
    {
        Widget.showIOErrorBox(getOwner());
    }


    /**
     * The hook!
     */
    protected Panel _panel;
}