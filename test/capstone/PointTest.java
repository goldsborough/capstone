package capstone;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public class PointTest
{
    private Point point;

    @Before public void setUp()
    {
        point = new Point(4, 2);
    }

    @Test public void testConstructsWell()
    {
        assertEquals(point.x(), new Integer(4));
        assertEquals(point.y(), new Integer(2));
    }

    @Test public void testEquals()
    {
        assertNotEquals(point, null);
        assertNotEquals(point, "asdf");
        assertNotEquals(point, new Point(64564, 134));

        assertEquals(point, point);
        assertEquals(point, new Point(4, 2));
    }

    @Test(expected=NullPointerException.class)
    public void testCompareToThrowsForNull() throws NullPointerException
    {
        point.compareTo(null);
    }

    @Test public void testCompareTo() throws NullPointerException
    {
        assertEquals(point.compareTo(point), 0);

        assertEquals(point.compareTo(new Point(0, 0)), +1);
        assertEquals(point.compareTo(new Point(4, 0)), +1);

        assertEquals(point.compareTo(new Point(5, 0)), -1);
        assertEquals(point.compareTo(new Point(4, 6)), -1);
    }
}