package capstone.element;

import capstone.utility.Pattern;
import capstone.utility.Point;
import capstone.data.Representation;

import java.util.Random;

public class DynamicObstacle extends Element
{
    public DynamicObstacle (Point point, Representation representation)
    {
        super(Kind.DYNAMIC_OBSTACLE, point, representation);

        _pattern = _randomPattern();
    }

    public void update()
    {
        _pattern.next(_point);
    }

    public void update(int rightBoundary, int bottomBoundary)
    {
        _pattern.safeNext(_point, rightBoundary, bottomBoundary);
    }

    public Pattern pattern()
    {
        return _pattern;
    }

    public void changePattern()
    {
        Pattern old = _pattern;

        do _pattern = _randomPattern();

        while (_pattern == old);
    }

    private Pattern _randomPattern()
    {
        Pattern pattern = _patternPool[_random.nextInt(_patternPool.length)];

        assert(pattern != null);

        return pattern;
    }

    private static Pattern[] _patternPool = {
        new Pattern("l2rl"),
        new Pattern("2r2l"),
        new Pattern("3r3l"),
        new Pattern("4r4l"),
        new Pattern("5r5l"),
        new Pattern("urdruldl"),
        new Pattern("(+1,-1)(+1,+1)(-1,-1)(-1,+1)"), // ^
        new Pattern("(+1,-1)(+1,+1)(-1,+1)(-1,-1)"), // diamond
        new Pattern("(+1,-1)(+1,+1)(+1,-1)(+1,+1)(-1,-1)(-1,+1)(-1,-1)(-1,+1)") // ^^
    };

    private static Random _random = new Random();

    private Pattern _pattern;
}
