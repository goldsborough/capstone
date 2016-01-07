package capstone.element;

import capstone.data.Representation;
import capstone.utility.Point;

/**
 * A exit element. Just a way to get the concept into the type-system.
 */
public class Exit extends Element
{
    /**
     *
     * Constructs a new exit from a Point and an Representation.
     *
     * @param point The point of the exit.
     *
     * @param representation The representation of the exit.
     */
    public Exit(Point point, Representation representation)
    {
        super(Element.Kind.EXIT, point, representation);
    }
}
