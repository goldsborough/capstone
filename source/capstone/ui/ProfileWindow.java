package capstone.ui;

import capstone.data.Profile;
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

/**
 * Created by petergoldsborough on 12/29/15.
 */
public class ProfileWindow extends Widget
{
    public ProfileWindow()
    {
        super("Profile Selection", Panel.Orientation.VERTICAL);

        super.setDrawShadow(false);

        _selected = new ArrayList<>();

        addSpace(0, 1);

        _createExtraPlayerSlot();
    }

    public ArrayList<Profile> profiles()
    {
        return _selected;
    }

    private void _createExtraPlayerSlot()
    {
        Panel slot = super._newSlot();

        _createNewProfileButton(slot);

        addSpace(5, 0);

        _createExistingProfileButton(slot);


        // Insert this slot at position of bottomslot
        if (_bottomSlot != null) _panel.removeComponent(_bottomSlot);

        add(slot);

        addSpace(0, 1);

        _createBottomSlot();
    }

    private void _createNewProfileButton(Panel slot)
    {
        Button button = new Button("New Profile", () -> _getNewProfile(slot));

        add(slot, button, Component.Alignment.LEFT_CENTER);
    }

    private void _createExistingProfileButton(Panel slot)
    {
        Button button = new Button("Existing Profile", () -> _getExistingProfile(slot));

        add(slot, button, Component.Alignment.RIGHT_CENTER);
    }

    private void _createBottomSlot()
    {
        _bottomSlot = new ButtonSlot(false, ButtonSlot.Kind.DONE);

        Button button = new Button("Add Player", this::_createExtraPlayerSlot);

        _bottomSlot.add(button);

        _bottomSlot.add(ButtonSlot.Kind.EXIT);

        add(_bottomSlot);
    }

    private void _createDoneButton(Panel slot)
    {
        Button button = new Button("Done", () -> {
            if (_selected.isEmpty()) _showEmptinessMessage();
            else super.close();
        });

        add(slot, button, Alignment.LEFT_CENTER);
    }

    private void _getNewProfile(Panel slot)
    {
        ProfileCreationWindow creation = new ProfileCreationWindow();

        getOwner().showWindow(creation, GUIScreen.Position.CENTER);

        Profile profile = creation.getProfile();

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

        _selected.add(profile);

        slot.removeAllComponents();

        add(slot, new Label(profile.id()));
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

    private ButtonSlot _bottomSlot;

    private ArrayList<Profile> _selected;
}
