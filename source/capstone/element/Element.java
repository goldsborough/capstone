package capstone.element;

import capstone.utility.Point;
import capstone.data.Representation;
import capstone.data.Theme;
import capstone.utility.Region;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public abstract class Element
{
    public enum Kind
    {
        WALL(0),
        ENTRANCE(1),
        EXIT(2),
        KEY(3),
        STATIC_OBSTACLE(4),
        DYNAMIC_OBSTACLE(5),
        MYSTERY_BOX(6),
        PLAYER(7);

        public static Collection<Kind> kinds()
        {
            return Collections.unmodifiableCollection(_kinds);
        }

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

                default: assert(false);
            }

            return null;
        }

        public String toLowerString()
        {
            return _lowerString;
        }

        public int code()
        {
            return _code;
        }

        private Kind(int code)
        {
            _code = code;
            _lowerString = _toLowerString(this.toString());
        }

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

        private final int _code;

        private final String _lowerString;
    }

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
                return new SequentialObstacle(point, representation);

            case MYSTERY_BOX:
                return new MysteryBox(point, representation);
        }

        throw new IllegalArgumentException("Kind invalid!");
    }

    public Element(Kind kind,
                   Point point,
                   Representation representation)
    {
        _kind = kind;

        this.point(point);
        this.representation(representation);
    }

    public Element(Element other)
    {
        this(other.kind(), other.point(), other.representation());
    }

    public void render(Screen screen, Region relativeTo)
    {
        _render(screen, relativeTo);
    }

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

    public Kind kind()
    {
        return _kind;
    }

    public Point point()
    {
        return _point;
    }

    public void point(Point point)
    {
        assert(point != null);

        _point = point;
    }

    public Representation representation()
    {
        return _representation;
    }

    public void representation(Representation representation)
    {
        assert(representation != null);

        _representation = representation;
    }

    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof  Element)) return false;

        if (object == this) return true;

        Element other = (Element) object;

        return this._kind.equals(other._kind)   &&
               this._point.equals(other._point);
    }

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
