package capstone.ui;

import capstone.element.Player;
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

        ScreenWriter writer = new ScreenWriter(_level.screen());

        _drawGameStatus(writer, row++);

        for (Player player : _level.players())
        {
            _drawPlayerStatus(writer, player, row++);
        }
    }

    private void _drawGameStatus(ScreenWriter writer, int row)
    {
        String status = String.format(
                "Keys: %1$d/%2$d\tPage: %3$s",
                _level.keysCollected(),
                _level.totalKeys(),
                _level.grid().currentIndex()
        );

        writer.drawString(0, row, status);
    }

    private void _drawPlayerStatus(ScreenWriter writer, Player player, int row)
    {
        writer.setBackgroundColor(player.representation().background());
        writer.setForegroundColor(player.representation().foreground());

        Character character = player.representation().character();

        writer.drawString(0, row, character.toString());

        writer.setBackgroundColor(Terminal.Color.DEFAULT);
        writer.setForegroundColor(Terminal.Color.DEFAULT);

        String status = String.format(" %1$s", player);

        writer.drawString(player.id().length(), row, status);
    }

    private Level _level;
}
