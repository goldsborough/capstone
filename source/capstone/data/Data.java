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

    public void store() throws IOException
    {
        store(new File("."));
    }

    public void store(File directory) throws IOException
    {
        assert(directory != null);

        File file = new File(directory, fileName());

        OutputStream output = new BufferedOutputStream(new FileOutputStream(file));

        Properties serialization = serialize();

        serialization.store(output, "Data for " + this.getClass().getName());
    }

    public abstract Properties serialize();

    public abstract String fileName();
}
