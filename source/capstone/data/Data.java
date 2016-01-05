package capstone.data;

import java.io.*;
import java.util.Properties;

/**
 * Created by petergoldsborough on 12/27/15.
 */

public abstract class Data implements Serializable
{
    public void load(File file) throws IOException
    {
        assert(file != null);

        InputStream input = new BufferedInputStream(new FileInputStream(file));

        Properties serialization = new Properties();

        serialization.load(input);

        deserialize(serialization);
    }

    public abstract void deserialize(Properties serialization);

    public abstract void store() throws IOException;

    public void store(File directory) throws IOException
    {
        assert(directory != null);

        File file = new File(directory, fileName());

        OutputStream output = new BufferedOutputStream(new FileOutputStream(file));

        Properties serialization = serialize();

        serialization.store(
                output,
                String.format("%1$s", this.getClass().getName())
        );
    }

    public abstract Properties serialize();

    public abstract String fileName();

    protected static String get(Properties properties, String key)
    {
        assert(properties.containsKey(key));

        return properties.getProperty(key);
    }

    protected static String pop(Properties properties, String key)
    {
        assert(properties.containsKey(key));

        String value = properties.getProperty(key);

        properties.remove(key);

        return value;
    }
}
