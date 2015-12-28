package capstone;

/**
 * Created by petergoldsborough on 12/27/15.
 */
public class Key extends com.googlecode.lanterna.input.Key
{
    public static Key fromString(String serialized)
    {
        assert(! serialized.isEmpty());

        if (serialized.length() == 1)
        {
            return new Key(serialized.charAt(0));
        }

        else return new Key(Key.Kind.valueOf(serialized));
    }

    public Key(char character)
    {
        super(character);
    }

    public Key(Key.Kind kind)
    {
        super(kind);
    }
}
