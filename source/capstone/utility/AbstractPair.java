package capstone.utility;

/**
 * Created by petergoldsborough on 01/03/16.
 */

// abstract: make class that's descriptive of members (e.g. 'x')
public abstract class AbstractPair<T extends Comparable<T>, U extends Comparable<U>>
{
    protected AbstractPair() { }

    public AbstractPair(T first, U second)
    {
        this.first(first);
        this.second(second);
    }

    public AbstractPair(AbstractPair<T, U> other)
    {
        assert(other != null);

        this.first(other.first());
        this.second(other.second());
    }

    protected void first(T first)
    {
        _first = first;

        // Invalidated
        _hashCode = null;
    }

    protected T first()
    {
        return _first;
    }

    protected void second(U second)
    {
        _second = second;

        // Invalidated
        _hashCode = null;
    }

    protected U second()
    {
        return _second;
    }

    public String toString()
    {
        return "(" + _first + ", " + _second + ")";
    }

    public String toStringPlain()
    {
        return String.format("%1$s,%2$s", _first, _second);
    }

    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof AbstractPair)) return false;

        if (object == this) return true;

        AbstractPair other = (AbstractPair) object;

        return this._first.equals(other._first)   &&
               this._second.equals(other._second);
    }

    @Override public int hashCode()
    {
        if (_hashCode == null)
        {
            assert(_first != null);
            assert(_second != null);

            _hashCode = _first.hashCode() ^ (_second.hashCode() << 1);
        }

        return _hashCode;
    }

    protected int compareTo(AbstractPair<T, U> other)
    {
        assert(other != null);

        if (other == this) return 0;

        int result = this._first.compareTo(other._first);

        if (result != 0) return result;

        return this._second.compareTo(other._second);
    }

    // So we have compareTo implemented
    protected T _first;
    protected U _second;

    protected Integer _hashCode;
}
