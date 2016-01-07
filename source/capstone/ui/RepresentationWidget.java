package capstone.ui;

import capstone.data.Representation;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * A Widget to create a Representation.
 */
public class RepresentationWidget extends Widget
{
    /**
     * Creates a RepresentationWidget with no (window) title.
     */
    public RepresentationWidget()
    {
        this("");
    }

    /**
     * Creates a RepresentationWidget with the given (window) title.
     *
     * @param title The title of the RepresentationWidget.
     */
    public RepresentationWidget(String title)
    {
        super(title);

        _title = title;

        addSpace(0, 1);

        // For choosing a character
        _characterWidget = _createCharacterWidget();

        addSpace(0, 2);

        _backgroundWidget = _createColorWidget("Background");

        addSpace(0, 2);

        _foregroundWidget = _createColorWidget("Foreground");
    }

    /**
     * @return The title of RepresentationWidget's window.
     */
    public String title()
    {
        return _title;
    }

    /**
     * @return The Representation created if the input was valid for
     *         the character and the colors, else null.
     */
    public Representation representation()
    {
        if (! isValid()) return null;

        return new Representation(
                _characterWidget.widget().text().charAt(0),
                _backgroundWidget.widget().item(),
                _foregroundWidget.widget().item()
        );
    }

    /**
     * @return True if the characterWidget and the color
     *         widgets all have valid input and it is safe
     *         to create a Representation from them, else false.
     */
    public boolean isValid()
    {
        return _characterWidget.isValid()                &&
               _backgroundWidget.widget().hasSelection() &&
               _foregroundWidget.widget().hasSelection();
    }

    /**
     * @return A string representation of which input field is invalid
     *         (the first one found). If none are invalid, null is returned.
     */
    public String culprit()
    {
        if (! _characterWidget.isValid()) return "Character";

        else if (! _backgroundWidget.widget().hasSelection())
        {
            return "Background Color";
        }

        else if (! _foregroundWidget.widget().hasSelection())
        {
            return "Foreground Color";
        }

        else return null;
    }

    /**
     *
     * Checks if the input on the character and color fields are valid,
     * and shows a message box with an error if any isn't.
     *
     * @return True if the character and color input was valid, else false.
     */
    public boolean validate()
    {
        if (! _characterWidget.validate()) return false;

        if (! _validate(_backgroundWidget)) return false;

        if (! _validate(_foregroundWidget)) return false;

        return true;
    }

    /**
     *
     * Validates one of the color fields.
     *
     * @param color The color widget to validate.
     *
     * @return True if the widget's item is valid, else false.
     */
    private boolean _validate(NamedWidget<ComboBox<Terminal.Color>> color)
    {
        boolean valid = color.widget().hasSelection();

        if (! valid)
        {
            MessageBox.showMessageBox(
                    getWindow().getOwner(),
                    "Invalid Input",
                    String.format("Please select an item for '%s'", color.name())
            );
        }

        return valid;
    }

    /**
     *
     * Creates an InputWidget for the Representation's character
     * and returns that InputWidget.
     *
     * @return A new InputWidget.
     */
    private InputWidget _createCharacterWidget()
    {
        InputWidget characterWidget = new InputWidget("Character:", ".");

        add(characterWidget);

        return characterWidget;
    }

    /**
     *
     * Creates a new input field for a color.
     *
     * @param where A string representation of where the color
     *              is i.e. "Background" or "Foreground".
     *
     * @return The created Widget.
     */
    private NamedWidget<ComboBox<Terminal.Color>>
    _createColorWidget(String where)
    {

        NamedWidget<ComboBox<Terminal.Color>> colorWidget = new NamedWidget<>(
                where + " Color",
                new ComboBox<>(Terminal.Color.values())
        );

        add(colorWidget);

        return colorWidget;
    }

    private InputWidget _characterWidget;

    private NamedWidget<ComboBox<Terminal.Color>> _backgroundWidget;

    private NamedWidget<ComboBox<Terminal.Color>>_foregroundWidget;

    private String _title;
}
