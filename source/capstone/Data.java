package capstone;

import java.io.*;
import java.util.Properties;

/**
 * Created by petergoldsborough on 12/27/15.
 */

public abstract class Data implements Serializable
{
    public static String root = ".";

    public Data(Properties serialization)
    {
        deserialize(serialization);
    }

    public Data(File file)
    {
        load(file);
    }

    public void load(File file) throws IOException
    {
        InputStream input = new BufferedInputStream(new FileInputStream(file));

        Properties serialization = new Properties();

        serialization.load(input);

        deserialize(serialization);
    }

    public abstract void deserialize(Properties serialization);

    public void store() throws IOException
    {
        store(new File(root, fileName()));
    }

    public void store(File file) throws IOException
    {
        OutputStream output = new BufferedOutputStream(new FileOutputStream(file));

        Properties serialization = serialize();

        serialization.store(output, "Data for " + this.getClass().getName());
    }

    public abstract Properties serialize();

    protected abstract String fileName();
}
