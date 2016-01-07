package capstone.data;

import capstone.element.Direction;
import capstone.ui.InputKey;
import capstone.utility.KeyMap;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A data-class to model a user-profile. Such a user-profile consists
 * of an ID, a Real Name, a KeyMap (mapping InputKeys to Directions)
 * and lastly a Representation.
 */

public final class Profile extends Data
{
    /**
     *
     * Deserializes the Profile from the given Properties.
     *
     * @param serialization The Properties to deserialize this Profile from.
     *
     */
    public Profile(Properties serialization)
    {
        deserialize(serialization);
    }

    /**
     *
     * Deserializes the Profile from the Properties stored in the given file.
     *
     * @param file The file containing the Properties, in turn
     *             holding a serialization for this Profile.
     *
     * @throws IOException in case of I/O badness.
     */
    public Profile(File file) throws IOException
    {
        load(file);
    }

    /**
     *
     * Constructs a new Profile from the given attributes.
     *
     * The ID must consist of only word characters (a-zA-Z0-9_) and hyphens.
     *
     * @param id The ID for the Profile.
     *
     * @param realName The real name for the Profile.
     *
     * @param keyMap The KeyMap instance for the Profile.
     *
     * @param representation The Representation for this Profile.
     */
    public Profile(String id,
                   String realName,
                   KeyMap keyMap,
                   Representation representation)
    {
        this.id(id);
        this.realName(realName);
        this.keyMap(keyMap);
        this.representation(representation);
    }

    /**
     *
     * Copy-constructor.
     *
     * @param other The other Profile to copy this one from.
     */
    public Profile(Profile other)
    {
        this(
                other._id,
                other._realName,
                other._keyMap,
                other._representation
        );
    }

    /**
     * @return The ID of the profile.
     */
    public String id()
    {
        return _id;
    }

    /**
     *
     * Sets the ID of the Profile.
     *
     * It must consist of only word characters (a-zA-Z0-9_) and hyphens.
     *
     * @param id The new ID for the Profile.
     */
    public void id(String id)
    {
        assert(id != null);
        assert(id.matches("[\\w-]+"));

        _id = id;
    }

    /**
     * @return The real name attribute of the Profile.
     */
    public String realName()
    {
        return _realName;
    }

    /**
     *
     * Sets the real name attribute of the Profile.
     *
     * @param realName The new real name for the Profile.
     */
    public void realName(String realName)
    {
        assert(realName != null);

        _realName = realName;
    }

    /**
     * @return The KeyMap of the Profile.
     */
    public KeyMap keyMap()
    {
        return _keyMap;
    }

    /**
     *
     * Sets the KeyMap for the Profile.
     *
     * @param keymap The new KeyMap for the Profile.
     */
    public void keyMap(KeyMap keymap)
    {
        assert(keymap != null);

        _keyMap = keymap;
    }

    /**
     *
     * Gets the Direction associated with the given InputKey in the Profile.
     *
     * @param key The InputKey to get the Direction for, if any.
     *
     * @return The Direction associated with the given InputKey if that
     *         InputKey is in the Profile's KeyMap, else null.
     */
    public Direction direction(InputKey key)
    {
        return _keyMap.get(key);
    }

    /**
     * @return The Representation of the Profile.
     */
    public Representation representation()
    {
        return _representation;
    }

    /**
     *
     * Sets the Representation for this Profile.
     *
     * @param representation The new Representation for this Profile.
     */
    public void representation(Representation representation)
    {
        assert(representation != null);

        _representation = representation;
    }

    /**
     *
     * Stores the Profile at resources/profiles with the filename
     * returned by the fileName() function.
     *
     * @throws IOException for I/O badness.
     */
    @Override public void store() throws IOException
    {
        super.store(new File("resources/profiles"));
    }

    /**
     *
     * Deserializes the Profile from the given Properties.
     *
     * @param serialization The properties containing the data.
     */
    @Override public void deserialize(Properties serialization)
    {
        assert(serialization.containsKey("id"));
        _id = serialization.getProperty("id");

        assert(serialization.containsKey("realName"));
        _realName = serialization.getProperty("realName");

        // No default constructor for KeyMap
        HashMap<InputKey, Direction> mapping = new HashMap<>();

        for (Direction direction : Direction.motion())
        {
            mapping.put(
                    InputKey.fromString(serialization.getProperty(direction.toString())),
                    direction
            );
        }

        _keyMap = new KeyMap(mapping);

        // Deserialize the Representation
        assert(serialization.containsKey("representation.character"));
        assert(serialization.containsKey("representation.background"));
        assert(serialization.containsKey("representation.foreground"));
        _representation = new Representation(
                serialization.getProperty("representation.character").charAt(0),
                Terminal.Color.valueOf(serialization.getProperty("representation.background")),
                Terminal.Color.valueOf(serialization.getProperty("representation.foreground"))
        );
    }

    /**
     *
     * Serializes the Profile.
     *
     * @return A Properties object containing the
     *         serialization for the Profile.
     */
    @Override public Properties serialize()
    {
        assert(_id != null);
        assert(_realName != null);
        assert(_representation != null);
        assert(_keyMap != null);

        Properties serialization = new Properties();

        serialization.setProperty("id", _id);
        serialization.setProperty("realName", _realName);

        for (Map.Entry<InputKey, Direction> entry : _keyMap)
        {
            serialization.setProperty(
                    entry.getValue().toString(),
                    entry.getKey().toString()
            );
        }

        serialization.setProperty(
                "representation.character",
                Character.toString(_representation.character())
        );

        serialization.setProperty(
                "representation.background",
                _representation.background().toString()
        );

        serialization.setProperty(
                "representation.foreground",
                _representation.foreground().toString()
        );

        return serialization;
    }

    /**
     *
     * Checks equality between this Profile and an object.
     *
     * @param object The object to check equality for.
     *
     * @return True if the object is a Profile for the same user-ID.
     */
    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof Profile)) return false;

        if (object == this) return true;

        Profile other = (Profile) object;

        return this._id.equals(other._id);
    }

    /**
     * @return The user-ID of the Profile with a .profile extension.
     */
    @Override public String fileName()
    {
        return String.format("%1$s.profile", _id);
    }

    /**
     * @return The hashCode of the Profile.
     */
    @Override public int hashCode()
    {
        return _id.hashCode();
    }

    private String _id;

    private String _realName;

    private KeyMap _keyMap;

    private Representation _representation;
}
