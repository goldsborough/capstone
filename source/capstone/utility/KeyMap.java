package capstone.utility;

import capstone.element.Direction;
import capstone.element.Player;
import capstone.ui.InputKey;
import com.googlecode.lanterna.input.Key;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KeyMap implements Iterable<Map.Entry<InputKey, Direction>>
{
    public static <K, V> HashMap<V, K> reverse(HashMap<K, V> forward)
    {
        HashMap<V, K> backward = new HashMap<>();

        for (Map.Entry<K, V> entry : forward.entrySet())
        {
            backward.put(entry.getValue(), entry.getKey());
        }

        return backward;
    }

    public static KeyMap Arrows()
    {
        return new KeyMap(
                new InputKey(InputKey.Kind.ArrowUp),
                new InputKey(InputKey.Kind.ArrowDown),
                new InputKey(InputKey.Kind.ArrowLeft),
                new InputKey(InputKey.Kind.ArrowRight)
        );
    }

    public static KeyMap WASD()
    {
        return new KeyMap(
                new InputKey('w'),
                new InputKey('s'),
                new InputKey('a'),
                new InputKey('d')
        );
    }

    // No default = surjectivity

    public KeyMap(char up, char down, char left, char right)
    {
        this(
                new InputKey(up),
                new InputKey(down),
                new InputKey(left),
                new InputKey(right)
        );
    }

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

        assert(_forward.size() == 4);
        assert(_backward.size() == 4);
    }

    public KeyMap(Map<InputKey, Direction> forward)
    {
        _forward = new HashMap<>(forward);
        _backward = reverse(_forward);
    }

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

    public void set(Direction direction, InputKey key)
    {
        set(key, direction);
    }

    // Because else you can't swap keys that are already mapped
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

    public void up(InputKey key)
    {
        set(key, Direction.UP);
    }

    public InputKey up()
    {
        return _backward.get(Direction.UP);
    }

    public void down(InputKey key)
    {
        set(key, Direction.DOWN);
    }

    public InputKey down()
    {
        return _backward.get(Direction.DOWN);
    }

    public void left(InputKey key)
    {
        set(key, Direction.LEFT);
    }

    public InputKey left()
    {
        return _backward.get(Direction.LEFT);
    }

    public void right(InputKey key)
    {
        set(key, Direction.RIGHT);
    }

    public InputKey right()
    {
        return _backward.get(Direction.RIGHT);
    }

    public Direction get(InputKey key)
    {
        return _forward.get(key);
    }

    public Direction get(Key key)
    {
        return get(InputKey.fromKey(key));
    }

    public InputKey get(Direction direction)
    {
        return _backward.get(direction);
    }

    public Iterator<Map.Entry<InputKey, Direction>> iterator()
    {
        return forward().iterator();
    }

    public Collection<Map.Entry<InputKey, Direction>> forward()
    {
        return Collections.unmodifiableCollection(_forward.entrySet());
    }

    public Collection<Map.Entry<Direction, InputKey>> backward()
    {
        return Collections.unmodifiableCollection(_backward.entrySet());
    }

    public Collection<InputKey> keys()
    {
        return Collections.unmodifiableCollection(_forward.keySet());
    }

    public Collection<Direction> directions()
    {
        return Collections.unmodifiableCollection(_backward.keySet());
    }

    public void clear()
    {
        _forward.clear();
        _backward.clear();
    }

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

    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof KeyMap)) return false;

        if (object == this) return true;

        KeyMap other = (KeyMap) object;

        return this._forward.equals(other._forward);
    }

    @Override public int hashCode()
    {
        int code = 982347031;

        for (InputKey key : _forward.keySet())
        {
            code ^= key.hashCode();
        }

        return code;
    }

    private HashMap<InputKey, Direction> _forward;

    private HashMap<Direction, InputKey> _backward;
}
