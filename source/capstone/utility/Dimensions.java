package capstone.utility;

/**
 * Dimensions are a pair of integers, one for a width and one for a height.
 */
public class Dimensions extends AbstractPair<Integer, Integer>
{
    /**
     *
     * Constructs a new Dimensions object.
     *
     * @param width The width of the dimensions.
     *
     * @param height The height of the dimensions.
     */
    public Dimensions(int width, int height)
    {
        super(width, height);
    }

    /**
     *
     * Copy-constructor.
     *
     * @param other The Dimensions instance to copy from.
     */
    public Dimensions(Dimensions other)
    {
        super(other);
    }

    /**
     * @return The width of the dimensions.
     */
    public Integer width()
    {
        return super.first();
    }

    /**
     *
     * Sets the width of the dimensions.
     *
     * @param width The width of the dimensions.
     */
    public void width(int width)
    {
        assert(width >= 0);

        super.first(width);
    }

    /**
     * @return The height of the dimensions.
     */
    public Integer height()
    {
        return super.second();
    }

    /**
     *
     * Sets the height of the dimensions.
     *
     * @param height The width of the dimensions.
     */
    public void height(int height)
    {
        assert(height >= 0);

        super.second(height);
    }
}
