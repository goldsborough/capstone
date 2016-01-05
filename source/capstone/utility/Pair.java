package capstone.utility;

/**
 * Created by petergoldsborough on 01/03/16.
 */
public final class Pair
        <T extends Comparable<T>, U extends Comparable<U>>
        extends AbstractPair<T, U>
        implements Comparable<Pair<T, U>>
{
    public Pair(T first, U second)
    {
        super(first, second);
    }

    public Pair(Pair<T, U> other)
    {
        super(other);
    }

    public T first()
    {
        return super.first();
    }

    public void first(T first)
    {
        super.first(first);
    }

    public U second()
    {
        return super.second();
    }

    public void second(U second)
    {
        super.second(second);
    }

    @Override public int compareTo(Pair<T, U> other)
    {
        return super.compareTo(other);
    }
}
