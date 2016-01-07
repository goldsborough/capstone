package capstone.element;

import capstone.data.Representation;
import capstone.utility.Delta;
import capstone.utility.Point;
import capstone.utility.Region;

import java.util.Random;
import java.util.Set;

/**
 * A sort of DynamicObstacle that generates its next point randomly. It
 * is not as exciting nor good as the IntelligentObstacle, so please do
 * not use it. It also has the overhead of heaving to compute whether
 * the level's region is smaller than the page's region because the randomness
 * is obviously not bounded by walls. That makes the interface of the Page
 * class more difficult because its update() method would require knowledge
 * of the Level's size and so on. Just don't use it.
 *
 */
@Deprecated public class RandomizedObstacle extends DynamicObstacle
{
    /**
     *
     * Constructs a new RandomizedObstacle.
     *
     * @param point The point at which to construct the RandomizedObstacle.
     *
     * @param representation The Representation for the RandomizedObstacle.
     */
    public RandomizedObstacle(Point point, Representation representation)
    {
        super(point, representation);
    }

    /**
     *
     * Generates random points until one is valid. Then returns
     * the distance to that point as the valid delta.
     *
     * @param region The region the point of the dynamic-obstacle
     *               must be in after the delta would be applied.
     *
     * @param taken  The set of points the point of the dynamic-obstacle
     *               must not be on after the delta would be applied.
     *
     * @return The next valid Delta.
     */
    @Override protected Delta _next(Region region, Set<Point> taken)
    {
        Point point;

        do point = _generate(region);

        while (taken.contains(point));

        return _point.distanceTo(point);
    }

    /**
     *
     * Geneates a new random point within a region.
     *
     * @param region The region within which the Point should be.
     *
     * @return The generated point.
     */
    private static Point  _generate(Region region)
    {
        int x = _random(region.southWest().x(), region.northEast().x());

        int y = _random(region.northEast().y(), region.southWest().y());

        return new Point(x, y);
    }

    /**
     *
     * Generates a random value with a range of a region.
     *
     * @param first The lower bound.
     *
     * @param last The upper bound.
     *
     * @return The generated value.
     */
    private static int _random(int first, int last)
    {
        // + 1 because a region is inclusive
        return _generator.nextInt(1 + last - first) + first;
    }

    private static Random _generator = new Random();
}
