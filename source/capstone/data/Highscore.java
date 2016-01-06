package capstone.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Data-class to keep track of highscores for a level.
 *
 * A highscore entry contains a the time a set of players required to
 * succeed at a level, and ids of the relevant players who succeeded.
 *
 */
public class Highscore extends Data implements Iterable<Highscore.Entry>
{
    /**
     *
     * A nicer kind of map-entry for highscore entries, containing the
     * time of the entry (how long the player(s) played until they won)
     * and the player-ids.
     *
     */
    public static class Entry
    {
        /**
         *
         * Constructs a new entry from the elapsed time until success and
         * the list of player-ids who played that game.
         *
         * @param time The time for success.
         *
         * @param players The ids of the players who won.
         *
         */
        public Entry(double time, List<String> players)
        {
            assert(players != null);

            _time = time;
            _players = players;
        }

        /**
         *
         * Utility constructor to construct an Entry from a map-entry.
         *
         * @param mapEntry The mapEntry to construct this Entry from.
         */
        public Entry(Map.Entry<Double, List<String>> mapEntry)
        {
            this(mapEntry.getKey(), mapEntry.getValue());
        }

        /**
         * @return The time required by the players to win.
         */
        public double time()
        {
            return _time;
        }

        /**
         * @return The ids of the players who won.
         */
        public List<String> players()
        {
            return _players;
        }

        /**
         * Equality for entries.
         *
         * @param object The object to check equality to this entry for.
         *
         * @return true if the object is an entry and contains the same
         *         time for the same players.
         */
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

    /**
     *
     * Constructs a Highscore from a properties object.
     *
     * @param properties The Properties object to construct the Highscore from.
     *
     */
    public Highscore(Properties properties)
    {
        assert(properties != null);

        _map = new TreeMap<>();

        deserialize(properties);
    }

    /**
     *
     * Constructs a highscore from a file containing a properties object.
     *
     * @param file The file containg the properties object.
     *
     * @throws IOException for any I/O badness.
     *
     */
    public Highscore(File file) throws IOException
    {
        this(_load(file));
    }

    /**
     *
     * Constructs a new Highscore instance for a level with the given name.
     *
     * @param level The name of the level.
     */
    public Highscore(String level)
    {
        _map = new TreeMap<>();

        this.level(level);
    }


    /**
     *
     * Constructs a new Highscore instance for a level with the given name,
     * containing only one entry for the given time and profiles.
     *
     * @param level The name of the level.
     *
     * @param time The time the players required to win.
     *
     * @param profiles The profiles of the players who won.
     *
     */
    public Highscore(String level, Double time, List<Profile> profiles)
    {
        _map = new TreeMap<>();

        this.level(level);

        this.put(time, profiles);
    }

    /**
     *
     * Constructs a new Highscore instance for the level with the given name,
     * constaining all the entries in the map provided.
     *
     * @param level The name of the level.
     *
     * @param entries The map of times to profiles who won.
     *
     */
    public Highscore(String level, Map<Double, List<Profile>> entries)
    {
        _map = new TreeMap<>();

        this.level(level);

        for (Map.Entry<Double, List<Profile>> entry : entries.entrySet())
        {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     *
     * Puts a new entry with the given time and profiles into the map.
     *
     * @param time The time it took to win.
     *
     * @param profiles The profiles of the players who won.
     *
     */
    public void put(double time, List<Profile> profiles)
    {
        assert(profiles != null);
        assert(! profiles.isEmpty());

        _map.put(time, _extractStrings(profiles));
    }

    /**
     *
     * Puts a new entry with the given time and ids into the map.
     *
     * @param time The time it took to win.
     *
     * @param ids The ids of the players who won.
     *
     */
    public void putIds(double time, List<String> ids)
    {
        assert(ids != null);
        assert(! ids.isEmpty());

        _map.put(time, ids);
    }

    /**
     *
     * Gets the entry at the given spot in the ranking.
     *
     * Note that the ranking is 1-indexed.
     * AssertionError for non-positive indices.
     *
     * @param index The requested index of the entry. 1-indexed.
     *
     */
    public Entry at(int index)
    {
        assert(index <= _map.size());
        assert(index > 0);

        Iterator<Map.Entry<Double, List<String>>>
                iterator = _map.entrySet().iterator();

        while (--index > 0) iterator.next();

        return new Entry(iterator.next());
    }

    /**
     *
     * Gets the time of the entry at the given index.
     *
     * @param index The index of the entry. 1-indexed.
     *
     * @return The time of the players at the index.
     *
     */
    public double timeAt(int index)
    {
        return at(index).time();
    }

    /**
     *
     * Gets the ids of the players of the entry at the given index.
     *
     * @param index The index of the entry. 1-indexed.
     *
     * @return The ids of the players at the index.
     *
     */
    public List<String> playersAt(int index)
    {
        return at(index).players();
    }

    /**
     * @return The name of the level this highscore object is for.
     */
    public String level()
    {
        return _level;
    }

    /**
     *
     * Sets the name of the level this highscore is for.
     *
     * @param level The new level name.
     */
    public void level(String level)
    {
        assert(level != null);

        _level = level;
    }

    /**
     * @return How many elements are in the highscore.
     */
    public int size()
    {
        return _map.size();
    }

    /**
     * @return Whether the highscore is empty.
     */
    public boolean isEmpty()
    {
        return _map.isEmpty();
    }

    /**
     * @return An iterator into the entries.
     */
    public Iterator<Entry> iterator()
    {
        return entries().iterator();
    }

    /**
     * @return A Collection of map-entries.
     */
    public Collection<Map.Entry<Double, List<String>>> mapEntries()
    {
        return Collections.unmodifiableCollection(_map.entrySet());
    }

    /**
     * @return A Collection of Highscore-entries.
     */
    public Collection<Entry> entries()
    {
        List<Entry> entries = new ArrayList<>();

        for (Map.Entry<Double, List<String>> entry : _map.entrySet())
        {
            entries.add(new Entry(entry));
        }

        return entries;
    }

    /**
     * Clears the highscore.
     */
    public void clear()
    {
        _map.clear();
    }

    /**
     *
     * Stores the highscore in the resources/highscores folder.
     *
     * @throws IOException for I/O badness
     */
    @Override public void store() throws IOException
    {
        super.store(new File("resources/highscores"));
    }

    /**
     *
     * Deserializes the highscore object.
     *
     * @param serialization The properties containing the data.
     */
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

    /**
     * @return A serialization of the highscore.
     */
    @Override public Properties serialize()
    {
        Properties properties = new Properties();

        properties.setProperty("level", _level);

        for (Map.Entry<Double, List<String>> entry : _map.entrySet())
        {
            properties.setProperty(
                    String.format("%1$.3f", entry.getKey()),
                    String.join(", ", entry.getValue())
            );
        }

        return properties;
    }

    /**
     * @return The name of the level with a .highscore extension.
     */
    @Override public String fileName()
    {
        return String.format("%1$s.highscore", _level);
    }

    /**
     *
     * Checks equality between highscores (objects).
     *
     * @param object The object to check equality for.
     *
     * @return True if the object is a Highscore object and has
     *         the same level-name and entries.
     */
    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof Highscore)) return false;

        if (object == this) return true;

        Highscore other = (Highscore) object;

        return _level.equals(other._level) && _map.equals(other._map);
    }

    /**
     *
     * Utility method to extract the IDs of a collection of profiles.
     *
     * @param profiles The profiles to extract the IDs of.
     *
     * @return A list of strings.
     */
    private static List<String> _extractStrings(Collection<Profile> profiles)
    {
        List<String> ids = new ArrayList<>();

        for (Profile profile : profiles) ids.add(profile.id());

        return ids;
    }

    private final TreeMap<Double, List<String>> _map;

    private String _level;
}
