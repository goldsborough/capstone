package capstone.data;

import capstone.element.Direction;
import capstone.utility.KeyMap;
import capstone.element.Player;
import capstone.ui.InputKey;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by petergoldsborough on 12/27/15.
 */

public final class Profile extends Data
{
    public Profile(Properties serialization)
    {
        deserialize(serialization);
    }

    public Profile(File file) throws IOException
    {
        load(file);
    }

    public Profile(String id,
                   String realName,
                   KeyMap keyMap,
                   Representation representation)
    {
        this.id(id);
        this.realName(realName);
        this.keyMap(keyMap);
        this.representation(representation);

        _timesPlayed = 0;
    }

    public Profile(Profile other)
    {
        this(
                other._id,
                other._realName,
                other._keyMap,
                other._representation
        );
    }

    public String id()
    {
        return _id;
    }

    public void id(String id)
    {
        assert(id != null);
        assert(id.matches("[\\w-]+"));

        _id = id;
    }

    public String realName()
    {
        return _realName;
    }

    public void realName(String realName)
    {
        assert(realName != null);

        _realName = realName;
    }

    public KeyMap keyMap()
    {
        return _keyMap;
    }

    public void keyMap(KeyMap keymap)
    {
        assert(keymap != null);

        _keyMap = keymap;
    }

    public Direction direction(InputKey key)
    {
        return _keyMap.get(key);
    }

    public int timesPlayed()
    {
        return _timesPlayed;
    }

    public void playedAGame()
    {
        ++_timesPlayed;
    }

    public Representation representation()
    {
        return _representation;
    }

    public void representation(Representation representation)
    {
        assert(representation != null);

        _representation = representation;
    }

    @Override public void store() throws IOException
    {
        super.store(new File("resources/profiles"));
    }

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

        assert(serialization.containsKey("timesPlayed"));
        _timesPlayed = Integer.parseInt(serialization.getProperty("timesPlayed"));

        assert(serialization.containsKey("representation.character"));
        assert(serialization.containsKey("representation.background"));
        assert(serialization.containsKey("representation.foreground"));
        _representation = new Representation(
                serialization.getProperty("representation.character").charAt(0),
                Terminal.Color.valueOf(serialization.getProperty("representation.background")),
                Terminal.Color.valueOf(serialization.getProperty("representation.foreground"))
        );
    }

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
                "timesPlayed",
                Integer.toString(_timesPlayed)
        );

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

    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof Profile)) return false;

        if (object == this) return true;

        Profile other = (Profile) object;

        return this._id.equals(other._id);
    }

    @Override public String fileName()
    {
        return String.format("%1$s.profile", _id);
    }

    @Override public int hashCode()
    {
        return _id.hashCode();
    }

    private String _id;

    private String _realName;

    private KeyMap _keyMap;

    private int _timesPlayed;

    private Representation _representation;
}