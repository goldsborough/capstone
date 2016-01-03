package capstone.utility;

import capstone.data.Profile;
import capstone.data.Representation;
import capstone.element.DynamicObstacle;
import capstone.element.Element;
import capstone.element.Exit;
import capstone.element.Wall;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by petergoldsborough on 12/31/15.
 */
public class ExistingPageTest
{
    private Page page;

    private Wall wall;

    private Exit exit;

    private Representation representation;

    private Region region;


    @Before public void setUp()
    {
        representation = new Representation(
                '',
                Terminal.Color.BLACK,
                Terminal.Color.RED

        );

        Profile profile = new Profile(
                "id",
                "Real Name",
                KeyMap.Arrows(),
                representation
        );

        region = new Region(0, 0, 10, 10);

        page = new Page(region);

        Representation representation = new Representation(
                '',
                Terminal.Color.BLACK,
                Terminal.Color.RED

        );

        wall = new Wall(new Point(0, 0), representation);
        exit = new Exit(new Point(1, 0), representation);

        page.add(wall);
        page.add(exit);
    }

    @Test public void testAdd()
    {
        assertTrue(page.elements().contains(wall));
        assertTrue(page.elements().contains(exit));
    }

    @Test public void testAtReturnsElementIfValidPoint()
    {
        assertThat(page.at(new Point(0, 0)), is(wall));
        assertThat(page.at(new Point(0, 1)), is(exit));

        assertNull(page.at(new Point(5, 5)));
    }

    @Test public void testAtReturnsNullforInvalidPoints()
    {
        assertNull(page.at(new Point(99, 99)));
    }

    @Test public void testHasAt()
    {
        assertTrue(page.hasAt(new Point(0, 0)));
        assertTrue(page.hasAt(new Point(0, 1)));

        assertFalse(page.hasAt(new Point(5, 5)));
    }

    @Test public void testContains()
    {
        assertTrue(page.contains(wall));
        assertTrue(page.contains(exit));

        wall.point(new Point(5, 5));

        assertFalse(page.contains(wall));
    }

    @Test public void testUpdateChangesDynamicObstaclePositions()
    {
        DynamicObstacle first = new DynamicObstacle(
                new Point(10, 10),
                representation
        );

        DynamicObstacle second = new DynamicObstacle(
                new Point(5, 5),
                representation
        );

        page.add(first);
        page.add(second);

        Map<DynamicObstacle, Point> previous = new HashMap<>();

        previous.put(first, first.point());
        previous.put(second, second.point());

        page.update();

        // All positions should have changed
        for (Map.Entry<DynamicObstacle, Point> entry : previous.entrySet())
        {
            assertThat(entry.getKey().point(), is(not(entry.getValue())));
        }
    }

    @Test public void testElementAccess()
    {
        List<Element> expected = new ArrayList<>();

        expected.add(wall);
        expected.add(exit);

        assertThat(page.elements(), is(expected));
    }

    @Test public void testPositionsAccess()
    {
        List<Point> expected = new ArrayList<>();

        expected.add(wall.point());
        expected.add(exit.point());

        assertThat(page.positions(), is(expected));
    }

    @Test public void testMapAccess()
    {
        Map<Point, Element> expected = new HashMap<>();

        expected.put(wall.point(), wall);
        expected.put(exit.point(), exit);

        assertThat(page.map(), is(expected));
    }

    @Test public void testRemoveWorksForValidPoint()
    {
        page.remove(wall);
    }

    @Test(expected=AssertionError.class)
    public void testRemoveThrowsForInvalidPoint()
    {
        page.remove(new Point(5, 5));
    }

    @Test(expected=AssertionError.class)
    public void testAtThrowsForNullPoint()
    {
        page.at(null);
    }

    @Test(expected=AssertionError.class)
    public void testHasAtThrowsForNullPoint()
    {
        page.hasAt(null);
    }

    @Test(expected=AssertionError.class)
    public void testContainsThrowsForNullPoint()
    {
        page.contains(null);
    }

    @Test(expected=AssertionError.class)
    public void testAddThrowsWhenTryingToAddToTakenPoint()
    {
        page.add(wall = new Wall(new Point(0, 0), representation));
    }

    @Test public void testSizeStaysUpdated()
    {
        page.clear();

        for (int i = 0; i < 10; ++i)
        {
            page.add(wall = new Wall(new Point(i, i), representation));

            assertThat(page.size(), is(i));
        }
    }

    @Test public void testIsOutsideForPointsIsCorrectForPointsOutside()
    {
        assertTrue(page.isOutside(new Point(10, 10)));
        assertTrue(page.isOutside(new Point(10, 0)));
        assertTrue(page.isOutside(new Point(0, 10)));
        assertTrue(page.isOutside(new Point(5, 10)));
        assertTrue(page.isOutside(new Point(10, 5)));
    }

    @Test public void testIsInsideForPointsIsCorrectForPointsOutside()
    {
        assertFalse(page.isInside(new Point(10, 10)));
        assertFalse(page.isInside(new Point(10, 0)));
        assertFalse(page.isInside(new Point(0, 10)));
        assertFalse(page.isInside(new Point(5, 10)));
        assertFalse(page.isInside(new Point(10, 5)));
    }

    @Test public void testIsOutsideForPointsIsCorrectForPointsInside()
    {
        assertFalse(page.isOutside(new Point(5, 5)));
        assertFalse(page.isOutside(new Point(9, 0)));
        assertFalse(page.isOutside(new Point(0, 9)));
        assertFalse(page.isOutside(new Point(5, 9)));
        assertFalse(page.isOutside(new Point(9, 5)));
        assertFalse(page.isOutside(new Point(0, 0)));
    }

    @Test public void testIsInsideForPointsIsCorrectForPointsInside()
    {
        assertTrue(page.isInside(new Point(5, 5)));
        assertTrue(page.isInside(new Point(9, 0)));
        assertTrue(page.isInside(new Point(0, 9)));
        assertTrue(page.isInside(new Point(5, 9)));
        assertTrue(page.isInside(new Point(9, 5)));
        assertTrue(page.isInside(new Point(0, 0)));
    }

    @Test public void testIsOutsideForElementsIsCorrectForPointsOutside()
    {
        wall.point(new Point(10, 10));
        assertTrue(page.isOutside(wall));

        wall.point(new Point(10, 0));
        assertTrue(page.isOutside(wall));

        wall.point(new Point(9, 10));
        assertTrue(page.isOutside(wall));
    }

    @Test public void testIsInsideForElementsIsCorrectForPointsOutside()
    {
        wall.point(new Point(10, 10));
        assertFalse(page.isInside(wall));

        wall.point(new Point(10, 0));
        assertFalse(page.isInside(wall));

        wall.point(new Point(9, 10));
        assertFalse(page.isInside(wall));
    }

    @Test public void testIsOutsideForElementsIsCorrectForPointsInside()
    {
        wall.point(new Point(9, 9));
        assertFalse(page.isOutside(wall));

        wall.point(new Point(9, 10));
        assertFalse(page.isOutside(wall));

        wall.point(new Point(0, 9));
        assertFalse(page.isOutside(wall));
    }

    @Test public void testIsInsideForElementsIsCorrectForPointsInside()
    {
        wall.point(new Point(9, 9));
        assertTrue(page.isInside(wall));

        wall.point(new Point(9, 10));
        assertTrue(page.isInside(wall));

        wall.point(new Point(0, 9));
        assertTrue(page.isInside(wall));
    }
}

