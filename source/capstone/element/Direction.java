package capstone.element;

import capstone.utility.Delta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by petergoldsborough on 01/06/16.
 */
public enum Direction
{
    UP(Delta.Up()),
    DOWN(Delta.Down()),
    LEFT(Delta.Left()),
    RIGHT(Delta.Right()),
    STAY(Delta.Stay());

    public static Collection<Direction> motion()
    {
        return _motion;
    }

    public Direction opposite()
    {
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

    public boolean isVertical()
    {
        return this == UP || this == DOWN;
    }

    public boolean isHorizontal()
    {
        return this == LEFT || this == RIGHT;
    }

    public Delta delta()
    {
        return _delta;
    }

    private Direction(Delta delta)
    {
        assert(delta != null);

        _delta = delta;
    }

    private static Collection<Direction> _motion = new ArrayList<Direction>(){{
        add(UP);
        add(DOWN);
        add(LEFT);
        add(RIGHT);
    }};

    private Delta _delta;
    private Direction _opposite;
}
