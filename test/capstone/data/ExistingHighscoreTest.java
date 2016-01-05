package capstone.data;

import capstone.data.Highscore;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public class ExistingHighscoreTest
{
    private Highscore highscore;

    private ArrayList<String> ids;

    private static <K, V> void assertContains(Map<K, V> map, K key, V value) {
        assertTrue(map.containsKey(key));
        assertThat(map.get(key), is(value));
    }

    @Before public void setUp()
    {
        highscore = new Highscore("Test");

        ids = new ArrayList<>();

        ids.add("test");
    }

    @Test public void testSerialize()
    {
        Properties expected = new Properties();

        expected.setProperty("level", "Test");

        expected.setProperty("1.230", "Player1, Player2");

        expected.setProperty("3.140", "Player3");

        expected.setProperty("7.680", "Player4");

        expected.setProperty("10.000", "Player3");
        
        ids.clear();
        ids.add("Player1");
        ids.add("Player2");
        
        highscore.putIds(1.23, new ArrayList<>(ids));

        ids.clear();
        ids.add("Player3");

        highscore.putIds(3.14, new ArrayList<>(ids));
        highscore.putIds(10.0, new ArrayList<>(ids));


        ids.clear();
        ids.add("Player4");

        highscore.putIds(7.68, new ArrayList<>(ids));

        assertThat(highscore.serialize(), is(expected));
    }

    @Test public void testDeserialize()
    {
        Properties properties = new Properties();

        properties.setProperty("level", "Mock");

        properties.setProperty("1.230", "Player1, Player2");

        properties.setProperty("3.140", "Player3");

        properties.setProperty("7.680", "Player4");

        properties.setProperty("10.000", "Player3");

        highscore.deserialize(properties);

        assertThat(highscore.level(), is("Mock"));

        assertThat(highscore.timeAt(1), is(1.23));
        assertThat(highscore.timeAt(2), is(3.14));
        assertThat(highscore.timeAt(3), is(7.68));
        assertThat(highscore.timeAt(4), is(10.0));


        ids.clear();
        ids.add("Player1");
        ids.add("Player2");

        assertThat(highscore.playersAt(1), is(ids));

        ids.clear();
        ids.add("Player3");

        assertThat(highscore.playersAt(2), is(ids));
        assertThat(highscore.playersAt(4), is(ids));


        ids.clear();
        ids.add("Player4");

        assertThat(highscore.playersAt(3), is(ids));
    }

    @Test public void testConstructsWell()
    {
        assertThat(highscore.level(), is("Test"));

        assertThat(highscore.size(), is(0));
        assertTrue(highscore.isEmpty());
    }

    @Test public void testCanInsertSinglePlayerEntryWell()
    {
        assert(highscore.isEmpty());

        highscore.putIds(1.23, ids);

        assertThat(highscore.size(), is(1));
        assertFalse(highscore.isEmpty());
        assertThat(highscore.playersAt(1), is(ids));
    }

    @Test public void testCanInsertMultiplePlayersEntryWell()
    {
        ids.add("tes2");

        assert(highscore.isEmpty());

        highscore.putIds(1.23, ids);

        assertThat(highscore.size(), is(1));
        assertFalse(highscore.isEmpty());
        assertThat(highscore.playersAt(1), is(ids));
    }

    @Test public void testOrdersEntrysProperly()
    {
        highscore.putIds(0.0001, ids);
        highscore.putIds(1, ids);
        highscore.putIds(10000, ids);

        assertThat(highscore.timeAt(1), is(0.0001));
        assertThat(highscore.timeAt(2), is(1.0));
        assertThat(highscore.timeAt(3), is(10000.0));
    }

    @Test public void testInterface()
    {
        assertThat(highscore.level(), is("Test"));

        highscore.putIds(1, ids);

        assertThat(highscore.timeAt(1), is(1.0));
        assertThat(highscore.playersAt(1), is(ids));

        assertThat(highscore.at(1).time(), is(1.0));
        assertThat(highscore.at(1).players(), is(ids));
    }

    @Test public void testIterableOfMapEntries()
    {
        highscore.putIds(1.0, ids);
        highscore.putIds(2.0, ids);

        HashMap<Double, Collection<String>> result = new HashMap<>();

        for (Map.Entry<Double, List<String>> entry : highscore.mapEntries()) {

            result.put(entry.getKey(), entry.getValue());
        }

        assertThat(result.size(), is(2));

        assertContains(result, 1.0, ids);
        assertContains(result, 2.0, ids);
    }

    @Test public void testIterableOfCustomEntries()
    {
        highscore.putIds(1.0, ids);
        highscore.putIds(2.0, ids);

        ArrayList<Highscore.Entry> expected = new ArrayList<>();

        expected.add(new Highscore.Entry(1.0, ids));
        expected.add(new Highscore.Entry(2.0, ids));

        assertThat(highscore.entries(), is(expected));
    }

    @Test public void testIsIterableOverCustomEntries()
    {
        highscore.putIds(1.0, ids);
        highscore.putIds(2.0, ids);

        ArrayList<Highscore.Entry> result = new ArrayList<>();

        highscore.forEach(result::add);

        ArrayList<Highscore.Entry> expected = new ArrayList<>();

        expected.add(new Highscore.Entry(1.0, ids));
        expected.add(new Highscore.Entry(2.0, ids));

        assertThat(result, is(expected));
    }

    @Test public void testClear()
    {
        highscore.putIds(1.23, ids);

        assert(! highscore.isEmpty());

        highscore.clear();

        assertTrue(highscore.isEmpty());
    }

    @Test(expected=AssertionError.class)
    public void testAtThrowsForInvalidIndex()
    {
        highscore.at(0);
    }

    @Test(expected=AssertionError.class)
    public void testNameThrowsForNull()
    {
        highscore.level(null);
    }

    @Test(expected=AssertionError.class)
    public void testPutThrowsForEmptyProfiles()
    {
        highscore.put(1.23, new ArrayList<>());
    }

    @Test(expected=AssertionError.class)
    public void testPutIdsThrowsForEmptyProfiles()
    {
        highscore.put(1.23, new ArrayList<>());
    }
}