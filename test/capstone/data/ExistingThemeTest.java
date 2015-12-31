package capstone.data;

import capstone.data.Representation;
import capstone.data.Theme;
import capstone.element.Element;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public class ExistingThemeTest
{
    private Theme theme;

    @Before public void setUp()
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

        theme = new Theme("Mock", map);
    }

    @Test public void testNameAccess()
    {
        theme.name("Lol");

        assertThat(theme.name(), is("Lol"));
    }

    @Test(expected=AssertionError.class)
    public void testNameThrowsForNull()
    {
        theme.name(null);
    }

    @Test public void testDeserialization()
    {
        Properties properties = new Properties();

        properties.setProperty("name", "DeserializationMock");

        char character = '0';

        for (Element.Kind kind : Element.Kind.values())
        {
            String string = kind.toString();

            properties.setProperty(string + ".character", Character.toString(character++));
            properties.setProperty(string + ".background", Terminal.Color.GREEN.toString());
            properties.setProperty(string + ".foreground", Terminal.Color.BLUE.toString());
        }

        theme.deserialize(properties);

        assertThat(theme.name(), is("DeserializationMock"));

        character = '0';

        Representation expected = new Representation(
                character,
                Terminal.Color.GREEN,
                Terminal.Color.BLUE
        );

        for (Element.Kind kind : Element.Kind.values())
        {
            assertThat(theme.representation(kind), is(expected));

            expected.character(++character);
        }
    }

    @Test public void testSerialization()
    {
        Properties expected = new Properties();

        expected.setProperty("name", "Mock");

        char character = 'A';

        for (Element.Kind kind : Element.Kind.values())
        {
            String string = kind.toString();

            expected.setProperty(string + ".character", Character.toString(character++));
            expected.setProperty(string + ".background", Terminal.Color.BLACK.toString());
            expected.setProperty(string + ".foreground", Terminal.Color.RED.toString());
        }

        Properties result = theme.serialize();

        assertThat(result, is(expected));
    }

    @Test public void testSettingRepresentation()
    {
        Representation expected = new Representation(
                '@',
                Terminal.Color.BLACK,
                Terminal.Color.RED
        );

        theme.representation(Element.Kind.WALL, expected);
        assertThat(theme.representation(Element.Kind.WALL), is(expected));
    }

}