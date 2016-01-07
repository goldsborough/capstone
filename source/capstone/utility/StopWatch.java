package capstone.utility;


/**
 * A class for timing things.
 */
public class StopWatch
{
    /**
     * Default-constructs a non-running stopwatch.
     */
    public StopWatch() { }

    /**
     *
     * Constructs a StopWatch with the given timeout,
     * but does not start it yet.
     *
     * The timeout determines after how many milliseconds
     * the timeOut() method returns true.
     *
     * @param timeout The timeout for the StopWatch, in milliseconds.
     */
    public StopWatch(long timeout)
    {
        this(timeout, false);
    }

    /**
     *
     * Constructs a StopWatch with the given timeout
     * and starts if the second argument is true.
     *
     * The timeout determines after how many milliseconds
     * the timeOut() method returns true.
     *
     * @param timeout The timeout for the StopWatch, in milliseconds.
     *
     * @param start Whether to start the watch right away.
     */
    public StopWatch(long timeout, boolean start)
    {
        this.timeout(timeout);

        if (start) start();
    }

    /**
     *
     * Constructs a StopWatch and optionally starts it right away.
     *
     * @param start Whether to start the StopWatch right away.
     */
    public StopWatch(boolean start)
    {
        if (start) start();
    }

    /**
     *
     * Starts the StopWatch.
     *
     * The elapsed time can be sampled via the seconds/milliseconds etc.
     * methods an if there is a timeout set, the timeOut() method will
     * return true after the timeout has passed following a call to start().
     *
     * Note that the StopWatch must be stopped before it can be started.
     *
     */
    public void start()
    {
        assert(isStopped());

        _running = true;

        _start = _now();
    }

    /**
     * Stops the StopWatch.
     *
     * After the StopWatch has stopped, the time returned by the sampling
     * methods (seconds, milliseconds etc.) is always the time it was upon
     * end, i.e. the StopWatch really stops running.
     *
     * The StopWatch must be running to stop it.
     */
    public void stop()
    {
        assert(isRunning());

        _end = _now();

        _running = false;
    }

    /**
     *
     * Pauses the StopWatch.
     *
     * Pausing the StopWatch means the time between calls to pause()
     * and resume() does not contribute to the time returned by the
     * sampling methods.
     *
     * The StopWatch must be running to pause it.
     *
     */
    public void pause()
    {
        assert(isRunning());

        _pauseStart = _now();

        _paused = true;
    }

    /**
     *
     * Resumes the StopWatch, after having been paused.
     *
     * The StopWatch must have been paused earlier before a call to resume().
     *
     */
    public void resume()
    {
        assert(isPaused());

        _pausedTime += _now() - _pauseStart;

        _paused = false;
    }

    /**
     * Resets the starting point of the StopWatch to now.
     *
     * This affects the result of the sampling-methods
     * and the timeOut methods. Any paused time is discarded.
     */
    public void reset()
    {
        assert(isRunning());

        _start = _now();

        _pausedTime = 0;
    }

    /**
     * @return The timeout set for the StopWatch if one was set, else null.
     */
    public long timeout()
    {
        return _timeout;
    }

    /**
     *
     * Sets the timeout for the StopWatch.
     *
     * After starting the watch, the timeOut() method can be
     * used to check if the StopWatch has timed-out yet (i.e. if
     * the elapsed time since the start is greater than the timeout).
     *
     * @param timeout The new non-negative timeout in milliseconds.
     */
    public void timeout(long timeout)
    {
        assert(timeout >= 0);

        _timeout = timeout;
    }

    /**
     *
     * Returns whether the StopWatch has timed-out, i.e. if
     * the elapsed time since the start is greater than the timeout.
     *
     * The StopWatch must have had a timeout set first.
     *
     * @return True if the StopWatch has timed-out, else false.
     */
    public boolean timedOut()
    {
        assert(_timeout != null);

        return milliseconds() >= _timeout;
    }

    /**
     * @return The millisecond timestamp at which the StopWatch was started.
     *         The timestamp is invalid if the StopWatch was not
     *         actually started ever.
     */
    public Long startTime()
    {
        return _start;
    }

    /**
     * @return The time elapsed since the call to start(), in nanoseconds.
     */
    public Long nanoseconds()
    {
        return (_now() - _start) - _pausedTime;
    }

    /**
     * @return The time elapsed since the call to start(), in microseconds.
     */
    public Double microseconds()
    {
        return nanoseconds() / 1E3;
    }

    /**
     * @return The time elapsed since the call to start(), in milliseconds.
     */
    public Double milliseconds()
    {
        return nanoseconds() / 1E6;
    }

    /**
     * @return The time elapsed since the call to start(), in seconds.
     */
    public Double seconds()
    {
        return nanoseconds() / 1E9;
    }

    /**
     * @return The time elapsed since the call to start(), in minutes.
     */
    public Double minutes()
    {
        return nanoseconds() / 6E10;
    }

    /**
     * @return The time elapsed since the call to start(), in hours.
     */
    public Double hours()
    {
        return nanoseconds() / 36E11;
    }

    /**
     * @return The time elapsed since the call to start(), in days.
     */
    public Double days()
    {
        return nanoseconds() / 864E11;
    }

    /**
     * @return True if the StopWatch was started and is not paused, else false.
     */
    public boolean isRunning()
    {
        return _running && ! _paused;
    }

    /**
     * @return True if the StopWatch was stopped.
     */
    public boolean isStopped()
    {
        return ! _running;
    }

    /**
     * @return True if the StopWatch is currently paused.
     */
    public boolean isPaused()
    {
        return _paused;
    }

    /**
     * @return True if the StopWatch was paused previously, but not right now.
     */
    public boolean isResumed()
    {
        return _pausedTime > 0 && ! _paused;
    }

    /**
     * @return The system-time in nano seconds if the StopWatch is running, else
     *         the value of the system-time when the StopWatch was stopped (so
     *         that the sampling methods return the time between start and end
     *         once the StopWatch is stopped).
     */
    private long _now()
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
