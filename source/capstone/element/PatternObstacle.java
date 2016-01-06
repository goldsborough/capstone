package capstone.element;

import capstone.data.Representation;
import capstone.utility.Delta;
import capstone.utility.Pattern;
import capstone.utility.Point;
import capstone.utility.Region;

import java.util.Random;
import java.util.Set;

/**
 * Created by petergoldsborough on 01/05/16.
 */
public class PatternObstacle extends DynamicObstacle
{
    public PatternObstacle(Point point, Representation representation)
    {
        super(point, representation);

        _pattern = _randomPattern();
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

    @Override protected Delta _next(Region region, Set<Point> taken)
    {
        // Ensure we only do one loop
        for (int i = 0; i < _pattern.length(); )
        {
            // Check if the next delta would be valid
            if (_valid(_pattern.peek(), region, taken)) break;

            else _pattern.skip();

            // If we've done a loop, just stay for this turn
            if (++i >= _pattern.length()) return Delta.Stay();
        }

        // Extract, then skip
        Delta next = _pattern.peek();

        _pattern.skip();

        return next;
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
