package capstone.element;

import capstone.utility.Point;
import capstone.data.Representation;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public class StaticObstacle extends Element
{
    public StaticObstacle (Point point, Representation representation)
    {
        super(Kind.STATIC_OBSTACLE, point, representation);
    }
}
