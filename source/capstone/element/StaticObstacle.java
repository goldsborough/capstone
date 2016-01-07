package capstone.element;

import capstone.data.Representation;
import capstone.utility.Point;

/**
 * A static-obstacle class. Gets the concept into the type-system. A
 * static-obstacle is basically a wall with different semantics on collision.
 */
public class StaticObstacle extends Element
{
    /**
     *
     * Constructs a new static-obstacle from a Point and a Representation.
     *
     * @param point The point of the static-obstacle.
     *
     * @param representation The representation of the static-obstacle.
     */
    public StaticObstacle (Point point, Representation representation)
    {
        super(Kind.STATIC_OBSTACLE, point, representation);
    }
}
