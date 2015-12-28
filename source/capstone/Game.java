/**
 * Created by petergoldsborough on 12/26/15.
 */

package capstone;

public class Game
{
    public Game()
    {
        this(1);
    }

    public Game(double frameRate)
    {
        this.frameRate(frameRate);
    }

    public void start()
    {
        // display start menu
    }

    public void frameRate(double frameRate)
    {
        _frameRate = (long) (1000 / frameRate);
    }

    public double frameRate()
    {
        return (1.0 / _frameRate) * 1000;
    }

    private void loop()
    {
        while (_level.done());
    }

    private long _frameRate;

    private Level _level;
}