package capstone.data;

import capstone.data.Representation;
import capstone.data.Theme;
import capstone.element.Element;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public class NewThemeTest
{
    @Test public void testConstructsWellFromProperties()
    {
        Properties properties = new Properties();

        properties.setProperty("name", "Mock");

        char character = 'A';

        for (Element.Kind kind : Element.Kind.values())
        {
            String string = kind.toString();

            properties.setProperty(string + ".character", Character.toString(character++));
            properties.setProperty(string + ".background", Terminal.Color.BLACK.toString());
            properties.setProperty(string + ".foreground", Terminal.Color.RED.toString());
        }

        Theme theme = new Theme(properties);

        assertThat(theme.name(), is("Mock"));

        character = 'A';

        Representation expected = new Representation(
                character,
                Terminal.Color.BLACK,
                Terminal.Color.RED
        );

        for (Element.Kind kind : Element.Kind.values())
        {
            assertThat(theme.representation(kind), is(expected));

            expected.character(++character);
        }
    }

    @Test public void testConstructsWellFromFile() throws IOException
    {
        Properties properties = new Properties();

        properties.setProperty("name", "Mock");

        char character = 'A';

        for (Element.Kind kind : Element.Kind.values())
        {
            String string = kind.toString();

            properties.setProperty(string + ".character", Character.toString(character++));
            properties.setProperty(string + ".background", Terminal.Color.BLACK.toString());
            properties.setProperty(string + ".foreground", Terminal.Color.RED.toString());
        }

        File file = new File("mock.theme");

        properties.store(new BufferedOutputStream(new FileOutputStream(file)), "Mock Theme");

        Theme theme = new Theme(file);

        assertThat(theme.name(), is("Mock"));

        character = 'A';

        Representation expected = new Representation(
                character,
                Terminal.Color.BLACK,
                Terminal.Color.RED
        );

        for (Element.Kind kind : Element.Kind.values())
        {
            assertThat(theme.representation(kind), is(expected));

            expected.character(++character);
        }

        assert(file.delete());
    }

    @Test public void testConstructsWellNormally()
    {
        HashMap<Element.Kind, Representation> map = new HashMap<>();

        char character = 'A';

        for (Element.Kind kind : Element.Kind.values())
        {
            Representation representation = new Representation(
                    character++,
                    Terminal.Color.BLACK,
                    Terminal.Color.RED
            );

            map.put(kind, representation);
        }

        Theme theme = new Theme("Mock", map);

        assertThat(theme.name(), is("Mock"));

        character = 'A';

        for (Element.Kind kind : Element.Kind.values())
        {
           Representation representation = new Representation(
                    character++,
                    Terminal.Color.BLACK,
                    Terminal.Color.RED
           );

            assertThat(theme.representation(kind), is(map.get(kind)));

            representation.character(character);
        }
    }
}