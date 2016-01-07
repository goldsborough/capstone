package capstone.element;

import capstone.data.Representation;
import capstone.utility.Point;

/**
 * A wall element. Just a way to get the concept into the type-system.
 */
public class Wall extends Element
{
    /**
     *
     * Constructs a new wall from a Point and an Representation.
     *
     * @param point The point of the wall.
     *
     * @param representation The representation of the wall.
     */
    public Wall(Point point, Representation representation)
    {
        super(Element.Kind.WALL, point, representation);
    }
}
