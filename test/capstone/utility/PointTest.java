package capstone.utility;

import capstone.utility.Delta;
import capstone.utility.Point;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

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
        assertEquals(point.x(), 4);
        assertEquals(point.y(), 2);
    }

    @Test public void testCopyConstructsWell()
    {
        assertEquals(new Point(point), point);
    }

    @Test public void testStringConstructorParsesWellFormedStrings()
    {
        String[] strings = {
                "(123, 4567)",
                "(123,4567)",
                "(123 4567)",
                "(  123, 4567)",
                "(  123  4567 )",
                "(123 | 4567)",
                "123, 4567",
                "123,4567",
                "123 4567",
                "[123, 4567]",
                "[123 4567]"
        };

        for (String string : strings)
        {
            point = new Point(string);

            assertThat(point.x(), is(123));
            assertThat(point.y(), is(4567));
        }

        String[] small = {"1,2", "1 2", "(1, 2)"};

        for (String string : small)
        {
            point = new Point(string);

            assertThat(point.x(), is(1));
            assertThat(point.y(), is(2));
        }
    }

    @Test(expected=AssertionError.class)
    public void testStringConstructorThrowsForIllFormedString1()
    {
        point = new Point("");
    }

    @Test(expected=AssertionError.class)
    public void testStringConstructorThrowsForIllFormedString2()
    {
        point = new Point("afdsfa");
    }

    @Test(expected=AssertionError.class)
    public void testStringConstructorThrowsForIllFormedString3()
    {
        point = new Point("123");
    }

    @Test(expected=AssertionError.class)
    public void testStringConstructorThrowsForIllFormedString4()
    {
        point = new Point("1 2 3 4 5 6");
    }

    @Test(expected=AssertionError.class)
    public void testStringConstructorThrowsForIllFormedString5()
    {
        point = new Point("(123, 45, 3)");
    }

    @Test(expected=AssertionError.class)
    public void testStringConstructorThrowsForIllFormedString6()
    {
        point = new Point("(123, a)");
    }

    @Test(expected=AssertionError.class)
    public void testConstructorThrowsForNegativeXCoordinate()
    {
        point = new Point(-5, 0);
    }

    @Test(expected=AssertionError.class)
    public void testConstructorThrowsForNegativeYCoordinate()
    {
        point = new Point(0, -1);
    }

    @Test public void testAccessInterface()
    {
        point.x(123);

        assertThat(point.x(), is(123));

        point.y(99);

        assertThat(point.y(), is(99));
    }

    @Test(expected=AssertionError.class)
    public void testXThrowsForNegativeArgument()
    {
        point.x(-5);
    }

    @Test(expected=AssertionError.class)
    public void testYThrowsForNegativeArgument()
    {
        point.y(-1);
    }

    @Test public void testEquals()
    {
        assertNotEquals(point, null);
        assertNotEquals(point, "asdf");
        assertNotEquals(point, new Point(64564, 134));

        assertEquals(point, point);
        assertEquals(point, new Point(4, 2));
    }

    @Test(expected=AssertionError.class)
    public void testCompareToThrowsForNull()
    {
        point.compareTo(null);
    }

    @Test public void testCompareTo()
    {
        assertEquals(point.compareTo(point), 0);

        assertEquals(point.compareTo(new Point(0, 0)), +1);
        assertEquals(point.compareTo(new Point(4, 0)), +1);

        assertEquals(point.compareTo(new Point(5, 0)), -1);
        assertEquals(point.compareTo(new Point(4, 6)), -1);
    }

    @Test public void testAbove()
    {
        Point above = point.above();

        assertThat(above.x(), is(point.x()));
        assertThat(above.y(), is(point.y() - 1));
    }

    @Test public void testBelow()
    {
        Point below = point.below();

        assertThat(below.x(), is(point.x()));
        assertThat(below.y(), is(point.y() + 1));
    }

    @Test public void testRight()
    {
        Point right = point.right();

        assertThat(right.x(), is(point.x() + 1));
        assertThat(right.y(), is(point.y()));
    }

    @Test public void testLeft()
    {
        Point right = point.left();

        assertThat(right.x(), is(point.x() - 1));
        assertThat(right.y(), is(point.y()));
    }

    @Test(expected=AssertionError.class)
    public void testMoveThrowsForNullDelta()
    {
        point.move(null);
    }

    @Test public void testMoveByDelta()
    {
        assertThat(new Point(point).move(new Delta(0, 0)), is(point));

        assertThat(new Point(point).move(new Delta(0, -1)), is(point.above()));
        assertThat(new Point(point).move(new Delta(0, 1)), is(point.below()));

        assertThat(new Point(point).move(new Delta(-1, 0)), is(point.left()));
        assertThat(new Point(point).move(new Delta(1, 0)), is(point.right()));

        assertThat(
                new Point(point).move(new Delta(5, 5)),
                is(new Point(point.x() + 5, point.y() + 5))
        );

        assertThat(
                new Point(point).move(new Delta(-2, -2)),
                is(new Point(point.x() - 2, point.y() - 2))
        );
    }

    @Test public void testMoveDirectly()
    {
        assertThat(point.move(0, 0), is(point));

        assertThat(new Point(point).move(0, -1), is(point.above()));
        assertThat(new Point(point).move(0, 1), is(point.below()));

        assertThat(new Point(point).move(-1, 0), is(point.left()));
        assertThat(new Point(point).move(1, 0), is(point.right()));

        assertThat(
                new Point(point).move(5, 5),
                is(new Point(point.x() + 5, point.y() + 5))
        );

        assertThat(
                new Point(point).move(-2, -2),
                is(new Point(point.x() - 2, point.y() - 2))
        );
    }

    @Test(expected=AssertionError.class)
    public void testMoveByDeltaThrowsWhenGoingNegative()
    {
        point.move(new Delta(-500, -500));
    }

    @Test(expected=AssertionError.class)
    public void testMoveDirectlyThrowsWhenGoingNegative()
    {
        point.move(-500, -500);
    }

    @Test(expected=AssertionError.class)
    public void testAboveThrowsWhenGoingNegative()
    {
        point = new Point(0, 0);

        point.above();
    }

    @Test(expected=AssertionError.class)
    public void testLeftThrowsWhenGoingNegative()
    {
        point = new Point(0, 0);

        point.left();
    }

    @Test public void testHashCode()
    {
        point = new Point(1, 3);

        assertThat(point.hashCode(), is (1 ^ (3 << 1)));
    }
}