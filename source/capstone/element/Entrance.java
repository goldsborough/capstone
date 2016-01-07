package capstone.element;

import capstone.data.Representation;
import capstone.utility.Point;

/**
 * A entrance element. Just a way to get the concept into the type-system.
 */
public class Entrance extends Element
{
    /**
     *
     * Constructs a new entrance from a Point and an Representation.
     *
     * @param point The point of the entrance.
     *
     * @param representation The representation of the entrance.
     */
    public Entrance(Point point, Representation representation)
    {
        super(Element.Kind.ENTRANCE, point, representation);
    }
}
