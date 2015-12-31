package capstone.data;

import capstone.data.Representation;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public class RepresentationTest
{
    private Representation representation;

    @Before public void setUp()
    {
        representation = new Representation(
                '',
                Terminal.Color.RED,
                Terminal.Color.BLUE
        );
    }

    @Test public void testConstructsWell()
    {
        assertThat(representation.character(), is(''));
        assertThat(representation.background(), is(Terminal.Color.RED));
        assertThat(representation.foreground(), is(Terminal.Color.BLUE));
    }

    @Test public void testCopyConstructsWell()
    {
        Representation copy = new Representation(representation);

        assertThat(copy, is(representation));
    }

    @Test public void testEquals()
    {
        Representation other = new Representation(
                '',
                Terminal.Color.RED,
                Terminal.Color.BLUE
        );

        assertThat(other, is(representation));
        assertThat(representation, is(other));
    }
}