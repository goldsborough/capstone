package capstone.utility;

import capstone.data.Representation;
import capstone.element.Element;
import capstone.element.Exit;
import capstone.element.Wall;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by petergoldsborough on 12/31/15.
 */
public class NewPageTest
{
    private Page page;

    private Representation representation;

    @Before public void setUp()
    {
        representation = new Representation(
                '',
                Terminal.Color.BLACK,
                Terminal.Color.RED

        );
    }

    @Test public void testNonCollectionConstructor()
    {
        page = new Page(new Region(0, 0, 1, 1));

        assertTrue(page.isEmpty());
        assertThat(page.size(), is(0));

        assertTrue(page.elements().isEmpty());
        assertTrue(page.positions().isEmpty());
        assertTrue(page.map().isEmpty());
    }

    @Test public void testCollectionConstructor()
    {
        Collection<Element> elements = Arrays.asList(
                new Wall(new Point(0, 0), representation),
                new Exit(new Point(0, 1), representation)
        );

        page = new Page(new Region(0, 0, 1, 1), elements);

        assertFalse(page.isEmpty());
        assertThat(page.size(), is(2));

        assertThat(page.elements(), is(elements));
    }

    @Test(expected=AssertionError.class)
    public void testNonCollectionConstructorThrowsForNullRegion()
    {
        page = new Page(null);
    }

    @Test(expected=AssertionError.class)
    public void testCollectionConstructorThrowsForNullRegion()
    {
        page = new Page(null, new ArrayList<>());
    }

    @Test(expected=AssertionError.class)
    public void testCollectionConstructorThrowsForNullCollection()
    {
        page = new Page(new Region(0, 0, 1, 1), null);
    }
}