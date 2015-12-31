package capstone.element;

import capstone.utility.Point;
import capstone.data.Representation;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public class Exit extends Element
{
    public Exit(Point point, Representation representation)
    {
        super(Element.Kind.EXIT, point, representation);
    }
}
