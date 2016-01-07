package capstone.utility;

import capstone.element.Direction;
import capstone.ui.InputKey;
import com.googlecode.lanterna.input.Key;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * A bidirectional hashtable to map keys to directions for players. Extra
 * overhead and care is taken to ensure a strong bijectivity between
 * the keys and the direction. There must at most and at least one and only
 * one key for each direction, i.e. this KeyMap contains four keys and
 * four directions at all times and never more, nore less.
 *
 */
public class KeyMap implements Iterable<Map.Entry<InputKey, Direction>>
{
    /**
     *
     * Static method to reverse map, i.e. create a new map from the
     * existing one, but with keys and values swapped entry-wise.
     *
     * @param forward The forward mapping (key -> value).
     *
     * @param <K> The key-type.
     *
     * @param <V> The value-type.
     *
     * @return The backward mapping (value -> key).
     */
    public static <K, V> HashMap<V, K> reverse(HashMap<K, V> forward)
    {
        HashMap<V, K> backward = new HashMap<>();

        for (Map.Entry<K, V> entry : forward.entrySet())
        {
            backward.put(entry.getValue(), entry.getKey());
        }

        return backward;
    }

    /**
     * @return A ready-made KeyMap using the common arrow-keys.
     */
    public static KeyMap Arrows()
    {
        return new KeyMap(
                new InputKey(InputKey.Kind.ArrowUp),
                new InputKey(InputKey.Kind.ArrowDown),
                new InputKey(InputKey.Kind.ArrowLeft),
                new InputKey(InputKey.Kind.ArrowRight)
        );
    }

    /**
     * @return A ready-made KeyMap using the W-A-S-D keys.
     */
    public static KeyMap WASD()
    {
        return new KeyMap(
                new InputKey('w'),
                new InputKey('s'),
                new InputKey('a'),
                new InputKey('d')
        );
    }

    // No default constructor = surjectivity

    /**
     *
     * Constructs the KeyMap with the given characters as keys.
     *
     * @param up The character for the up-direction.
     *
     * @param down The character for the down-direction.
     *
     * @param left The character for the left-direction.
     *
     * @param right The character for the right-direction.
     */
    public KeyMap(char up, char down, char left, char right)
    {
        this(
                new InputKey(up),
                new InputKey(down),
                new InputKey(left),
                new InputKey(right)
        );
    }

    /**
     *
     * Utility constructor to pass the four character-keys as a string.
     *
     * The string must have exactly four characters.
     *
     * @param keys The string containing four characters.
     */
    public KeyMap(String keys)
    {
        this(
                keys.charAt(0),
                keys.charAt(1),
                keys.charAt(2),
                keys.charAt(3)
        );

        assert(keys.length() == 4);
    }

    /**
     *
     * Constructs the KeyMap from the four InputKey Kinds (i.e. special keys).
     *
     * @param up The Kind for the up-direction.
     *
     * @param down The Kind for the down-direction.
     *
     * @param left The Kind for the left-direction.
     *
     * @param right The Kind for the right-direction.
     */
    public KeyMap(InputKey.Kind up,
                  InputKey.Kind down,
                  InputKey.Kind left,
                  InputKey.Kind right)
    {
        this(
                new InputKey(up),
                new InputKey(down),
                new InputKey(left),
                new InputKey(right)
        );
    }

    /**
     *
     * Constructs the KeyMap from the four full InputKey objects.
     *
     * @param up The key for the up-direction.
     *
     * @param down The key for the down-direction.
     *
     * @param left The key for the left-direction.
     *
     * @param right The key for the right-direction.
     */
    public KeyMap(InputKey up,
                  InputKey down,
                  InputKey left,
                  InputKey right)
    {
        _forward = new HashMap<>();
        _backward = new HashMap<>();

        _forward.put(up, Direction.UP);
        _forward.put(down, Direction.DOWN);
        _forward.put(left, Direction.LEFT);
        _forward.put(right, Direction.RIGHT);

        _backward.put(Direction.UP, up);
        _backward.put(Direction.DOWN, down);
        _backward.put(Direction.LEFT, left);
        _backward.put(Direction.RIGHT, right);

        // We're good from here on
        assert(_forward.size() == 4);
        assert(_backward.size() == 4);
    }

    /**
     *
     * Constructs the KeyMap from the given forward-mapping
     * (forward means key -> direction).
     *
     * @param forward The forward mapping.
     */
    public KeyMap(Map<InputKey, Direction> forward)
    {
        _forward = new HashMap<>(forward);
        _backward = reverse(_forward);

        // We're good from here on
        assert(_forward.size() == 4);
        assert(_backward.size() == 4);
    }

    /**
     *
     * Sets the given key for the given direction.
     *
     * Bijectivity must not be violated and is heavily asserted.
     *
     * @param key The InputKey.
     *
     * @param direction The Direction.
     *
     * @see KeyMap#swap
     */
    public void set(InputKey key, Direction direction)
    {
        assert(_forward.size() == 4);  // injectivity
        assert(_backward.size() == 4); // surjectivity

        // Erase old relation
        _forward.remove(_backward.get(direction));

        _forward.put(key, direction);
        _backward.put(direction, key);

        // If the forward mapping is not 4 anymore,
        // then we have violated injectivity
        assert(_forward.size() == 4);
        assert(_backward.size() == 4);
    }

    /**
     *
     * Backward version of set.
     *
     * @param direction The Direction.
     *
     * @param key The InputKey.
     */
    public void set(Direction direction, InputKey key)
    {
        set(key, direction);
    }

    /**
     *
     * Swaps the two keys, i.e. swaps the directions they map to.
     *
     * @param first The first InputKey.
     *
     * @param second The second InputKey.
     */
    public void swap(InputKey first, InputKey second)
    {
        assert(_forward.size() == 4);  // injectivity
        assert(_backward.size() == 4); // surjectivity

        assert(_forward.containsKey(first));
        assert(_forward.containsKey(second));

        Direction temp = _forward.get(first);

        _forward.put(first, _forward.get(second));
        _forward.put(second, temp);

        _backward.put(temp, second);
        _backward.put(_forward.get(first), first);

        // If the forward mapping is not 4 anymore,
        // then we have violated injectivity
        assert(_forward.size() == 4);
        assert(_backward.size() == 4);
    }

    /**
     *
     * Swaps the two directions, i.e. swaps the keys they map to.
     *
     * @param first The first Direction.
     *
     * @param second The second Direction.
     */
    public void swap(Direction first, Direction second)
    {
        assert(_forward.size() == 4);  // injectivity
        assert(_backward.size() == 4); // surjectivity

        assert(_backward.containsKey(first));
        assert(_backward.containsKey(second));

        InputKey temp = _backward.get(first);

        _backward.put(first, _backward.get(second));
        _backward.put(second, temp);

        _forward.put(temp, second);
        _forward.put(_backward.get(first), first);

        // If the forward mapping is not 4 anymore,
        // then we have violated injectivity
        assert(_forward.size() == 4);
        assert(_backward.size() == 4);
    }

    /**
     * @return The key mapped to the up-direction.
     */
    public InputKey up()
    {
        return _backward.get(Direction.UP);
    }

    /**
     *
     * Sets the key that maps to the up-direction.
     *
     * @param key The new InputKey for the up-direction.
     */
    public void up(InputKey key)
    {
        set(key, Direction.UP);
    }

    /**
     * @return The key mapped to the down-direction.
     */
    public InputKey down()
    {
        return _backward.get(Direction.DOWN);
    }

    /**
     *
     * Sets the key that maps to the down-direction.
     *
     * @param key The new InputKey for the down-direction.
     */
    public void down(InputKey key)
    {
        set(key, Direction.DOWN);
    }

    /**
     * @return The key mapped to the left-direction.
     */
    public InputKey left()
    {
        return _backward.get(Direction.LEFT);
    }

    /**
     *
     * Sets the key that maps to the left-direction.
     *
     * @param key The new InputKey for the left-direction.
     */
    public void left(InputKey key)
    {
        set(key, Direction.LEFT);
    }

    /**
     * @return The key mapped to the right-direction.
     */
    public InputKey right()
    {
        return _backward.get(Direction.RIGHT);
    }

    /**
     *
     * Sets the key that maps to the right-direction.
     *
     * @param key The new InputKey for the right-direction.
     */
    public void right(InputKey key)
    {
        set(key, Direction.RIGHT);
    }

    /**
     *
     * Returns the Direction associated with the given InputKey.
     *
     * @param key The InputKey to get the Direction for.
     *
     * @return The Direction associated with the given key.
     */
    public Direction get(InputKey key)
    {
        return _forward.get(key);
    }

    /**
     *
     * Returns the Direction associated with the given lanterna-Key.
     *
     * Provided for utility to not have to create an
     * InputKey from the lanterna-Key first.
     *
     * @param key The lanterna-Key to get the Direction for.
     *
     * @return The Direction associated with the given key.
     */
    public Direction get(Key key)
    {
        return get(InputKey.fromKey(key));
    }

    /**
     *
     * Returns the InputKey associated with the given Direction.
     *
     * @param direction The Direction to get the InputKey for.
     *
     * @return The InputKey associated with the given Direction.
     */
    public InputKey get(Direction direction)
    {
        return _backward.get(direction);
    }

    /**
     * @return An iterator over the forward mapping (key -> direction).
     */
    public Iterator<Map.Entry<InputKey, Direction>> iterator()
    {
        return forward().iterator();
    }

    /**
     * @return A collection of forward-map-entries (key -> direction).
     */
    public Collection<Map.Entry<InputKey, Direction>> forward()
    {
        return Collections.unmodifiableCollection(_forward.entrySet());
    }

    /**
     * @return A collection of backward-map-entries (direction -> key).
     */
    public Collection<Map.Entry<Direction, InputKey>> backward()
    {
        return Collections.unmodifiableCollection(_backward.entrySet());
    }

    /**
     * @return All the keys in the KeyMap.
     */
    public Collection<InputKey> keys()
    {
        return Collections.unmodifiableCollection(_forward.keySet());
    }

    /**
     * @return All the directions in the KeyMap.
     */
    public Collection<Direction> directions()
    {
        return Collections.unmodifiableCollection(_backward.keySet());
    }

    /**
     * @return A string representation of the KeyMap: [key: direction].
     *         Full direction names are used.
     */
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("[");

        // Want deterministic order (hashmap is unsorted)

        builder.append(String.format(
                "%s: %s, ",
                _backward.get(Direction.UP),
                "UP"
        ));

        builder.append(String.format(
                "%s: %s, ",
                _backward.get(Direction.DOWN),
                "DOWN"
        ));

        builder.append(String.format(
                "%s: %s, ",
                _backward.get(Direction.LEFT),
                "LEFT"
        ));

        builder.append(String.format(
                "%s: %s",
                _backward.get(Direction.RIGHT),
                "RIGHT"
        ));

        builder.append("]");

        return builder.toString();
    }

    /**
     * @return A compressed string representation of the KeyMap:
     *        [key: direction]. Only the first character
     *        of direction names are used.
     */
    public String toStringCompressed()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("[");

        // Want deterministic order (hashmap is unsorted)

        builder.append(String.format(
                "%s: %c, ",
                _backward.get(Direction.UP),
                'U'
        ));

        builder.append(String.format(
                "%s: %c, ",
                _backward.get(Direction.DOWN),
                'D'
        ));

        builder.append(String.format(
                "%s: %c, ",
                _backward.get(Direction.LEFT),
                'L'
        ));

        builder.append(String.format(
                "%s: %c",
                _backward.get(Direction.RIGHT),
                'R'
        ));

        builder.append("]");

        return builder.toString();
    }

    /**
     *
     * Determines equality between the KeyMap and an object.
     *
     * Checks if the object is a KeyMap and the mapping is the same.
     *
     * @param object The object to check equality for.
     *
     * @return True if the object is a KeyMap and has the same mapping.
     */
    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof KeyMap)) return false;

        if (object == this) return true;

        KeyMap other = (KeyMap) object;

        return this._forward.equals(other._forward);
    }

    /**
     * @return A hashCode for the KeyMap.
     */
    @Override public int hashCode()
    {
        int code = 982347031; // magic number

        for (InputKey key : _forward.keySet())
        {
            code ^= key.hashCode();
        }

        return code;
    }

    private HashMap<InputKey, Direction> _forward;

    private HashMap<Direction, InputKey> _backward;
}
