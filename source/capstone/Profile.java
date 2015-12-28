package capstone;

import com.googlecode.lanterna.terminal.Terminal;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

/**
 * Created by petergoldsborough on 12/27/15.
 */

public final class Profile extends Data
{
    public static Calendar currentTime()
    {
        Calendar now = new GregorianCalendar();

        return new GregorianCalendar(
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH),
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE)
        );
    }

    public Profile(Properties serialization)
    {
        _keyMap = new KeyMap();
        _joined = new GregorianCalendar();

        deserialize(serialization);
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

        _joined = currentTime();
        _timesPlayed = 0;
    }


    @Override public void deserialize(Properties serialization)
    {
        assert(serialization.containsKey("id"));
        _id = serialization.getProperty("id");

        assert(serialization.containsKey("realName"));
        _realName = serialization.getProperty("realName");

        _keyMap.clear();

        for (Player.Direction direction : Player.Direction.values())
        {
            _keyMap.put(
                    Key.fromString(serialization.getProperty(direction.toString())),
                    direction
            );
        }

        assert(serialization.containsKey("joined"));
        _joined.setTimeInMillis(
                Long.parseLong(serialization.getProperty("joined"))
        );

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
        Properties serialization = new Properties();

        serialization.setProperty("id", _id);
        serialization.setProperty("realName", _realName);

        for (KeyMap.Entry<Key, Player.Direction> entry : _keyMap.entrySet())
        {
            serialization.setProperty(
                    entry.getValue().toString(),
                    entry.getKey().toString()
            );
        }

        serialization.setProperty(
                "joined",
                Long.toString(_joined.getTimeInMillis())
        );

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

    public Calendar joined()
    {
        return _joined;
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

    @Override protected String fileName()
    {
        return String.format("%1$s.profile", _id);
    }

    private String _id;

    private String _realName;

    private KeyMap _keyMap;

    private Calendar _joined;

    private int _timesPlayed;

    private Representation _representation;
}
