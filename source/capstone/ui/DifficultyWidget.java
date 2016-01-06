package capstone.ui;

import capstone.game.Level;

/**
 * Created by petergoldsborough on 01/05/16.
 */
public class DifficultyWidget extends ListWidget<Level.Difficulty>
{
    public DifficultyWidget()
    {
        super("Select Difficulty", false);

        for (Level.Difficulty difficulty : Level.Difficulty.values())
        {
            add(difficulty, super::close);
        }

        add(new ButtonSlot(ButtonSlot.Kind.CANCEL));
    }

    public Level.Difficulty difficulty()
    {
        return _selected;
    }
}
