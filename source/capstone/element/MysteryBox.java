package capstone.element;

import capstone.utility.Point;
import capstone.data.Representation;
import capstone.utility.Region;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.dialog.MessageBox;

import java.util.Random;

public class MysteryBox extends Element
{
    public enum Event
    {
        EMPTY("This mystery-box is empty."),
        HEAL("You find Freibier and are healed!"),
        INJURE("You find a Diskrete Strukturen Klausur and are injured!"),

        NEW_KEY("A new key appears!"),
        NEW_STATIC_OBSTACLE("A new static obstacle appears!"),
        NEW_DYNAMIC_OBSTACLE("A new dynamic obstacle appears!"),
        NEW_MYSTERY_BOX("A new mystery-box appears!"),
        NEW_WALL("A new wall appears!"),

        REMOVE_DYNAMIC_OBSTACLE("A dynamic obstacle disappears!"),
        REMOVE_STATIC_OBSTACLE("A static obstacle disappears!"),
        LOSE_KEY("You lose a key!");

        public static Event Random()
        {
            switch(_random.nextInt(11))
            {
                case 0: return EMPTY;
                case 1: return HEAL;
                case 2: return INJURE;

                case 3: return NEW_KEY;
                case 4: return NEW_STATIC_OBSTACLE;
                case 5: return NEW_DYNAMIC_OBSTACLE;
                case 6: return NEW_MYSTERY_BOX;
                case 7: return NEW_WALL;

                case 8: return REMOVE_DYNAMIC_OBSTACLE;
                case 9: return REMOVE_STATIC_OBSTACLE;
                case 10: return LOSE_KEY;
            }

            return null;
        }

        public String message()
        {
            return _message;
        }

        private Event(String message)
        {
            _message = message;
        }

        private static final Random _random = new Random();

        private final String _message;
    }

    public static void showMessage(GUIScreen gui, String message)
    {
        MessageBox.showMessageBox(gui, "Mystery Message", message);
    }

    public MysteryBox(Point point, Representation representation)
    {
        super(Kind.MYSTERY_BOX, point, representation);
    }

    public Event reveal(GUIScreen gui, Region region)
    {
        assert(gui != null);
        assert(_revealed == false);

        _revealed = true;

        _event = Event.Random();

        MessageBox.showMessageBox(
                gui,
            "Mystery Event",
            _event.message()
        );

        super.unrender(gui.getScreen(), region);

        return _event;
    }

    public Event event()
    {
        assert(_revealed);

        return _event;
    }

    public boolean isRevealed()
    {
        return _revealed;
    }

    private boolean _revealed;

    private Event _event;
}
