package capstone.ui;

import capstone.data.Profile;
import capstone.element.Player;
import capstone.game.Level;
import capstone.utility.Dimensions;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Manages showing the StatusBar for a Level.
 *
 * The StatusBar shows the current status for the whole game, which consists
 * of the ratio of keys collected to the number of total keys, as well
 * as the page-index of the current page in the page-grid. Then, for each
 * Player, it shows the status of that Player. If the Player is alive, the
 * representation will be descriptive in the Player's number of lives and
 * position on the screen. If the Player is dead, that is displayed instead
 * of any status. Also, it is displayed if a Player is still hidden.
 *
 */
public class StatusBar
{
    /**
     *
     * Constructs a new StatusBar for a given Level.
     *
     * It is not drawn immediately. Use the draw() method for that.
     *
     * @param level The Level to show the StatusBar for.
     */
    public StatusBar(Level level)
    {
        this.level(level);
    }

    /**
     * @return The level associated with this StatusBar.
     */
    public Level level()
    {
        return _level;
    }

    /**
     *
     * Sets the level associated with this StatusBar.
     *
     * @param level The level to associate with this StatusBar.
     */
    public void level(Level level)
    {
        assert(level != null);

        _level = level;
    }

    /**
     * Draws the StatusBar on the level's Screen.
     */
    public void draw()
    {
        // Get the one-past-the-end row of page. Calling pageSize
        // ensures there is enough space for the StatusBar.
        int row = _level.pageSize().getRows();

        _writer = new ScreenWriter(_level.screen());

        // Meta-information about the level
        _drawGameStatus(row++);

        // Non-hidden players
        for (Player player : _level.players())
        {
            _drawPlayerStatus(player, row++);
        }

        // Hidden players
        for (Profile profile : _level.hidden())
        {
            _drawHiddenPlayerStatus(profile, row++);
        }
    }

    /**
     * @return The number of rows the status-bar will require to be drawn.
     */
    public int requiredRows()
    {
        int rows = 1; // level status

        // Dead and alive
        rows += _level.players().size();

        rows += _level.hidden().size();

        return rows;
    }

    /**
     *
     * Draws information about the level.
     *
     * @param row The row at which to draw on the screen.
     */
    private void _drawGameStatus(int row)
    {
        _writer.setBackgroundColor(Terminal.Color.RED);

        String left = String.format(
                "  Keys: %1$d/%2$d",
                _level.keysCollected(),
                _level.totalKeys()
        );

        String right = String.format(
                "Page: %1$s/%2$s",
                _level.grid().currentIndex(),
                _gridDimensions()
        );

        int padding = _level.pageSize().getColumns();

        StringBuilder builder = new StringBuilder();

        builder.append(left); // left adjusted
        builder.append(_empty(padding - left.length() - right.length()));
        builder.append(right); // right adjusted

        _writer.drawString(0, row, builder.toString());
    }

    /**
     *
     * Draws the status for an active player.
     *
     * All the string-processing has been outsourced to the
     * Player class so this is nice and short.
     *
     * @param player The Player to draw the status for.
     *
     * @param row The row at which to draw on the screen.
     */
    private void _drawPlayerStatus(Player player, int row)
    {
        _drawProfile(player.profile(), row);

        int width = _level.pageSize().getColumns() - 2;

        _writer.drawString(2, row, player.toString(width));
    }

    /**
     *
     * Draws the status for a hidden player.
     *
     * The format is ID: HIDDEN. This cannot be refactored to the Player
     * class because hidden players are not yet Players, but only Profiles
     * (they don't have a Point yet).
     *
     * @param profile The Profile of the player to draw the status for.
     *
     * @param row The row at which to draw on the screen.
     */
    private void _drawHiddenPlayerStatus(Profile profile, int row)
    {
        _drawProfile(profile, row);

        String status = String.format("%1$s: HIDDEN", profile.id());

        _writer.drawString(2, row, status);
    }

    /**
     * @return The dimensions of the grid, but inclusive.
     */
    private Dimensions _gridDimensions()
    {
        Dimensions dimensions = new Dimensions(_level.grid().dimensions());

        dimensions.height(dimensions.height() - 1);
        dimensions.width(dimensions.width() - 1);

        return dimensions;
    }

    /**
     *
     * Draws the character of Profile with the colors in its Representation.
     *
     * @param profile The Profile to draw.
     *
     * @param row The row at which to draw on the screen.
     */
    private void _drawProfile(Profile profile, int row)
    {
        _writer.setBackgroundColor(profile.representation().background());
        _writer.setForegroundColor(profile.representation().foreground());

        Character character = profile.representation().character();

        _writer.drawString(0, row, character.toString());

        _writer.setBackgroundColor(Terminal.Color.DEFAULT);
        _writer.setForegroundColor(Terminal.Color.DEFAULT);
    }

    /**
     *
     * Creates an empty string of the given width.
     *
     * @param width How many spaces the string should contain.
     *
     * @return The empty string of the specifed width.
     */
    private static String _empty(int width)
    {
        return new String(new char[width]).replace("\0", " ");
    }

    private Level _level;

    private ScreenWriter _writer;
}
