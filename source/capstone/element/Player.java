package capstone.element;

import capstone.data.Profile;
import capstone.utility.Point;

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

    public void move(Direction direction)
    {
        assert(direction != null);
        assert(isAlive());

        switch(direction)
        {
            case UP: moveUp(); break;

            case DOWN: moveDown(); break;

            case LEFT: moveLeft(); break;

            case RIGHT: moveRight(); break;

            default: assert(false);
        }
    }

    public void moveUp()
    {
        assert(isAlive());
        assert(_point.y() > 0);

        _previousPoint = _point;
        _point = _point.above();
    }

    public void moveDown()
    {
        assert(isAlive());

        _previousPoint = _point;
        _point = _point.below();
    }

    public void moveLeft()
    {
        assert(isAlive());
        assert(_point.x() > 0);

        _previousPoint = _point;
        _point = _point.left();
    }

    public void moveRight()
    {
        assert(isAlive());

        _previousPoint = _point;
        _point = _point.right();
    }

    public void goBack()
    {
        assert(isAlive());
        
        // Should never go back twice in a row
        assert(_previousPoint != null);

        _point = _previousPoint;
        _previousPoint = null;
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

        return super.equals(other)             &&
               _profile.equals(other._profile) &&
               _lives == other._lives;
    }

    private Profile _profile;

    private int _lives;

    private Point _previousPoint;
}
