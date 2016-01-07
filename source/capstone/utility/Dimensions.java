package capstone.utility;

/**
 * Created by petergoldsborough on 01/04/16.
 */
public class Dimensions extends AbstractPair<Integer, Integer>
{
    public Dimensions(int width, int height)
    {
        super(width, height);
    }

    public Dimensions(Dimensions other)
    {
        super(other);
    }

    public Integer width()
    {
        return super.first();
    }

    public void width(int width)
    {
        assert(width >= 0);

        super.first(width);
    }

    public Integer height()
    {
        return super.second();
    }

    public void height(int height)
    {
        assert(height >= 0);

        super.second(height);
    }
}
