package capstone.utility;

/**
 * A basic pair class like std::pair in C++ that encapsulates
 * two objects of same or different type. It is an abstract class because
 * such a pair lacks expressiveness, first() and second() are not descriptive
 * names and should never be used for the concrete situation. For example,
 * an index should have a row or a column, a point should have an x and a y
 * and an area should have a width and a height and an expressive program
 * should ensure those those attributes are accessible over those names,
 * not generic first() and second() attributes. At the same time, there
 * is a lot of boiler-plate code associated with a pair, such as
 * equality checking, hash-code computation/caching, assignment, assertions,
 * comparisons etc. that this class can take care of on higher level in the
 * hierarchy. A class overriding AbstractPair should minimally define a
 * constructor taking its two attributes as arguments, a copy-constructor and
 * setters and getters with the descriptive names of the attributes. I.e. an
 * expressive utility class encapsulating two items can be created with 6
 * small methods.
 */
public abstract class AbstractPair<T extends Comparable<T>, U extends Comparable<U>>
{
    /**
     *
     * Constructs the pair from these two arguments.
     *
     * @param first The argument for the first attribute.
     *
     * @param second The argument for the second attribute.
     */
    public AbstractPair(T first, U second)
    {
        this.first(first);
        this.second(second);
    }

    /**
     *
     * Copy-constructor.
     *
     * @param other Another object of the same type.
     */
    public AbstractPair(AbstractPair<T, U> other)
    {
        assert(other != null);

        this.first(other.first());
        this.second(other.second());
    }

    /**
     * Default constructor, required in some cases.
     */
    protected AbstractPair() { }

    /**
     * @return The first attribute.
     */
    protected T first()
    {
        return _first;
    }

    /**
     *
     * Sets the first attribute.
     *
     * @param first A new value for the first attribute.
     */
    protected void first(T first)
    {
        _first = first;

        // Invalidated
        _hashCode = null;
    }

    /**
     * @return The second attribute.
     */
    protected U second()
    {
        return _second;
    }

    /**
     *
     * Sets the second attribute.
     *
     * @param second A new value for the second attribute.
     */
    protected void second(U second)
    {
        _second = second;

        // Invalidated
        _hashCode = null;
    }

    /**
     *
     * Returns a string representation of the pair.
     *
     * @return "(first, second)"
     */
    public String toString()
    {
        return "(" + _first + ", " + _second + ")";
    }

    /**
     *
     * A plain string representation of the pair, e.g. for serialization.
     *
     * @return "first,second"
     */
    public String toStringPlain()
    {
        return String.format("%1$s,%2$s", _first, _second);
    }

    /**
     *
     * Checks equality to another object.
     *
     * @param object The object to check equality for.
     *
     * @return True if the object is an instance of the same
     *         concrete pair class and if the two attributes
     *         of the pairs are equal to each other (calls .equals
     *         on those methods.)
     */
    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        // Don't want two different types of pairs to
        // equal each other, so don't use instanceof
        if (this.getClass() != object.getClass()) return false;

        if (object == this) return true;

        AbstractPair other = (AbstractPair) object;

        return this._first.equals(other._first)   &&
               this._second.equals(other._second);
    }

    /**
     * @return A hashcode for the pair.
     *         Both attributes participate in the value.
     */
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

    /**
     *
     * A possible implementation of compareTo comparing only the two
     * attributes, that can but must not necessarily be used by subclasses.
     * Note that the AbstractPair class is itself not comparable. A subclass
     * may simply override the method from the Comparable interface by
     * calling this method.
     *
     * @param other The other instance of this class to compare to.
     *
     * @return +1 if this value is "greater" (whatever that means for
     *         the concrete pair) than the other, -1 if it is less and
     *         0 if it is equal.
     */
    protected int compareTo(AbstractPair<T, U> other)
    {
        assert(other != null);

        if (other == this) return 0;

        int result = this._first.compareTo(other._first);

        if (result != 0) return result;

        return this._second.compareTo(other._second);
    }

    protected T _first;
    protected U _second;

    protected Integer _hashCode;
}
