package capstone.element;

import capstone.data.Representation;
import capstone.utility.Delta;
import capstone.utility.Point;
import capstone.utility.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by petergoldsborough on 01/05/16.
 */
public class SequentialObstacle extends DynamicObstacle
{
    public SequentialObstacle(Point point, Representation representation)
    {
        super(point, representation);

        _back = Direction.STAY;
    }

    @Override protected Delta _next(Region region, Set<Point> taken)
    {
        // First try all directions other than that of _back
        for (Direction direction : _adjacent(_back))
        {
            if (_valid(direction.delta(), region, taken))
            {
                return _select(direction);
            }
        }

        // Then if no other direction valid, go back
        if (_valid(_back.delta(), region, taken))
        {
            return _select(_back);
        }

        // Lastly, can only stay (will try all
        // other positions again next time, though!)
        return _select(Direction.STAY);
    }

    private Delta _select(Direction direction)
    {
        Delta delta = direction.delta();

        _back = direction.opposite();

        return delta;
    }

    private Collection<Direction> _adjacent(Direction direction)
    {
        // Because we'll be inserting at the front
        List<Direction> adjacent = new LinkedList<>();

        // Collect all directions except this, stay and the opposite
        for (Direction other : Direction.motion())
        {
            if (other != direction &&
                other != direction.opposite())
            {
                adjacent.add(other);
            }
        }

        // Randomize them
        Collections.shuffle(adjacent);

        if (direction.opposite() == Direction.STAY) return adjacent;

        // Then give the opposite direction precedence. Because
        // we store _back, giving the opposite directon precedence
        // actually means giving the same direction precedence
        // in terms of how the object is moving. This can be
        // problematic for example in a square, with only one
        // exit, as the object will just move around the edge
        // of the square in certain cases. To prevent that, we
        // randomize everything. There is thus a 25% probability
        // that it will not move in the same direction first.
        if (_randomize(75)) adjacent.add(0, direction.opposite());

        else adjacent.add(direction.opposite());

        return adjacent;
    }

    private boolean _randomize(int probability)
    {
        assert(probability <= 100);
        assert(probability >= 0);

        return _random.nextInt(100) < probability;
    }

    private static Random _random  = new Random();

    private Direction _back;
}
