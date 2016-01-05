package capstone.element;

import capstone.data.Profile;
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

    public enum Direction { UP, DOWN, LEFT, RIGHT }

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

    public Player moveUp()
    {
        assert(isAlive());
        assert(_point.y() > 0);

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
        assert(_point.x() > 0);

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
        
        // Should never go back twice in a row
        assert(_previousPoint != null);

        _point = _previousPoint;
        _previousPoint = null;

        return this;
    }

    public boolean canGoBack()
    {
        return _previousPoint != null;
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
                    "%1$s: %2$d/%3$d Lives\t %4$s",
                    id(),
                    lives(),
                    Player.MAXIMUM_LIVES,
                    point()
            );
        }

        else return String.format("%1$s: DEAD", id());
    }

    private Profile _profile;

    private int _lives;

    private Point _previousPoint;
}
