package capstone.utility;

import capstone.element.Player;
import capstone.ui.InputKey;
import com.googlecode.lanterna.input.Key;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KeyMap implements Iterable<Map.Entry<InputKey, Player.Direction>>
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

    public KeyMap(InputKey up, InputKey down, InputKey left, InputKey right)
    {
        _forward = new HashMap<>();
        _backward = new HashMap<>();

        _forward.put(up, Player.Direction.UP);
        _forward.put(down, Player.Direction.DOWN);
        _forward.put(left, Player.Direction.LEFT);
        _forward.put(right, Player.Direction.RIGHT);

        _backward.put(Player.Direction.UP, up);
        _backward.put(Player.Direction.DOWN, down);
        _backward.put(Player.Direction.LEFT, left);
        _backward.put(Player.Direction.RIGHT, right);

        assert(_forward.size() == 4);
        assert(_backward.size() == 4);
    }

    public KeyMap(Map<InputKey, Player.Direction> forward)
    {
        _forward = new HashMap<>(forward);
        _backward = reverse(_forward);
    }

    public void set(InputKey key, Player.Direction direction)
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

    public void set(Player.Direction direction, InputKey key)
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

        Player.Direction temp = _forward.get(first);

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
        set(key, Player.Direction.UP);
    }

    public InputKey up()
    {
        return _backward.get(Player.Direction.UP);
    }

    public void down(InputKey key)
    {
        set(key, Player.Direction.DOWN);
    }

    public InputKey down()
    {
        return _backward.get(Player.Direction.DOWN);
    }

    public void left(InputKey key)
    {
        set(key, Player.Direction.LEFT);
    }

    public InputKey left()
    {
        return _backward.get(Player.Direction.LEFT);
    }

    public void right(InputKey key)
    {
        set(key, Player.Direction.RIGHT);
    }

    public InputKey right()
    {
        return _backward.get(Player.Direction.RIGHT);
    }

    public Player.Direction get(InputKey key)
    {
        return _forward.get(key);
    }

    public Player.Direction get(Key key)
    {
        return get(InputKey.fromKey(key));
    }

    public InputKey get(Player.Direction direction)
    {
        return _backward.get(direction);
    }

    public Iterator<Map.Entry<InputKey, Player.Direction>> iterator()
    {
        return forward().iterator();
    }

    public Iterable<Map.Entry<InputKey, Player.Direction>> forward()
    {
        return _forward.entrySet();
    }

    public Iterable<Map.Entry<Player.Direction, InputKey>> backward()
    {
        return _backward.entrySet();
    }

    public Iterable<InputKey> keys()
    {
        return _forward.keySet();
    }

    public Iterable<Player.Direction> directions()
    {
        return _backward.keySet();
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
                _backward.get(Player.Direction.UP),
                "UP"
        ));

        builder.append(String.format(
                "%s: %s, ",
                _backward.get(Player.Direction.DOWN),
                "DOWN"
        ));

        builder.append(String.format(
                "%s: %s, ",
                _backward.get(Player.Direction.LEFT),
                "LEFT"
        ));

        builder.append(String.format(
                "%s: %s",
                _backward.get(Player.Direction.RIGHT),
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
                _backward.get(Player.Direction.UP),
                'U'
        ));

        builder.append(String.format(
                "%s: %c, ",
                _backward.get(Player.Direction.DOWN),
                'D'
        ));

        builder.append(String.format(
                "%s: %c, ",
                _backward.get(Player.Direction.LEFT),
                'L'
        ));

        builder.append(String.format(
                "%s: %c",
                _backward.get(Player.Direction.RIGHT),
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

    private HashMap<InputKey, Player.Direction> _forward;
    private HashMap<Player.Direction, InputKey> _backward;
}
