package capstone.data;

import capstone.element.Element;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * A data-class to model a Theme for a Level.
 *
 * A Theme is modular data-structure to encapsulate the representation of
 * Elements in a Level. Basically, a Theme is a collection of
 * Kind-Representation pairs that can be utilized to easily plug and swap
 * the Representation of Elements in a level.
 *
 * All Themes have a name.
 */
public class Theme
        extends Data
        implements Iterable<Map.Entry<Element.Kind, Representation>>
{
    /**
     *
     * Constructs the Theme from the serialization
     * in the given Properties object.
     *
     * @param properties The Properties file containing
     *                   the serialization for the Theme.
     */
    public Theme(Properties properties)
    {
        _representations = new HashMap<>();

        deserialize(properties);
    }

    /**
     *
     * Constructs a Theme from a File containing the
     * Properties serialization for the Theme.
     *
     * @param file The File containing the Properties
     *             to deserialize this Theme from.
     *
     * @throws IOException for I/O badness.
     */
    public Theme(File file) throws IOException
    {
        _representations = new HashMap<>();

        load(file);
    }

    /**
     *
     * Constructs a new empty Theme with the given name.
     *
     * @param name The name for the Theme.
     */
    public Theme(String name)
    {
        this.name(name);

        _representations = new HashMap<>();
    }

    /**
     *
     * Constructs a Theme with a name and fills it with the entries
     * of the passed mapping from Kinds to Representations.
     *
     * @param name The name for the Theme.
     *
     * @param map The mapping from Kinds to Representation
     *            to initialize this Theme with.
     */
    public Theme(String name, Map<Element.Kind, Representation> map)
    {
        this.name(name);

        _representations = new HashMap<>(map);
    }

    /**
     *
     * Stores the Theme under resources/themes with the filename
     * returned by the fileName() function.
     *
     * @throws IOException for I/O badness.
     */
    @Override public void store() throws IOException
    {
        super.store(new File("resources/themes"));
    }

    /**
     *
     * Deserializes the Theme from the given Properties.
     *
     * @param serialization The properties containing the data.
     */
    @Override public void deserialize(Properties serialization)
    {
        _representations.clear();

        assert(serialization.containsKey("name"));
        _name = serialization.getProperty("name");

        for (Element.Kind kind : Element.Kind.values())
        {
            if (kind == Element.Kind.PLAYER) continue;

            String string = kind.toString();

            assert(serialization.containsKey(string + ".character"));
            assert(serialization.containsKey(string + ".background"));
            assert(serialization.containsKey(string + ".background"));

            Representation representation = new Representation(
                    serialization.getProperty(string + ".character").charAt(0),
                    Terminal.Color.valueOf(serialization.getProperty(string + ".background")),
                    Terminal.Color.valueOf(serialization.getProperty(string + ".foreground"))
            );

            _representations.put(kind, representation);
        }
    }

    /**
     * @return A Properties object containing the Serialization for the Theme.
     */
    @Override public Properties serialize()
    {
        Properties serialization = new Properties();

        serialization.setProperty("name", _name);

        for (Element.Kind kind : Element.Kind.values())
        {
            if (kind == Element.Kind.PLAYER) continue;

            String string = kind.toString();
            Representation representation = _representations.get(kind);

            assert(representation != null);
            assert(representation.character()  != null);
            assert(representation.background() != null);
            assert(representation.foreground() != null);

            serialization.setProperty(
                    string + ".character",
                    representation.character().toString()
            );

            serialization.setProperty(
                    string + ".background",
                    representation.background().toString()
            );

            serialization.setProperty(
                    string + ".foreground",
                    representation.foreground().toString()
            );
        }

        return serialization;
    }

    /**
     * @return The name of the Theme.
     */
    public String name()
    {
        return _name;
    }

    /**
     *
     * Sets the name of the Theme.
     *
     * @param name The new name for the Theme.
     */
    public void name(String name)
    {
        assert(name != null);

        _name = name;
    }

    /**
     *
     * Sets the Representation of the Kind to the one supplied.
     *
     * @param kind The Kind to set the Representation for.
     *
     * @param representation The Representation for the Kind.
     */
    public void representation(Element.Kind kind, Representation representation)
    {
        assert(kind != null);
        assert(representation != null);

        _representations.put(kind, representation);
    }

    /**
     *
     * Returns the Representation for the given Kind.
     *
     * @param kind The Kind to get the Representation for.
     *
     * @return The Representation for the given Kind.
     */
    public Representation representation(Element.Kind kind)
    {
        return _representations.get(kind);
    }

    /**
     * @return An iterator over the entries of the Theme.
     */
    public Iterator<Map.Entry<Element.Kind, Representation>> iterator()
    {
        return _representations.entrySet().iterator();
    }

    /**
     *
     * Checks equality between this Theme and an object.
     *
     * @param object The object to check equality for.
     *
     * @return True if the Object is a Theme with the same name and
     *         representation mapping.
     */
    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof Theme)) return false;

        if (object == this) return true;

        Theme other = (Theme) object;

        return _name.equals(other._name) &&
               _representations.equals(other._representations);
    }

    /**
     * @return The name of the Theme with a .theme extension.
     */
    @Override public String fileName()
    {
        return String.format("%s.theme", _name);
    }

    private String _name;

    private HashMap<Element.Kind, Representation> _representations;
}
