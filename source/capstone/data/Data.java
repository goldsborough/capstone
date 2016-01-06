package capstone.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * Created by petergoldsborough on 12/27/15.
 */

public abstract class Data implements Serializable
{
    public void load(File file) throws IOException
    {
        assert(file != null);

        deserialize(_load(file));
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

    protected static Properties _load(File file) throws IOException
    {
        Properties session = new Properties();

        session.load(new BufferedInputStream(new FileInputStream(file)));

        return session;
    }

    protected static String _getName(File file)
    {
        String filename = file.getName();

        return filename.substring(0, filename.lastIndexOf('.'));
    }
}
