package capstone.gui;

import capstone.gui.Level;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by petergoldsborough on 12/29/15.
 */
public class LevelTest
{
    private Level level;

    @Before public void setUp()
    {

    }

    @Test public void testSerialize()
    {
        Properties expected = new Properties();

        expected.setProperty("layout", "TestLayout");
        expected.setProperty("theme", "TestTheme");
        expected.setProperty("0,0", "Player1");
        expected.setProperty("3,4", "Player2");

        //assertThat(level.serialize(), is(expected));
    }
}