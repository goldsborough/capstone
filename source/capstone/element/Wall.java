package capstone.element;

import capstone.data.Representation;
import capstone.utility.Point;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public class Wall extends Element
{
    public Wall(Point point, Representation representation)
    {
        super(Element.Kind.WALL, point, representation);
    }

    public void interact(Player player)
    {

    }
}
