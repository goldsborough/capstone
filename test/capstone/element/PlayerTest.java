package capstone.element;

import capstone.data.Profile;
import capstone.data.Representation;
import capstone.element.Player;
import capstone.utility.KeyMap;
import capstone.utility.Point;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by petergoldsborough on 12/28/15.
 */

public class PlayerTest
{
    private Player player;
    private Point point;
    private Profile profile;

    @Before public void setUp()
    {
        point = new Point(5, 5);

        Representation representation = new Representation(
                '$',
                Terminal.Color.BLACK,
                Terminal.Color.RED
        );

        profile = new Profile(
                "test",
                "Real Name",
                KeyMap.Arrows(),
                representation
        );

        player = new Player(point, profile);
    }

    @Test public void testConstructsWell()
    {
        assertThat(player.profile(), is(new Profile(profile)));

        assertThat(player.id(), is(profile.id()));

        assertThat(player.lives(), is(Player.MAXIMUM_LIVES));

        assertTrue(player.isAlive());

        assertFalse(player.isDead());
    }

    @Test(expected=AssertionError.class)
    public void testCannotGoBackAfterConstructionBeforeHavingMadeAtLeastOneMove()
    {
        player.goBack();
    }

    @Test public void testCopyConstructsWell()
    {
        Player copy = new Player(player);

        assertThat(copy, is(player));
    }

    @Test public void testInvidualMoveUp()
    {
        player.moveUp();

        assertThat(player.point(), is(point.above()));
    }

    @Test public void testInvidualMoveDown()
    {
        player.moveDown();

        assertThat(player.point(), is(point.below()));
    }

    @Test public void testInvidualMoveLeft()
    {
        player.moveLeft();

        assertThat(player.point(), is(point.left()));
    }

    @Test public void testInvidualMoveRight()
    {
        player.moveRight();

        assertThat(player.point(), is(point.right()));
    }

    @Test public void testGeneralizedMoveUp()
    {
        player.move(Direction.UP);

        assertThat(player.point(), is(point.above()));
    }

    @Test public void testGeneralizedMoveDown()
    {
        player.move(Direction.DOWN);

        assertThat(player.point(), is(point.below()));
    }

    @Test public void testGeneralizedMoveLeft()
    {
        player.move(Direction.LEFT);

        assertThat(player.point(), is(point.left()));
    }

    @Test public void testGeneralizedMoveRight()
    {
        player.move(Direction.RIGHT);

        assertThat(player.point(), is(point.right()));
    }

    @Test(expected=AssertionError.class)
    public void testThrowsForUpMoveWhenDead()
    {
        while(player.isAlive()) player.injure();

        player.moveUp();
    }

    @Test(expected=AssertionError.class)
    public void testThrowsForDownMoveWhenDead()
    {
        while(player.isAlive()) player.injure();

        player.moveDown();
    }

    @Test(expected=AssertionError.class)
    public void testThrowsForLeftMoveWhenDead()
    {
        while(player.isAlive()) player.injure();

        player.moveLeft();
    }

    @Test(expected=AssertionError.class)
    public void testThrowsForRightMoveWhenDead()
    {
        while(player.isAlive()) player.injure();

        player.moveRight();
    }

    @Test(expected=AssertionError.class)
    public void testThrowsForBackMoveWhenDead()
    {
        while(player.isAlive()) player.injure();

        player.goBack();
    }

    @Test public void testInjure()
    {
        int before = player.lives();

        player.injure();

        assertThat(player.lives(), is(before - 1));
    }

    @Test public void testHeal()
    {
        int before = player.lives();

        player.injure();

        assert(player.lives() != before);

        player.heal();

        assertThat(player.lives(), is(before));
    }

    @Test public void testDoesNotHealBeyondMaximum()
    {
        assert(player.lives() <= Player.MAXIMUM_LIVES);

        while (player.lives() < Player.MAXIMUM_LIVES) player.heal();

        player.heal();

        assertThat(player.lives(), is(Player.MAXIMUM_LIVES));
    }

    @Test(expected=AssertionError.class)
    public void testThrowsWhenInjuringAndAlreadyDead()
    {
        assert(player.lives() >= 0);

        while(player.lives() > 0) player.injure();

        player.injure();
    }

    @Test public void testIsDeadAndNotAliveWhenLivesZero()
    {
        while(player.lives() > 0) player.injure();

        assertTrue(player.isDead());
        assertFalse(player.isAlive());
    }

    @Test public void testGoBackWorks()
    {
        player.moveLeft();

        assert(! player.point().equals(point));

        player.goBack();

        assertThat(player.point(), is(point));

        player.moveDown();
        player.moveRight();

        player.goBack();

        assertThat(player.point(), is(point.below()));
    }

    @Test(expected=AssertionError.class)
    public void testGoBackThrowsWhenCalledTwiceInARow()
    {
        player.moveLeft();
        player.goBack();
        player.goBack();
    }
}