package capstone.utility;

public class Delta extends AbstractPair<Integer, Integer> implements Comparable<Delta>
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
        super(x, y);
    }

    public Delta(Delta other)
    {
        super(other);
    }

    public int x()
    {
        return super.first();
    }

    public void x(int x)
    {
        super.first(x);

        _invalidate();
    }

    public int y()
    {
        return super.second();
    }

    public void y(int y)
    {
        super.second(y);

        _invalidate();
    }

    public double euclidian()
    {
        if (_euclidian == null)
        {
            final int x = x(), y = y();

            _euclidian = Math.sqrt(x*x + y*y);
        }

        return _euclidian;
    }

    public int manhattan()
    {
        if (_manhattan == null) _manhattan = x() + y();

        return _manhattan;
    }

    public Delta invert()
    {
        return new Delta(-x(), -y());
    }

    @Override public int compareTo(Delta other)
    {
        assert(other != null);

        if (other == this) return 0;

        // Make sure it's not null
        manhattan();

        return _manhattan.compareTo(other.manhattan());
    }

    private void _invalidate()
    {
        _euclidian = null;
        _manhattan = null;
    }

    private Integer _manhattan;
    private Double _euclidian;
}

