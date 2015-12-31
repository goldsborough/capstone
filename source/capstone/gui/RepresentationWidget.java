package capstone.gui;

import capstone.data.Representation;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by petergoldsborough on 12/30/15.
 */
public class RepresentationWidget extends Widget
{
    public RepresentationWidget()
    {
        this("");
    }

    public RepresentationWidget(String title)
    {
        super(title);

        _title = title;

        _characterWidget = _createCharacterWidget();

        addSpace(0, 2);

        _backgroundWidget = _createColorWidget("Background");

        addSpace(0, 2);

        _foregroundWidget = _createColorWidget("Foreground");
    }

    public String title()
    {
        return _title;
    }

    public Representation representation()
    {
        if (! isValid()) return null;

        assert(_backgroundWidget.widget().item() != null);
        assert(_foregroundWidget.widget().item() != null);

        return new Representation(
                _characterWidget.widget().text().charAt(0),
                _backgroundWidget.widget().item(),
                _foregroundWidget.widget().item()
        );
    }

    public boolean isValid()
    {
        return _characterWidget.isValid()                &&
               _backgroundWidget.widget().item() != null &&
               _foregroundWidget.widget().item() != null;
    }

    public String culprit()
    {
        if (! _characterWidget.isValid()) return "character";

        else if (! _backgroundWidget.isVisible()) return "Background Color";

        else return "Foreground Color";
    }

    public boolean validate()
    {
        if (! _characterWidget.validate()) return false;

        return _validate(_backgroundWidget) && _validate(_foregroundWidget);
    }

    private boolean _validate(NamedWidget<ComboBox<Terminal.Color>> color)
    {
        boolean valid = color.widget().isNull();

        if (! valid)
        {
            MessageBox.showMessageBox(
                    getOwner(),
                    "Invalid Input",
                    String.format("Please select an item for '%s'", color.title())
            );
        }

        return valid;
    }

    private InputWidget _createCharacterWidget()
    {
        InputWidget characterWidget = new InputWidget("Character:", "\\S");

        add(characterWidget);

        return characterWidget;
    }

    private NamedWidget<ComboBox<Terminal.Color>> _createColorWidget(String ground)
    {
        NamedWidget<ComboBox<Terminal.Color>> colorWidget = new NamedWidget<>(
                ground + " Color",
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
