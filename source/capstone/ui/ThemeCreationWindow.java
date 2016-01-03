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
 * Created by petergoldsborough on 12/30/15.
 */
public class ThemeCreationWindow extends Widget
{
    public ThemeCreationWindow()
    {
        super("Theme Creation");

        _representations = new HashMap<>();

        _createNameEntry();

        _addEntry(Element.Kind.WALL);

        _addEntry(Element.Kind.ENTRANCE);

        _addEntry(Element.Kind.EXIT);

        _addEntry(Element.Kind.KEY);

        _addEntry(Element.Kind.STATIC_OBSTACLE);

        _addEntry(Element.Kind.DYNAMIC_OBSTACLE);

        _addEntry(Element.Kind.MYSTERY_BOX);

        _createBottomButtons();
    }

    public Theme theme()
    {
        if (! validate()) return null;

        return _makeTheme();
    }

    public boolean validate()
    {
        if (! _name.validate()) return false;

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

    private void _showMessageBox(RepresentationWidget widget)
    {
        String message = String.format(
                "\n%s: The input for '%s' was not valid!",
                widget.title(),
                widget.culprit()
        );

        MessageBox.showMessageBox(
                getOwner(),
                "Invalid Input",
                message
        );
    }

    private void _createNameEntry()
    {
        addSpace(0, 2);

        _name = new InputWidget("Name", "[\\s\\w]+");

        add(_name);

        addSpace(0, 2);
    }

    private RepresentationWidget _addEntry(Element.Kind kind)
    {
        RepresentationWidget widget = _createRepresentationWidget(kind);

        Button button = new Button(kind.toLowerString(), () -> {
            getOwner().showWindow(widget, GUIScreen.Position.CENTER);
        });

        add(button, Alignment.LEFT_CENTER);

        addSpace(0, 2);

        _representations.put(kind, widget);

        return widget;
    }

    RepresentationWidget _createRepresentationWidget(Element.Kind kind)
    {
        RepresentationWidget widget = new RepresentationWidget(kind.toLowerString());

        widget.addSpace(0, 2);

        widget.add(new ButtonSlot(false, ButtonSlot.Kind.DONE, ButtonSlot.Kind.CANCEL));

        return widget;
    }

    private Theme _makeTheme()
    {
        HashMap<Element.Kind, Representation> map = new HashMap<>();

        for (Map.Entry<Element.Kind, RepresentationWidget> entry : _representations.entrySet())
        {
            Representation representation = entry.getValue().representation();

            assert(representation != null);
            assert(representation.character()  != null);
            assert(representation.background() != null);
            assert(representation.foreground() != null);

            map.put(entry.getKey(), representation);
        }

        Theme theme = new Theme(_name.text(), map);

        try
        {
            theme.store(new File("resources/themes"));

        } catch (IOException e) { _showIOErrorBox(); }

        return theme;
    }

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
