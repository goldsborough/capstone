package capstone.data;

import org.junit.Test;

import java.io.*;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

class MockData extends Data
{
    public MockData(Properties properties)
    {
        deserialize(properties);
    }

    public MockData(File directory) throws IOException
    {
        load(directory);
    }

    public MockData(int x, double y, String s)
    {
        this.x = x;
        this.y = y;
        this.s = s;
    }

    @Override public void store() throws IOException
    {
        super.store(new File("."));
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

    @Override
    public String fileName()
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

    @Test public void testStoresDataToDefaultDestinationWell() throws IOException
    {
        MockData mock = new MockData(123, 0.123, "å∂€∑");

        mock.store();

        File file = new File(mock.fileName());

        Properties serialization = new Properties();

        serialization.load(new BufferedInputStream(new FileInputStream(file)));

        assertEquals(serialization.size(), 3);

        assertContains(serialization, "x", "123");
        assertContains(serialization, "y", "0.123");
        assertContains(serialization, "s", "å∂€∑");

        assert(file.delete());
    }

    @Test public void testStoresDataToGivenDestinationWell() throws IOException
    {
        MockData mock = new MockData(123, 0.123, "å∂€∑");

        File file = new File(".");

        mock.store(file);

        file = new File(file, mock.fileName());

        Properties serialization = new Properties();

        serialization.load(new FileInputStream(file));

        assertEquals(serialization.size(), 3);

        assertContains(serialization, "x", "123");
        assertContains(serialization, "y", "0.123");
        assertContains(serialization, "s", "å∂€∑");

        assert(file.delete());
    }

    @Test public void testGetGetsCorrectValue()
    {
        Properties properties = new Properties();

        properties.setProperty("key", "value");

        assertThat(Data.get(properties, "key"), is("value"));
    }

    @Test(expected=AssertionError.class)
    public void testGetThrowsForNonExistingKey()
    {
        Properties properties = new Properties();

        assertThat(Data.get(properties, "key"), is("value"));
    }

    @Test public void testPopGetsCorrectValue()
    {
        Properties properties = new Properties();

        properties.setProperty("key", "value");

        assertThat(Data.pop(properties, "key"), is("value"));
    }

    @Test public void testPopRemovesKey()
    {
        Properties properties = new Properties();

        properties.setProperty("key", "value");

        assert(properties.containsKey("key"));

        Data.pop(properties, "key");

        assertFalse(properties.containsKey("key"));

    }

    @Test(expected=AssertionError.class)
    public void testPopThrowsForNonExistingKey()
    {
        Properties properties = new Properties();

        assertThat(Data.get(properties, "key"), is("value"));
    }

    @Test public void testGetNameReturnsCorrectFileName()
    {
        assertThat(Data._getName(new File("abc.def.ghi")), is("abc.def"));
    }
}