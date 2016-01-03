package capstone.utility;

import capstone.data.Representation;
import capstone.element.DynamicObstacle;
import capstone.element.Element;
import capstone.element.Entrance;
import capstone.element.Exit;
import capstone.element.Key;
import capstone.element.MysteryBox;
import capstone.element.StaticObstacle;
import capstone.element.Wall;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by petergoldsborough on 01/03/16.
 */
public class ExistingPageGridTest
{
    private PageGrid grid;

    private Representation representation;

    private List<Element> elements;

    private List<Element> remaining;

    public void fill()
    {
        remaining.forEach(grid::add);
    }

    @Before public void setUp()
    {
        grid = new PageGrid(
                new LevelSize(4, 4),
                new TerminalSize(2, 2)
        );

        representation = new Representation(
                '',
                Terminal.Color.BLACK,
                Terminal.Color.RED

        );

        elements.add(new Wall(new Point(0, 0), representation));
        elements.add(new Key(new Point(1, 1), representation));

        elements.add(new DynamicObstacle(new Point(0, 2), representation));
        elements.add(new StaticObstacle(new Point(1, 3), representation));

        elements.add(new MysteryBox(new Point(2, 0), representation));
        elements.add(new Entrance(new Point(3, 0), representation));

        elements.add(new Exit(new Point(2, 2), representation));
        elements.add(new Wall(new Point(3, 3), representation));

        elements.forEach(grid::add);


        remaining.add(new Wall(new Point(0, 1), representation));
        remaining.add(new Wall(new Point(1, 1), representation));

        remaining.add(new Wall(new Point(1, 2), representation));
        remaining.add(new Wall(new Point(0, 3), representation));

        remaining.add(new Wall(new Point(2, 1), representation));
        remaining.add(new Wall(new Point(3, 1), representation));

        remaining.add(new Wall(new Point(2, 3), representation));
        remaining.add(new Wall(new Point(3, 2), representation));
    }

    @Test public void testAdd()
    {
        assertFalse(grid.hasNoElements());
        assertThat(grid.numberOfElements(), is(8));

        grid.forEach(page -> assertThat(page.size(), is(2)));


        assertTrue(grid.fetch(0, 0).contains(elements.get(0)));
        assertTrue(grid.fetch(0, 0).contains(elements.get(1)));

        assertTrue(grid.fetch(0, 1).contains(elements.get(2)));
        assertTrue(grid.fetch(0, 1).contains(elements.get(3)));

        assertTrue(grid.fetch(1, 0).contains(elements.get(4)));
        assertTrue(grid.fetch(1, 0).contains(elements.get(5)));

        assertTrue(grid.fetch(1, 1).contains(elements.get(6)));
        assertTrue(grid.fetch(1, 1).contains(elements.get(7)));
    }

    @Test public void testRemove()
    {
        grid.remove(elements.get(0));

        assertThat(grid.numberOfElements(), is(7));

        Page page = grid.fetch(0, 0);

        assertThat(page.size(), is(1));

        assertFalse(page.contains(elements.get(0)));
        assertTrue(page.contains(elements.get(1)));
    }

    @Test public void testContains()
    {
        elements.forEach(e -> assertTrue(grid.contains(e)));
    }

    @Test public void testAt()
    {
        for (Element element : elements)
        {
            assertThat(grid.at(element.point()), is(element));
        }
    }

    @Test public void testHasAt()
    {
        elements.forEach(e -> assertTrue(grid.hasAt(e.point())));
    }

    @Test public void testFetchPageOf()
    {
        for (Element element : elements)
        {
            assertTrue(grid.fetchPageOf(element).contains(element));
        }
    }

    @Test public void testPageIndexOf()
    {
        assertThat(grid.pageIndexOf(elements.get(0)), is(new Index(0, 0)));
        assertThat(grid.pageIndexOf(elements.get(1)), is(new Index(0, 0)));

        assertThat(grid.pageIndexOf(elements.get(2)), is(new Index(0, 1)));
        assertThat(grid.pageIndexOf(elements.get(3)), is(new Index(0, 1)));

        assertThat(grid.pageIndexOf(elements.get(4)), is(new Index(1, 0)));
        assertThat(grid.pageIndexOf(elements.get(5)), is(new Index(1, 0)));

        assertThat(grid.pageIndexOf(elements.get(6)), is(new Index(1, 1)));
        assertThat(grid.pageIndexOf(elements.get(7)), is(new Index(1, 1)));
    }

    @Test public void testFetch()
    {
        Page page = grid.fetch(0, 0);

        assertTrue(page.contains(elements.get(0)));
        assertTrue(page.contains(elements.get(1)));
    }

    @Test public void testCurrentIndex()
    {
        grid.fetch(0, 0);
        assertThat(grid.currentIndex(), is(new Index(0, 0)));

        grid.fetch(1, 1);
        assertThat(grid.currentIndex(), is(new Index(1, 1)));
    }

    @Test public void testCurrentPage()
    {
        Page page = grid.fetch(0, 0);
        assertThat(grid.currentPage(), is(page));

        page = grid.fetch(1, 1);
        assertThat(grid.currentIndex(), is(page));
    }

    @Test public void testSizeAccess()
    {
        assertThat(grid.levelWidth(), is(4));
        assertThat(grid.levelHeight(), is(4));
        assertThat(grid.levelSize(), is(new LevelSize(4, 4)));

        assertThat(grid.terminalWidth(), is(2));
        assertThat(grid.terminalHeight(), is(2));
        assertThat(grid.terminalSize(), is(new LevelSize(2, 2)));
    }

    @Test public void testIsFull()
    {
        fill();

        assertTrue(grid.isFull());
        assertFalse(grid.isEmpty());

        assertThat(grid.size(), is(grid.capacity()));
    }

    @Test public void testCapacity()
    {
        assertThat(grid.capacity(), is(grid.levelWidth() * grid.levelHeight()));
    }

    @Test public void testKindGenerateForNonFullLevel()
    {
        Element result = grid.generate(Element.Kind.WALL);

        assertNotNull(result);
    }

    @Test public void testKindGenerateForFullLevel()
    {
        fill();

        assert(grid.isFull());

        Element result = grid.generate(Element.Kind.WALL);

        assertNull(result);
    }

    @Test public void testKindRemoveForExistingElementOfThatKind()
    {
        Element removed = grid.remove(Element.Kind.MYSTERY_BOX);

        assertNotNull(removed);

        assertThat(grid.numberOfElements(), is(7));
        assertThat(grid.capacity(), is(grid.levelWidth() * grid.levelHeight()));

        assertFalse(grid.fetchPageFor(removed).contains(removed));
    }

    @Test public void testKindRemoveForNoExistingElementOfThatKind()
    {
        Element removed = grid.remove(Element.Kind.MYSTERY_BOX);

        assertNull(removed);

        assertThat(grid.numberOfElements(), is(8));
        assertThat(grid.capacity(), is(grid.levelWidth() * grid.levelHeight()));
    }

    @Test public void testKindFindForExistingElementOfThatKind()
    {
        Element found = grid.find(Element.Kind.MYSTERY_BOX);

        assertNotNull(found);

        assertTrue(found.equals(elements.get(4)));
    }

    @Test public void testKindFindForNonExistingElementOfThatKind()
    {
        grid.remove(element.get(4));

        Element found = grid.find(Element.Kind.MYSTERY_BOX);

        assertNull(found);
    }

    @Test public void testFindFreeSpaceForNonFullLevel()
    {
        Point found = grid.findFreeSpace();

        assertNotNull(found);

        assertFalse(grid.fetchPageFor(found).hasAt(found));
    }

    @Test public void testFindFreeSpaceForFullLevel()
    {
        fill();

        Point found = grid.findFreeSpace();

        assertNull(found);
    }

    @Test public void testFollow()
    {
        grid.fetch(0, 0);

        assert(grid.currentIndex(), is(new Index(0, 0));

        Element outside = new Wall(new Point(2, 3), representation);

        grid.follow(outside);

        assertThat(grid.currentIndex(), is(new Index(0, 1)));
    }

    @Test public void testFetchAboveReturnsCorrectPageWhenIsPossible()
    {
        grid.fetch(0, 1);

        assertNotNull(grid.fetchAbove());

        assertThat(grid.currentIndex(), is(new Index(0, 1)));
    }

    @Test public void testFetchAboveReturnsNullWhenFetchingNotPossible()
    {
        assert(grid.currentIndex().equals(new Index(0, 0)));

        assertNull(grid.fetchAbove());

        assertThat(grid.currentIndex(), is(new Index(0, 0)));
    }

    @Test public void testFetchBelowReturnsCorrectPageWhenIsPossible()
    {
        assert(grid.currentIndex().equals(new Index(0, 0)));

        assertNotNull(grid.fetchBelow());

        assertThat(grid.currentIndex(), is(new Index(0, 1)));
    }

    @Test public void testFetchBelowReturnsNullWhenFetchingNotPossible()
    {
        grid.fetch(1, 1); // can't go down

        assertNull(grid.fetchBelow());

        assertThat(grid.currentIndex(), is(new Index(1, 1)));
    }

    @Test public void testFetchLeftReturnsCorrectPageWhenIsPossible()
    {
        grid.fetch(1, 0);

        assertNotNull(grid.fetchLeft());

        assertThat(grid.currentIndex(), is(new Index(0, 0)));
    }

    @Test public void testFetchLeftReturnsNullWhenFetchingNotPossible()
    {
        assert(grid.currentIndex().equals(new Index(0, 0)));

        assertNull(grid.fetchLeft());

        assertThat(grid.currentIndex(), is(new Index(0, 0)));
    }

    @Test public void testFetchRightReturnsCorrectPageWhenIsPossible()
    {
        assert(grid.currentIndex().equals(new Index(0, 0)));

        assertNotNull(grid.fetchRight());

        assertThat(grid.currentIndex(), is(new Index(1, 0)));
    }

    @Test public void testFetchRightReturnsNullWhenFetchingNotPossible()
    {
        grid.fetch(1, 1);

        assertNull(grid.fetchRight());

        assertThat(grid.currentIndex(), is(new Index(1, 1)));
    }

    @Test public void testResizeOnlyForTerminalSizeChange()
    {
        // 16 squares
        // check same elements contained...

        grid.resize(new TerminalSize(1, 1));
    }

    @Test public void testResizeOnlyForLevelSizeChange()
    {
        // 16 squares
        grid.resize(new LevelSize(8, 8));
    }

    @Test public void testResizeForTerminalSizeAndLevelSizeChange()
    {
        grid.resize(new LevelSize(8, 8), new TerminalSize(3, 3));
    }

    @Test(expected=AssertionError.class)
    public void testAddThrowsForNullArgument()
    {
        grid.add(null);
    }

    @Test(expected=AssertionError.class)
    public void testFetchThrowsForInvalidIndex()
    {
        grid.fetch(500, 500);
    }
}