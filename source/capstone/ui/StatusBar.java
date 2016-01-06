package capstone.ui;

import capstone.data.Profile;
import capstone.element.Player;
import capstone.game.Level;
import capstone.utility.Dimensions;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by petergoldsborough on 01/05/16.
 */
public class StatusBar
{
    public StatusBar(Level level)
    {
        this.level(level);
    }

    public Level level()
    {
        return _level;
    }

    public void level(Level level)
    {
        assert(level != null);

        _level = level;
    }

    public void draw()
    {
        int row = _level.pageSize().getRows();

        _writer = new ScreenWriter(_level.screen());

        _drawGameStatus(row++);

        for (Player player : _level.players())
        {
            _drawPlayerStatus(player, row++);
        }

        for (Profile profile : _level.hidden())
        {
            _drawHiddenPlayerStatus(profile, row++);
        }
    }

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
                _dimensions()
        );

        int padding = _level.pageSize().getColumns();

        StringBuilder builder = new StringBuilder();

        builder.append(left);
        builder.append(_empty(padding - left.length() - right.length()));
        builder.append(right);

        _writer.drawString(0, row, builder.toString());
    }

    private void _drawPlayerStatus(Player player, int row)
    {
        _drawCharacter(player.profile(), row);

        int width = _level.pageSize().getColumns() - 2;

        _writer.drawString(2, row, player.toString(width));
    }

    private Dimensions _dimensions()
    {
        Dimensions dimensions = new Dimensions(_level.grid().dimensions());

        dimensions.height(dimensions.height() - 1);
        dimensions.width(dimensions.width() - 1);

        return dimensions;
    }

    private void _drawHiddenPlayerStatus(Profile profile, int row)
    {
        _drawCharacter(profile, row);

        String status = String.format("%1$s: HIDDEN", profile.id());

        _writer.drawString(2, row, status);
    }

    private void _drawCharacter(Profile profile, int row)
    {
        _writer.setBackgroundColor(profile.representation().background());
        _writer.setForegroundColor(profile.representation().foreground());

        Character character = profile.representation().character();

        _writer.drawString(0, row, character.toString());

        _writer.setBackgroundColor(Terminal.Color.DEFAULT);
        _writer.setForegroundColor(Terminal.Color.DEFAULT);
    }

    private static String _empty(int width)
    {
        return new String(new char[width]).replace("\0", " ");
    }

    private Level _level;

    private ScreenWriter _writer;
}
