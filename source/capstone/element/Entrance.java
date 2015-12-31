package capstone.element;

import capstone.utility.Point;
import capstone.data.Representation;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public class Entrance extends Element
{
    public Entrance(Point point, Representation representation)
    {
        super(Element.Kind.ENTRANCE, point, representation);
    }
}
