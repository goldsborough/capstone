package capstone;

public final class Point implements Comparable<Point>
{
    public Point(int x, int y)
    {
        _x = x;
        _y = y;
    }

    public void x(int x)
    {
        _x = x;
    }

    public Integer x()
    {
        return _x;
    }

    public void y(int y)
    {
        _y = y;
    }

    public Integer y()
    {
        return _y;
    }

    @Override public int compareTo(Point other) throws NullPointerException
    {
        if (other == null) throw new NullPointerException();

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

    private Integer _x;
    private Integer _y;
}
