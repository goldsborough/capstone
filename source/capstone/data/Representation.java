package capstone.data;

import com.googlecode.lanterna.terminal.Terminal;

public class Representation
{
    public Representation(Character character,
                          Terminal.Color background,
                          Terminal.Color foreground)
    {
        _character = character;
        _background = background;
        _foreground = foreground;
    }

    public Representation(Representation other)
    {
        _character = other._character;
        _background= other._background;
        _foreground = other._foreground;
    }

    public Character character()
    {
        return _character;
    }

    public void character(Character character)
    {
        _character = character;
    }

    public Terminal.Color background()
    {
        return _background;
    }

    public void background(Terminal.Color background)
    {
        _background = background;
    }

    public Terminal.Color foreground()
    {
        return _foreground;
    }

    public void foreground(Terminal.Color foreground)
    {
        _foreground = foreground;
    }

    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof Representation)) return false;

        if (object == this) return true;

        Representation other = (Representation) object;

        return _character.equals(other._character)   &&
               _background.equals(other._background) &&
               _foreground.equals(other._foreground);
    }

    private Character _character;

    private Terminal.Color _background;

    private Terminal.Color _foreground;
}
