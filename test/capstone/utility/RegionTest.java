package capstone.utility;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by petergoldsborough on 01/03/16.
 */
public class RegionTest
{
    private Region region;

    @Test public void testPointConstructor()
    {
        region = new Region(
                new Point(0, 1),
                new Point(1, 0)
        );

        assertThat(region.southWest(), is(new Point(0, 1)));
        assertThat(region.northEast(), is(new Point(1, 0)));
    }

    @Test public void testCoordinateConstructor()
    {
        region = new Region(0, 1, 1, 0);

        assertThat(region.southWest(), is(new Point(0, 1)));
        assertThat(region.northEast(), is(new Point(1, 0)));
    }

    @Test(expected=AssertionError.class)
    public void testPointConstructorThrowsForSouthWestPointLargerThanNorthEast1()
    {
        region = new Region(
                new Point(2, 2),
                new Point(1, 1)
        );
    }

    @Test(expected=AssertionError.class)
    public void testPointConstructorThrowsForSouthWestPointLargerThanNorthEast2()
    {
        region = new Region(
                new Point(2, 0),
                new Point(1, 1)
        );
    }

    @Test(expected=AssertionError.class)
    public void testPointConstructorThrowsForSouthWestPointLargerThanNorthEast3()
    {
        region = new Region(
                new Point(0, 0),
                new Point(1, 1)
        );
    }

    @Test public void testAllIndividualPointAccessorsAreCorrect()
    {
        region = new Region(
                new Point(0, 1),
                new Point(1, 0)
        );

        assertThat(region.southWest(), is(new Point(0, 1)));
        assertThat(region.southEast(), is(new Point(1, 1)));

        assertThat(region.northWest(), is(new Point(0, 0)));
        assertThat(region.northEast(), is(new Point(1, 0)));
    }

    @Test public void testGeneralizedPointAccessorIsCorrect()
    {
        region = new Region(
                new Point(0, 1),
                new Point(1, 0)
        );

        assertThat(
                region.at(Region.Vertical.SOUTH, Region.Horizontal.WEST),
                is(new Point(0, 1))
        );

        assertThat(
                region.at(Region.Vertical.SOUTH, Region.Horizontal.EAST),
                is(new Point(1, 1))
        );


        assertThat(
                region.at(Region.Vertical.NORTH, Region.Horizontal.WEST),
                is(new Point(0, 0))
        );

        assertThat(
                region.at(Region.Vertical.NORTH, Region.Horizontal.EAST),
                is(new Point(1, 0))
        );
    }

    @Test public void testSetters()
    {
        region = new Region(
                new Point(0, 1),
                new Point(1, 0)
        );

        region.southWest(new Point(0, 3));
        region.southEast(new Point(6, 4));
        region.northWest(new Point(0, 1));
        region.northEast(new Point(7, 1));

        assertThat(region.southWest(), is(new Point(0, 4)));
        assertThat(region.southEast(), is(new Point(7, 4)));
        assertThat(region.northWest(), is(new Point(0, 1)));
        assertThat(region.northEast(), is(new Point(7, 1)));
    }

    @Test(expected=AssertionError.class)
    public void testConstraintsAreMaintainedForSetting()
    {
        region = new Region(
                new Point(0, 1),
                new Point(1, 0)
        );

        Point point = new Point(123, 123);

        region.southWest(point);
    }

    @Test public void testContainsIsCorrectForContainedPoints()
    {
        region = new Region(
                new Point(0, 1),
                new Point(1, 0)
        );

        assertTrue(region.contains(new Point(0, 0)));
        assertTrue(region.contains(new Point(0, 1)));
        assertTrue(region.contains(new Point(1, 0)));
        assertTrue(region.contains(new Point(1, 1)));
    }

    @Test public void testContainsIsCorrectForNonContainedPoints()
    {
        region = new Region(
                new Point(0, 1),
                new Point(1, 0)
        );

        assertFalse(region.contains(new Point(5, 5)));
        assertFalse(region.contains(new Point(3, 1)));
        assertFalse(region.contains(new Point(2, 2)));
    }

    @Test public void testHeight()
    {
        region = new Region(
                new Point(0, 1),
                new Point(1, 0)
        );

        assertThat(region.height(), is(2));

        region.southWest(new Point(0, 500));

        assertThat(region.height(), is(501));
    }

    @Test public void testWidth()
    {
        region = new Region(
                new Point(0, 1),
                new Point(1, 0)
        );

        assertThat(region.width(), is(2));

        region.northEast(new Point(600, 0));

        assertThat(region.width(), is(601));
    }

    @Test public void testArea()
    {
        region = new Region(
                new Point(0, 1),
                new Point(1, 0)
        );

        assertThat(region.area(), is(4));
    }

    @Test public void testCircumference()
    {
        region = new Region(
                new Point(0, 1),
                new Point(1, 0)
        );

        assertThat(region.circumference(), is(8));
    }
}