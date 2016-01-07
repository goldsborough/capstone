package capstone.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;

/**
 * A Pattern is a finite set of deltas that a PatternObstacle moves by.
 */
public class Pattern implements Iterable<Delta>
{
    /**
     *
     * Constructs a pattern from one of the allowed pattern Strings.
     * Basically, instead of writing `new Delta(-1, 0)` for going left,
     * you can write "l". That's my kind of convenience. Alternatively,
     * if the manhattan distance is greater 1, you can also write the
     * coordinates directly, i.e. "(-1, 0)". The regex just looks for
     * two numbers separated by a one or more non-digit characters,
     * so you're free to format them however you wish.
     * Examples of allowed patterns:
     *
     * l = LEFT
     * r = RIGHT
     * u = UP
     * d = DOWN
     *
     * (x, y)
     * x y
     * x, y
     * [x, y]
     * (x y)
     * sdfasdflasfadsfdfa x asfjksdfjls y as;fkasirjlnafnrlf
     *
     * @param pattern The pattern string.
     */
    public Pattern(String pattern)
    {
        ArrayList<Delta> temporary = new ArrayList<>();

        Matcher matcher = _regex.matcher(pattern);

        // Finds first match and asserts there is at least one
        assert(matcher.find());

        // Find all the deltas
        do _addDeltas(matcher, temporary, _parseDelta(matcher));

        while(matcher.find());

        _pattern = temporary.toArray(new Delta[temporary.size()]);
    }

    /**
     *
     * Constructs a Pattern from an array of Deltas.
     *
     * @param pattern An array of deltas. Must be at least one.
     */
    public Pattern(Delta... pattern)
    {
        assert(pattern.length > 0);

        _pattern = pattern;
        _index = 0;
    }

    /**
     *
     * Constructs a Pattern from a collection of Deltas.
     *
     * @param pattern the collection of deltas. Must be non-empty.
     */
    public Pattern(Collection<Delta> pattern)
    {
        assert(! pattern.isEmpty());

        _pattern = (Delta[]) pattern.toArray();
        _index = 0;
    }


    /**
     *
     * Applies the current delta in the pattern to the given point,
     * without moving to the next Delta in the pattern.
     *
     * @param point The point to apply the Delta to.
     *
     * @return The resulting point (same one that is passed and modified).
     *
     * @see Pattern#safeApply
     *
     */
    public Point apply(Point point)
    {
        assert(_index >= 0);
        assert(_index < _pattern.length);

        point.move(_pattern[_index]);

        return point;
    }

    /**
     *
     * Applies the current delta in the pattern to the given point
     * and moves to the next Delta in the pattern after.
     *
     * @param point The point to apply the Delta to.
     *
     * @return The resulting point (same one that is passed and modified).
     *
     * @see Pattern#safeNext
     *
     */
    public Point next(Point point)
    {
        assert(_index >= 0);
        assert(_index < _pattern.length);

        apply(point);

        // Don't need to code defensively. Long live assertions.
        if (++_index == _pattern.length) _index = 0;

        return point;
    }

    /**
     *
     * Applies the current delta in the pattern to the given point
     * and moves to the previous Delta in the pattern after.
     *
     * @param point The point to apply the Delta to.
     *
     * @return The resulting point (same one that is passed and modified).
     *
     * @see Pattern#safePrevious
     *
     */
    public Point previous(Point point)
    {
        assert(_index >= 0);
        assert(_index < _pattern.length);

        apply(point);

        // Don't need to code defensively. Long live assertions.
        if (--_index == -1) _index = _pattern.length - 1;

        return point;
    }

    /**
     *
     * Performs apply() only if the resulting point
     * does not go outside the region.
     *
     * @param point The point to apply the Delta to, if it's safe.
     *
     * @param region The region the point should not trespass.
     *
     * @return The resulting point (same one that is passed and modified).
     *
     */
    public Point safeApply(Point point, Region region)
    {
        assert(_index >= 0);
        assert(_index < _pattern.length);

        Delta delta = _pattern[_index];

        if (point.wouldGoOutside(delta, region)) return point;

        return point.move(_pattern[_index]);
    }

    /**
     *
     * Performs next() only if the resulting point
     * does not go outside the region.
     *
     * @param point The point to apply the Delta to, if it's safe.
     *
     * @param region The region the point should not trespass.
     *
     * @return The resulting point (same one that is passed and modified).
     *
     */
    public Point safeNext(Point point, Region region)
    {
        assert(_index >= 0);
        assert(_index < _pattern.length);

        safeApply(point, region);

        // No defensive coding!
        if (++_index == length()) _index = _pattern.length - 1;

        return point;
    }

    /**
     * Performs previous() only if the resulting point
     * does not go outside the region.
     *
     * @param point The point to apply the Delta to, if it's safe.
     *
     * @param region The region the point should not trespass.
     *
     * @return The resulting point (same one that is passed and modified).
     */
    public Point safePrevious(Point point, Region region)
    {
        assert(_index >= 0);
        assert(_index < _pattern.length);

        safeApply(point, region);

        // No defensive coding!
        if (--_index == -1) _index = _pattern.length - 1;

        return point;
    }

    /**
     *
     * @param index The index to check
     *
     * @return True if the pattern is at that index, else false.
     *
     */
    public boolean isAt(int index)
    {
        return _index == index;
    }

    /**
     * @param index The index to check
     *
     * @return The delta at that index in the pattern.
     */

    public Delta at(int index)
    {
        assert(index >= 0);
        assert(index < _pattern.length);

        return _pattern[index];
    }

    /**
     *
     * Jumps to the index in the pattern.
     *
     * @param index The index to jump to.
     */
    public void jumpTo(int index)
    {
        assert(index >= 0);
        assert(index < _pattern.length);

        _index = index;
    }

    /**
     *
     * Skips the given number of steps in the pattern. The number of steps
     * can be negative and its absolute value greater than the pattern.
     * An absolute value greater than the pattern is like looping multiple
     * times backward or forward until the steps are zero.
     *
     * @param steps The number of steps to skip.
     */
    public void skip(int steps)
    {
        _index = (_index + steps) % _pattern.length;
    }

    /**
     * Skips one step forward in the pattern.
     */
    public void skip()
    {
        skip(1);
    }

    /**
     * @return The current index of the pattern.
     */
    public int index()
    {
        return _index;
    }

    /**
     *
     * Equivalent to at(index())
     *
     * @return Returns the current delta in the pattern.
     */
    public Delta peek()
    {
        return _pattern[_index];
    }

    /**
     * @return The length of the pattern (number of steps).
     */
    public int length()
    {
        return _pattern.length;
    }

    /**
     * @return An iterator over the Deltas in the pattern.
     */
    public Iterator<Delta> iterator()
    {
        return Arrays.asList(_pattern).iterator();
    }

    /**
     *
     * Handles matching one Delta in a pattern-string.
     *
     * @param matcher The Matcher object holding the last match.
     *
     * @return The resulting Delta.
     */
    private Delta _parseDelta(Matcher matcher)
    {
        if (matcher.group(2) != null)
        {
            return _parseCoordinates(matcher);
        }

        return _parseCharacter(matcher);
    }

    /**
     *
     * Parses a coordinate, i.e. pattern with an x and y component, as
     * opposed to the characters 'l','r,'u' and 'd' that are also valid patterns.
     *
     * @param matcher The Matcher object holding the last match.
     *
     * @return The resulting Delta.
     */
    private Delta _parseCoordinates(Matcher matcher)
    {
        assert(matcher.group(2) != null);
        assert(matcher.group(3) != null);

        int dx = Integer.parseInt(matcher.group(2));
        int dy = Integer.parseInt(matcher.group(3));

        return new Delta(dx, dy);
    }

    /**
     *
     * Parses a character, i.e. one of the characters 'l','r,'u' or 'd'.
     *
     * @param matcher The Matcher object holding the last match.
     *
     * @return The resulting Delta.
     */
    private Delta _parseCharacter(Matcher matcher)
    {
        switch(matcher.group(4))
        {
            case "u": return Delta.Up();
            case "d": return Delta.Down();
            case "l": return Delta.Left();
            case "r": return Delta.Right();
        }

        return null;
    }

    /**
     *
     * Handles one match in full delta pattern. Basically adds
     * the last-parsed Delta a certain number of times. That
     * number of times is either retrieved from the first group
     * of the regular expression (it's even named "count", wooh!),
     * or is 1 if that group is null.
     *
     * @param matcher The Matcher object holding the last match.
     *
     * @param collection The current collection of Deltas.
     *
     * @param delta The next Delta.
     */
    private void _addDeltas(Matcher matcher,
                            Collection<Delta> collection,
                            Delta delta)
    {
        int count;

        if (matcher.group("count") == null) count = 1;

        else count = Integer.parseInt(matcher.group("count"));

        for (int i = 0; i < count; ++i) collection.add(delta);
    }

    // Regex is love, Regex is life
    private static final java.util.regex.Pattern _regex = java.util.regex.Pattern.compile(
       "(?<count>\\d+)?\\s*(?:\\(\\s*(-?\\d+)\\s*[,;\\s]\\s*(-?\\d+)\\)|([lrud]))"
    );

    private final Delta[] _pattern;

    private int _index;
}
