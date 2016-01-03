package capstone.utility;

import java.util.regex.*;

/**
 * Created by petergoldsborough on 01/03/16.
 */

// abstract: make class that's descriptive of members (e.g. 'x')
public abstract class Pair<T extends Comparable<T>>
{
    protected Pair() { }

    public Pair(T first, T second)
    {
        this.first(first);
        this.second(second);
    }

    public Pair(Pair<T> other)
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

    protected void second(T second)
    {
        _second = second;

        // Invalidated
        _hashCode = null;
    }

    protected T second()
    {
        return _second;
    }

    public String toString()
    {
        return "(" + _first + ", " + _second + ")";
    }

    public String toStringPlain()
    {
        return _first + ", " + _second;
    }

    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof Pair)) return false;

        if (object == this) return true;

        Pair other = (Pair) object;

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

    protected int compareTo(Pair<T> other)
    {
        assert(other != null);

        if (other == this) return 0;

        int result = this._first.compareTo(other._first);

        if (result != 0) return result;

        return this._second.compareTo(other._second);
    }

    // So we have compareTo implemented
    protected T _first;
    protected T _second;

    protected Integer _hashCode;
}
