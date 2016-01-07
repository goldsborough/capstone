package capstone.element;

import capstone.data.Representation;
import capstone.utility.Delta;
import capstone.utility.Point;
import capstone.utility.Region;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * The best DynamicObstacle concrete class and the only one in active use.
 * It is a non-deterministic DynamicObstacle that walks the labyrinth in an
 * intelligent manner with a certain behavior and randomization to it. It does
 * not, however, have any finite pattern as does the Pattern Obstacle.
 *
 * The behavior of an IntelligentObstacle is such that it will always attempt
 * to walk the Labyrinth in the same direction until it either collides with
 * another Element, trespasses the allowed Region, or in some cases when a
 * certain internal randomization causes a random change in direction. Only
 * when it collides does it try to go backwards.
 */
public class IntelligentObstacle extends DynamicObstacle
{
    /**
     *
     * Constructs the DynamicObstacle at the given
     * Point, with the given Representation.
     *
     * @param point The Point at which to construct the obstacle.
     *
     * @param representation The Representation for the obstacle.
     */
    public IntelligentObstacle(Point point, Representation representation)
    {
        super(point, representation);

        _back = Direction.STAY;
    }

    /**
     *
     * Returns the next valid Delta for the obstacle.
     *
     * @param region The region the point of the dynamic-obstacle
     *               must be in after the Delta would be applied.
     *
     * @param taken  The set of points the point of the dynamic-obstacle
     *               must not be on after the Delta would be applied.
     *
     * @return The next valid Delta.
     */
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

    /**
     *
     * Selected a direction by returning it's Delta
     * and setting the _back field to its opposite.
     *
     * @param direction The Direction to select.
     *
     * @return The Delta associated with the Direction.
     */
    private Delta _select(Direction direction)
    {
        Delta delta = direction.delta();

        _back = direction.opposite();

        return delta;
    }

    /**
     *
     * Returns an adjacent collection of Directions for the next movement
     * of the IntelligentObstacle. Adjacent Directions are defined as all
     * Directions of motion (not STAY) except the Direction itself.
     *
     * The opposite of the Direction passed is given precedence over the other
     * directions of motion with a certain probability. Note that this method
     * is always called with the backward direction of the previous move, so
     * this means the forward direction is given precedence with a certain
     * probability. Precedence means the forward direction will be put first
     * in the collection of Directions to try, with the other Directions
     * random-shuffled.
     *
     * @param direction The Direction to collect the adjacent Directions for.
     *
     * @return A Collection of Directions.
     */
    private Collection<Direction> _adjacent(Direction direction)
    {
        // Because we'll be inserting at the front
        List<Direction> adjacent = new LinkedList<>();

        // Collect all directions except this one
        for (Direction other : Direction.motion())
        {
            if (other != direction) adjacent.add(other);
        }

        // Randomize them
        Collections.shuffle(adjacent);

        if (direction == Direction.STAY) return adjacent;

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
