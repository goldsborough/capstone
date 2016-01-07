package capstone.utility;

import capstone.element.Direction;
import capstone.element.Player;
import capstone.ui.InputKey;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by petergoldsborough on 12/28/15.
 */

public class KeyMapTest
{
    private static <K, V> void assertContains(Map<K, V> map, K key, V value)
    {
        assertTrue(map.containsKey(key));
        assertThat(map.get(key), is(value));
    }

    @Test public void testReversesWell()
    {
        HashMap<Integer, String> forward = new HashMap<>();

        forward.put(1, "one");
        forward.put(2, "two");
        forward.put(3, "three");

        HashMap<String, Integer> backward = KeyMap.reverse(forward);

        assertContains(backward, "one", 1);
        assertContains(backward, "two", 2);
        assertContains(backward, "three", 3);
    }

    @Test public void testArrowsFactoryIs()
    {
        KeyMap map = KeyMap.Arrows();

        assertThat(map.up(), CoreMatchers.is(new InputKey(InputKey.Kind.ArrowUp)));
        assertThat(map.down(), is(new InputKey(InputKey.Kind.ArrowDown)));
        assertThat(map.left(), is(new InputKey(InputKey.Kind.ArrowLeft)));
        assertThat(map.right(), is(new InputKey(InputKey.Kind.ArrowRight)));
    }

    @Test public void testConstructsWellFromKeys()
    {
        KeyMap map = new KeyMap(
                new InputKey('a'),
                new InputKey('6'),
                new InputKey(InputKey.Kind.Enter),
                new InputKey(InputKey.Kind.ArrowDown)
        );

        assertThat(map.up(), is(new InputKey('a')));
        assertThat(map.down(), is(new InputKey('6')));
        assertThat(map.left(), is(new InputKey(InputKey.Kind.Enter)));
        assertThat(map.right(), is(new InputKey(InputKey.Kind.ArrowDown)));
    }

    @Test public void testConstructsWellFromKinds()
    {
        KeyMap map = new KeyMap(
                InputKey.Kind.ArrowUp,
                InputKey.Kind.ArrowDown,
                InputKey.Kind.ArrowLeft,
                InputKey.Kind.ArrowRight
        );

        assertThat(map, is(KeyMap.Arrows()));
    }

    @Test(expected = AssertionError.class)
    public void testConstructorThrowsForNonBijectiveMapping()
    {
        new KeyMap("aaaa");
    }

    @Test public void testConstructsWellFromCharacters()
    {
        KeyMap map = new KeyMap('w', 'a', 's', 'd');

        assertThat(map.up(), is(new InputKey('w')));
        assertThat(map.down(), is(new InputKey('a')));
        assertThat(map.left(), is(new InputKey('s')));
        assertThat(map.right(), is(new InputKey('d')));
    }

    @Test public void testConstructsWellFromString()
    {
        KeyMap map = new KeyMap("asdf");

        assertThat(map.up(), is(new InputKey('a')));
        assertThat(map.down(), is(new InputKey('s')));
        assertThat(map.left(), is(new InputKey('d')));
        assertThat(map.right(), is(new InputKey('f')));
    }

    @Test(expected=AssertionError.class)
    public void testConstructorThrowsForTooLongString()
    {
        new KeyMap("asdfghjkl");
    }

    @Test public void testSetsWellForward()
    {
        KeyMap map = KeyMap.Arrows();

        map.set(new InputKey('a'), Direction.UP);
        map.set(new InputKey('b'), Direction.DOWN);
        map.set(new InputKey(InputKey.Kind.Delete), Direction.LEFT);
        map.set(new InputKey(InputKey.Kind.Insert), Direction.RIGHT);

        assertThat(map.up(), is(new InputKey('a')));
        assertThat(map.down(), is(new InputKey('b')));
        assertThat(map.left(), is(new InputKey(InputKey.Kind.Delete)));
        assertThat(map.right(), is(new InputKey(InputKey.Kind.Insert)));
    }

    @Test public void testSetsWellBackwards()
    {
        KeyMap map = KeyMap.Arrows();

        map.set(Direction.UP, new InputKey('a'));
        map.set(Direction.DOWN, new InputKey('b'));
        map.set(Direction.LEFT, new InputKey(InputKey.Kind.Delete));
        map.set(Direction.RIGHT, new InputKey(InputKey.Kind.Insert));

        assertThat(map.up(), is(new InputKey('a')));
        assertThat(map.down(), is(new InputKey('b')));
        assertThat(map.left(), is(new InputKey(InputKey.Kind.Delete)));
        assertThat(map.right(), is(new InputKey(InputKey.Kind.Insert)));
    }

    @Test public void testSetsWellIndividually()
    {
        KeyMap map = KeyMap.Arrows();

        map.up(new InputKey('a'));
        map.down(new InputKey('b'));
        map.left(new InputKey(InputKey.Kind.Delete));
        map.right(new InputKey(InputKey.Kind.Insert));

        assertThat(map.up(), is(new InputKey('a')));
        assertThat(map.down(), is(new InputKey('b')));
        assertThat(map.left(), is(new InputKey(InputKey.Kind.Delete)));
        assertThat(map.right(), is(new InputKey(InputKey.Kind.Insert)));
    }

    @Test public void testSwapsKeysWell()
    {
        KeyMap map = KeyMap.WASD();

        map.swap(new InputKey('a'), new InputKey('d'));

        assertThat(map.left(), is(new InputKey('d')));
        assertThat(map.right(), is(new InputKey('a')));
    }

    @Test public void testDirectionsWell()
    {
        KeyMap map = KeyMap.WASD();

        map.swap(Direction.LEFT, Direction.RIGHT);

        assertThat(map.left(), is(new InputKey('d')));
        assertThat(map.right(), is(new InputKey('a')));
    }

    @Test public void testImplicitlyForwardIterable()
    {
        KeyMap map = KeyMap.WASD();

        HashMap<InputKey, Direction> expected = new HashMap<>();

        expected.put(new InputKey('w'), Direction.UP);
        expected.put(new InputKey('s'), Direction.DOWN);
        expected.put(new InputKey('a'), Direction.LEFT);
        expected.put(new InputKey('d'), Direction.RIGHT);

        HashMap<InputKey, Direction> result = new HashMap<>();

        for (Map.Entry<InputKey, Direction> entry : map)
        {
            result.put(entry.getKey(), entry.getValue());
        }

        assertThat(expected, is(result));
    }

    @Test public void testExplicitlyBackwardIterable()
    {
        KeyMap map = KeyMap.WASD();

        HashMap<Direction, InputKey> expected = new HashMap<>();

        expected.put(Direction.UP, new InputKey('w'));
        expected.put(Direction.DOWN, new InputKey('s'));
        expected.put(Direction.LEFT, new InputKey('a'));
        expected.put(Direction.RIGHT, new InputKey('d'));

        HashMap<Direction, InputKey> result = new HashMap<>();

        for (Map.Entry<Direction, InputKey> entry : map.backward())
        {
            result.put(entry.getKey(), entry.getValue());
        }

        assertThat(expected, is(result));
    }

    @Test public void testExplicitlyForwardIterable()
    {
        KeyMap map = KeyMap.WASD();

        HashMap<InputKey, Direction> expected = new HashMap<>();

        expected.put(new InputKey('w'), Direction.UP);
        expected.put(new InputKey('s'), Direction.DOWN);
        expected.put(new InputKey('a'), Direction.LEFT);
        expected.put(new InputKey('d'), Direction.RIGHT);

        HashMap<InputKey, Direction> result = new HashMap<>();

        for (Map.Entry<InputKey, Direction> entry : map.forward())
        {
            result.put(entry.getKey(), entry.getValue());
        }

        assertThat(expected, is(result));
    }

    @Test public void testGivesAccessToKeys()
    {
        KeyMap map = KeyMap.Arrows();

        HashSet<InputKey> expected = new HashSet<>();

        expected.add(new InputKey(InputKey.Kind.ArrowUp));
        expected.add(new InputKey(InputKey.Kind.ArrowDown));
        expected.add(new InputKey(InputKey.Kind.ArrowLeft));
        expected.add(new InputKey(InputKey.Kind.ArrowRight));

        for (InputKey key : map.keys())
        {
            assertTrue(expected.contains(key));
        }
    }

    @Test public void testGivesAccessToDirections()
    {
        KeyMap map = KeyMap.Arrows();

        HashSet<Direction> expected = new HashSet<>();

        expected.add(Direction.UP);
        expected.add(Direction.DOWN);
        expected.add(Direction.LEFT);
        expected.add(Direction.RIGHT);

        for (Direction direction : map.directions())
        {
            assertTrue(expected.contains(direction));
        }
    }
    @Test(expected=AssertionError.class)
    public void testThrowsForInjectivityViolationForForwardInsertion()
    {
        KeyMap map = KeyMap.WASD();

        map.set(new InputKey('w'), Direction.LEFT);
    }

    @Test(expected=AssertionError.class)
    public void testThrowsForInjectivityViolationForBackwardInsertion()
    {
        KeyMap map = KeyMap.WASD();

        map.set(Direction.LEFT, new InputKey('w'));
    }

    @Test public void testToString()
    {
        KeyMap map = KeyMap.WASD();

        String expected = "[w: UP, s: DOWN, a: LEFT, d: RIGHT]";

        assertThat(map.toString(), is(expected));
    }

    @Test public void testToStringCompressed()
    {
        KeyMap map = KeyMap.WASD();

        String expected = "[w: U, s: D, a: L, d: R]";

        assertThat(map.toStringCompressed(), is(expected));
    }
}