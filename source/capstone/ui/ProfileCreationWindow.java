package capstone.ui;

import capstone.data.Profile;
import capstone.utility.KeyMap;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.dialog.MessageBox;

import java.io.File;
import java.io.IOException;


public class ProfileCreationWindow extends Widget
{
    public ProfileCreationWindow()
    {
        super("New Profile", Panel.Orientation.VERTICAL);

        super.setSoloWindow(true);

        addSpace(0, 2);

        _idWidget = _createIdWidget();

        addSpace(0, 2);

        _realNameWidget = _createRealNameWidget();

        addSpace(0, 2);

        _representationWidget = _createRepresentationWidget();

        addSpace(0, 2);

        _keyMapWidget = _createKeyMapWidget();

        addSpace(0, 2);

        _createBottomButtons();
    }

    public Profile profile()
    {
        return _profile;
    }

    private InputWidget _createIdWidget()
    {
        InputWidget idWidget = new InputWidget("ID:       ", "[\\w-]+");

        add(idWidget);

        return idWidget;
    }

    private InputWidget _createRealNameWidget()
    {
        InputWidget realNameWidget = new InputWidget("Real Name:", "[\\w- ]+");

        add(realNameWidget);

        return realNameWidget;
    }

    private NamedWidget<ComboBox<KeyMap>> _createKeyMapWidget()
    {
        ComboBox<KeyMap> combo = new ComboBox<>();

        combo.listWidget(new KeyMapCreationWidget(combo));

        NamedWidget<ComboBox<KeyMap>> keyMapWidget = new NamedWidget<>(
                "Keys",
                combo
        );

        add(keyMapWidget);

        return keyMapWidget;
    }

    private RepresentationWidget _createRepresentationWidget()
    {
        RepresentationWidget widget = new RepresentationWidget();

        add(widget);

        return widget;
    }

    protected void _createBottomButtons()
    {
        ButtonSlot slot = new ButtonSlot(false);

        slot.add(_createCreateButton());

        slot.add(ButtonSlot.Kind.CANCEL);

        slot.add(ButtonSlot.Kind.EXIT);

        add(slot);
    }

    private Button _createCreateButton()
    {
        return new Button("Create", () -> {
            if (_validateInput()) {
                _makeProfile();
                super.close();
            }
        });
    }

    private void _makeProfile()
    {
        _profile = new Profile(
            _idWidget.text(),
            _realNameWidget.text(),
            _keyMapWidget.widget().item(),
            _representationWidget.representation()
        );

        try
        {
            _profile.store(new File("resources/profiles"));

        } catch (IOException e) { _showIOErrorBox(); }
    }

    private boolean _validateInput()
    {
        if (! _idWidget.validate()) return false;
        if (! _realNameWidget.validate()) return false;
        if (! _representationWidget.validate()) return false;

        if (_keyMapWidget.widget().item() == null)
        {
            MessageBox.showMessageBox(
                    getWindow().getOwner(),
                    "Invalid Input",
                    "Please select an item for 'Keys'"
            );

            return false;
        }

        return true;
    }

    private InputWidget _idWidget;

    private InputWidget _realNameWidget;

    private RepresentationWidget _representationWidget;

    private NamedWidget<ComboBox<KeyMap>> _keyMapWidget;

    private Profile _profile;
}
