package capstone.utility;

public class Point implements Comparable<Point>
{
    public Point(int x, int y)
    {
        this.x(x);
        this.y(y);
    }

    public Point(Point other)
    {
        assert(other != null);

        this.x(other.x());
        this.y(other.y());
    }

    public Point above()
    {
        assert(_y > 0);

        return new Point(_x, _y - 1);
    }

    public Point below()
    {
        return new Point(_x, _y + 1);
    }

    public Point left()
    {
        assert(_x > 0);

        return new Point(_x - 1, _y);
    }

    public Point right()
    {
        return new Point(_x + 1, _y);
    }

    public void x(int x)
    {
        assert(x >= 0);

        _x = x;
    }

    public int x()
    {
        return _x;
    }

    public void y(int y)
    {
        assert(y >= 0);

        _y = y;
    }

    public int y()
    {
        return _y;
    }

    public Point move(int dx, int dy)
    {
        assert(_x + dx >= 0);
        assert(_y + dy >= 0);

        _x += dx;
        _y += dy;

        return this;
    }

    public Point move(Delta delta)
    {
        assert(delta != null);

        return move(delta.x(), delta.y());
    }

    @Override public int compareTo(Point other)
    {
        assert(other != null);

        if (other == this) return 0;

        int result = this._x.compareTo(other._x);

        if (result != 0) return result;

        return this._y.compareTo(other._y);
    }

    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof Point)) return false;

        if (object == this) return true;

        Point other = (Point) object;

        return this._x.equals(other._x) && this._y.equals(other._y);
    }

    @Override public int hashCode()
    {
        return _x ^ (_y << 1);
    }

    // So we have compareTo implemented
    private Integer _x;
    private Integer _y;
}
