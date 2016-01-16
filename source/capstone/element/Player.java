package capstone.element;

import capstone.data.Profile;
import capstone.utility.Delta;
import capstone.utility.Point;
import capstone.utility.Region;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;

/**
 * In MVC terms, the Player is the View and a Profile the Model, the
 * ProfileWindow possibly acting as the Controller. A Player is thus
 * the visual representation of a Profile. A Player is also an Element,
 * therefore has a point and a representation that can be rendered on
 * the screen. Different from most other Elements, the Player class provides
 * a variety of methods to perform motion on the Player's point. Also, a
 * Player is defined as having a certain number of lives, the maximum
 * and initial count being defined in the static variable MAXIMUM_LIVES
 * contained in the Player class. When the life-count of Player goes to
 * zero (by repetitive calls to injure()), a Player is said to be "Dead"
 * and before that the Player is defined as "Alive". Moreover, for various
 * purposes of game-logic a Player has the option of going-back (i.e.
 * going to the Point the Player was before its last motion), given the
 * Player has performed at least one such motion.
 */
public class Player extends Element
{
    public static final int MAXIMUM_LIVES = 3;

    /**
     *
     * Constructs the Player at the given Point, with the given Profile.
     *
     * @param point The Point at which to construct the Player.
     *
     * @param profile The Profile for the Player.
     */
    public Player(Point point, Profile profile)
    {
        super(Element.Kind.PLAYER, point, profile.representation());

        _lives = MAXIMUM_LIVES;
        _profile = profile;
        _previousPoint = null;
    }

    /**
     *
     * Copy-Constructor.
     *
     * @param other The other Player.
     */
    public Player(Player other)
    {
        super(other);

        _lives = other._lives;
        _profile = other._profile;
        _previousPoint = null;

        assert(_lives <= MAXIMUM_LIVES);
    }


    /**
     *
     * Renders the Player onto the Screen, relative to the Region.
     *
     * A Player has the Property that he/she is given an additional
     * styling in that he/she blinks on the screen.
     *
     * @param screen     The Screen to render the Element onto.
     *
     * @param relativeTo The Region containing the Element.
     */
    @Override public void render(Screen screen, Region relativeTo)
    {
        _render(screen, relativeTo, ScreenCharacterStyle.Blinking);
    }

    /**
     * Reduces the life-count of the Player, given the life-count is non-zero.
     */
    public void injure()
    {
        assert(_lives <= MAXIMUM_LIVES);
        assert(isAlive());

        --_lives;
    }

    /**
     * Increases the life-count of the Player if possible.
     * The life-count will never be increased past the MAXIMUM_LIVES.
     */
    public void heal()
    {
        assert(_lives <= MAXIMUM_LIVES);

        if (_lives < MAXIMUM_LIVES) ++_lives;
    }

    /**
     *
     * Moves the Player in the given Direction.
     *
     * @param direction The Direction to move the Player towards.
     *
     * @return The modified Player instance (this).
     */
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

    /**
     *
     * Moves the Player by the given delta-x and delta-y.
     *
     * Non-negativity-constraints are of course
     * maintained for the both coordinates.
     *
     * @param dx How much to move the x-component of the Player's point by.
     *
     * @param dy How much to move the y-component of the Player's point by.
     *
     * @return The modified Player instance (this).
     */
    public Player move(int dx, int dy)
    {
        assert(isAlive());

        _previousPoint = _point;

        // Modifications always on a new point.
        _point = new Point(_point).move(dx, dy);

        return this;
    }

    /**
     *
     * Moves the Player by the given Delta.
     *
     * @param delta The Delta to move the Player by.
     *
     * @return The modified Player instance (this).
     */
    public Player move(Delta delta)
    {
        return move(delta.x(), delta.y());
    }

    /**
     *
     * Moves the Player in the upward direction.
     *
     * @return The modified Player instance (this).
     */
    public Player moveUp()
    {
        assert(isAlive());

        _previousPoint = _point;
        _point = _point.above();

        return this;
    }

    /**
     *
     * Moves the Player in the downward direction.
     *
     * @return The modified Player instance (this).
     */
    public Player moveDown()
    {
        assert(isAlive());

        _previousPoint = _point;
        _point = _point.below();

        return this;
    }

    /**
     *
     * Moves the Player in the leftward direction.
     *
     * @return The modified Player instance (this).
     */
    public Player moveLeft()
    {
        assert(isAlive());

        _previousPoint = _point;
        _point = _point.left();

        return this;
    }

    /**
     *
     * Moves the Player in the rightward direction.
     *
     * @return The modified Player instance (this).
     */
    public Player moveRight()
    {
        assert(isAlive());

        _previousPoint = _point;
        _point = _point.right();

        return this;
    }

    /**
     *
     * Goes back to the previous Point of the Player
     *
     * Only allowed if the Player has performed at least one movement.
     *
     * The previous point of the Player is not reset, i.e. successive
     * calls to goBack() have no effect before any new motion is performed.
     * This is a cautionary measure to handle the case where a Player may
     * be asked to go back multiple times before having the chance to move
     * (like: dynamic-obstacle running against the Player).
     *
     * @return The modified Player instance (this).
     */
    public Player goBack()
    {
        assert(isAlive());
        assert(canGoBack());

        _point = _previousPoint;

        return this;
    }

    /**
     *
     * Checks if the Player's Point would go negative on either
     * component if the Delta were applied to the Player's Point.
     *
     * @param delta The Delta to test.
     *
     * @return True if the Delta would invalidate the constraints set
     *         for Points (non-negativity on both components), else true
     *         if the resulting Point were a valid one, such that move
     *         can be called with this Delta.
     */
    public boolean wouldGoNegative(Delta delta)
    {
        return _point.wouldGoNegative(delta);
    }

    /**
     *
     * Checks if the Player's Point would go outside the given Region if
     * the Delta were applied to the Player's point.
     *
     * @param delta The Delta to test.
     *
     * @return True if the Delta would invalidate the constraints set
     *         for Points (non-negativity on both components) or move
     *         the Player outside the Region, else true if the resulting
     *         Point were a valid one, such that move can be called with
     *         this Delta.
     */
    public boolean wouldGoOutside(Delta delta, Region region)
    {
        return _point.wouldGoOutside(delta, region);
    }

    /**
     * @return True if the Player has yet performed
     *         at least one motion, else false.
     */
    public boolean canGoBack()
    {
        return _previousPoint != null;
    }

    /**
     * @return The previous point of the player, which is null initially.
     */
    public Point previousPoint()
    {
        return _previousPoint;
    }

    /**
     * @return The ID of the player.
     */
    public String id()
    {
        return _profile.id();
    }

    /**
     * @return The Profile of the player.
     */
    public Profile profile()
    {
        return _profile;
    }

    /**
     * @return The number of lives of the Player.
     */
    public Integer lives()
    {
        return _lives;
    }

    /**
     * @return True if the life-count of the Player is positive, else false.
     */
    public boolean isAlive()
    {
        return _lives > 0;
    }

    /**
     *
     * Constraints are such that the life-count will never be negative.
     *
     * @return True if the life-count of the Player is zero, else false.
     */
    public boolean isDead()
    {
        return _lives == 0;
    }

    /**
     * @return Whether the Player's life-count is the maximum it can be.
     */
    public boolean hasFullHealth()
    {
        return _lives == MAXIMUM_LIVES;
    }

    /**
     *
     * Checks equality between this Player and an object.
     *
     * @param object The object to check equality for.
     *
     * @return True if the object is a Player with the same ID.
     */
    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof Player)) return false;

        if (object == this) return true;

        Player other = (Player) object;

        return this.id().equals(other.id());
    }

    /**
     * @return A string-representation of the Player, of the format:
     *         ID: life-count/maximum-life-count Lives Position: position
     *         if the Player is alive, else ID: DEAD
     *
     * @see Player#toString(int)
     */
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

    /**
     *
     * Pads the result of toString() such that the Position information
     * is right-adjusted and the ID and life-count information is left-
     * adjusted. The resulting String will have at least the width
     * specified.
     *
     * @param width The minimum width the resulting String should have.
     *
     * @return A string-representation with the same format as the toString()
     *         method, but with ID and life-count information left-adjusted
     *         and Position ifnormation right-adjusted to the width.
     *
     * @see Player#toString
     */
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

    /**
     * @param width How many spaces to create the empty string with.
     *
     * @return A string with *width*-many space characters if the
     *         width is non-negative, else an empty string.
     */
    private static String _empty(int width)
    {
        // This is not actually a constraint but it could
        // really happen that the status information is
        // greater than the width
        if (width <= 0) return "";

        return new String(new char[width]).replace("\0", " ");
    }

    private Profile _profile;

    private int _lives;

    private Point _previousPoint;
}
