package capstone.utility;

import capstone.utility.Delta;
import capstone.utility.Pattern;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by petergoldsborough on 12/29/15.
 */
public class NewPatternTest
{
    @Test(expected=AssertionError.class)
    public void testVariadicConstructorThrowsForEmpyDirections()
    {
        new Pattern();
    }

    @Test public void testVariadicConstructorResultsInCorrectSequence()
    {
        Pattern pattern = new Pattern(
                Delta.Left(),
                Delta.Left(),
                Delta.Up(),
                Delta.Right(),
                Delta.Down()
        );

        assertThat(pattern.length(), is(5));

        assertThat(pattern.at(0), is(Delta.Left()));
        assertThat(pattern.at(1), is(Delta.Left()));
        assertThat(pattern.at(2), is(Delta.Up()));
        assertThat(pattern.at(3), is(Delta.Right()));
        assertThat(pattern.at(4), is(Delta.Down()));
    }

    @Test(expected=AssertionError.class)
    public void testCollectionConstructorThrowsForEmpyDirections()
    {
        new Pattern(new ArrayList<>());
    }

    @Test public void testCollectionConstructorResultsInCorrectSequence()
    {
        Pattern pattern = new Pattern(Arrays.asList(
                Delta.Left(),
                Delta.Left(),
                Delta.Up(),
                Delta.Right(),
                Delta.Down()
        ));

        assertThat(pattern.length(), is(5));

        assertThat(pattern.at(0), is(Delta.Left()));
        assertThat(pattern.at(1), is(Delta.Left()));
        assertThat(pattern.at(2), is(Delta.Up()));
        assertThat(pattern.at(3), is(Delta.Right()));
        assertThat(pattern.at(4), is(Delta.Down()));
    }

    @Test public void testPointStringConstructorResultsInCorrectSequence()
    {
        Pattern pattern = new Pattern("2(-1, 0)(0,-1)(1,0)(0,1)");

        assertThat(pattern.length(), is(5));

        assertThat(pattern.at(0), is(Delta.Left()));
        assertThat(pattern.at(1), is(Delta.Left()));
        assertThat(pattern.at(2), is(Delta.Up()));
        assertThat(pattern.at(3), is(Delta.Right()));
        assertThat(pattern.at(4), is(Delta.Down()));
    }

    @Test public void testShortStringConstructorResultsInCorrectSequence()
    {
        Pattern pattern = new Pattern("2lurd");

        assertThat(pattern.length(), is(5));

        assertThat(pattern.at(0), is(Delta.Left()));
        assertThat(pattern.at(1), is(Delta.Left()));
        assertThat(pattern.at(2), is(Delta.Up()));
        assertThat(pattern.at(3), is(Delta.Right()));
        assertThat(pattern.at(4), is(Delta.Down()));

        pattern = new Pattern("urdruldl");

        assertThat(pattern.length(), is(8));

        assertThat(pattern.at(0), is(Delta.Up()));
        assertThat(pattern.at(1), is(Delta.Right()));
        assertThat(pattern.at(2), is(Delta.Down()));
        assertThat(pattern.at(3), is(Delta.Right()));
        assertThat(pattern.at(4), is(Delta.Up()));
        assertThat(pattern.at(5), is(Delta.Left()));
        assertThat(pattern.at(6), is(Delta.Down()));
        assertThat(pattern.at(7), is(Delta.Left()));
    }
}