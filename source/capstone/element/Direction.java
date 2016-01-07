package capstone.element;

import capstone.utility.Delta;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An enum class describing directions of motion.
 */
public enum Direction
{
    UP(Delta.Up()),
    DOWN(Delta.Down()),
    LEFT(Delta.Left()),
    RIGHT(Delta.Right()),
    STAY(Delta.Stay());

    /**
     * @return The directions representing motion, i.e. all except STAY.
     */
    public static Collection<Direction> motion()
    {
        return _motion;
    }

    /**
     * @return The direction opposite to that of the instance.
     *         The opposite of STAY is defined as STAY again.
     */
    public Direction opposite()
    {
        // Lazy caching
        if (_opposite == null)
        {
            switch (valueOf(name()))
            {
                case UP:    _opposite = DOWN;  break;
                case DOWN:  _opposite = UP;    break;
                case LEFT:  _opposite = RIGHT; break;
                case RIGHT: _opposite = LEFT;  break;
                case STAY:  _opposite = STAY;  break;
            }
        }

        return _opposite;
    }

    /**
     * @return True if the direction is up or down, else false.
     */
    public boolean isVertical()
    {
        return this == UP || this == DOWN;
    }

    /**
     * @return True if the direction is left or right, else false.
     */
    public boolean isHorizontal()
    {
        return this == LEFT || this == RIGHT;
    }

    /**
     * @return The delta associated with the direction.
     */
    public Delta delta()
    {
        return _delta;
    }

    /**
     *
     * Constructs the Direction and associates it with the given delta.
     *
     * @param delta The delta this Direction should be associated with.
     */
    private Direction(Delta delta)
    {
        assert(delta != null);

        _delta = delta;
    }

    /**
     * The directions representing motion.
     */
    private static Collection<Direction> _motion = new ArrayList<Direction>(){{
        add(UP);
        add(DOWN);
        add(LEFT);
        add(RIGHT);
    }};

    private Delta _delta;
    private Direction _opposite;
}
