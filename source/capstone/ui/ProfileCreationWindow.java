package capstone.ui;

import capstone.data.Profile;
import capstone.utility.KeyMap;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.dialog.MessageBox;

import java.io.File;
import java.io.IOException;


/**
 *
 * The Window for creating a profile.
 *
 * A profile can be created from an ID, a real name,
 * a Representation and a KeyMap.
 *
 */
public class ProfileCreationWindow extends Widget
{
    /**
     * Constructs a new ProfileCreationWindow with an input
     * field for the Profile's ID abd real-name, a Button for
     * a RepresentationWidget and a KeyMapCreationWidget.
     */
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

    /**
     * @return The profile created, if any was created, else null.
     */
    public Profile profile()
    {
        return _profile;
    }

    /**
     * @return An InputWidget for the ID.
     */
    private InputWidget _createIdWidget()
    {
        // Allow only word characters (a-zA-Z0-9_) and hyphens
        InputWidget idWidget = new InputWidget("ID:       ", "[\\w-]+");

        add(idWidget);

        return idWidget;
    }

    /**
     *
     * @return An InputWidget for the user's real-name.
     */
    private InputWidget _createRealNameWidget()
    {
        // Allow only word characters (a-zA-Z0-9_) and hyphens
        InputWidget realNameWidget = new InputWidget("Real Name:", "[\\w- ]+");

        add(realNameWidget);

        return realNameWidget;
    }

    /**
     * @return A RepresentationWidget for creating the Profile's Representation.
     */
    private RepresentationWidget _createRepresentationWidget()
    {
        RepresentationWidget widget = new RepresentationWidget();

        add(widget);

        return widget;
    }

    /**
     * @return A new named combo-box for creating a KeyMap.
     */
    private NamedWidget<ComboBox<KeyMap>> _createKeyMapWidget()
    {
        ComboBox<KeyMap> combo = new ComboBox<>();

        combo.listWidget(new KeyMapCreationWidget(combo));

        NamedWidget<ComboBox<KeyMap>> keyMapWidget
                = new NamedWidget<>("Keys", combo);

        add(keyMapWidget);

        return keyMapWidget;
    }

    /**
     * Create the buttons at the bottom of the window.
     *
     * Those buttons are a button to create the profile (which also
     * validates the input), then a button to cancel the creation
     * and lastly a button to exit the program.
     */
    protected void _createBottomButtons()
    {
        ButtonSlot slot = new ButtonSlot(false);

        slot.add(_createCreateButton());

        slot.add(ButtonSlot.Kind.CANCEL);

        slot.add(ButtonSlot.Kind.EXIT);

        add(slot);
    }

    /**
     * @return A Button that validates the input and,
     *         if the input was valid, calls _makeProfile().
     */
    private Button _createCreateButton()
    {
        return new Button("Create", () -> {
            if (_validateInput()) {
                _makeProfile();
                super.close();
            }
        });
    }

    /**
     * Fetches the data from the various widgets and creates a Profile,
     * which it also tries to store under resources/profiles.
     */
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
            _profile.store();

        } catch (IOException e) { _showIOErrorBox(); }
    }

    /**
     *
     * Validates the various input fields.
     *
     * @return True if all the input is valid and it is
     *         safe to create a Profile, else false.
     */
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
