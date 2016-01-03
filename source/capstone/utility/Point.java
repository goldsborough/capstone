package capstone.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Point extends Pair<Integer> implements Comparable<Point>
{
    public Point(int first, int second)
    {
        super(first, second);
    }

    public Point(Point other)
    {
        super(other);
    }

    public Point(String string)
    {
        Matcher matcher = _pattern.matcher(string);

        assert(matcher.matches());

        this.x(Integer.parseInt(matcher.group(1)));
        this.y(Integer.parseInt(matcher.group(2)));
    }

    public Point above()
    {
        assert(y() > 0);

        return new Point(x(), y()- 1);
    }

    public Point below()
    {
        return new Point(x(), y() + 1);
    }

    public Point left()
    {
        assert(x() > 0);

        return new Point(x() - 1, y());
    }

    public Point right()
    {
        return new Point(x() + 1, y());
    }

    public void x(int x)
    {
        assert(x >= 0);

        super.first(x);
    }

    public int x()
    {
        return super.first();
    }

    public void y(int y)
    {
        assert(y >= 0);

        super.second(y);
    }

    public int y()
    {
        return super.second();
    }

    public Point move(int dx, int dy)
    {
        assert(_first + dx >= 0);
        assert(_second + dy >= 0);

        _first += dx;
        _first += dy;

        return this;
    }

    public Point move(Delta delta)
    {
        assert(delta != null);

        return move(delta.x(), delta.y());
    }

    @Override public int compareTo(Point other)
    {
        return super.compareTo(other);
    }

    private static final Pattern _pattern = Pattern.compile(
            "[^\\d]*(\\d+)[^\\d]*(\\d+)[^\\d]*"
    );
}
