

/**
 * Created by petergoldsborough on 12/26/15.
 */

package capstone.gui;

import capstone.data.Data;
import capstone.data.Profile;
import capstone.element.Player;
import capstone.data.Theme;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class Level extends Data
{
    public Level(File session, Collection<Profile> players) throws IOException
    {

    }

    public Level(Properties session, Collection<Profile> players)
    {

    }

    public Level(File layout, Theme theme, Collection<Profile> players) throws IOException
    {

    }

    public Level(Properties layout, Theme theme, Collection<Profile> players)
    {

    }

    public boolean done()
    {
        return true;
    }

    @Override
    public void deserialize(Properties serialization)
    {

    }

    @Override
    public Properties serialize()
    {
        return null;
    }

    @Override
    public String fileName()
    {
        return null;
    }
}