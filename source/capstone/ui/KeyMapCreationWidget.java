package capstone.ui;

import capstone.utility.KeyMap;
import com.googlecode.lanterna.gui.component.ActionListBox;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.dialog.MessageBox;

/**
 * Created by petergoldsborough on 12/30/15.
 */
public class KeyMapCreationWidget extends ListWidget<KeyMap>
{
    public KeyMapCreationWidget(ComboBox associatedComboBox)
    {
        _panel.removeAllComponents();

        _createCharacterInputSlot(associatedComboBox);

        _setupListWidget(associatedComboBox);

        super.addCancelButton();
    }

    @Override public KeyMap item()
    {
        return _selected;
    }

    private void _createCharacterInputSlot(ComboBox associatedComboBox)
    {
        Panel slot = new Panel(Panel.Orientation.HORISONTAL);

        ValidatedTextBox textBox = new ValidatedTextBox("(?:\\s*.\\s*){4}");

        add(slot, textBox, Alignment.LEFT_CENTER);

        Button ok = new Button("OK", () -> {
            if (_validate(textBox)) {
                _selected = new KeyMap(textBox.text().toLowerCase());
                associatedComboBox.text(_selected.toString());
                super.close();
        }});

        add(slot, ok, Alignment.RIGHT_CENTER);

        add(slot);
    }

    private boolean _validate(ValidatedTextBox textBox)
    {
        boolean valid = textBox.isValid();

        if (! valid)
        {
            MessageBox.showMessageBox(
                _panel.getParent().getWindow().getOwner(),
                "Invalid Input!",
                "\nThe characters you entered were not valid!\n\n Must be four non-space characters."
            );
        }

        return valid;
    }

    private void _setupListWidget(ComboBox associatedComboBox)
    {
        _list = new ActionListBox();

        super.add(_list);

        _addKeyMap(KeyMap.Arrows(), associatedComboBox);

        _addKeyMap(KeyMap.WASD(), associatedComboBox);

        _addKeyMap(new KeyMap("ijkl"), associatedComboBox);

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

    private void _addKeyMap(KeyMap map, ComboBox associatedComboBox)
    {
        super.add(map, () -> {
            associatedComboBox.text(map.toStringCompressed());
        });
    }
}
