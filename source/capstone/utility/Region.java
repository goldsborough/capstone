package capstone.utility;

/**
 * Created by petergoldsborough on 01/03/16.
 */
public class Region extends AbstractPair<Point, Point>
{
    public enum Vertical { SOUTH, NORTH }

    public enum Horizontal { WEST, EAST }

    public Region(Point southWest, Point northEast)
    {
        super(southWest, northEast);

        assert(southWest.x() <= northEast.x());
        assert(southWest.y() >= northEast.y());
    }

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

    public Region(Region other)
    {
        super(other);
    }

    public Point southWest()
    {
        return super.first();
    }

    public void southWest(Point southWest)
    {
        assert(southWest.x() <= northEast().x());
        assert(southWest.y() >= northEast().y());

        super.first(southWest);

        _invalidate();
    }

    public Point southEast()
    {
        if (_southEast == null)
        {
            _southEast = new Point(northEast().x(), southWest().y());
        }

        return _southEast;
    }

    public void southEast(Point southEast)
    {
        southWest(new Point(_first.x(), southEast.y()));
        northEast(new Point(southEast.x(), _second.y()));

        _southEast = southEast;
    }


    public Point northWest()
    {
        if (_northWest == null)
        {
            _northWest = new Point(southWest().x(), northEast().y());
        }

        return _northWest;
    }

    public void northWest(Point northWest)
    {
        southWest(new Point(northWest.x(), _first.y()));
        northEast(new Point(_second.x(), northWest.y()));

        _northWest = northWest;
    }

    public Point northEast()
    {
        return super.second();
    }

    public void northEast(Point northEast)
    {
        assert(southWest().x() <= northEast.x());
        assert(southWest().y() >= northEast.y());

        super.second(northEast);

        _invalidate();
    }

    public int height()
    {
        return southWest().y() - northEast().y() + 1;
    }

    public int width()
    {
        return northEast().x() - southWest().x() + 1;
    }

    public int area()
    {
        return height() * width();
    }

    public int circumference()
    {
        return 2 * height() + 2 * width();
    }

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

    public boolean contains(Point point)
    {
        if (point.x() < southWest().x()) return false;

        if (point.x() > northEast().x()) return false;

        if (point.y() > southWest().y()) return false;

        if (point.y() < northEast().y()) return false;

        return true;
    }

    private void _invalidate()
    {
        _southEast = null;
        _northWest = null;
    }

    private Point _southEast;
    private Point _northWest;
}