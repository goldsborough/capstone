package capstone.utility;

import capstone.data.Representation;
import capstone.element.Element;
import capstone.element.Key;
import capstone.element.Wall;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by petergoldsborough on 01/03/16.
 */
public class NewPageGridTest
{
    private PageGrid grid;

    @Test public void testNonCollectionConstructorWorksForPerfectFit()
    {
        grid = new PageGrid(
                new LevelSize(4, 4),
                new TerminalSize(2, 2)
        );

        assertTrue(grid.hasNoElements());
        assertThat(grid.numberOfElements(), is(0));

        assertThat(grid.numberOfPages(), is(4));

        assertThat(grid.levelWidth(), is(4));
        assertThat(grid.levelHeight(), is(4));
        assertThat(grid.levelSize(), is(new LevelSize(4, 4)));

        assertThat(grid.terminalWidth(), is(2));
        assertThat(grid.terminalHeight(), is(2));
        assertThat(grid.terminalSize(), is(new LevelSize(2, 2)));

        assertTrue(grid.isPerfectFit());
        assertFalse(grid.isRagged());
    }

    @Test public void testNonCollectionConstructorWorksForSmallerLevelThanTerminalSize()
    {
        grid = new PageGrid(
                new LevelSize(3, 3),
                new TerminalSize(4, 4)
        );

        assertTrue(grid.hasNoElements());
        assertThat(grid.numberOfElements(), is(0));

        assertThat(grid.numberOfPages(), is(4));

        assertThat(grid.levelWidth(), is(3));
        assertThat(grid.levelHeight(), is(3));
        assertThat(grid.levelSize(), is(new LevelSize(3, 3)));

        assertThat(grid.terminalWidth(), is(4));
        assertThat(grid.terminalHeight(), is(4));
        assertThat(grid.terminalSize(), is(new LevelSize(4, 4)));

        assertFalse(grid.isPerfectFit());
        assertTrue(grid.isRagged());
    }

    @Test public void testNonCollectionConstructorWorksForRaggedFit()
    {
        grid = new PageGrid(
                new LevelSize(2, 2),
                new TerminalSize(4, 4)
        );

        assertTrue(grid.hasNoElements());
        assertThat(grid.numberOfElements(), is(0));

        assertThat(grid.numberOfPages(), is(4));

        assertThat(grid.levelWidth(), is(2));
        assertThat(grid.levelHeight(), is(2));
        assertThat(grid.levelSize(), is(new LevelSize(2, 2)));

        assertThat(grid.terminalWidth(), is(4));
        assertThat(grid.terminalHeight(), is(4));
        assertThat(grid.terminalSize(), is(new LevelSize(4, 4)));

        assertFalse(grid.isPerfectFit());
        assertTrue(grid.isRagged());
    }

    @Test public void testCollectionConstructor()
    {
        Representation representation = new Representation(
                '',
                Terminal.Color.BLACK,
                Terminal.Color.RED

        );

        Collection<Element> elements = new ArrayList<>();

        elements.add(new Wall(new Point(0, 0), representation));

        elements.add(new Key(new Point(1, 1), representation));

        grid = new PageGrid(
                new LevelSize(2, 2),
                new TerminalSize(4, 4),
                elements
        );

        assertFalse(grid.hasNoElements());
        assertThat(grid.numberOfElements(), is(2));

        assertThat(grid.numberOfPages(), is(4));

        Page page = grid.fetch(0, 0);

        elements.forEach(e -> assertTrue(page.contains(e)));

        assertThat(grid.levelWidth(), is(2));
        assertThat(grid.levelHeight(), is(2));
        assertThat(grid.levelSize(), is(new LevelSize(2, 2)));

        assertThat(grid.terminalWidth(), is(4));
        assertThat(grid.terminalHeight(), is(4));
        assertThat(grid.terminalSize(), is(new LevelSize(4, 4)));

        assertFalse(grid.isPerfectFit());
        assertTrue(grid.isRagged());
    }
}