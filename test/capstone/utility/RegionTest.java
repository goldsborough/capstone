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
                new Point(0, 0),
                new Point(1, 1)
        );

        assertThat(region.southWest(), is(new Point(0, 0)));
        assertThat(region.northEast(), is(new Point(1, 1)));
    }

    @Test public void testCoordinateConstructor()
    {
        region = new Region(0, 0, 1, 1);

        assertThat(region.southWest(), is(new Point(0, 0)));
        assertThat(region.northEast(), is(new Point(1, 1)));
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
                new Point(0, 2),
                new Point(1, 1)
        );
    }

    @Test public void testAllIndividualPointAccessorsAreCorrect()
    {
        region = new Region(
                new Point(0, 0),
                new Point(1, 1)
        );

        assertThat(region.southWest(), is(new Point(0, 0)));
        assertThat(region.southEast(), is(new Point(0, 1)));

        assertThat(region.northWest(), is(new Point(1, 0)));
        assertThat(region.northEast(), is(new Point(1, 1)));
    }

    @Test public void testGeneralizedPointAccessorIsCorrect()
    {
        region = new Region(
                new Point(0, 0),
                new Point(1, 1)
        );

        assertThat(
                region.at(Region.Vertical.SOUTH, Region.Horizontal.WEST),
                is(new Point(0, 0))
        );

        assertThat(
                region.at(Region.Vertical.SOUTH, Region.Horizontal.EAST),
                is(new Point(0, 1))
        );


        assertThat(
                region.at(Region.Vertical.NORTH, Region.Horizontal.WEST),
                is(new Point(1, 0))
        );

        assertThat(
                region.at(Region.Vertical.NORTH, Region.Horizontal.EAST),
                is(new Point(1, 1))
        );
    }

    @Test public void testSetters()
    {
        Point point = new Point(123, 123);

        region.southWest(point);
        region.southEast(point);
        region.northWest(point);
        region.northEast(point);

        assertThat(region.southWest(), is(point));
        assertThat(region.southEast(), is(point));
        assertThat(region.northWest(), is(point));
        assertThat(region.northEast(), is(point));
    }

    @Test(expected=AssertionError.class)
    public void testContstaintsAreMaintainedForSetting()
    {
        Point point = new Point(123, 123);

        region.southWest(point);
        region.southEast(new Point(0, 0));
    }

    @Test public void testContainsIsCorrectForContainedPoints()
    {
        assertTrue(region.contains(new Point(0, 0)));
        assertTrue(region.contains(new Point(0, 1)));
        assertTrue(region.contains(new Point(1, 0)));
        assertTrue(region.contains(new Point(1, 1)));
    }

    @Test public void testcontainsIsCorrectForNonContainedPoints()
    {
        assertFalse(region.contains(new Point(5, 5)));
        assertFalse(region.contains(new Point(3, 1)));
        assertFalse(region.contains(new Point(2, 2)));
    }

    @Test public void testHeight()
    {
        assertThat(region.height(), is(1));

        region.northEast(new Point(3, 500));

        assertThat(region.height(), is(500));
    }

    @Test public void testWidth()
    {
        assertThat(region.width(), is(1));

        region.northEast(new Point(600, 4));

    }
}