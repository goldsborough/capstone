package capstone.utility;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by petergoldsborough on 12/29/15.
 */
public class ExistingPatternTest
{
    private Pattern pattern;
    private Point point;
    private Delta[] deltas;

    @Before public void setUp()
    {
        deltas = new Delta[]{
            Delta.Left(),
            Delta.Left(),
            Delta.Up(),
            Delta.Right(),
            Delta.Down()
        };

        pattern = new Pattern(deltas);

        point = new Point(2, 2);
    }

    @Test public void testAccess()
    {
        assertTrue(pattern.isAt(0));
        assertThat(pattern.index(), is(0));

        assertThat(pattern.at(0), is(Delta.Left()));
        assertThat(pattern.at(1), is(Delta.Left()));
        assertThat(pattern.at(2), is(Delta.Up()));
        assertThat(pattern.at(3), is(Delta.Right()));
        assertThat(pattern.at(4), is(Delta.Down()));
    }

    @Test public void testJumpTo()
    {
        assert(pattern.isAt(0));

        pattern.jumpTo(1);

        assertTrue(pattern.isAt(1));
        assertThat(pattern.index(), is(1));
    }

    @Test(expected=AssertionError.class)
    public void testJumpToThrowsIfOutOfBoundsJumpRequested()
    {
        pattern.jumpTo(8000);
    }

    @Test(expected=AssertionError.class)
    public void testJumpToThrowsIfNegativeJumpRequested()
    {
        pattern.jumpTo(-1);
    }

    @Test public void testApplyModifiesPointWell()
    {
        assert(pattern.isAt(0));

        pattern.apply(point);

        assertThat(point.x(), is(1));
        assertThat(point.y(), is(2));
    }

    @Test public void testApplyDoesNotProgress()
    {
        assert(pattern.isAt(0));

        pattern.apply(point);

        assertTrue(pattern.isAt(0));
    }

    @Test public void testNextProgressesInPattern()
    {
        assert(pattern.isAt(0));

        pattern.next(point);

        assertTrue(pattern.isAt(1));
    }

    @Test public void testNextModifiesPointWell()
    {
        assert(pattern.isAt(0));

        pattern.next(point);

        assertThat(point.x(), is(1));
        assertThat(point.y(), is(2));
    }

    @Test public void testPreviousGoesBackInPattern()
    {
        pattern.jumpTo(1);
        assert(pattern.isAt(1));

        pattern.previous(point);

        assertTrue(pattern.isAt(0));
    }

    @Test public void testPreviousModifiesPointWell()
    {
        pattern.jumpTo(1);
        assert(pattern.isAt(1));

        pattern.previous(point);

        assertThat(point.x(), is(1));
        assertThat(point.y(), is(2));
    }

    @Test public void testSafeApplyDoesModifyIfNotGoingOutOfBounds()
    {
        assert(pattern.isAt(0));

        pattern.safeApply(point, new Region(0, 4, 4, 0));

        assertThat(point.x(), is(1));
        assertThat(point.y(), is(2));
    }

    @Test public void testSafeApplyDoesNotModifyWhenWouldGoOutOfBounds()
    {
        assert(pattern.isAt(0));

        pattern.jumpTo(1);

        point.x(0);

        // left would go negative
        pattern.safeApply(point, new Region(0, 4, 4, 0));

        assertThat(point.x(), is(0));
        assertThat(point.y(), is(2));

        point.y(3);

        pattern.safeApply(point, new Region(0, 4, 4, 0));

        assertThat(point.x(), is(0));
        assertThat(point.y(), is(3));
    }

    @Test public void testSafeNextDoesModifyIfNotGoingOutOfBounds()
    {
        assert(pattern.isAt(0));

        pattern.safeNext(point, new Region(0, 4, 4, 0));

        assertThat(point.x(), is(1));
        assertThat(point.y(), is(2));
    }

    @Test public void testSafeNextDoesNotModifyWhenWouldGoOutOfBounds()
    {
        assert(pattern.isAt(0));

        point.x(0);

        // left would go negative
        pattern.safeNext(point, new Region(0, 4, 4, 0));

        assertThat(point.x(), is(0));
        assertThat(point.y(), is(2));

        point.y(3);

        pattern.safeNext(point, new Region(0, 4, 4, 0));

        assertThat(point.x(), is(0));
        assertThat(point.y(), is(3));
    }

    @Test public void testSafePreviousDoesModifyIfNotGoingOutOfBounds()
    {
        assert(pattern.isAt(0));

        pattern.safePrevious(point, new Region(0, 4, 4, 0));

        assertThat(point.x(), is(1));
        assertThat(point.y(), is(2));
    }

    @Test public void testSafePreviousDoesNotModifyWhenWouldGoOutOfBounds()
    {
        assert(pattern.isAt(0));

        pattern.jumpTo(1);

        point.x(0);

        // left would go negative
        pattern.safePrevious(point, new Region(0, 4, 4, 0));

        assertThat(point.x(), is(0));
        assertThat(point.y(), is(2));

        point.y(3);

        pattern.safePrevious(point, new Region(0, 4, 4, 0));

        assertThat(point.x(), is(0));
        assertThat(point.y(), is(3));
    }

    @Test public void testLengthIsCorrect()
    {
        assertThat(pattern.length(), is(5));
    }

    @Test public void testIteration()
    {
        int index = 0;

        for (Delta delta : pattern)
        {
            assertThat(delta, is(deltas[index++]));
        }
    }

    @Test public void testPeek()
    {
        for (Delta delta : deltas)
        {
            assertThat(pattern.peek(), is(delta));

            pattern.skip();
        }
    }

    @Test public void testSkipSkipsOneByDefault()
    {
        assert(pattern.isAt(0));

        pattern.skip();

        assertTrue(pattern.isAt(1));
    }

    @Test public void testSkipNone()
    {
        assert(pattern.isAt(0));

        pattern.skip(0);

        assertTrue(pattern.isAt(0));
    }

    @Test public void testSkipTwo()
    {
        assert(pattern.isAt(0));

        pattern.skip(2);

        assertTrue(pattern.isAt(2));
    }

    @Test public void testSkipWrapsAround()
    {
        assert(pattern.isAt(0));

        pattern.skip(pattern.length() + 1);

        assertTrue(pattern.isAt(1));
    }
}