package capstone.element;

import capstone.data.Representation;
import capstone.element.MysteryBox;
import capstone.utility.Point;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public class MysteryBoxTest
{
    private MysteryBox box;

    @Before public void setUp()
    {
        Representation representation = new Representation(
          '?',
          Terminal.Color.BLACK,
          Terminal.Color.RED
        );

        box = new MysteryBox(
                new Point(0, 0),
                representation
        );
    }

    @Test public void testRevealsWell()
    {
        assertNotNull(box.reveal());
    }

    @Test(expected=AssertionError.class)
    public void testThrowsWhenRevealedASecondTime()
    {
        box.reveal();
        box.reveal();
    }
}