package capstone.ui;

import capstone.data.Profile;
import capstone.utility.KeyMap;
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.dialog.FileDialog;
import com.googlecode.lanterna.gui.dialog.MessageBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by petergoldsborough on 12/29/15.
 */
public class ProfileWindow extends Widget
{
    public ProfileWindow()
    {
        this(new ArrayList<>());
    }

    public ProfileWindow(Collection<Profile> profiles)
    {
        super("Profile Selection", Panel.Orientation.VERTICAL);

        assert(profiles != null);

        super.setDrawShadow(false);

        _selected = new HashSet<>();
        _keyMaps = new HashMap<>();

        for (Profile profile : profiles)
        {
            addSpace(0, 1);

            add(new Label(profile.id()));
        }

        addSpace(0, 1);

        _createExtraPlayerSlot();
    }

    public List<Profile> profiles()
    {
        return new ArrayList<>(_selected);
    }


    private void _createExtraPlayerSlot()
    {
        _createExtraPlayerSlot(super._newSlot());
    }

    private void _createExtraPlayerSlot(Panel slot)
    {
        _createPlayerSlot(slot);

        // Insert this slot at position of bottomslot
        if (_bottomSlot != null) remove(_bottomSlot);

        add(slot);

        addSpace(0, 1);

        _createBottomSlot();
    }

    private void _createPlayerSlot(Panel slot)
    {
        _createNewProfileButton(slot);

        addSpace(5, 0);

        _createExistingProfileButton(slot);
    }

    private void _createNewProfileButton(Panel slot)
    {
        Button button = new Button("New Profile", () -> _getNewProfile(slot));

        add(slot, button, Component.Alignment.LEFT_CENTER);
    }

    private void _createExistingProfileButton(Panel slot)
    {
        Button button = new Button(
                "Existing Profile",
                () -> _getExistingProfile(slot)
        );

        add(slot, button, Component.Alignment.RIGHT_CENTER);
    }

    private void _createBottomSlot()
    {
        _bottomSlot = new ButtonSlot(false);

        _bottomSlot.add(new Button("Done", () -> {
            if (_selected.isEmpty()) _showEmptinessMessage();
            else super.close();
        }));

        Button button = new Button("Add Player", this::_createExtraPlayerSlot);

        _bottomSlot.add(button);

        _bottomSlot.add(ButtonSlot.Kind.CANCEL);

        add(_bottomSlot);
    }

    private void _getNewProfile(Panel slot)
    {
        ProfileCreationWindow creation = new ProfileCreationWindow();

        getOwner().showWindow(creation, GUIScreen.Position.CENTER);

        Profile profile = creation.profile();

        if (profile != null) _addProfile(slot, profile);
    }

    private void _getExistingProfile(Panel slot)
    {
        File file = _openFileDialog();

        if (file != null)
        {
            try
            {
                _addProfile(slot, new Profile(file));
            }

            catch(IOException e) { _showIOErrorBox(); }
        }
    }

    private void _addProfile(Panel slot, Profile profile)
    {
        assert(profile != null);

        if (_selected.contains(profile))
        {
            MessageBox.showMessageBox(
                    getOwner(),
                    "Error",
                    "Profile already selected!"
            );
        }

        else
        {
            _checkKeyMapConflicts(profile);

            _selected.add(profile);
            _keyMaps.put(profile.keyMap(), profile);

            slot.removeAllComponents();

            add(slot, new Button(profile.id(), () -> {
                _selected.remove(profile);
                slot.removeAllComponents();
                _createPlayerSlot(slot);
            }));
        }
    }

    private File _openFileDialog()
    {
        return FileDialog.showOpenFileDialog(
                super.getOwner(),
                new File("resources/profiles"),
                "Choose Profile"
        );
    }


    private void _showEmptinessMessage()
    {
        MessageBox.showMessageBox(
                super.getOwner(),
                "No Profiles Selected!",
                "\nPlease select at least one profile!"
        );
    }

    private void _checkKeyMapConflicts(Profile profile)
    {
        Profile other = _keyMaps.get(profile.keyMap());

        if (other != null)
        {
            String message = String.format(
                    "\n%1$s has the same key-map as %2$s!\n"
                  + "\nOne of them will not be able to move.\n"
                  + "\nRemove one of these players to resolve this issue.",
                    profile.id(),
                    other.id()

            );

            MessageBox.showMessageBox(
                    getOwner(),
                    "Warning",
                    message
            );
        }
    }

    private ButtonSlot _bottomSlot;

    private Set<Profile> _selected;

    private Map<KeyMap, Profile> _keyMaps;
}
