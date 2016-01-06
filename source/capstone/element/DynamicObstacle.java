package capstone.element;

import capstone.data.Representation;
import capstone.utility.Delta;
import capstone.utility.Point;
import capstone.utility.Region;

import java.util.Random;
import java.util.Set;

public abstract class DynamicObstacle extends Element
{
    public static DynamicObstacle Random(Point point, Representation representation)
    {
        switch (_random.nextInt(3))
        {
            case 0: return new PatternObstacle   (point, representation);
            case 1: return new RandomizedObstacle(point, representation);
            case 2: return new SequentialObstacle(point, representation);
        }

        throw new AssertionError();
    }

    public DynamicObstacle (Point point, Representation representation)
    {
        super(Kind.DYNAMIC_OBSTACLE, point, representation);
    }

    public Point update(Region region, Set<Point> taken)
    {
        return _point = peekPoint(region, taken);
    }

    public Delta peekDelta(Region region, Set<Point> taken)
    {
        if (taken.size() == region.area()) return Delta.Stay();

        return _next(region, taken);
    }

    public Point peekPoint(Region region, Set<Point> taken)
    {
        return new Point(_point).move(peekDelta(region, taken));
    }

    protected abstract Delta _next(Region region, Set<Point> taken);

    protected boolean _valid(Delta delta, Region region, Set<Point> taken)
    {
        if (_point.wouldGoOutside(delta, region)) return false;

        Point next = new Point(_point).move(delta);

        return ! taken.contains(next);
    }

    private static Random _random = new Random();
}
