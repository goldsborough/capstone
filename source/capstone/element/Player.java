package capstone.element;

import capstone.data.Profile;
import capstone.utility.Delta;
import capstone.utility.Point;
import capstone.utility.Region;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;

/**
 * Created by petergoldsborough on 12/27/15.
 */
public class Player extends Element
{
    public static int MAXIMUM_LIVES = 3;

    public Player(Point point, Profile profile)
    {
        super(Element.Kind.PLAYER, point, profile.representation());

        _lives = MAXIMUM_LIVES;
        _profile = profile;
        _previousPoint = null;
    }

    public Player(Player other)
    {
        super(other);

        _lives = other._lives;
        _profile = other._profile;
        _previousPoint = null;

        assert(_lives <= MAXIMUM_LIVES);
    }


    @Override public void render(Screen screen, Region relativeTo)
    {
        _render(screen, relativeTo, ScreenCharacterStyle.Blinking);
    }

    public void injure()
    {
        assert(_lives <= MAXIMUM_LIVES);
        assert(isAlive());

        --_lives;
    }

    public void heal()
    {
        assert(_lives <= MAXIMUM_LIVES);

        if (_lives < MAXIMUM_LIVES) ++_lives;
    }

    public Player move(Direction direction)
    {
        assert(direction != null);
        assert(isAlive());

        switch(direction)
        {
            case UP: moveUp(); break;

            case DOWN: moveDown(); break;

            case LEFT: moveLeft(); break;

            case RIGHT: moveRight(); break;
        }

        return this;
    }

    public Player move(int dx, int dy)
    {
        assert(isAlive());

        _previousPoint = _point;

        // Modifications always on a new point.
        _point = new Point(_point).move(dx, dy);

        return this;
    }

    public Player move(Delta delta)
    {
        return move(delta.x(), delta.y());
    }

    public Player moveUp()
    {
        assert(isAlive());

        _previousPoint = _point;
        _point = _point.above();

        return this;
    }

    public Player moveDown()
    {
        assert(isAlive());

        _previousPoint = _point;
        _point = _point.below();

        return this;
    }

    public Player moveLeft()
    {
        assert(isAlive());

        _previousPoint = _point;
        _point = _point.left();

        return this;
    }

    public Player moveRight()
    {
        assert(isAlive());

        _previousPoint = _point;
        _point = _point.right();

        return this;
    }

    public Player goBack()
    {
        assert(isAlive());
        assert(canGoBack());

        _point = _previousPoint;

        return this;
    }

    public boolean wouldGoNegative(Delta delta)
    {
        return _point.wouldGoNegative(delta);
    }

    public boolean canGoBack()
    {
        return _previousPoint != null;
    }

    public Point previousPoint()
    {
        return _previousPoint;
    }

    public String id()
    {
        return _profile.id();
    }

    public Profile profile()
    {
        return _profile;
    }

    public Integer lives()
    {
        return _lives;
    }

    public boolean isAlive()
    {
        return _lives > 0;
    }

    public boolean isDead()
    {
        return _lives == 0;
    }

    public boolean hasFullHealth()
    {
        return _lives == MAXIMUM_LIVES;
    }

    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof Player)) return false;

        if (object == this) return true;

        Player other = (Player) object;

        return this.id().equals(other.id());
    }

    public String toString()
    {
        if (isAlive())
        {
            return String.format(
                    "%1$s: %2$d/%3$d Lives Position: %4$s",
                    id(),
                    lives(),
                    Player.MAXIMUM_LIVES,
                    point()
            );
        }

        else return String.format("%1$s: DEAD", id());
    }

    public String toString(int width)
    {
        if (isAlive())
        {
            String left = String.format(
                    "%1$s: %2$d/%3$d Lives",
                    id(),
                    lives(),
                    Player.MAXIMUM_LIVES
            );

            String right = String.format(
                    "Position: %1$s",
                    point()
            );

            StringBuilder builder = new StringBuilder();

            builder.append(left);
            builder.append(_empty(width - left.length() - right.length()));
            builder.append(right);

            return builder.toString();
        }

        else return String.format("%1$s: DEAD", id());
    }

    private static String _empty(int width)
    {
        return new String(new char[width]).replace("\0", " ");
    }

    private Profile _profile;

    private int _lives;

    private Point _previousPoint;
}
