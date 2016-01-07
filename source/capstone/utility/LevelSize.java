package capstone.utility;

import com.googlecode.lanterna.terminal.TerminalSize;

/**
 * Tiny class for the size of a level.
 */
public class LevelSize extends TerminalSize
{
    /**
     *
     * Constructs the LevelSize with the specified columns and rows.
     *
     * @param columns The columns (width) of the level.
     *
     * @param rows The rows (height) of the level.
     */
    public LevelSize(int columns, int rows)
    {
        super(columns, rows);
    }

    /**
     *
     * Copy-constructor.
     *
     * @param other The other LevelSize to copy this one from.
     */
    public LevelSize(LevelSize other)
    {
        super(other);
    }

}
