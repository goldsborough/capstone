package capstone;

import java.util.HashMap;

/**
 * Created by petergoldsborough on 12/27/15.
 */
public class KeyMap extends HashMap<Key, Player.Direction>
{
    public static KeyMap Arrows()
    {
        return new KeyMap(
                new Key(Key.Kind.ArrowUp),
                new Key(Key.Kind.ArrowDown),
                new Key(Key.Kind.ArrowLeft),
                new Key(Key.Kind.ArrowRight)
        );
    }

    public static KeyMap F1234()
    {
        return new KeyMap(
                new Key(Key.Kind.F1),
                new Key(Key.Kind.F2),
                new Key(Key.Kind.F3),
                new Key(Key.Kind.F4)
        );
    }

    public static KeyMap WASD()
    {
        return new KeyMap(
                new Key('w'),
                new Key('a'),
                new Key('s'),
                new Key('d')
        );
    }

    public KeyMap() { }

    public KeyMap(Key up, Key down, Key left, Key right)
    {
        this.put(up, Player.Direction.UP);
        this.put(down, Player.Direction.DOWN);
        this.put(left, Player.Direction.LEFT);
        this.put(right, Player.Direction.RIGHT);
    }

    public void up(Key key)
    {
        super.put(key, Player.Direction.UP);
    }

    public void down(Key key)
    {
        super.put(key, Player.Direction.DOWN);
    }

    public void left(Key key)
    {
        super.put(key, Player.Direction.LEFT);
    }

    public void right(Key key)
    {
        super.put(key, Player.Direction.RIGHT);
    }
}
