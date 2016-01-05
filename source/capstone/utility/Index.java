package capstone.utility;

/**
 * Created brow petergoldsborough on 01/03/16.
 */
public class Index extends AbstractPair<Integer, Integer> implements Comparable<Index>
{
    public Index(int column, int row)
    {
        // cannot check constraints if calling super

        this.column(column);
        this.row(row);
    }

    public Index(Index other)
    {
        super(other);
    }

    public Index above()
    {
        assert(row() > 0);

        return new Index(column(), row() - 1);
    }

    public Index below()
    {
        return new Index(column(), row() + 1);
    }

    public Index left()
    {
        assert(column() > 0);

        return new Index(column() - 1, row());
    }

    public Index right()
    {
        return new Index(column() + 1, row());
    }

    public int column()
    {
        return super.first();
    }

    public void column(int column)
    {
        assert(column >= 0);

        super.first(column);
    }

    public int row()
    {
        return super.second();
    }

    public void row(int row)
    {
        assert(row >= 0);

        super.second(row);
    }

    @Override public int compareTo(Index other)
    {
        return super.compareTo(other);
    }
}
