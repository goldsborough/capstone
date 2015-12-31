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
 * Created by petergoldsborough on 12/28/15.
 */
public class Theme extends Data implements Iterable<Map.Entry<Element.Kind, Representation>>
{
    public Theme(Properties properties)
    {
        _representations = new HashMap<>();

        deserialize(properties);
    }

    public Theme(File file) throws IOException
    {
        _representations = new HashMap<>();

        load(file);
    }

    public Theme(String name, Map<Element.Kind, Representation> map)
    {
        this.name(name);

        _representations = new HashMap<>(map);
    }

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

    public String name()
    {
        return _name;
    }

    public void name(String name)
    {
        assert(name != null);

        _name = name;
    }

    public void representation(Element.Kind kind, Representation representation)
    {
        _representations.put(kind, representation);
    }

    public Representation representation(Element.Kind kind)
    {
        return _representations.get(kind);
    }

    public Iterator<Map.Entry<Element.Kind, Representation>> iterator()
    {
        return _representations.entrySet().iterator();
    }

    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof Theme)) return false;

        if (object == this) return true;

        Theme other = (Theme) object;

        return _name.equals(other._name) &&
               _representations.equals(other._representations);
    }

    @Override
    public String fileName()
    {
        return String.format("%s.theme", _name);
    }

    private String _name;

    private HashMap<Element.Kind, Representation> _representations;
}
