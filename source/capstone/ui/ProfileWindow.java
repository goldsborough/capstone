package capstone.ui;

import capstone.data.Profile;
import capstone.utility.KeyMap;
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.component.Button;
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
 * Window for selecting profiles.
 */
public class ProfileWindow extends Widget
{
    /**
     * Opens a ProfileWindow with no previous profiles.
     */
    public ProfileWindow()
    {
        this(new ArrayList<>());
    }

    /**
     *
     * Constructs a new ProfileWindow and adds the profiles as
     * "already-selected". The point is that when you cancel at a later
     * stage (e.g. level-selection) you can add the profiles the user
     * selected before so the window looks like how you left it and you
     * don't have to reselect all profiles.
     *
     * @param profiles The profiles to display as "already-selected".
     */
    public ProfileWindow(Collection<Profile> profiles)
    {
        super("Profile Selection", Panel.Orientation.VERTICAL);

        assert(profiles != null);

        _selected = new HashSet<>();
        _keyMaps = new HashMap<>();

        // Add already selected profiles
        for (Profile profile : profiles)
        {
            Panel slot = _newSlot();

            _addProfileRemoveButton(slot, profile);

            add(slot);
        }

        addSpace(0, 1);

        // Create slot to add a player and create a bottom slot
        if (profiles.isEmpty()) _createExtraPlayerSlot();

        // Or only create a bottom slot (nicer when going back)
        else _createBottomSlot();
    }

    /**
     * @return The selected profiles. Empty if none yet.
     */
    public List<Profile> profiles()
    {
        return new ArrayList<>(_selected);
    }


    /**
     * Creates a new slot for adding a profile. Such a slot includes a button
     * for creating a new profile or selecting an existing one. It also
     * takes care of first removing the bottom slot (done/cancel/add-player)
     * and then re-adding at the bottom (below the new player slot).
     */
    private void _createExtraPlayerSlot()
    {
        Panel slot = super._newSlot();

        _createPlayerSlot(slot);

        // We're inserting this slot at position of
        // bottomSlot so we need to first remove it.
        if (_bottomSlot != null) remove(_bottomSlot);

        add(slot);

        addSpace(0, 1);

        _createBottomSlot();
    }

    /**
     *
     * Creates the slot for adding a new player or choosing an existing one.
     *
     * @param slot The slot to which to add the buttons.
     */
    private void _createPlayerSlot(Panel slot)
    {
        _createNewProfileButton(slot);

        addSpace(5, 0);

        _createExistingProfileButton(slot);
    }

    /**
     *
     * Creates the button for creating a new profile.
     *
     * @param slot The slot to which to add the button.
     */
    private void _createNewProfileButton(Panel slot)
    {
        Button button = new Button("New Profile", () -> _getNewProfile(slot));

        add(slot, button, Component.Alignment.LEFT_CENTER);
    }

    /**
     *
     * Creates th button for choosing an existing profile.
     *
     * @param slot The slot to which to add the button.
     */
    private void _createExistingProfileButton(Panel slot)
    {
        Button button = new Button(
                "Existing Profile",
                () -> _getExistingProfile(slot)
        );

        add(slot, button, Component.Alignment.RIGHT_CENTER);
    }

    /**
     * Creates the bottom slot containing the
     * "Done", "Add Player" and "Cancel" buttons.
     */
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

    /**
     *
     * Manages creating a new profile.
     *
     * @param slot The slot to add the created id to.
     */
    private void _getNewProfile(Panel slot)
    {
        ProfileCreationWindow creation = new ProfileCreationWindow();

        getOwner().showWindow(creation, GUIScreen.Position.CENTER);

        Profile profile = creation.profile();

        if (profile != null) _addProfile(slot, profile);
    }

    /**
     *
     * Manages selecting an existing profile from a file dialog.
     *
     * @param slot The slot to which to add the selected id, if not cancelled.
     */
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

    /**
     *
     * Manages adding a new player to a slot. Handles things like duplicate
     * profile selection and handling keymap conflicts.
     *
     * @param slot The slot to add the player ID to.
     *
     * @param profile The relevant profile.
     */
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

            // For checking key-map conflicts.
            _keyMaps.put(profile.keyMap(), profile);

            // Replace the two buttons for creating a profile with
            // a new button displaying the ID of the profile. When
            // you click on the button, the profile is de-selected
            // again.
            slot.removeAllComponents();

            _addProfileRemoveButton(slot, profile);
        }
    }

    /**
     * Opens a file dialog for selection.
     *
     * @return The selected file, if any.
     */
    private File _openFileDialog()
    {
        return FileDialog.showOpenFileDialog(
                super.getOwner(),
                new File("resources/profiles"),
                "Choose Profile"
        );
    }


    /**
     * Shows a message to let the user know that no profiles have
     * been selected yet, which is bad. This happens when the user
     * presses "Done" without having created or chosen a profile.
     */
    private void _showEmptinessMessage()
    {
        MessageBox.showMessageBox(
                super.getOwner(),
                "No Profiles Selected!",
                "\nPlease select at least one profile!"
        );
    }

    /**
     *
     * Checks if the profile selected has a keymap conflicting
     * with any previously selected profile. If so, shows a warning
     * message that one of them will not be able to move in the game.
     *
     * @param profile The profile to check the keymap of.
     */
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

    /**
     *
     * Ads the button for removing a profile after having selected one.
     * When a profile is de-selected, it is replaced with the slot to
     * add a create a new profile or select an existing one.
     *
     * @param slot The slot to make the modifications to.
     *
     * @param profile The newly selected player.
     */
    private void _addProfileRemoveButton(Panel slot, Profile profile)
    {
        add(slot, new Button(profile.id(), () -> {
            _selected.remove(profile);
            _keyMaps.remove(profile.keyMap());
            slot.removeAllComponents();
            _createPlayerSlot(slot);
        }));
    }

    private ButtonSlot _bottomSlot;

    private Set<Profile> _selected;

    private Map<KeyMap, Profile> _keyMaps;
}
