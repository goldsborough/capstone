package capstone.element;

import capstone.data.Representation;
import capstone.utility.Delta;
import capstone.utility.Pattern;
import capstone.utility.Point;
import capstone.utility.Region;

import java.util.Random;
import java.util.Set;

/**
 * A dynamic obstacle following a finite-pattern similar
 * to a finite-state-machine. Not really so good, especially in
 * dense levels it's so common that a pattern obstacle will have
 * such a pattern that it's just awkwardly stuck. So deprecated
 * in favor of the IntelligentObstacle.
 */
@Deprecated public class PatternObstacle extends DynamicObstacle
{
    /**
     *
     * Constructs a PatternObstacle with a point and a representation.
     *
     * @param point The point of the pattern obstacle.
     *
     * @param representation The representation of the obstacle.
     */
    public PatternObstacle(Point point, Representation representation)
    {
        super(point, representation);

        _pattern = _randomPattern();
    }

    /**
     * @return The current pattern of the obstacle.
     */
    public Pattern pattern()
    {
        return _pattern;
    }

    /**
     * Changes the current pattern of the obstacle to another random one.
     */
    public void changePattern()
    {
        Pattern old = _pattern;

        do _pattern = _randomPattern();

        while (_pattern == old); // Ah, damn randomness.
    }

    /**
     *
     * Gets the next safe Delta of the pattern. Safe in the sense
     * that the next point of the obstacle (currentPoint + Delta)
     * will not be outside the region and will not hit one of the
     * taken points.
     *
     * @param region The safe region in which the obstacle must stay.
     *
     * @param taken The set of taken points.
     *
     * @return The safe Delta by which the point of the PatternObstacle
     *         can definitely be moved.
     */
    @Override protected Delta _next(Region region, Set<Point> taken)
    {
        // Ensure we only do one loop through the pattern
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

    /**
     * @return A random pattern from the pattern pool.
     */
    private Pattern _randomPattern()
    {
        int randomIndex = _random.nextInt(_patternPool.length);

        Pattern pattern = _patternPool[randomIndex];

        assert(pattern != null);

        return pattern;
    }

    /**
     * The pool of patterns from which the PatternObstacle can choose from.
     *
     * Note that they use the shorthand mnemonics/strings that a Pattern can be
     * constructed with, because life is too short to write `new Delta(+1, -1)`
     * every god damn time.
     */
    private static Pattern[] _patternPool = {
            new Pattern("l2rl"), // left, 2x right, left
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
