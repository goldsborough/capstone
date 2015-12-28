package capstone;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

class MockData extends Data
{
    public MockData()
    {
        this.x = null;
        this.y = null;
        this.s = null;
    }

    public MockData(int x, double y, String s)
    {
        this.x = x;
        this.y = y;
        this.s = s;
    }

    @Override public void deserialize(Properties serialization)
    {
        assert(serialization.containsKey("x"));
        this.x = Integer.parseInt(serialization.getProperty("x"));

        assert(serialization.containsKey("y"));
        this.y = Double.parseDouble(serialization.getProperty("y"));

        assert(serialization.containsKey("s"));
        this.s = serialization.getProperty("s");
    }

    @Override public Properties serialize()
    {
        Properties serialization = new Properties();

        serialization.setProperty("x", this.x.toString());
        serialization.setProperty("y", this.y.toString());
        serialization.setProperty("s", this.s);

        return serialization;
    }

    @Override protected String fileName()
    {
        return String.format("%1$d_%2$.3f_%3$s.data", x, y, s);
    }

    public Integer x;
    public Double  y;
    public String  s;

}

public class DataTest
{
    private static <K, V> void assertContains(Map<K, V> map, K key, V value)
    {
        assertTrue(map.containsKey(key));
        assertEquals(map.get(key), value);
    }

    @Test public void testLoadsDataWell() throws IOException
    {
        File file = new File("mock.data");

        Properties serialization = new Properties();

        serialization.setProperty("x", "69");
        serialization.setProperty("y", "3.14");
        serialization.setProperty("s", "abc");

        serialization.store(
                new FileOutputStream(file),
                "Mock Data"
        );

        MockData mock = new MockData(file);

        assertEquals(mock.x, new Integer(69));
        assertEquals(mock.y, new Double(3.14));
        assertEquals(mock.s, "abc");

        assert(file.delete());
    }

    @Test(expected=IOException.class)
    public void testThrowsForFaultyFileWhenLoading() throws IOException
    {
        new MockData(new File("asdf"));
    }

    @Test public void testStoresDataToGivenDestinationWell() throws IOException
    {
        File file = new File("mock.data");

        MockData mock = new MockData(123, 0.123, "å∂€∑");

        mock.store(file);

        Properties serialization = new Properties();

        serialization.load(new FileInputStream(file));

        assertEquals(serialization.size(), 3);

        assertContains(serialization, "x", "123");
        assertContains(serialization, "y", "0.123");
        assertContains(serialization, "s", "å∂€∑");
    }

    @Test public void testStoresDataToDefaultDestinationWell() throws IOException
    {
        MockData mock = new MockData(123, 0.123, "å∂€∑");

        mock.store();

        File file = new File("123_0.123_å∂€∑.data");

        Properties serialization = new Properties();

        serialization.load(new FileInputStream(file));

        assertEquals(serialization.size(), 3);

        assertContains(serialization, "x", "123");
        assertContains(serialization, "y", "0.123");
        assertContains(serialization, "s", "å∂€∑");

        assert(file.delete());
    }
}