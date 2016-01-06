package capstone.utility;

import capstone.data.Profile;
import capstone.data.Representation;
import capstone.element.SequentialObstacle;
import capstone.element.Element;
import capstone.element.Exit;
import capstone.element.Wall;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Before;
import org.junit.Test;

import javax.smartcardio.TerminalFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Before public void setUp()
    {
        representation = new Representation(
                '',
                Terminal.Color.BLACK,
                Terminal.Color.RED

        );

        Region region = new Region(0, 10, 10, 0);

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
        Set<Element> elements = new HashSet<>();

        page.elements().forEach(elements::add);

        assertTrue(elements.contains(wall));
        assertTrue(elements.contains(exit));
    }

    @Test public void testAtReturnsElementIfValidPoint()
    {
        assertThat(page.at(new Point(0, 0)), is(wall));
        assertThat(page.at(new Point(1, 0)), is(exit));

        assertNull(page.at(new Point(5, 5)));
    }

    @Test public void testAtReturnsNullForInvalidPoints()
    {
        assertNull(page.at(new Point(99, 99)));
    }

    @Test public void testHasAt()
    {
        assertTrue(page.hasAt(new Point(0, 0)));
        assertTrue(page.hasAt(new Point(1, 0)));

        assertFalse(page.hasAt(new Point(5, 5)));
    }

    @Test public void testContains()
    {
        assertTrue(page.contains(wall));
        assertTrue(page.contains(exit));

        assertFalse(page.contains(new Wall(new Point(10, 10), representation)));
    }

    @Test public void testUpdateChangesSequentialObstaclePositions()
    {
        SequentialObstacle first = new SequentialObstacle(
                new Point(10, 10),
                representation
        );

        SequentialObstacle second = new SequentialObstacle(
                new Point(5, 5),
                representation
        );

        page.add(first);
        page.add(second);

        Map<SequentialObstacle, Point> previous = new HashMap<>();

        previous.put(first, new Point(first.point()));
        previous.put(second, new Point(second.point()));

        page.update();

        // All positions should have changed
        for (Map.Entry<SequentialObstacle, Point> entry : previous.entrySet())
        {
            assertThat(entry.getKey().point(), is(not(entry.getValue())));
        }
    }

    @Test public void testElementAccess()
    {
        Set<Element> expected = new HashSet<>();

        expected.add(wall);
        expected.add(exit);

        for (Element element : page.elements())
        {
            assertTrue(expected.contains(element));
        }
    }

    @Test public void testPositionsAccess()
    {
        Set<Point> expected = new HashSet<>();

        expected.add(wall.point());
        expected.add(exit.point());

        for (Point point : page.positions())
        {
            assertTrue(expected.contains(point));
        }
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

        assertTrue(page.isEmpty());

        for (int i = 1; i <= 10; ++i)
        {
            page.add(wall = new Wall(new Point(i, i), representation));

            assertThat(page.size(), is(i));
        }
    }

    @Test public void testIsOutsideForPointsIsCorrectForPointsOutside()
    {
        assertTrue(page.isOutside(new Point(11, 11)));
        assertTrue(page.isOutside(new Point(11, 0)));
        assertTrue(page.isOutside(new Point(0, 11)));
        assertTrue(page.isOutside(new Point(5, 11)));
        assertTrue(page.isOutside(new Point(11, 5)));
    }

    @Test public void testIsInsideForPointsIsCorrectForPointsOutside()
    {
        assertFalse(page.isInside(new Point(11, 11)));
        assertFalse(page.isInside(new Point(11, 0)));
        assertFalse(page.isInside(new Point(0, 11)));
        assertFalse(page.isInside(new Point(5, 11)));
        assertFalse(page.isInside(new Point(11, 5)));
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
        wall.point(new Point(11, 11));
        assertTrue(page.isOutside(wall));

        wall.point(new Point(11, 0));
        assertTrue(page.isOutside(wall));

        wall.point(new Point(9, 11));
        assertTrue(page.isOutside(wall));
    }

    @Test public void testIsInsideForElementsIsCorrectForPointsOutside()
    {
        wall.point(new Point(11, 11));
        assertFalse(page.isInside(wall));

        wall.point(new Point(11, 0));
        assertFalse(page.isInside(wall));

        wall.point(new Point(9, 11));
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

    @Test(expected=AssertionError.class)
    public void testAddThrowsForPointOutsideOfRegion()
    {
        wall.point(new Point(99, 99));

        page.add(wall);
    }

    @Test public void testIndividualElementKindAccess()
    {
        assertThat(
                page.walls().toArray(),
                is(new Element[]{wall})
        );

        assertThat(
                page.exits().toArray(),
                is(new Element[]{exit})
        );

        assertTrue(page.entrances().isEmpty());
        assertTrue(page.keys().isEmpty());
        assertTrue(page.staticObstacles().isEmpty());
        assertTrue(page.dynamicObstacles().isEmpty());
        assertTrue(page.mysteryBoxes().isEmpty());
    }

    @Test public void testGeneralizedElementAccess()
    {
        assertTrue(page.elements(Element.Kind.WALL)
                .containsAll(page.walls()
        ));

        assertTrue(page.elements(Element.Kind.ENTRANCE)
                        .containsAll(page.entrances()
        ));

        assertTrue(page.elements(Element.Kind.EXIT)
                        .containsAll(page.exits()
        ));

        assertTrue(page.elements(Element.Kind.KEY)
                        .containsAll(page.keys()
        ));

        assertTrue(page.elements(Element.Kind.STATIC_OBSTACLE)
                        .containsAll(page.staticObstacles()
        ));

        assertTrue(page.elements(Element.Kind.DYNAMIC_OBSTACLE)
                        .containsAll(page.dynamicObstacles()
        ));

        assertTrue(page.elements(Element.Kind.MYSTERY_BOX)
                        .containsAll(page.mysteryBoxes()
        ));
    }

    @Test public void testFreeSpace()
    {
        Set<Point> free = new HashSet<>(page.freeSpace());

        for (int x = 0; x < 10; ++x)
        {
            for (int y = 0; y <= 10; ++y)
            {
                if (y == 0 && x <= 1)
                {
                    assertFalse(free.contains(new Point(x, y)));
                }

                else assertTrue(free.contains(new Point(x, y)));
            }
        }
    }

    @Test public void testFreePoint()
    {
        Point free = page.freePoint(page.region());

        assertFalse(page.hasAt(free));
    }
}

