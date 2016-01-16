package capstone.data;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import capstone.element.Direction;
import capstone.ui.InputKey;
import capstone.utility.KeyMap;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by petergoldsborough on 12/27/15.
 */
public class ProfileTest
{
    private Profile profile;
    private Representation representation;

    private static <K, V> void assertContains(Map<K, V> map, K key, V value)
    {
        assertTrue(map.containsKey(key));
        assertThat(map.get(key), is(value));
    }

    @Before
    public void setUp()
    {
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
    }

    @Test public void testConstructsWell()
    {
        assertThat(profile.id(), is("test"));
        assertThat(profile.realName(), is("Real Name"));
        assertThat(profile.keyMap(), is(KeyMap.Arrows()));

        assertThat(profile.representation(), is(representation));
    }

    @Test public void testCopyConstructsWell()
    {
        Profile copy = new Profile(profile);

        assertThat(copy, is(profile));
    }

    @Test public void testDirection()
    {
        assertThat(
                profile.direction(new InputKey(Key.Kind.ArrowUp)),
                is(Direction.UP)
        );

        assertThat(
                profile.direction(new InputKey(Key.Kind.ArrowDown)),
                is(Direction.DOWN)
        );

        assertThat(
                profile.direction(new InputKey(Key.Kind.ArrowLeft)),
                is(Direction.LEFT)
        );

        assertThat(
                profile.direction(new InputKey(Key.Kind.ArrowRight)),
                is(Direction.RIGHT)
        );
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

        test.setProperty("representation.character", "@");
        test.setProperty("representation.background", "WHITE");
        test.setProperty("representation.foreground", "GREEN");

        // Internal check that we've made different
        // properties, not code check
        assert(! test.equals(profile.serialize()));

        // Will deserialize
        profile = new Profile(test);

        assertThat(profile.serialize(), is(test));
    }
}