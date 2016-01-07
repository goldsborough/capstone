package capstone.element;

import capstone.data.Representation;
import capstone.utility.Delta;
import capstone.utility.Point;
import capstone.utility.Region;

import java.util.Random;
import java.util.Set;

/**
 * Abstract class for all dynamic-obstacles, i.e. all obstacles that move.
 *
 * It defines all operations a DynamicObstacle needs on the outside. Minimally,
 * a concrete subclass of this class need only implement the _next method to
 * return the next safe delta according to its dynamic motion.
 */
public abstract class DynamicObstacle extends Element
{
    /**
     *
     * Factory-method to create a random dynamic-obstacle concrete-object.
     *
     * All dynamic-obstacle types except IntelligentObstacle
     * are deprecated so basically don't use this method.
     *
     * @param point The point to construct the dynamic-obstacle at.
     *
     * @param representation The representation to construct
     *                       the dynamic-obstacle with.
     *
     * @return An instance of one of the subclasses of DynamicObstacle.
     *         Which subclass it is is random.
     */
    @Deprecated public static DynamicObstacle
    Random(Point point, Representation representation)
    {
        switch (_random.nextInt(3))
        {
            case 0: return new PatternObstacle   (point, representation);
            case 1: return new RandomizedObstacle(point, representation);
            case 2: return new IntelligentObstacle(point, representation);
        }

        throw new AssertionError();
    }

    /**
     *
     * Constructs a new dynamic-obstacle from a Point and an Representation.
     *
     * So subclasses don't have to specify their kind anymore.
     *
     * @param point The point of the dynamic-obstacle.
     *
     * @param representation The representation of the dynamic-obstacle.
     */
    public DynamicObstacle(Point point, Representation representation)
    {
        super(Kind.DYNAMIC_OBSTACLE, point, representation);
    }

    /**
     *
     * Updates the point in a safe way.
     *
     * Safety here means that any update to the dynamic-obstacle's
     * point will result in the point being:
     *
     * 1. Within the given region.
     * 2. Not on any point included in the given set of taken points.
     *
     * What that update means depends on the concrete class. The
     * dynamic-obstacle could move randomly, according to some pattern etc.
     *
     * @param region The region the point of the dynamic-obstacle
     *               must be in after updating.
     *
     * @param taken The set of points the point of the dynamic-obstacle
     *              must not be on after updating.
     *
     * @return A valid point fitting both constraints.
     */
    public Point update(Region region, Set<Point> taken)
    {
        return _point = peekPoint(region, taken);
    }

    /**
     *
     * Returns the next safe Delta, i.e. by what Delta the point
     * of the dynamic-obstacle will move when calling update().
     *
     * @param region The region the point of the dynamic-obstacle
     *               must be in after the delta would be applied.
     *
     * @param taken The set of points the point of the dynamic-obstacle
     *              must not be on after the delta would be applied.
     *
     * @return A valid delta fitting both constraints.
     */
    public Delta peekDelta(Region region, Set<Point> taken)
    {
        // Check if there can even be a valid delta!
        if (taken.size() == region.area())
        {
            // Badness
            assert(! taken.contains(_point));

            return Delta.Stay();
        }

        return _next(region, taken);
    }

    /**
     *
     * Returns the next safe point, i.e. the point the
     * dynamic-obstacle's point will be when calling update.
     *
     * @param region The region the point returned must be in.
     *
     * @param taken The set of points the point returned must not be on.
     *
     * @return A valid point fitting both constraints.
     */
    public Point peekPoint(Region region, Set<Point> taken)
    {
        return new Point(_point).move(peekDelta(region, taken));
    }

    /**
     *
     * The method concrete subclasses must implement to yield the
     * next Delta according to their dynamic motion.
     *
     * @param region The region the point of the dynamic-obstacle
     *               must be in after the delta would be applied.
     *
     * @param taken The set of points the point of the dynamic-obstacle
     *              must not be on after the delta would be applied.
     *
     * @return A valid delta fitting both constraints.
     */
    protected abstract Delta _next(Region region, Set<Point> taken);

    /**
     *
     * A verification method subclasses can use to check if a delta, applied
     * to the current point, would be valid.
     *
     * @param delta The delta to test.
     *
     * @param region The region the point of the dynamic-obstacle
     *               must be in after the delta would be applied.
     *
     * @param taken The set of points the point of the dynamic-obstacle
     *              must not be on after the delta would be applied.
     *
     * @return A valid delta fitting both constraints.
     */
    protected boolean _valid(Delta delta, Region region, Set<Point> taken)
    {
        if (_point.wouldGoOutside(delta, region)) return false;

        Point next = new Point(_point).move(delta);

        return ! taken.contains(next);
    }

    private static Random _random = new Random();
}
