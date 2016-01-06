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
 * Base class for all data-managing objects.
 *
 * Defines a few methods for loading and storing files and properties.
 *
 */

public abstract class Data implements Serializable
{
    /**
     *
     * Loads a file for a properties file.
     *
     * @param file The file to load.
     *
     * @throws IOException for any I/O error opening the file.
     *
     */
    public void load(File file) throws IOException
    {
        assert(file != null);

        deserialize(_load(file));
    }

    /**
     *
     * How to deserialize the data and load the fields of the class. Depends
     * on the data and the class, of course.
     *
     * @param serialization The properties containing the data.
     *
     */
    public abstract void deserialize(Properties serialization);

    /**
     *
     * The default store operation. The subclasses determine what the
     * default path is, e.g. "resources/profiles".
     *
     * @throws IOException for any I/O error.
     *
     */
    public abstract void store() throws IOException;


    /**
     *
     * Stores the properties retrieved via the abstract serialize() method
     * at the directory, the filename coming from the abstract fileName()
     * method, which subclasses must implement to determine how to store the
     * file e.g. the name of the level and the date for a session.
     *
     * @param directory Where to store the data.
     *
     * @throws IOException for any I/O error.
     *
     */
    public void store(File directory) throws IOException
    {
        assert(directory != null);

        // Makes a file at directory/filename()
        File file = new File(directory, fileName());

        OutputStream output = new BufferedOutputStream(new FileOutputStream(file));

        Properties serialization = serialize();

        serialization.store(
                output,
                String.format("%1$s", this.getClass().getName())
        );
    }

    /**
     *
     * Returns the data as a Properties file.
     *
     * @return The data of the class as a Properties file.
     */
    public abstract Properties serialize();

    /**
     *
     * A filename representation for the data, containing relevant information
     * such as the name of a level or the id of a profile. Also should have
     * a proper file extension such as .profile.
     *
     * @return The filename string.
     */
    public abstract String fileName();

    /**
     *
     * Gets the property associated with the given key from a properties file.
     *
     * Method names of Properites are too long. Also gotta assert that the key
     * is contained first.
     *
     * @param properties Where to get the data from.
     *
     * @param key The key to get the value/property for.
     *
     * @return The value associated with the key.
     */
    protected static String get(Properties properties, String key)
    {
        assert(properties.containsKey(key));

        return properties.getProperty(key);
    }

    /**
     *
     * Gets and removes the property associated with
     * the given key from a properties file.
     *
     * Method names of Properites are too long. Also gotta assert that the key
     * is contained first.
     *
     * @param properties Where to get the data from.
     *
     * @param key The key to get the value/property for.
     *
     * @return The value associated with the key.
     *
     */
    protected static String pop(Properties properties, String key)
    {
        assert(properties.containsKey(key));

        String value = properties.getProperty(key);

        properties.remove(key);

        return value;
    }

    /**
     *
     * Utility method to load a Properties object from a file.
     *
     * @param file The file to load.
     *
     * @return The contained properties, if any.
     *
     * @throws IOException when batman becomes the next pope.
     */
    protected static Properties _load(File file) throws IOException
    {
        assert(file != null);

        Properties session = new Properties();

        session.load(new BufferedInputStream(new FileInputStream(file)));

        return session;
    }

    /**
     *
     * Utility method to get the filename of a file, without extension.
     *
     * @param file The file to get the filename of.
     *
     * @return The filename of the file, without extension.
     */
    protected static String _getName(File file)
    {
        assert(file != null);

        String filename = file.getName();

        return filename.substring(0, filename.lastIndexOf('.'));
    }
}
