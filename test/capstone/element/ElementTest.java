package capstone.element;

import capstone.data.Representation;
import capstone.data.Theme;
import capstone.utility.Point;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by petergoldsborough on 12/28/15.
 */

class MockElement extends Element
{
    public MockElement(Point point, Representation representation)
    {
        super(Kind.WALL, point, representation);
    }

    public MockElement(MockElement other)
    {
        super(other);
    }
}

public class ElementTest
{
    private static Point point;

    private static Theme theme;

    @BeforeClass public static void setUp()
    {
        point = new Point(1, 2);

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

    @Test public void testConstructingKindFromCode()
    {
        assertThat(Element.Kind.fromCode(0), is(Element.Kind.WALL));
        assertThat(Element.Kind.fromCode(1), is(Element.Kind.ENTRANCE));
        assertThat(Element.Kind.fromCode(2), is(Element.Kind.EXIT));
        assertThat(Element.Kind.fromCode(3), is(Element.Kind.KEY));
        assertThat(Element.Kind.fromCode(4), is(Element.Kind.STATIC_OBSTACLE));
        assertThat(Element.Kind.fromCode(5), is(Element.Kind.DYNAMIC_OBSTACLE));
        assertThat(Element.Kind.fromCode(6), is(Element.Kind.MYSTERY_BOX));
    }

    @Test public void testConvertingKindsToCodes()
    {
        int expectedCode = 0;

        for (Element.Kind kind : Element.Kind.values())
        {
            assertThat(kind.code(), is(expectedCode++));
        }
    }

    @Test public void testElementFactory()
    {
        for (Element.Kind kind : Element.Kind.values())
        {
            if(kind == Element.Kind.PLAYER) continue;

            assertThat(Element.Create(kind, point, theme).kind(), is(kind));
        }
    }

    @Test(expected=AssertionError.class)
    public void testElementFactoryThrowsForNullKind()
    {
        Element.Create(null, point, theme);
    }

    @Test(expected=AssertionError.class)
    public void testElementFactoryThrowsForNullTheme()
    {
        Element.Create(Element.Kind.WALL, point, null);
    }

    @Test(expected=AssertionError.class)
    public void testElementFactoryThrowsForNullPoint()
    {
        Element.Create(Element.Kind.WALL, null, theme);
    }

    @Test(expected=AssertionError.class)
    public void testElementFactoryThrowsForPlayerKind()
    {
        Element.Create(null, point, theme);
    }

    @Test public void testConstructsWell()
    {
        Element element = new MockElement(
                point,
                theme.representation(Element.Kind.WALL)
        );

        Representation representation = new Representation(
                theme.representation(Element.Kind.WALL)
        );

        assertThat(element.kind(), is(Element.Kind.WALL));
        assertThat(element.point(), is(point));
        assertThat(element.representation(), is(representation));
    }

    @Test public void testCopyConstructsWell()
    {
        Element element = new MockElement(
                point,
                theme.representation(Element.Kind.WALL)
        );

        Element copy = new MockElement((MockElement) element);

        assertThat(copy, is(element));
    }

    @Test(expected=AssertionError.class)
    public void testConstructorThrowsForNullPoint()
    {
        Element element = new MockElement(
                null,
                theme.representation(Element.Kind.WALL)
        );
    }

    @Test(expected=AssertionError.class)
    public void testConstructorThrowsForNullRepresentation()
    {
        Element element = new MockElement(
                point,
                null
        );
    }

    @Test public void testAccessInterface()
    {
        Element element = new MockElement(
                point,
                theme.representation(Element.Kind.WALL)
        );

        element.point(new Point(5, 6));

        assertThat(element.point(), is(new Point(5, 6)));

        Representation other = theme.representation(Element.Kind.ENTRANCE);

        assert(! other.equals(element.representation()));

        element.representation(other);

        assertThat(element.representation(), is(other));

        assertThat(element.kind(), is(Element.Kind.WALL));
    }

    @Test(expected=AssertionError.class)
    public void testThrowsForNullPoint()
    {
        Element element = new MockElement(
                point,
                theme.representation(Element.Kind.WALL)
        );

        element.point(null);
    }

    @Test(expected=AssertionError.class)
    public void testThrowsForNullRepresentation()
    {
        Element element = new MockElement(
                point,
                theme.representation(Element.Kind.WALL)
        );

        element.representation(null);
    }

    @Test public void testEquals()
    {
        Element element = new MockElement(
                point,
                theme.representation(Element.Kind.WALL)
        );

        Element other = new MockElement(
                point,
                theme.representation(Element.Kind.WALL)
        );

        assertThat(element, is(other));
    }
}