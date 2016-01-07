package capstone.data;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * A class encapsulating the information an element needs to be
 * represented on the screen: a character, a background and a
 * foreground color.
 */
public class Representation
{
    /**
     *
     * Constructs a new Representation.
     *
     * @param character The character the element will be displayed as.
     *
     * @param background The background color of the element.
     *
     * @param foreground The foreground color (i.e. text color) of the element.
     */
    public Representation(Character character,
                          Terminal.Color background,
                          Terminal.Color foreground)
    {
        _character = character;
        _background = background;
        _foreground = foreground;
    }

    /**
     *
     * Copy-Constructor.
     *
     * @param other The Representation to copy this one from.
     */
    public Representation(Representation other)
    {
        _character = other._character;
        _background= other._background;
        _foreground = other._foreground;
    }

    /**
     * @return The character used for the Representation.
     */
    public Character character()
    {
        return _character;
    }

    /**
     *
     * Sets the character for the Representation.
     *
     * @param character A new character for the Representation.
     */
    public void character(Character character)
    {
        _character = character;
    }

    /**
     * @return The background color of the Representation.
     */
    public Terminal.Color background()
    {
        return _background;
    }

    /**
     *
     * Sets the background color of the Representation.
     *
     * @param background A new background color of the Representation.
     */
    public void background(Terminal.Color background)
    {
        _background = background;
    }

    /**
     * @return The foreground color of the Representation.
     */
    public Terminal.Color foreground()
    {
        return _foreground;
    }

    /**
     *
     * Sets the foreground color of the Representation.
     *
     * @param foreground A new background color of the Representation.
     */
    public void foreground(Terminal.Color foreground)
    {
        _foreground = foreground;
    }

    /**
     *
     * Checks equality between this Representation and an object.
     *
     * @param object The object to check equality for.
     *
     * @return True if the object is a Representation instance with the same
     *         character, color and background, else false.
     */
    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof Representation)) return false;

        if (object == this) return true;

        Representation other = (Representation) object;

        // reference equality ok for enum members
        return _character.equals(other._character)   &&
               _background.equals(other._background) &&
               _foreground.equals(other._foreground);
    }

    private Character _character;

    private Terminal.Color _background;

    private Terminal.Color _foreground;
}
