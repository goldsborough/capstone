package capstone.ui;

import capstone.data.Representation;
import capstone.data.Theme;
import capstone.element.Element;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.dialog.MessageBox;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A Widget to manage creation of a new Theme.
 *
 * This "Theme Creation" Window shows an InputWidget for a name along
 * with buttons to open RepresentationWidgets for each of the Kinds,
 * such that every Kind gets a Representation in the Theme.
 */
public class ThemeCreationWidget extends Widget
{
    /**
     * Constructs a new ThemeCreationWidget.
     */
    public ThemeCreationWidget()
    {
        super("Theme Creation");

        _representations = new HashMap<>();

        _createNameEntry();

        Element.Kind.kinds().forEach(this::_addEntry);

        // CANCEL and EXIT
        _createBottomButtons();
    }

    /**
     *
     * First validates the entries for the various Kinds. If the input
     * is valid for all RepresentationWidgets and the InputWidget for
     * the Theme's name, a valid Theme is returned, else null.
     *
     * @return Returns a valid Theme, if possible.
     */
    public Theme theme()
    {
        if (! validate()) return null;

        return _makeTheme();
    }

    /**
     *
     * Validates the NameWidget and the
     * RepresentationWidgets for all the Kinds.
     *
     * @return True if the Theme is valid, else false.
     */
    public boolean validate()
    {
        if (! _name.validate()) return false;

        // Validate the RepresentationWidgets
        for (RepresentationWidget widget : _representations.values())
        {
            if (! widget.isValid())
            {
                _showMessageBox(widget);

                return false;
            }
        }

        return true;
    }

    /**
     *
     * Shows a message box that some input was invalid in the given Widget.
     *
     * @param widget The RepresentationWidget that was invalid.
     */
    private void _showMessageBox(RepresentationWidget widget)
    {
        String message = String.format(
                "\n%s: The input for '%s' was not valid!",
                widget.title(),
                widget.culprit() // Tells you what was invalid
        );

        MessageBox.showMessageBox(
                getOwner(),
                "Invalid Input",
                message
        );
    }

    /**
     * Creates the InputWidget for the name of the Theme.
     */
    private void _createNameEntry()
    {
        addSpace(0, 2);

        _name = new InputWidget("Name", "[\\s\\w]+");

        add(_name);

        addSpace(0, 2);
    }

    /**
     *
     * Adds a new button to the ThemeCreationWidget, which,
     * when clicked, opens a RepresentationWidget for the Kind.
     *
     * @param kind The Kind of Element to add an Entry for.
     */
    private void _addEntry(Element.Kind kind)
    {
        RepresentationWidget widget = _createRepresentationWidget(kind);

        Button button = new Button(kind.toLowerString(), () -> {
            getOwner().showWindow(widget, GUIScreen.Position.CENTER);
        });

        add(button, Alignment.LEFT_CENTER);

        addSpace(0, 2);

        _representations.put(kind, widget);
    }

    /**
     *
     * Creates anew RepresentationWidget for a given Kind.
     *
     * @param kind The Kind to create the RepresentationWidget for.
     *
     * @return The resulting RepresentationWidget.
     */
    RepresentationWidget _createRepresentationWidget(Element.Kind kind)
    {
        RepresentationWidget widget =
                new RepresentationWidget(kind.toLowerString());

        widget.addSpace(0, 2);

        widget.add(new ButtonSlot(
                false,
                ButtonSlot.Kind.DONE,
                ButtonSlot.Kind.CANCEL)
        );

        return widget;
    }

    /**
     * @return
     */
    private Theme _makeTheme()
    {
        HashMap<Element.Kind, Representation> map = new HashMap<>();

        // Collect the Representations of the Kinds
        for (Element.Kind kind : _representations.keySet())
        {
            Representation representation =
                    _representations.get(kind).representation();

            assert(representation != null);
            assert(representation.character()  != null);
            assert(representation.background() != null);
            assert(representation.foreground() != null);

            map.put(kind, representation);
        }

        Theme theme = new Theme(_name.text(), map);

        try
        {
            theme.store(new File("resources/themes"));

        } catch (IOException e) { _showIOErrorBox(); }

        return theme;
    }

    /**
     * Adds the buttons at the bottom of the Widget.
     *
     * Those buttons are DONE, CANCEL and EXIT.
     *
     * DONE only closes the window if the input is validated.
     */
    private void _createBottomButtons()
    {
        add(new ButtonSlot(
                false,
                new Button("Done", () -> { if (validate()) super.close(); }),
                new Button("Cancel", super::close),
                new Button("Exit", () -> System.exit(0))
        ));
    }

    private InputWidget _name;

    private Map<Element.Kind, RepresentationWidget> _representations;
}
