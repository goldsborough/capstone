package capstone.ui;

import com.googlecode.lanterna.input.Key;

/**
 * A superior lanterna-Key class that adds some factory and utility
 * functions as well as a proper equals() implementation.
 */
public class InputKey extends Key
{
    /**
     *
     * Deserializes an InputKey, i.e. returns an
     * InputKey for its string representation.
     *
     * @param serialized The string representation of the InputKey.
     *
     * @return The resulting InputKey.
     */
    public static InputKey fromString(String serialized)
    {
        assert(! serialized.isEmpty());

        if (serialized.length() == 1)
        {
            return new InputKey(serialized.charAt(0));
        }

        else return new InputKey(InputKey.Kind.valueOf(serialized));
    }

    /**
     *
     * Constructs an InputKey from a lanterna-Key.
     *
     * This cannot be a constructor because you cannot do the
     * logic below before calling the lanterna-Key's constructor,
     * i.e. you couldn't perform the checks below before calling
     * super(...).
     *
     * @param key The lanterna-Key.
     *
     * @return The corresponding InputKey.
     */
    public static InputKey fromKey(Key key)
    {
        if (key.getKind() == Kind.NormalKey)
        {
            return new InputKey(key.getCharacter());
        }

        else return new InputKey(key.getKind());
    }

    /**
     *
     * Returns a copy of the InputKey.
     *
     * Cannot be a copy-constructor because of the super() problem
     * (can't do anything before calling super())
     *
     * @param key The InputKey to copy.
     *
     * @return A copy of the InputKey.
     */
    public static InputKey copy(InputKey key)
    {
        return fromKey(key);
    }

    /**
     *
     * Constructs an InputKey from a character.
     *
     * @param character The character to construct this InputKey with.
     */
    public InputKey(char character)
    {
        super(character);
    }

    /**
     *
     * Constructs an InputKey from an InputKey.Kind.
     *
     * @param kind The InputKey.Kind to construct this InputKey with.
     */
    public InputKey(InputKey.Kind kind)
    {
        super(kind);
    }

    /**
     *
     * Checks if this InputKey is equal to another object.
     *
     * @param object The object to check equality for.
     *
     * @return True if the object is an InputKey and
     *         has the same character or kind.
     */
    @Override public boolean equals(Object object)
    {
        if (object == null) return false;

        if (! (object instanceof InputKey)) return false;

        if (object == this) return true;

        InputKey other = (InputKey) object;

        if (! this.getKind().equals(other.getKind())) return false;

        if (this.getKind().equals(Kind.NormalKey))
        {
            return this.getCharacter() == other.getCharacter();
        }

        else return true;
    }

    /**
     * @return A string representation of the InputKey.
     */
    public String toString()
    {
        Kind kind = super.getKind();

        if (kind == Kind.NormalKey)
        {
            // Just need to keep everything lower-case!
            return Character.toString(super.getCharacter()).toLowerCase();
        }

        else return kind.toString();
    }
}
