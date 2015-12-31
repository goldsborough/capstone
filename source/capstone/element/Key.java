package capstone.element;

import capstone.utility.Point;
import capstone.data.Representation;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public class Key extends Element
{
    public Key(Point point, Representation representation)
    {
        super(Element.Kind.KEY, point, representation);
    }
}
