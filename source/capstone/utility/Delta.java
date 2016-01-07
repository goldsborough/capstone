package capstone.utility;

import capstone.element.Direction;

/**
 * A class to model a Delta to a point.
 */
public class Delta
        extends AbstractPair<Integer, Integer>
        implements Comparable<Delta>
{
    /**
     *
     * Factory-function to create an upward Delta.
     *
     * @return A Delta for one position above.
     */
    public static Delta Up()
    {
        return new Delta(0, -1);
    }

    /**
     *
     * Factory-function to create a downward Delta.
     *
     * @return A Delta for one position below.
     */
    public static Delta Down()
    {
        return new Delta(0, +1);
    }

    /**
     *
     * Factory-function to create a leftward Delta.
     *
     * @return A Delta for one position to the left.
     */
    public static Delta Left()
    {
        return new Delta(-1, 0);
    }

    /**
     *
     * Factory-function to create a rightward Delta.
     *
     * @return A Delta for one position to the right.
     */
    public static Delta Right()
    {
        return new Delta(+1, 0);
    }

    /**
     *
     * Factory-function to create the Delta to stay,
     * i.e. no change in position.
     *
     * @return A Delta for staying on the same point.
     */
    public static Delta Stay()
    {
        return new Delta(0, 0);
    }

    /**
     *
     * Constructs a new Delta given the delta-x and delta-y.
     *
     * @param x The delta for the x-component.
     * @param y The delta for the y-component.
     */
    public Delta(int x, int y)
    {
        super(x, y);
    }

    /**
     *
     * Copy-Constructor.
     *
     * @param other The other Delta to copy this one from.
     */
    public Delta(Delta other)
    {
        super(other);
    }

    /**
     *
     * Constructs a Delta from a Direction.
     *
     * @param direction The direction to create this Delta with.
     */
    public Delta(Direction direction)
    {
        switch (direction)
        {
            case UP:    this.x( 0); this.y(-1); break;
            case DOWN:  this.x( 0); this.y(+1); break;
            case LEFT:  this.x(-1); this.y( 0); break;
            case RIGHT: this.x(+1); this.y( 0); break;
        }
    }

    /**
     * @return The x-component of the Delta.
     */
    public int x()
    {
        return super.first();
    }

    /**
     *
     * Sets the x-component of the Delta.
     *
     * @param x The new x-component for the Delta.
     */
    public void x(int x)
    {
        super.first(x);

        _invalidate();
    }

    /**
     * @return The y-component of the Delta.
     */
    public int y()
    {
        return super.second();
    }

    /**
     *
     * Sets the y-component of the Delta.
     *
     * @param y The new y-component for the Delta.
     */
    public void y(int y)
    {
        super.second(y);

        _invalidate();
    }

    /**
     * @return The Euclidian-distance of the delta, i.e. the root of the
     *         sum of the squares of the Delta's components.
     */
    public double euclidian()
    {
        // Compute lazily and cache
        if (_euclidian == null)
        {
            final int x = x(), y = y();

            _euclidian = Math.sqrt(x*x + y*y);
        }

        return _euclidian;
    }

    /**
     * @return The Manhattan-distance of the Delta,
     *         i.e. the sum of its components.
     */
    public int manhattan()
    {
        // Compute lazily and cache
        if (_manhattan == null)
        {
            _manhattan = Math.abs(x() + y());
        }

        return _manhattan;
    }

    /**
     * @return A new Delta that is the inverse of this Delta,
     *         i.e. with the components negated.
     */
    public Delta invert()
    {
        return new Delta(-x(), -y());
    }

    /**
     *
     * Compares two Deltas by their Manhattan-distance.
     *
     * Manhattan-distance because it's cheaper to compute.
     *
     * @param other The other Delta to compare this one with.
     *
     * @return +1 if the Manhattan-distance of this Delta is
     *         greater than that of the other Delta, -1 if it
     *         is less and else 0.
     */
    @Override public int compareTo(Delta other)
    {
        assert(other != null);

        if (other == this) return 0;

        // Make sure it's not null
        manhattan();

        return _manhattan.compareTo(other.manhattan());
    }

    /**
     * Invalidates the cached distances.
     */
    private void _invalidate()
    {
        _euclidian = null;
        _manhattan = null;
    }

    private Integer _manhattan;
    private Double _euclidian;
}

