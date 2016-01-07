package capstone.ui;

import capstone.utility.KeyMap;
import com.googlecode.lanterna.gui.component.ActionListBox;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.dialog.MessageBox;

/**
 * Manages creation of a new KeyMap.
 *
 * The user has the option of inputting four custom-characters to make
 * up the keymap, or of choosing from one of the preset KeyMaps
 * (e.g. Arrows or W-A-S-D).
 *
 * The KeyMapCreationWidget has to be associated with a ComboBox
 * (the one that opens the KeyMapCreationWidget) because it must
 * override some of the behavior of the ComboBox.
 *
 * This must be a subclass of ListWidget and not be composed
 * of it because ComboBoxes only accept ListWidgets.
 *
 */
public class KeyMapCreationWidget extends ListWidget<KeyMap>
{
    /**
     *
     * Creates a KeyMapCreationWidget and associates it with a ComboBox.
     *
     * @param associatedComboBox The ComboBox associated
     *                           with the KeyMapCreationWidget.
     */
    public KeyMapCreationWidget(ComboBox associatedComboBox)
    {
        // Clear first.
        _panel.removeAllComponents();

        _createCharacterInputSlot(associatedComboBox);

        _setupListWidget(associatedComboBox);

        super.addCancelButton();
    }

    /**
     * @return The selected KeyMap, if any was selected, else null.
     */
    @Override public KeyMap item()
    {
        return _selected;
    }

    /**
     *
     * Creates the slot for the input-region where the user can input
     * his/her own custom key-combination for the keymap.
     *
     * @param associatedComboBox The ComboBox associated
     *                           with the KeyMapCreationWidget.
     */
    private void _createCharacterInputSlot(ComboBox associatedComboBox)
    {
        Panel slot = super._newSlot();

        // Cannot use {4} here because that won't work with the groups
        ValidatedTextBox textBox = new ValidatedTextBox(
                "\\s*(.)\\s*(.)\\s*(.)\\s*(.)\\s*"
        );

        add(slot, textBox, Alignment.LEFT_CENTER);

        Button ok = new Button("OK", () -> {
            _okAction(associatedComboBox, textBox);
        });

        add(slot, ok, Alignment.RIGHT_CENTER);

        add(slot);
    }

    /**
     *
     * The action performed when the OK button is
     * pressed on the custom keymap input widget.
     *
     * @param associatedComboBox The ComboBox associated
     *                           with the KeyMapCreationWidget.
     *
     * @param textBox The ValidatedTextBox used for the input.
     */
    private void _okAction(ComboBox associatedComboBox,
                           ValidatedTextBox textBox)
    {
        if (! _validate(textBox)) return;

        String keys = textBox.concatenatedGroups().toLowerCase();

        _selected = new KeyMap(keys);

        associatedComboBox.text(_selected.toString());

        super.close();
    }

    /**
     *
     * Validates a ValidatedTextBox and displays an
     * error message if the input was not valid.
     *
     * @param textBox The ValidatedTextBox used for the input.
     *
     * @return True if the input was valid and it is safe to fetch
     *         the text from the ValidatedTextBox, else false.
     */
    private boolean _validate(ValidatedTextBox textBox)
    {
        boolean valid = textBox.isValid();

        if (! valid)
        {
            MessageBox.showMessageBox(
                _panel.getParent().getWindow().getOwner(),
                "Invalid Input!",
                "\nThe characters you entered were not valid!\n\n"
              + " Must be four non-space characters."
            );
        }

        return valid;
    }

    /**
     *
     * Sets up the ListWidget this class derives from
     * and adds a bunch of KeyMap presets.
     *
     * @param associatedComboBox The ComboBox associated
     *                           with the KeyMapCreationWidget.
     */
    private void _setupListWidget(ComboBox associatedComboBox)
    {
        _list = new ActionListBox();

        super.add(_list);

        _addKeyMap(KeyMap.Arrows(), associatedComboBox);

        _addKeyMap(KeyMap.WASD(), associatedComboBox);

        _addKeyMap(new KeyMap("ikjl"), associatedComboBox);

        _addKeyMap(new KeyMap(
                        InputKey.Kind.F1,
                        InputKey.Kind.F2,
                        InputKey.Kind.F3,
                        InputKey.Kind.F4
                ),
                associatedComboBox
        );

        _addKeyMap(new KeyMap(
                        InputKey.Kind.F5,
                        InputKey.Kind.F6,
                        InputKey.Kind.F7,
                        InputKey.Kind.F8
                ),
                associatedComboBox
        );

        _addKeyMap(new KeyMap(
                        InputKey.Kind.F9,
                        InputKey.Kind.F10,
                        InputKey.Kind.F11,
                        InputKey.Kind.F12
                ),
                associatedComboBox
        );
    }

    /**
     *
     * Adds a KeyMap to the ListWidget this class derives from and
     * makes it's action that of setting the associated ComboBox's text.
     *
     * @param keyMap The KeyMap to add.
     *
     * @param associatedComboBox The ComboBox associated
     *                           with the KeyMapCreationWidget.
     */
    private void _addKeyMap(KeyMap keyMap, ComboBox associatedComboBox)
    {
        super.add(keyMap, () -> {
            associatedComboBox.text(keyMap.toString());
        });
    }
}
