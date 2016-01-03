package capstone.utility;

/**
 * Created by petergoldsborough on 01/03/16.
 */
public class Region extends Pair<Point>
{
    public enum Vertical { SOUTH, NORTH }

    public enum Horizontal { WEST, EAST }

    public Region(Point southWest, Point northEast)
    {
        super(southWest, northEast);

        assert(southWest.compareTo(northEast) == -1);
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
        assert(southWest.compareTo(northEast()) == -1);

        super.second(southWest);
    }

    public Point southEast()
    {
        return new Point(northEast().x(), southWest().y());
    }

    public void southEast(Point southEast)
    {
        southWest(new Point(_first.x(), southEast.y()));
        northEast(new Point(southEast.x(), _second.y()));
    }


    public Point northWest()
    {
        return new Point(southWest().x(), northEast().y());
    }

    public void northWest(Point northWest)
    {
        southWest(new Point(northWest.x(), _first.y()));
        northEast(new Point(_second.x(), northWest.y()));
    }

    public Point northEast()
    {
        return super.second();
    }

    public void northEast(Point northEast)
    {
        assert(northEast.compareTo(southWest()) == +1);

        super.second(northEast);
    }

    public int height()
    {
        return southWest().y() - northEast().y();
    }

    public int width()
    {
        return northEast().x() - southWest().x();
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

        if (point.y() < southWest().y()) return false;

        if (point.y() > northEast().y()) return false;

        return true;
    }
}