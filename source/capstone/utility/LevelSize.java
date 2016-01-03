package capstone.utility;

import com.googlecode.lanterna.terminal.TerminalSize;

/**
 * Created by petergoldsborough on 01/03/16.
 */
public class LevelSize extends TerminalSize
{
    public LevelSize(int columns, int rows)
    {
        super(columns, rows);
    }

    public LevelSize(LevelSize other)
    {
        super(other);
    }

}
