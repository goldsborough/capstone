package capstone.data;

import capstone.utility.AbstractPair;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public class Highscore extends Data implements Iterable<Highscore.Entry>
{
    public static class Entry
    {
        public Entry(double time, List<String> players)
        {
            assert(players != null);

            _time = time;
            _players = players;
        }

        public Entry(Map.Entry<Double, List<String>> mapEntry)
        {
            this(mapEntry.getKey(), mapEntry.getValue());
        }

        public double time()
        {
            return _time;
        }

        public List<String> players()
        {
            return _players;
        }

        @Override public boolean equals(Object object)
        {
            if (object == null) return false;

            if (! (object instanceof Entry)) return false;

            if (object == this) return true;

            Entry other = (Entry) object;

            return this._time == other._time             &&
                    _players.containsAll(other._players) &&
                    other._players.containsAll(_players);
        }

        private final double _time;
        private final List<String> _players;
    }

    public Highscore(Properties properties)
    {
        assert(properties != null);

        _map = new TreeMap<>();

        deserialize(properties);
    }

    public Highscore(File file) throws IOException
    {
        assert(file != null);

        _map = new TreeMap<>();

        load(file);
    }

    public Highscore(String level)
    {
        _map = new TreeMap<>();

        this.level(level);
    }

    public Highscore(String level, Double time, List<Profile> profiles)
    {
        _map = new TreeMap<>();

        this.level(level);

        this.put(time, profiles);
    }

    public Highscore(String level, Map<Double, List<Profile>> ids)
    {
        _map = new TreeMap<>();

        this.level(level);

        for (Map.Entry<Double, List<Profile>> entry : ids.entrySet())
        {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public void put(double time, List<Profile> profiles)
    {
        assert(profiles != null);
        assert(! profiles.isEmpty());

        _map.put(time, _extractStrings(profiles));
    }

    public void putIds(double time, List<String> ids)
    {
        assert(ids != null);
        assert(! ids.isEmpty());

        _map.put(time, ids);
    }

    public Entry at(int index)
    {
        assert(index <= _map.size());
        assert(index > 0);

        Iterator<Map.Entry<Double, List<String>>>
                iterator = _map.entrySet().iterator();

        while (--index > 0) iterator.next();

        return new Entry(iterator.next());
    }

    public Double timeAt(int index)
    {
        return at(index).time();
    }

    public List<String> playersAt(int index)
    {
        return at(index).players();
    }

    public String level()
    {
        return _level;
    }

    public void level(String level)
    {
        assert(level != null);

        _level = level;
    }

    public int size()
    {
        return _map.size();
    }

    public boolean isEmpty()
    {
        return _map.isEmpty();
    }

    public Iterator<Entry> iterator()
    {
        return entries().iterator();
    }

    public Collection<Map.Entry<Double, List<String>>> mapEntries()
    {
        return Collections.unmodifiableCollection(_map.entrySet());
    }

    public Iterable<Entry> entries()
    {
        List<Entry> entries = new ArrayList<>();

        for (Map.Entry<Double, List<String>> entry : _map.entrySet())
        {
            entries.add(new Entry(entry));
        }

        return entries;
    }

    public void clear()
    {
        _map.clear();
    }

    @Override public void store() throws IOException
    {
        super.store(new File("resources/highscores"));
    }

    @Override public void deserialize(Properties serialization)
    {
        assert(serialization != null);

        this.level(serialization.getProperty("level"));

        serialization.remove("level");

        for (Map.Entry<Object, Object> entry : serialization.entrySet())
        {
            Double time = Double.parseDouble((String) entry.getKey());

            String[] ids = ((String) entry.getValue()).split("\\s*[^\\w-]\\s*");

            _map.put(time, new ArrayList<>(Arrays.asList(ids)));
        }
    }

    @Override public Properties serialize()
    {
        Properties properties = new Properties();

        properties.setProperty("level", _level);

        for (Map.Entry<Double, List<String>> entry : _map.entrySet())
        {
            properties.setProperty(
                    String.format("%1$.3f s", entry.getKey()),
                    String.join(", ", entry.getValue())
            );
        }

        return properties;
    }

    @Override public String fileName()
    {
        return String.format("%1$s.highscore", _level);
    }

    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof Highscore)) return false;

        if (object == this) return true;

        Highscore other = (Highscore) object;

        return _level.equals(other._level) && _map.equals(other._map);
    }

    private static List<String> _extractStrings(Collection<Profile> profiles)
    {
        List<String> ids = new ArrayList<>();

        for (Profile profile : profiles) ids.add(profile.id());

        return ids;
    }

    private final TreeMap<Double, List<String>> _map;

    private String _level;
}
