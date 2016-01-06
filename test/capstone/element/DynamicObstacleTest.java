package capstone.element;

import capstone.data.Representation;
import capstone.utility.Delta;
import capstone.utility.Point;
import capstone.utility.Region;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Created by petergoldsborough on 01/05/16.
 */

class MockDynamicObstacle extends DynamicObstacle
{

    public MockDynamicObstacle(Point point, Representation representation)
    {
        super(point, representation);
    }

    @Override protected Delta _next(Region region, Set<Point> taken)
    {
        Delta delta = Delta.Right();

        if (_valid(delta, region, taken)) return delta;

        else return Delta.Left();
    }
}

public class DynamicObstacleTest
{
    private DynamicObstacle obstacle;

    private Region region;

    private Set<Point> taken;

    @Before public void setUp()
    {
        Representation representation = new Representation(
                '!',
                Terminal.Color.RED,
                Terminal.Color.BLACK
        );

        obstacle = new MockDynamicObstacle(
            new Point(0, 0),
            representation
        );

        region = new Region(0, 1, 2, 0);

        taken = new HashSet<Point>(){{
            add(new Point(2, 0));
        }};
    }

    @Test public void testPeekPoint()
    {
        assertThat(
                obstacle.peekPoint(region, taken),
                is(new Point(1, 0))
        );
    }

    @Test public void testSafeUpdateUpdatesWhenSupposedTo()
    {
        obstacle.update(region, taken);

        assertThat(obstacle.point(), is (new Point(1, 0)));
    }

    @Test public void testSafeUpdateDoesNotUpdateWhenNotSupposedTo()
    {
        obstacle.update(region, taken);

        assert(obstacle.point().equals(new Point(1, 0)));

        obstacle.update(region, taken);

        assertThat(obstacle.point(), is (new Point(1, 0)));
    }
}