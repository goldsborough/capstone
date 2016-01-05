package capstone.element;

import capstone.data.Representation;
import capstone.element.DynamicObstacle;
import capstone.utility.Pattern;
import capstone.utility.Point;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by petergoldsborough on 12/29/15.
 */
public class DynamicObstacleTest
{
    private DynamicObstacle obstacle;

    private Point point;

    @Before public void setUp()
    {
        point = new Point(2, 2);

        Representation representation = new Representation(
                '$',
                Terminal.Color.BLACK,
                Terminal.Color.BLUE
        );

        obstacle = new DynamicObstacle(point, representation);
    }

    @Test public void testConstructsWell() {
        assertNotNull(obstacle.pattern());
    }

    @Test public void testUpdateModifiesPosition()
    {
        Point oldPoint = new Point(point);

        obstacle.update();

        assertThat(obstacle.point(), is(not(oldPoint)));
    }

    @Test public void testSafeUpdateDoesNotModifyPositionWhenGoingOutOfBounds()
    {
        obstacle.point(new Point(0, 0));

        obstacle.safeUpdate(1, 1);

        assertThat(obstacle.point(), is(new Point(0, 0)));
    }

    @Test public void testChangePattern()
    {
        Pattern oldPattern = obstacle.pattern();

        obstacle.changePattern();

        assertThat(obstacle.pattern(), is(not(oldPattern)));
    }
}