package capstone.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Point in a coordinate system.
 *
 * Note that the y component is measured in rows,
 * i.e. it increases when going down.
 *
 * Also note that a point may never be
 * negative, either for x or for y components.
 */
public class Point
        extends AbstractPair<Integer, Integer>
        implements Comparable<Point>
{
    /**
     *
     * Constructs a Point from its two components.
     *
     * @param x The x-component of the point.
     * @param y The y-component of the point.
     */
    public Point(int x, int y)
    {
        super(x, y);

        assert(x  >= 0);
        assert(y >= 0);
    }

    /**
     *
     * Copy-constructor.
     *
     * @param other The other Point to construct this one from.
     */
    public Point(Point other)
    {
        super(other);
    }

    /**
     *
     * Convenience constructor to construct a Point
     * from a string representation. The string must contain
     * two numbers separated by one or more non-digit characters, e.g.:
     *
     * (1, 2)
     * 1, 2
     * 1 2
     * 1, 2
     * [1, 2]
     *
     * @param string The string representing the point.
     */
    public Point(String string)
    {
        Matcher matcher = _pattern.matcher(string);

        assert(matcher.matches());

        // No idea why I have to call it twice. None.
        matcher.matches();

        this.x(Integer.parseInt(matcher.group(1)));
        this.y(Integer.parseInt(matcher.group(2)));
    }

    /**
     *
     * Returns the point above this one.
     *
     * Throws if the point's y coordinate is not greater 0.
     *
     * @return The point above this one.
     */
    public Point above()
    {
        assert(y() > 0);

        return new Point(x(), y() - 1);
    }

    /**
     *
     * Returns the point below this one.
     *
     * @return The point below this one.
     */
    public Point below()
    {
        return new Point(x(), y() + 1);
    }

    /**
     *
     * Returns the point left of this one.
     *
     * Throws if the point's x coordinate is not greater 0.
     *
     * @return The point left of this one.
     */
    public Point left()
    {
        assert(x() > 0);

        return new Point(x() - 1, y());
    }

    /**
     *
     * Returns the point right of this one.
     *
     * @return The point right of this one.
     */
    public Point right()
    {
        return new Point(x() + 1, y());
    }

    /**
     * @return The Point's x-component.
     */
    public int x()
    {
        return super.first();
    }

    /**
     *
     * Sets the point's x-component.
     *
     * Must be non-negative.
     *
     * @param x The new value for the points' x-component.
     */
    public void x(int x)
    {
        assert(x >= 0);

        super.first(x);
    }

    /**
     * @return The Point's y-component.
     */
    public int y()
    {
        return super.second();
    }

    /**
     *
     * Sets the point's y-component.
     *
     * Must be non-negative.
     *
     * @param y The new value for the points' y-component.
     */
    public void y(int y)
    {
        assert(y >= 0);

        super.second(y);
    }

    /**
     *
     * Moves the point by the specified delta-x and delta-y.
     *
     * @param dx How much to move the x-component of the point by.
     *
     * @param dy How much to move the y-component of the point by.
     *
     * @return The resulting point (this).
     */
    public Point move(int dx, int dy)
    {
        assert(! wouldGoNegative(dx, dy));

        first(_first + dx);
        second(_second + dy);

        return this;
    }

    /**
     *
     * Moves the point by the specified Delta.
     *
     * @param delta The Delta to move the point by.
     *
     * @return The resulting point (this).
     */
    public Point move(Delta delta)
    {
        assert(delta != null);

        return move(delta.x(), delta.y());
    }

    /**
     *
     * Tests if the point would go negative if the delta were applied.
     *
     * This method exists to keep the constraint tight
     * that a point must never be negative.
     *
     * @param delta The Delta to test.
     *
     * @return True if applying the delta to the point would
     *         make it go negative on either or both components,
     *         else false if the point would remain valid.
     */
    public boolean wouldGoNegative(Delta delta)
    {
        assert(delta != null);

        return wouldGoNegative(delta.x(), delta.y());
    }

    /**
     *
     * Tests if the point would go negative if it was moved by
     * the specified delta-x and delta-y were applied.
     *
     * This method exists to keep the constraint tight
     * that a point must never be negative.
     *
     * @param dx The delta-x to test for.
     *
     * @param dy The delta-y to test for.
     *
     * @return True if applying the delta-x and delta-y to the point
     *         would make it go negative on either or both components,
     *         else false if the point would remain valid.
     */
    public boolean wouldGoNegative(int dx, int dy)
    {
        if (_first  + dx < 0) return true;

        if (_second + dy < 0) return true;

        return false;
    }

    /**
     *
     * Tests if applying the Delta would cause the
     * Point to move outside the region.
     *
     * @param delta The Delta to test for.
     *
     * @param region The region to test for.
     *
     * @return True if applying the Delta to the Point would cause
     *         it to trespass the boundaries of the Region, else
     *         false if it would be within the Region.
     */
    public boolean wouldGoOutside(Delta delta, Region region)
    {
        if (wouldGoNegative(delta)) return true;

        Point test = new Point(this);

        test.move(delta);

        return ! region.contains(test);
    }

    /**
     *
     * Returns the distance to another Point, as a Delta object.
     *
     * @param other The other Point.
     *
     * @return The Delta either Point would have to
     *         travel to reach the respective other point.
     */
    public Delta distanceTo(Point other)
    {
        assert(other != null);

        return new Delta(other.x() - this.x(), other.y() - this.y());
    }

    /**
     *
     * Compares two Points by their x, then y-coordinates.
     *
     * @param other The Point to compare-to.
     *
     * @return +1 if this Point's x coordinate is greater than
     *         that of the other point, -1 if it is less. If it
     *         is equal, the same comparison is performed on the
     *         y-coordinate. If both coordinates are equal,
     *         the whole Point is equal.
     */
    @Override public int compareTo(Point other)
    {
        return super.compareTo(other);
    }

    /**
     * Two numbers between non-digit characters.
     */
    private static final Pattern _pattern = Pattern.compile(
            "[^\\d]*(\\d+)[^\\d]+(\\d+)[^\\d]*"
    );
}
