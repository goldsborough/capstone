package capstone.element;

import capstone.data.Representation;
import capstone.utility.Pattern;
import capstone.utility.Point;
import capstone.utility.Region;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by petergoldsborough on 12/29/15.
 */
public class PatternObstacleTest
{
    private PatternObstacle obstacle;

    private Point point;

    @Before public void setUp()
    {
        point = new Point(2, 2);

        Representation representation = new Representation(
                '$',
                Terminal.Color.BLACK,
                Terminal.Color.BLUE
        );

        obstacle = new PatternObstacle(point, representation);
    }

    @Test public void testConstructsWell()
    {
        assertNotNull(obstacle.pattern());
    }

    @Test public void testSafeUpdateDoesNotModifyPositionWhenGoingOutOfBounds()
    {
        Region region = new Region(0, 1, 1, 0);

        obstacle.point(new Point(0, 0));

        obstacle.update(region, new HashSet<>());

        assertTrue(region.contains(obstacle.point()));
    }

    @Test public void testChangePattern()
    {
        Pattern oldPattern = obstacle.pattern();

        obstacle.changePattern();

        assertThat(obstacle.pattern(), is(not(oldPattern)));
    }
}