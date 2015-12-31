package capstone.gui;

/**
 * Created by petergoldsborough on 12/27/15.
 */
public class InputKey extends com.googlecode.lanterna.input.Key
{
    public static InputKey fromString(String serialized)
    {
        assert(! serialized.isEmpty());

        if (serialized.length() == 1)
        {
            return new InputKey(serialized.charAt(0));
        }

        else return new InputKey(InputKey.Kind.valueOf(serialized));
    }

    public InputKey(char character)
    {
        super(character);
    }

    public InputKey(InputKey.Kind kind)
    {
        super(kind);
    }

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

    public String toString()
    {
        Kind kind = super.getKind();

        if (kind == Kind.NormalKey)
        {
            return Character.toString(super.getCharacter()).toUpperCase();
        }

        else return kind.toString();
    }

}
