package capstone.element;

import capstone.data.Representation;
import capstone.data.Theme;
import capstone.utility.Point;
import capstone.utility.Region;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * The base-class for all renderable elements of the game.
 *
 * Defines some base operations and attributes all elements have in common.
 * All elements are defined by a point on the screen, a kind enum member to
 * cheat the type-system as well as a Representation object used to render
 * the element onto the screen. All elements can be rendered onto a screen
 * and "unrendered" off it.
 *
 */
public abstract class Element
{
    /**
     * The kind of each Element. A way to cheat the type-system and
     * "fade" the world between an Element's static and dynamic type.
     *
     * The PLAYER kind is a bit different than all others. This Kind
     * must exist because every Element has a kind and a Player is an Element,
     * but at the same time it is unwanted in many contexts because it
     * has a special status. That is why the kinds() method returns
     * a collection of all kinds except Player. The factory method in
     * the Element class also does now allow creation of Players.
     *
     */
    public enum Kind
    {
        WALL,
        ENTRANCE,
        EXIT,
        KEY,
        STATIC_OBSTACLE,
        DYNAMIC_OBSTACLE,
        MYSTERY_BOX,
        PLAYER;

        /**
         * @return All kinds except for PLAYER.
         */
        public static Collection<Kind> kinds()
        {
            return Collections.unmodifiableCollection(_kinds);
        }

        /**
         *
         * Factory-function get the a Kind given it's code.
         *
         * @param code The code (ordinal) of the enum.
         *
         * @return The Kind associated with that code.
         */
        public static Kind fromCode(int code)
        {
            switch(code)
            {
                case 0: return WALL;
                case 1: return ENTRANCE;
                case 2: return EXIT;
                case 3: return KEY;
                case 4: return STATIC_OBSTACLE;
                case 5: return DYNAMIC_OBSTACLE;
                case 6: return MYSTERY_BOX;
                case 7: return PLAYER;
            }

            throw new AssertionError();
        }

        /**
         * @return The result of toString(), but with proper
         *         capitalization and underlines replaced with spaces.
         */
        public String toLowerString()
        {
            // Lazy caching
            if (_lowerString == null)
            {
                _lowerString = _toLowerString(this.toString());
            }

            return _lowerString;
        }

        /**
         * @return The code (ordinal) of the Kind.
         */
        public int code()
        {
            return ordinal();
        }

        /**
         *
         * Turns the toString() of the Kind into a nicer representation.
         *
         * @param string The result of toString()
         *
         * @return The result of toString(), but with proper
         *         capitalization and underlines replaced with spaces.
         */
        private static String _toLowerString(String string)
        {
            StringBuilder builder = new StringBuilder();

            for (String word : string.split("_"))
            {
                builder.append(word.charAt(0));

                builder.append(word.substring(1).toLowerCase());

                builder.append(" ");
            }

            return builder.toString();
        }

        /**
         * All kinds except PLAYER.
         */
        private static final ArrayList<Kind> _kinds = new ArrayList<Kind>(
                Arrays.asList(
                        WALL,
                        ENTRANCE,
                        EXIT,
                        KEY,
                        STATIC_OBSTACLE,
                        DYNAMIC_OBSTACLE,
                        MYSTERY_BOX
        ));

        private String _lowerString;
    }

    /**
     *
     * Factory-function for Elements.
     *
     * @param kind The Kind of the Element to create. Must not be PLAYER.
     *
     * @param point The Point the Element should be constructed at.
     *
     * @param theme The Theme containing the Representation for the new Element.
     *
     * @return A newly constructed Element.
     */
    public static Element Create(Kind kind, Point point, Theme theme)
    {
        assert(kind != null);
        assert(point != null);
        assert(theme != null);
        assert(kind != Kind.PLAYER);

        Representation representation = theme.representation(kind);

        switch (kind)
        {
            case WALL:
                return new Wall(point, representation);

            case ENTRANCE:
                return new Entrance(point, representation);

            case EXIT:
                return new Exit(point, representation);

            case KEY:
                return new Key(point, representation);

            case STATIC_OBSTACLE:
                return new StaticObstacle(point, representation);

            case DYNAMIC_OBSTACLE:
                return new IntelligentObstacle(point, representation);

            case MYSTERY_BOX:
                return new MysteryBox(point, representation);
        }

        throw new IllegalArgumentException("Kind invalid!");
    }

    /**
     *
     * Constructs a new Element.
     *
     * @param kind The Kind of the Element to create.
     *
     * @param point The Point the Element should be constructed at.
     *
     * @param representation The Representation for the Element.
     *
     */
    public Element(Kind kind,
                   Point point,
                   Representation representation)
    {
        _kind = kind;

        this.point(point);
        this.representation(representation);
    }

    /**
     *
     * Copy-constructor.
     *
     * @param other The other Element to copy this one from.
     */
    public Element(Element other)
    {
        this(other.kind(), other.point(), other.representation());
    }

    /**
     *
     * Renders the Element onto the screen, relativeTo the region.
     *
     * The relativeTo Region is necessary because the points of Elements
     * are always absolute with respected to the level, while they must
     * be relative to the Terminal when (un)rendered. Thus the relativeTo
     * Region is first "subtracted" from the Point. The relativeTo Region
     * must be the Region the Element is contained in.
     *
     * @param screen The Screen to render the Element onto.
     *
     * @param relativeTo The Region containing the Element.
     */
    public void render(Screen screen, Region relativeTo)
    {
        _render(screen, relativeTo);
    }

    /**
     *
     * Unrenders the Element onto the screen, relativeTo the region.
     *
     * The relativeTo Region is necessary because the points of Elements
     * are always absolute with respected to the level, while they must
     * be relative to the Terminal when (un)rendered. Thus the relativeTo
     * Region is first "subtracted" from the Point. The relativeTo Region
     * must be the Region the Element is contained in.
     *
     * @param screen The Screen to unrender the Element from.
     *
     * @param relativeTo The Region containing the Element.
     */
    public void unrender(Screen screen, Region relativeTo)
    {
        ScreenWriter writer = new ScreenWriter(screen);

        writer.setBackgroundColor(Terminal.Color.DEFAULT);
        writer.setForegroundColor(Terminal.Color.DEFAULT);

        writer.drawString(
                _point.x() - relativeTo.southWest().x(),
                _point.y() - relativeTo.northEast().y(),
                " "
        );
    }

    /**
     * @return The Kind of the Element.
     */
    public Kind kind()
    {
        return _kind;
    }

    /**
     * @return The current Point of the Element.
     */
    public Point point()
    {
        return _point;
    }

    /**
     *
     * Sets the Point of the Element.
     *
     * @param point The new Point for the Element.
     */
    public void point(Point point)
    {
        assert(point != null);

        _point = point;
    }

    /**
     * @return The Representation of the Element.
     */
    public Representation representation()
    {
        return _representation;
    }

    /**
     *
     * Sets the Representation of the Element.
     *
     * @param representation The new Representation for the Element.
     */
    public void representation(Representation representation)
    {
        assert(representation != null);

        _representation = representation;
    }

    /**
     *
     * Checks equality between the Element and an object.
     *
     * @param object The object to check equality for.
     *
     * @return True if the object is an Element of
     *         the same kind, at the same point.
     */
    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof  Element)) return false;

        if (object == this) return true;

        Element other = (Element) object;

        return this._kind.equals(other._kind)   &&
               this._point.equals(other._point);
    }

    /**
     *
     * Renders the Element onto the Screen relative to the Region,
     * and also applying the given styles. This method can be used
     * by subclasses that wish to add some styles to the rendering
     * of the Elements of their kind. The default implementation of
     * render() in Element calls this method with no styles.
     *
     * @param screen The Screen to render Elements onto.
     *
     * @param relativeTo The Region containing the Element.
     *
     * @param styles The array of ScreenCharacterStyles to apply.
     */
    protected void _render(Screen screen,
                           Region relativeTo,
                           ScreenCharacterStyle... styles)
    {
        ScreenWriter writer = new ScreenWriter(screen);

        writer.setBackgroundColor(_representation.background());
        writer.setForegroundColor(_representation.foreground());

        writer.drawString(
                _point.x() - relativeTo.southWest().x(),
                _point.y() - relativeTo.northEast().y(),
                Character.toString(_representation.character()),
                styles
        );
    }

    protected final Kind _kind;

    protected Point _point;

    protected Representation _representation;
}
