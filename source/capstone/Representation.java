package capstone;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by petergoldsborough on 12/28/15.
 */

public final class Representation
{
    public Representation(Character character,
                          Terminal.Color background,
                          Terminal.Color foreground)
    {
        _character = character;
        _background = background;
        _foreground = foreground;
    }

    public Character character()
    {
        return _character;
    }

    public Terminal.Color background()
    {
        return _background;
    }

    public Terminal.Color foreground()
    {
        return _foreground;
    }

    private final Character _character;

    private final Terminal.Color _background;

    private final Terminal.Color _foreground;
}
