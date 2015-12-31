package capstone.utility;

import capstone.utility.Delta;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by petergoldsborough on 12/29/15.
 */
public class DeltaTest
{
    private Delta delta;

    @Before public void setUp()
    {
        delta = new Delta(4, 2);
    }

    @Test
    public void testConstructsWell()
    {
        assertEquals(delta.x(), 4);
        assertEquals(delta.y(), 2);
    }

    @Test public void testInterface()
    {
        delta.x(123);

        assertThat(delta.x(), is(123));

        delta.y(99);

        assertThat(delta.y(), is(99));
    }

    @Test public void testEuclidianDistance()
    {
        assertThat(delta.euclidian(), is(Math.sqrt(4*4 + 2*2)));
    }

    @Test public void testManhattanDistance()
    {
        assertThat(delta.manhattan(), is(delta.x() + delta.y()));
    }

    @Test public void testInvert()
    {
        assertThat(delta.invert(), is(new Delta(-4, -2)));

        assertThat(new Delta(-1, -1).invert(), is(new Delta(1, 1)));
    }

    @Test public void testUp()
    {
        assertThat(delta.Up(), is(new Delta(0, -1)));
    }

    @Test public void testDown()
    {
        assertThat(delta.Down(), is(new Delta(0, +1)));
    }

    @Test public void testLeft()
    {
        assertThat(delta.Left(), is(new Delta(-1, 0)));
    }

    @Test public void testRight()
    {
        assertThat(delta.Right(), is(new Delta(+1, 0)));
    }

    @Test public void testEquals()
    {
        assertNotEquals(delta, null);
        assertNotEquals(delta, "asdf");
        assertNotEquals(delta, new Delta(64564, 134));

        assertEquals(delta, delta);
        assertEquals(delta, new Delta(4, 2));
    }

    @Test(expected=AssertionError.class)
    public void testCompareToThrowsForNull()
    {
        delta.compareTo(null);
    }

    @Test public void testCompareTo()
    {
        // Compared by Manhattan Distance

        assertEquals(delta.compareTo(delta), 0);
        assertEquals(delta.compareTo(new Delta(3, 3)), 0);
        assertEquals(delta.compareTo(new Delta(5, 1)), 0);

        assertEquals(delta.compareTo(new Delta(0, 0)), +1);
        assertEquals(delta.compareTo(new Delta(4, 0)), +1);
        assertEquals(delta.compareTo(new Delta(5, 0)), +1);


        assertEquals(delta.compareTo(new Delta(4, 6)), -1);
        assertEquals(delta.compareTo(new Delta(4, 3)), -1);
        assertEquals(delta.compareTo(new Delta(50, 5)), -1);
    }
}