package capstone.element;

import capstone.utility.Point;
import capstone.data.Representation;

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
        REMOVE_STATIC_OBSTACLE("A dynamic obstacle disappears!"),
        LOSE_KEY("You lose a key!"),

        UNLOCK_DOOR("A door is unlocked!");

        public static Event Random()
        {
            switch(_random.nextInt(12))
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

                case 11: return UNLOCK_DOOR;

                default: assert(false);
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

    public MysteryBox(Point point, Representation representation)
    {
        super(Kind.MYSTERY_BOX, point, representation);

        _event = Event.Random();
    }

    public Event reveal()
    {
        assert(_event != null);

        // show box with message

        Event returned = _event;

        _event = null;

        return returned;
    }

    private Event _event;
}
