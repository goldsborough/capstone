package capstone.utility;

import capstone.utility.Delta;
import capstone.utility.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;

/**
 * Created by petergoldsborough on 12/29/15.
 */
public class Pattern implements Iterable<Delta>
{
    public Pattern(String pattern)
    {
        ArrayList<Delta> temporary = new ArrayList<>();

        Matcher matcher = _regex.matcher(pattern);

        // Finds first match and asserts there is at least one
        assert(matcher.find());

        do _addDeltas(matcher, temporary, _parseDelta(matcher));

        while(matcher.find());

        _pattern = temporary.toArray(new Delta[temporary.size()]);
    }

    public Pattern(Delta... pattern)
    {
        assert(pattern.length > 0);

        _pattern = pattern;
        _index = 0;
    }

    public Pattern(Collection<Delta> pattern)
    {
        assert(! pattern.isEmpty());

        _pattern = (Delta[]) pattern.toArray();
        _index = 0;
    }

    public Point apply(Point point)
    {
        assert(_index >= 0);
        assert(_index < _pattern.length);

        point.move(_pattern[_index]);

        return point;
    }

    public Point next(Point point)
    {
        assert(_index >= 0);
        assert(_index < _pattern.length);

        apply(point);

        // No defensive coding!
        if (++_index == _pattern.length) _index = 0;

        return point;
    }

    public Point previous(Point point)
    {
        assert(_index >= 0);
        assert(_index < _pattern.length);

        apply(point);

        // No defensive coding!
        if (--_index == -1) _index = _pattern.length - 1;

        return point;
    }

    public Point safeApply(Point point, Region region)
    {
        assert(_index >= 0);
        assert(_index < _pattern.length);

        Delta delta = _pattern[_index];

        if (point.wouldGoOutside(delta, region)) return point;

        return point.move(_pattern[_index]);
    }

    public Point safeNext(Point point, Region region)
    {
        assert(_index >= 0);
        assert(_index < _pattern.length);

        safeApply(point, region);

        // No defensive coding!
        if (++_index == length()) _index = _pattern.length - 1;

        return point;
    }

    public Point safePrevious(Point point, Region region)
    {
        assert(_index >= 0);
        assert(_index < _pattern.length);

        safeApply(point, region);

        // No defensive coding!
        if (--_index == -1) _index = _pattern.length - 1;

        return point;
    }

    public boolean isAt(int index)
    {
        return _index == index;
    }

    public Delta at(int index)
    {
        assert(index >= 0);
        assert(index < _pattern.length);

        return _pattern[index];
    }

    public void jumpTo(int index)
    {
        assert(index >= 0);
        assert(index < _pattern.length);

        _index = index;
    }

    public void skip(int steps)
    {
        _index = (_index + steps) % _pattern.length;
    }

    public void skip()
    {
        skip(1);
    }

    public int position()
    {
        return _index;
    }

    public Delta peek()
    {
        return _pattern[_index];
    }

    public int length()
    {
        return _pattern.length;
    }

    public Iterator<Delta> iterator()
    {
        return Arrays.asList(_pattern).iterator();
    }

    private Delta _parseDelta(Matcher matcher)
    {
        if (matcher.group(2) != null)
        {
            return _parseCoordinates(matcher);
        }

        return _parseCharacter(matcher);
    }

    private Delta _parseCoordinates(Matcher matcher)
    {
        assert(matcher.group(2) != null);
        assert(matcher.group(3) != null);

        int dx = Integer.parseInt(matcher.group(2));
        int dy = Integer.parseInt(matcher.group(3));

        return new Delta(dx, dy);
    }

    private Delta _parseCharacter(Matcher matcher)
    {
        switch(matcher.group(4))
        {
            case "u": return Delta.Up();
            case "d": return Delta.Down();
            case "l": return Delta.Left();
            case "r": return Delta.Right();

            default: assert(false);
        }

        return null;
    }

    private void _addDeltas(Matcher matcher,
                            Collection<Delta> collection,
                            Delta delta)
    {
        int count;

        if (matcher.group("count") == null) count = 1;

        else count = Integer.parseInt(matcher.group("count"));

        for (int i = 0; i < count; ++i) collection.add(delta);
    }

    private static final java.util.regex.Pattern _regex = java.util.regex.Pattern.compile(
       "(?<count>\\d+)?\\s*(?:\\(\\s*(-?\\d+)\\s*[,;\\s]\\s*(-?\\d+)\\)|([lrud]))"
    );

    private final Delta[] _pattern;

    private int _index;
}
