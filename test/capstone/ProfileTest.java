package capstone;

import static org.junit.Assert.*;

import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

/**
 * Created by petergoldsborough on 12/27/15.
 */
public class ProfileTest
{
    private Profile profile;
    private Calendar before;
    private Calendar after;
    private Representation representation;

    private static <K, V> void assertContains(Map<K, V> map, K key, V value)
    {
        assertTrue(map.containsKey(key));
        assertEquals(map.get(key), value);
    }

    @Before
    public void setUp()
    {
        // Minute could pass while we create the profile,
        // so we will check that it equals the time before OR after
        before = Profile.currentTime();

        representation = new Representation(
                '',
                Terminal.Color.RED,
                Terminal.Color.BLUE
        );

        profile = new Profile(
                "test",
                "Real Name",
                KeyMap.Arrows(),
                representation
        );

        after = Profile.currentTime();
    }

    @Test public void testConstructsWell()
    {
        assertEquals(profile.id(), "test");
        assertEquals(profile.realName(), "Real Name");
        assertEquals(profile.keyMap(), KeyMap.Arrows());

        assertTrue(
                profile.joined().equals(before) ||
                profile.joined().equals(after)
        );

        assertEquals(profile.timesPlayed(), 0);
        assertEquals(profile.representation(), representation);
    }

    @Test public void testIncrementsTimesPlayedWhenPlayedAGameIsCalled()
    {
        int before = profile.timesPlayed();

        profile.playedAGame();

        assertEquals(profile.timesPlayed(), before + 1);
    }

    @Test(expected=AssertionError.class)
    public void testRejectsIDWithSymbolCharacters()
    {
        profile.id("@$%@");
    }

    @Test(expected=AssertionError.class)
    public void testRejectsIDWithSpaceCharacters()
    {
        profile.id("\n   \r\t");
    }

    @Test(expected=AssertionError.class)
    public void testRejectsNullID()
    {
        profile.id(null);
    }

    @Test(expected=AssertionError.class)
    public void testRejectsNullKeyMap()
    {
        profile.keyMap(null);
    }

    @Test(expected=AssertionError.class)
    public void testRejectsNullRealName()
    {
        profile.realName(null);
    }

    @Test(expected=AssertionError.class)
    public void testRejectsNullRepresentation()
    {
        profile.representation(null);
    }
    @Test public void testSerialize()
    {
        Properties serialized = profile.serialize();

        assertContains(serialized, "id", "test");

        assertContains(serialized, "realName", "Real Name");

        assertContains(serialized, "UP", "ArrowUp");

        assertContains(serialized, "DOWN", "ArrowDown");

        assertContains(serialized, "LEFT", "ArrowLeft");

        assertContains(serialized, "RIGHT", "ArrowRight");

        assertTrue(serialized.containsKey("joined"));
        assertTrue(
                serialized.get("joined").equals(Long.toString(before.getTimeInMillis())) ||
                serialized.get("joined").equals(Long.toString(after.getTimeInMillis()))
        );

        assertContains(serialized, "timesPlayed", "0");

        assertContains(serialized, "representation.character", "");
        assertContains(serialized, "representation.background", "RED");
        assertContains(serialized, "representation.foreground", "BLUE");
    }

    @Test public void testDeserialize() throws IOException
    {
        Properties test = new Properties();

        test.setProperty("id", "chicken");
        test.setProperty("realName", "Chicken Egg");

        test.setProperty("UP", "ArrowDown");
        test.setProperty("DOWN", "ArrowUp");
        test.setProperty("LEFT", "ArrowRight");
        test.setProperty("RIGHT", "ArrowLeft");

        test.setProperty("joined", Long.toString(Profile.currentTime().getTimeInMillis()));

        test.setProperty("timesPlayed", "123");

        test.setProperty("representation.character", "@");
        test.setProperty("representation.background", "WHITE");
        test.setProperty("representation.foreground", "GREEN");

        // Internal check that we've made different
        // properties, not code check
        assert(! test.equals(profile.serialize()));

        // Will deserialize
        profile = new Profile(test);

        assertEquals(profile.serialize(), test);
    }

    @Test public void testFileName()
    {
        assertEquals(profile.fileName(), "test.profile");
    }
}