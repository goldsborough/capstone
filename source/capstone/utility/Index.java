package capstone.utility;

/**
 * Created by petergoldsborough on 01/03/16.
 */
public class Index extends Pair<Integer> implements Comparable<Index>
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

    public int column()
    {
        return super.first();
    }

    public void column(int column)
    {
        assert(column >= 0);

        super.second(column);
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
