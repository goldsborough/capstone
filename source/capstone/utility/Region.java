package capstone.utility;

/**
 * Represents a region in a coordinate system. It is fully defined by a
 * south-western and north-eastern point, but provides access to all four
 * corners as well as other information about the region, e.g. it's area.
 */
public class Region extends AbstractPair<Point, Point> implements Comparable<Region>
{
    public enum Vertical { SOUTH, NORTH }

    public enum Horizontal { WEST, EAST }

    /**
     *
     * Constructs the Region from its south-western and north-eastern point.
     *
     * @param southWest The south-western point.
     *
     * @param northEast The north-eastern point.
     */
    public Region(Point southWest, Point northEast)
    {
        super(southWest, northEast);

        assert(southWest.x() <= northEast.x());
        assert(southWest.y() >= northEast.y());
    }

    /**
     *
     * Constructs a Region with the given width and height, in the top left
     * corner of the coordinate system, i.e. it is equivalent to a having the
     * south-western point be at (0, height) and the north-eastern point at
     * (width, 0).
     *
     * @param width The width of the region.
     *
     * @param height The height of the region.
     */
    public Region(int width, int height)
    {
        this(0, height, width, 0);
    }

    /**
     *
     * Utility constructor to not have to create new Points.
     *
     * @param southWestX The x-coordinate of the south-western point.
     *
     * @param southWestY The y-coordinate of the south-western point.
     *
     * @param northEastX The x-coordinate of the north-eastern point.
     *
     * @param northEastY The y-coordinate of the north-eastern point.
     */
    public Region(int southWestX,
                  int southWestY,
                  int northEastX,
                  int northEastY)
    {
        this(
                new Point(southWestX, southWestY),
                new Point(northEastX, northEastY)
        );
    }

    /**
     *
     * Copy-constructor.
     *
     * @param other The other region to construct this one from.
     */
    public Region(Region other)
    {
        super(other);
    }

    /**
     *
     * @return The south-western point.
     */
    public Point southWest()
    {
        return super.first();
    }

    /**
     *
     * Sets the south-western point.
     *
     * @param southWest The new south-western point.
     */
    public void southWest(Point southWest)
    {
        assert(southWest.x() <= northEast().x());
        assert(southWest.y() >= northEast().y());

        super.first(southWest);

        _invalidate();
    }

    /**
     *
     * Note that accessing the south-western point is more efficient.
     *
     * @return The south-eastern point.
     */
    public Point southEast()
    {
        if (_southEast == null)
        {
            _southEast = new Point(northEast().x(), southWest().y());
        }

        return _southEast;
    }

    /**
     *
     * Sets the south-eastern point.
     *
     * Note that accessing the south-western point is more efficient.
     *
     * @param southEast The new south-eastern point.
     */
    public void southEast(Point southEast)
    {
        southWest(new Point(_first.x(), southEast.y()));
        northEast(new Point(southEast.x(), _second.y()));

        _southEast = southEast;
    }

    /**
     *
     * Note that accessing the north-eastern point is more efficient.
     *
     * @return The north-western point.
     */
    public Point northWest()
    {
        if (_northWest == null)
        {
            _northWest = new Point(southWest().x(), northEast().y());
        }

        return _northWest;
    }

    /**
     *
     * Sets the north-western point.
     *
     * Note that accessing the north-eastern point is more efficient.
     *
     * @param northWest The new north-western point.
     */
    public void northWest(Point northWest)
    {
        southWest(new Point(northWest.x(), _first.y()));
        northEast(new Point(_second.x(), northWest.y()));

        _northWest = northWest;
    }

    /**
     * @return The north-eastern point.
     */
    public Point northEast()
    {
        return super.second();
    }

    /**
     *
     * Sets the north-eastern point.
     *
     * @param northEast The new north-eastern point.
     */
    public void northEast(Point northEast)
    {
        assert(southWest().x() <= northEast.x());
        assert(southWest().y() >= northEast.y());

        super.second(northEast);

        _invalidate();
    }

    /**
     * @return The height of the region.
     */
    public int height()
    {
        return southWest().y() - northEast().y() + 1;
    }

    /**
     * @return The width of the region.
     */
    public int width()
    {
        return northEast().x() - southWest().x() + 1;
    }

    /**
     * @return The area of the region.
     */
    public int area()
    {
        return height() * width();
    }

    /**
     * @return The circumference of the region.
     */
    public int circumference()
    {
        return 2 * height() + 2 * width();
    }

    /**
     *
     * Returns the point at the specified location.
     *
     * @param vertical NORTH or SOUTH.
     *
     * @param horizontal WEST or EAST.
     *
     * @return The point at the location specified by
     *         the vertical and horizontal components.
     */
    public Point at(Vertical vertical, Horizontal horizontal)
    {
        if (vertical == Vertical.SOUTH)
        {
            if (horizontal == Horizontal.WEST) return southWest();

            else return southEast();
        }

        else
        {
            if (horizontal == Horizontal.WEST) return northWest();

            else return northEast();
        }
    }

    /**
     *
     * Whether the region contains the given point.
     *
     * @param point The point to test.
     *
     * @return True if the point is any of the
     *         points within the region, else false.
     */
    public boolean contains(Point point)
    {
        if (point.x() < southWest().x()) return false;

        if (point.x() > northEast().x()) return false;

        if (point.y() > southWest().y()) return false;

        if (point.y() < northEast().y()) return false;

        return true;
    }

    /**
     *
     * Compares to Regions.
     *
     * Comparisons are performed by area.
     *
     * @param other The region to compare to.
     *
     * @return +1 if the area of this region is greater than that of the
     *         other region, -1 if it is less and 0 if it is exactly equal.
     */
    @Override public int compareTo(Region other)
    {
        assert(other != null);

        return Integer.compare(this.area(), other.area());
    }

    /**
     * Invalidates the cached points.
     */
    private void _invalidate()
    {
        _southEast = null;
        _northWest = null;
    }

    private Point _southEast;
    private Point _northWest;
}