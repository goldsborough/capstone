package capstone.utility;


/**
 * Created by petergoldsborough on 12/28/15.
 */
public class StopWatch
{
    public StopWatch() { }

    public StopWatch(long timeout)
    {
        this.timeout(timeout);
    }

    public StopWatch(boolean start)
    {
        if (start) start();
    }

    public void start()
    {
        assert(isStopped());

        _running = true;

        _start = now();
    }

    public void stop()
    {
        assert(isRunning());

        _end = now();

        _running = false;
    }

    public void pause()
    {
        assert(isRunning());

        _pauseStart = now();

        _paused = true;
    }

    public void resume()
    {
        assert(isPaused());

        _pausedTime += now() - _pauseStart;

        _paused = false;
    }

    public void reset()
    {
        assert(isRunning());

        _start = now();
    }

    public long timeout()
    {
        return _timeout;
    }

    public void timeout(long timeout)
    {
        assert(timeout >= 0);

        _timeout = timeout;
    }

    public boolean timedOut()
    {
        assert(_timeout != null);

        return milliseconds() >= _timeout;
    }

    public Long startTime()
    {
        return _start;
    }

    public Long nanoseconds()
    {
        return (now() - _start) - _pausedTime;
    }

    public Double microseconds()
    {
        return nanoseconds() / 1E3;
    }

    public Double milliseconds()
    {
        return nanoseconds() / 1E6;
    }

    public Double seconds()
    {
        return nanoseconds() / 1E9;
    }

    public Double minutes()
    {
        return nanoseconds() / 6E10;
    }

    public Double hours()
    {
        return nanoseconds() / 36E11;
    }

    public Double days()
    {
        return nanoseconds() / 864E11;
    }

    public boolean isRunning()
    {
        return _running && ! _paused;
    }

    public boolean isStopped()
    {
        return ! _running || _paused;
    }

    public boolean isPaused()
    {
        return _paused;
    }

    public boolean isResumed()
    {
        return _pausedTime > 0 && ! _paused;
    }

    private long now()
    {
        return _running ? System.nanoTime() : _end;
    }

    private long _start;

    private long _end;

    private long _pauseStart;

    private long _pausedTime;

    private Long _timeout;

    private boolean _running;

    private boolean _paused;
}
