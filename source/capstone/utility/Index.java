package capstone.utility;

/**
 * A class modelling a two-dimensional index.
 */
public class Index
        extends AbstractPair<Integer, Integer>
        implements Comparable<Index>
{
    /**
     *
     * Creates an new Index from the given column and row.
     *
     * @param column The column of the Index.
     *
     * @param row The row of the Index.
     */
    public Index(int column, int row)
    {
        super(column, row);
    }

    /**
     *
     * Copy-Constructor.
     *
     * @param other The other Index to copy this one from.
     */
    public Index(Index other)
    {
        super(other);
    }

    /**
     *
     * The Index above must be valid.
     *
     * @return The Index above this one.
     */
    public Index above()
    {
        assert(row() > 0);

        return new Index(column(), row() - 1);
    }

    /**
     *
     * The Index below must be valid.
     *
     * @return The Index below this one.
     */
    public Index below()
    {
        return new Index(column(), row() + 1);
    }

    /**
     *
     * The Index to the left must be valid.
     *
     * @return The Index to the left this one.
     */
    public Index left()
    {
        assert(column() > 0);

        return new Index(column() - 1, row());
    }

    /**
     *
     * The Index to the right must be valid.
     *
     * @return The Index to the right this one.
     */
    public Index right()
    {
        return new Index(column() + 1, row());
    }

    /**
     * @return The column of the Index.
     */
    public int column()
    {
        return super.first();
    }

    /**
     *
     * Sets the column of the Index.
     *
     * @param column The new column for the Index.
     */
    public void column(int column)
    {
        assert(column >= 0);

        super.first(column);
    }

    /**
     * @return The row of the Index.
     */
    public int row()
    {
        return super.second();
    }

    /**
     *
     * Sets the row of the Index.
     *
     * @param row The new row for the Index.
     */
    public void row(int row)
    {
        assert(row >= 0);

        super.second(row);
    }

    /**
     * 
     * Compares the Index to another Index first by column, then by row.
     * 
     * @param other The Index to compare this one to.
     *
     * @return +1 if the column of this Index is greater than that of the other
     *         Index, -1 if it is less. If it is equal, the result of the same
     *         comparison for the row is returned. If both are equal, 0 the
     *         result is zero.
     */
    @Override public int compareTo(Index other)
    {
        return super.compareTo(other);
    }
}
