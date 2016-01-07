package capstone.element;

import capstone.data.Representation;
import capstone.utility.Point;

/**
 * A key element. Just a way to get the concept into the type-system.
 */
public class Key extends Element
{
    /**
     *
     * Constructs a new key from a Point and an Representation.
     *
     * @param point The point of the key.
     *
     * @param representation The representation of the key.
     */
    public Key(Point point, Representation representation)
    {
        super(Element.Kind.KEY, point, representation);
    }
}
