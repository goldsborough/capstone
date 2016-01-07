package capstone.element;

import capstone.data.Representation;
import capstone.utility.Point;
import capstone.utility.Region;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.dialog.MessageBox;

import java.util.Random;

/**
 * A MysteryBox is a kind of element, that, when collided with, reveals an
 * Event that can be interpreted by the Level class to perform some sort of
 * action or event, such as create a new Element (e.g. key) or injure a Player.
 */
public class MysteryBox extends Element
{
    /**
     * The types of Events that a mystery-box can reveal.
     *
     * Each Event is associated with a message-string.
     */
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

        /**
         *
         * Factory function to yield a random Event.
         *
         * @return A random Events.
         */
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

            throw new AssertionError();
        }

        /**
         * @return The message associated with the MysteryBox.
         */
        public String message()
        {
            return _message;
        }

        /**
         *
         * Constructs a new Event enum-member and associates it with a message.
         *
         * @param message The message to associate with the MysteryBox.
         */
        private Event(String message)
        {
            _message = message;
        }

        private static final Random _random = new Random();

        private final String _message;
    }

    /**
     *
     * Shows a MessageBox on the given screen with the given message,
     * and gives the MessageBox the title "MysteryMessage".
     *
     * @param gui The GUIScreen to display the MessageBox on.
     *
     * @param message The message to display.
     */
    public static void showMessage(GUIScreen gui, String message)
    {
        MessageBox.showMessageBox(gui, "Mystery Message", message);
    }

    /**
     *
     * Constructs a new MessageBox.
     *
     * @param point The point to construct the MessageBox at.
     *
     * @param representation The Representation to give the MessageBox.
     */
    public MysteryBox(Point point, Representation representation)
    {
        super(Kind.MYSTERY_BOX, point, representation);
    }

    /**
     *
     * Reveals the MysteryBox's event and displays its message
     * on the GUIScreen passed, then unrenders itself from the
     * screen.
     *
     * @param gui The GUIScreen to display the MessageBox on and
     *            where to unrender the MysteryBox from.
     *
     * @param relativeTo The relative Region of the MysteryBox, for unrendering.
     *
     * @return The Event contained by the MysteryBox.
     */
    public Event reveal(GUIScreen gui, Region relativeTo)
    {
        assert(gui != null);
        assert(_revealed == false);

        _revealed = true;

        _event = Event.Random();

        _showMessageBox(gui);

        super.unrender(gui.getScreen(), relativeTo);

        return _event;
    }

    /**
     *
     * The MessageBox must be revealed first before you can acces its event.
     *
     * @return The Event of the MysteryBox.
     */
    public Event event()
    {
        assert(_revealed);

        return _event;
    }

    /**
     * @return True if the MyseryBox has been revealed yet, else false.
     */
    public boolean isRevealed()
    {
        return _revealed;
    }

    /**
     *
     * Shows the MysteryBox's message on the GUIScreen in a MessageBox.
     *
     * @param gui The GUIScreen to display the MysteryBox's message on.
     */
    private void _showMessageBox(GUIScreen gui)
    {
        MessageBox.showMessageBox(
                gui,
                "Mystery Event",
                _event.message()
        );
    }

    private boolean _revealed;

    private Event _event;
}
