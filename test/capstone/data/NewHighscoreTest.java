package capstone.data;

import capstone.data.Highscore;
import capstone.element.Element;
import capstone.utility.KeyMap;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by petergoldsborough on 12/29/15.
 */
public class NewHighscoreTest
{
    private Highscore highscore;

    public <T, U> void assertEquals(Collection<T> first, Collection<U> second)
    {
        assertTrue(first.containsAll(second));
        assertTrue(second.containsAll(first));
    }

    @Test public void testConstructsWellFromProperties()
    {
        Properties properties = new Properties();

        properties.setProperty("level", "Test");

        properties.setProperty("1.230", "Player1, Player2");

        properties.setProperty("3.140", "Player3");

        properties.setProperty("7.680", "Player4");

        properties.setProperty("10.000", "Player3");

        highscore = new Highscore(properties);

        assertThat(highscore.size(), is(4));

        List<String> players = new ArrayList<>();

        players.add("Player1");
        players.add("Player2");

        assertThat(highscore.playersAt(1), is(players));

        players.clear();
        players.add("Player3");

        assertThat(highscore.playersAt(2), is(players));
        assertThat(highscore.playersAt(4), is(players));

        players.clear();
        players.add("Player4");

        assertThat(highscore.playersAt(3), is(players));
    }

    @Test public void testConstructsWellFromFile() throws IOException
    {
        Properties properties = new Properties();

        properties.setProperty("level", "Test");

        properties.setProperty("1.230", "Player1, Player2");

        properties.setProperty("3.140", "Player3");

        properties.setProperty("7.680", "Player4");

        properties.setProperty("10.000", "Player3");

        File file = new File("mock.highscore");

        properties.store(
                new BufferedOutputStream(new FileOutputStream(file)),
                "Mock Highscore"
        );

        highscore = new Highscore(file);

        assertThat(highscore.size(), is(4));

        ArrayList<String> players = new ArrayList<>();

        players.add("Player1");
        players.add("Player2");

        assertThat(highscore.playersAt(1), is(players));

        players.clear();
        players.add("Player3");

        assertEquals(highscore.playersAt(2), players);
        assertEquals(highscore.playersAt(4), players);

        players.clear();
        players.add("Player4");

        assertThat(highscore.playersAt(3), is(players));

        assert(file.delete());
    }

    @Test public void testConstructsWellFromProfileMap()
    {
        Representation representation = new Representation(
                '',
                Terminal.Color.RED,
                Terminal.Color.BLUE
        );

        Profile profile = new Profile(
                "test",
                "Real Name",
                KeyMap.Arrows(),
                representation
        );

        HashMap<Double, List<Profile>> map = new HashMap<>();

        List<Profile> profiles = new ArrayList<>();

        profiles.add(new Profile(profile));

        map.put(1.23, new ArrayList<>(profiles));

        profile.id("test2");
        profile.keyMap(KeyMap.WASD());

        profiles.add(new Profile(profile));

        map.put(4.56, new ArrayList<>(profiles));


        highscore = new Highscore("Test", map);

        assertThat(highscore.timeAt(1), is(1.23));
        assertThat(highscore.timeAt(2), is(4.56));

        assertEquals(
                highscore.playersAt(2),
                Arrays.asList("test", "test2")
        );

        profiles.remove(1);

        assertEquals(
                highscore.playersAt(1),
                Collections.singletonList("test")
        );
    }

    @Test public void testConstructsWellFromSingleProfile()
    {
        Representation representation = new Representation(
                '',
                Terminal.Color.RED,
                Terminal.Color.BLUE
        );

        Profile profile = new Profile(
                "test",
                "Real Name",
                KeyMap.Arrows(),
                representation
        );

        List<Profile> profiles = new ArrayList<>();

        profiles.add(profile);

        highscore = new Highscore("Test", 1.23, profiles);

        assertThat(highscore.size(), is(1));

        assertThat(highscore.timeAt(1), is(1.23));

        assertEquals(
                highscore.playersAt(1),
                Collections.singletonList("test")
        );
    }
}
