package capstone.element;

import capstone.data.Representation;
import capstone.utility.Delta;
import capstone.utility.Point;
import capstone.utility.Region;

import java.util.Random;
import java.util.Set;

/**
 * Created by petergoldsborough on 01/05/16.
 */
@Deprecated public class RandomizedObstacle extends DynamicObstacle
{
    public RandomizedObstacle(Point point, Representation representation)
    {
        super(point, representation);
    }

    @Override protected Delta _next(Region region, Set<Point> taken)
    {
        Point point;

        do point = _generate(region);

        while (taken.contains(point));

        return _point.distanceTo(point);
    }

    private Point _generate(Region region)
    {
        int x = _random(region.southWest().x(), region.northEast().x());

        int y = _random(region.northEast().y(), region.southWest().y());

        return new Point(x, y);
    }

    private int _random(int first, int last)
    {
        // + 1 because a region is inclusive
        return _generator.nextInt(1 + last - first) + first;
    }

    private static Random _generator = new Random();
}
