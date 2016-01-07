package capstone.ui;

import capstone.game.Level;

/**
 * A Widget to select the difficulty of a level.
 */
public class DifficultyWidget extends ListWidget<Level.Difficulty>
{
    /**
     * Constructs a new DifficultyWidget.
     */
    public DifficultyWidget()
    {
        super("Select Difficulty", false);

        for (Level.Difficulty difficulty : Level.Difficulty.values())
        {
            add(difficulty, super::close);
        }

        add(new ButtonSlot(ButtonSlot.Kind.CANCEL));
    }

    /**
     * @return The selected difficulty, if any yet.
     */
    public Level.Difficulty difficulty()
    {
        return _selected;
    }
}
