package capstone.utility;

import capstone.utility.StopWatch;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by petergoldsborough on 12/28/15.
 */
public class StopWatchTest
{
    private StopWatch watch;

    @Before public void setUp()
    {
        watch = new StopWatch();
    }

    @Test public void testNotYetRunningWhenConstructorArgumentFalse() {
        assertTrue(watch.isStopped());
    }

    @Test public void testStartsWhenConstructorArgumentTrue()
    {
        watch = new StopWatch(true);

        assertTrue(watch.isRunning());
        assertFalse(watch.isStopped());
    }

    @Test public void testCanProbeBeforeStopped()
    {
        watch.start();

        assertNotNull(watch.seconds());
        assertNotNull(watch.milliseconds());
        assertNotNull(watch.microseconds());
        assertNotNull(watch.nanoseconds());
    }

    @Test public void testStopResets() throws InterruptedException
    {
        watch.start();

        Thread.sleep(10); // milliseconds

        watch.stop();
        watch.start();

        assertTrue(watch.milliseconds() < 10);
    }

    @Test public void testResetResetsWhileRunning() throws InterruptedException
    {
        watch.start();

        Thread.sleep(10);

        watch.reset();

        assertTrue(watch.milliseconds() < 10);
    }

    @Test(expected=AssertionError.class)
    public void testResetThrowsWhenNotRunning()
    {
        watch.reset();
    }

    @Test public void testPauseDoesNotReset() throws InterruptedException
    {
        watch.start();

        Thread.sleep(10); // milliseconds

        watch.pause();
        watch.resume();

        assertTrue(watch.milliseconds() >= 10);
    }

    @Test public void testPausingWorks() throws InterruptedException
    {
        watch.start();

        Thread.sleep(10); // milliseconds

        watch.pause();

        Thread.sleep(10); // milliseconds

        watch.resume();

        assertTrue(watch.milliseconds() < 20);
    }

    @Test public void testIsPausedReturnsTrueAfterCallingPause()
    {
        watch.start();
        watch.pause();

        assertTrue(watch.isPaused());
    }

    @Test public void testIsResumedReturnsTrueWhenAppropriate()
    {
        watch.start();
        watch.pause();
        watch.resume();

        assertTrue(watch.isResumed());
    }

    @Test(expected=AssertionError.class)
    public void testStartThrowsWhenCalledAndAlreadyRunning()
    {
        watch.start();
        watch.start();
    }

    @Test(expected=AssertionError.class)
    public void testStopThrowsWhenNotRunning()
    {
        watch.stop();
    }

    @Test(expected=AssertionError.class)
    public void testPauseThrowsWhenNotRunning()
    {
        watch.pause();
    }

    @Test(expected=AssertionError.class)
    public void testResumeThrowsWhenNotPaused()
    {
        watch.resume();
    }
}