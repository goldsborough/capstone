package capstone.utility;

public class Delta implements Comparable<Delta>
{
    public static Delta Up()
    {
        return new Delta(0, -1);
    }

    public static Delta Down()
    {
        return new Delta(0, +1);
    }

    public static Delta Left()
    {
        return new Delta(-1, 0);
    }

    public static Delta Right()
    {
        return new Delta(+1, 0);
    }

    public Delta(int x, int y)
    {
        this.x(x);
        this.y(y);
    }

    public void x(int x)
    {
        _x = x;

        _euclidian = null;
        _manhattan = null;
    }

    public int x()
    {
        return _x;
    }

    public void y(int y)
    {
        _y = y;

        _euclidian = null;
        _manhattan = null;
    }

    public int y()
    {
        return _y;
    }

    public double euclidian()
    {
        if (_euclidian == null)
        {
            _euclidian = Math.sqrt(_x*_x + _y*_y);
        }

        return _euclidian;
    }

    public int manhattan()
    {
        if (_manhattan == null) _manhattan = _x + _y;

        return _manhattan;
    }

    public Delta invert()
    {
        return new Delta(-_x, -_y);
    }

    @Override public int compareTo(Delta other)
    {
        assert(other != null);

        if (other == this) return 0;

        // Make sure it's not null
        manhattan();

        return _manhattan.compareTo(other.manhattan());
    }

    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof Delta)) return false;

        if (object == this) return true;

        Delta other = (Delta) object;

        return this._x.equals(other._x) && this._y.equals(other._y);
    }

    @Override public int hashCode()
    {
        return _x ^ (_y << 1);
    }

    private Integer _x;
    private Integer _y;

    private Integer _manhattan;
    private Double _euclidian;
}

