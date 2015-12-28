package capstone;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;


/**
 * Created by petergoldsborough on 12/26/15.
 */

public class NewLevelTest {

    private Level level;
    private Profile profile;
/*
    @Before public void setUp() throws IOException
    {
        Properties layout = new Properties();

        layout.load(new BufferedInputStream(new FileInputStream("mock.level")));

        ArrayList<Profile> profiles = new ArrayList<>(1);

        profiles.add(profile);

        level = new Level(layout, profiles);
    }

    @Test public void isNotDoneAfterConstruction()
    {
        assertFalse(level.done());
    }

    @Test public void createsPlayerWellAfterConstruction() throws NoSuchFieldException
    {
        assertEquals(level.players().size(), 1);

        ArrayList<String> ids = new ArrayList<>(1);

        ids.add(profile.id());

        assertEquals(level.players(), ids);
    }

    @Test public void updatesPlayerPositionWell()
    {

    }
*/
}