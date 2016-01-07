package capstone.game;

import capstone.data.Profile;
import capstone.data.Theme;
import capstone.element.Direction;
import capstone.element.Element;
import capstone.element.MysteryBox;
import capstone.element.Player;
import capstone.ui.StatusBar;
import capstone.utility.Delta;
import capstone.utility.LevelBuilder;
import capstone.utility.LevelSize;
import capstone.utility.Page;
import capstone.utility.PageGrid;
import capstone.utility.Point;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The Level class is the main coordinating-unit of all game-logic.
 * It manages player movements, rendering elements and all game-events.
 */
public class Level
{
    /**
     * A measure of the difficulty of the level.
     *
     * The difficulty basically determines how many frames to skip
     * until the dynamic obstacles are updated. A lower difficulty
     * means more frames are skipped, the highest difficulty means
     * the dynamic obstacles move at the same frame-rate as players.
     */
    public enum Difficulty
    {
        VERY_EASY(3),
        EASY(2),
        MEDIUM(1),
        HARD(0);

        public int delay()
        {
            return _delay;
        }

        private Difficulty(int delay)
        {
            _delay = delay;
        }

        private int _delay;
    }

    /**
     *
     * Constructs a Level.
     *
     * The power of refactoring (LevelBuilder)
     * made this class 1000 lines smaller.
     *
     * @param builder The LevelBuilder containing all the data for this class.
     */
    public Level(LevelBuilder builder)
    {
        _theme = builder.theme();

        _name = builder.name();

        _levelSize = builder.levelSize();

        _totalKeys = builder.totalKeys();

        _hidden = builder.hidden();

        _gui = builder.gui();

        _screen = _gui.getScreen();

        _IDMap = builder.IDMap();

        _players = builder.players();

        _grid = builder.grid();

        _page = builder.page();

        _difficulty = builder.difficulty();

        _statusBar = new StatusBar(this);

        _deadPlayers = new ArrayList<>();

        redraw();
    }

    /**
     *
     * The most important method in the class, which is called on every
     * frame to do everything there is to do. It first checks if the
     * screen size has changed to determine if the screen has to be
     * redrawn and the PageGrid updated. It then updates the page
     * which moves the dynamic obstacles further if the frameCount
     * equals the difficulty (see description of Difficulty enum).
     * Then, it moves the players and evaluates their new positions
     * to check for collisions and determine what those collisions mean.
     *
     * @param directions The map from IDs to Directions, as passed by
     *                   the Game class.
     */
    public void update(Map<String, Direction> directions)
    {
        // Resize pending?
        _checkResize();

        // Move dynamic obstacles
        _updatePage();

        Page old = _page;

        // First move the players that are in the map
        _movePlayers(directions);

        // Then evaluate the positions of all players
        // Also those that didn't move.
        _evaluatePlayers(directions);

        // See if we followed a player to another page
        // and have to re-render the page onto the screen.
        if (_page != old)
        {
            _screen.clear();
            _page.render(_screen);
        }

        _statusBar.draw();

        _screen.refresh();
    }

    /**
     * @return The name of the level.
     */
    public String name()
    {
        return _name;
    }

    /**
     * @return True if the game has been won or lost.
     */
    public boolean isDone()
    {
        return hasWon() || hasLost();
    }

    /**
     * @return True if the player(s) collected all
     *         keys and exited one of the doors.
     */
    public boolean hasWon()
    {
        return _won;
    }

    /**
     * @return True if all players are dead.
     */
    public boolean hasLost()
    {
        return _players.isEmpty() && _hidden.isEmpty();
    }

    /**
     * Does all the things necessary to redraw the current page
     * onto the screen. This method is very expensive and should
     * only be called when you have to do a complete re-rendering
     * of EVERYTHING in the level, e.g. after showing the menu
     * (which is a completely different screen).
     */
    public void redraw()
    {
        _clear(_screen);

        _page.render(_screen);

        for (Player player : _players)
        {
            player.render(_screen, _page.region());
        }

        _statusBar.draw();

        _screen.refresh();
    }

    /**
     * @return The theme of the level.
     */
    public Theme theme()
    {
        return _theme;
    }

    /**
     *
     * Sets the theme of the level.
     *
     * @param theme The new theme.
     */
    public void theme(Theme theme)
    {
        assert(theme != null);

        _theme = theme;

        for (Page page : _grid.pages())
        {
            for (Element element : page)
            {
                element.representation(_theme.representation(element.kind()));
            }
        }

        // assuming that this method is called from the
        // menu, because it will do a redraw after. Else
        // we'd have to call _page.render(_screen); here,
        // but it's realistically never gonna happen.
    }

    /**
     * @return A list of active and dead players.
     */
    public List<Player> players()
    {
        List<Player> players = new ArrayList<>(_players);

        players.addAll(_deadPlayers);

        return players;
    }

    /**
     * @return A list of the players still hidden in the level.
     */
    public List<Profile> hidden()
    {
        return Collections.unmodifiableList(_hidden);
    }

    /**
     *
     * Returns the size of a page on the screen, i.e. the terminalSize
     * without the rows for the statusbar.
     *
     * @return The size of a page on the screen.
     */
    public TerminalSize pageSize()
    {
        TerminalSize size = _screen.getTerminalSize();

        int columns = size.getColumns();

        int rows = size.getRows();

        rows -= 1; // level information

        rows -= _players.size();

        rows -= _deadPlayers.size();

        if (_hidden != null) rows -= _hidden.size();

        return new TerminalSize(columns, rows);
    }

    /**
     * @return The number of keys collected by the player(s) so far.
     */
    public int keysCollected()
    {
        return _keysCollected;
    }

    /**
     * @return The total number of keys in the level (collected and not).
     */
    public int totalKeys()
    {
        return _totalKeys;
    }

    /**
     * @return The grid used in the level.
     */
    public PageGrid grid()
    {
        return _grid;
    }

    /**
     * @return The current page shown on the screen.
     *         Same as grid().currentPage().
     */
    public Page currentPage()
    {
        return _page;
    }

    /**
     * @return The gui on which the level is rendered.
     */
    public GUIScreen gui()
    {
        return _gui;
    }

    /**
     *
     * Sets the GUIScreen of the level.
     *
     * @param gui The GUIScreen on which the level is to be rendered.
     */
    public void gui(GUIScreen gui)
    {
        assert(gui != null);

        _gui = gui;

        _screen = _gui.getScreen();

        redraw();
    }

    /**
     * @return The Screen underlying the GUIScreen.
     */
    public Screen screen()
    {
        return _screen;
    }

    /**
     * @return The size of the entire level.
     */
    public LevelSize size()
    {
        return _levelSize;
    }

    /**
     * @return The difficulty setting.
     */
    public Difficulty difficulty()
    {
        return _difficulty;
    }

    /**
     *
     * Sets the difficulty of the Level.
     *
     * @param difficulty The new difficulty for the level.
     */
    public void difficulty(Difficulty difficulty)
    {
        assert(difficulty != null);

        _difficulty = difficulty;
    }

    /**
     *
     * Creates a LevelBuilder instance and stores the level through it.
     *
     * @throws IOException for any I/O badness.
     */
    public void store() throws  IOException
    {
        new LevelBuilder(this).store();
    }

    /**
     * Handles calling _page.update() when the _frameCount is
     * equal to the delay associated with the difficulty.
     *
     * @see Difficulty
     */
    private void _updatePage()
    {
        if (_frameCount++ >= _difficulty.delay())
        {
            _page.update(_screen);

            _frameCount = 0;
        }
    }

    /**
     *
     * Moves each player that performed a move in the current frame.
     *
     * @param directions The map from ids to directions.
     */
    private void _movePlayers(Map<String, Direction> directions)
    {
        for (String id : directions.keySet())
        {
            // This is what the _IDMap is for
            Player player = _IDMap.get(id);

            // Happens when hidden players press a key
            if (player == null) continue;

            // refactored
            _move(player, directions.get(id));
        }
    }

    /**
     *
     * Moves a player into the specified direction if the player
     * does not go negative. If the player stood on an element,
     * e.g. an entrance, that element is re-rendered. If that
     * element the player stood on was an entrance and there
     * are still hidden players, one of them can be unhidden.
     *
     * @param player The player to move.
     *
     * @param direction The direction to move the player in.
     */
    private void _move(Player player, Direction direction)
    {
        player.unrender(_screen, _page.region());

        // See if the player stood on something, e.g. entrance
        Element element = _page.at(player.point());

        if (element != null)
        {
            if (element.kind() == Element.Kind.ENTRANCE && ! _hidden.isEmpty())
            {
                _unhidePlayer(element.point());
            }

            else element.render(_screen, _page.region());
        }

        Delta delta = new Delta(direction);

        if (! player.wouldGoNegative(delta))
        {
            _page = _grid.follow(player.move(delta));
        }
    }

    /**
     *
     * Promotes a profile from the _hidden profiles into an actual
     * player, by creating a new player at the given point (should
     * be an entrance) with the first profile retrieved from the
     * _hidden collection. That profile is random.
     *
     * @param point The point at which to unhide one of the profiles.
     */
    private void _unhidePlayer(Point point)
    {
        assert(! _hidden.isEmpty());

        // Get the first profile we can
        Iterator<Profile> iterator = _hidden.iterator();

        Player player = new Player(point, iterator.next());

        _IDMap.put(player.id(), player);

        _players.add(player);

        // Remove the profile from the hidden profiles
        iterator.remove();
    }

    /**
     *
     * Evaluates the positions of all players. This means checking
     * if they need to go back in case of a collision or checking
     * if they're dead.
     *
     * @param directions The map from ids to moved directions.
     */
    private void _evaluatePlayers(Map<String, Direction> directions)
    {
        // First check their movements to see if they went back
        for (int i = 0; i < _players.size(); )
        {
            Player player = _players.get(i);

            _evaluate(player);

            if (player.isAlive()) ++i;
        }

        // Then check if the players collided or moved past each other
        for (Player player : _players)
        {
            if (_page.isInside(player))
            {
                _checkPlayerCollision(player, directions);

                player.render(_screen, _page.region());
            }
        }
    }

    /**
     *
     * Evaluates a player's position with respect to the other game elements
     * in the level. It is looked if the player's point collides with any
     * other element in the screen and if so, a certain action is performed.
     * What that action is depends on the element, e.g. just going backwards
     * when bumping into walls, or revealing a mystery-event when opening
     * a mystery-box, for example. When, after evaluation, a player is seen
     * to have moved onto another page, the grid's current page is updated.
     *
     * @param player The player to evaluate.
     */
    private void _evaluate(Player player)
    {
        assert(player != null);

        Element element = _page.at(player.point());

        if (element == null) return;

        switch (element.kind())
        {
            case WALL:
                player.goBack();
                break;

            // Because this happens at the start we first have
            // to check if the player even can go back
            case ENTRANCE:
                if (player.canGoBack()) player.goBack();
                break;

            case EXIT:
            {
                if (_keysCollected == _totalKeys) _won = true;

                else player.goBack();

                break;
            }

            case KEY:
            {
                element.unrender(_screen, _page.region());
                _page.remove(element);

                ++_keysCollected;

                break;
            }

            case STATIC_OBSTACLE:
            case DYNAMIC_OBSTACLE:
            {
                player.injure();
                if (! player.isDead()) player.goBack();
                break;
            }

            case MYSTERY_BOX:
                _handleMysteryBox((MysteryBox) element, player);
                break;
        }

        // Could also happen due to the mystery box, that's why
        // it's down here and not with the obstacles
        if (player.isDead()) _kill(player);

        // If the player moved out of the page, the index of the
        // grid is updated. The screen is not re-rendered, this
        // operation just modifies a few numbers (the index) in
        // the grid, so it's very cheap and we can do it for all
        // players. The last page to be followed one will be
        // rendered then.
        _page = _grid.follow(player);
    }

    /**
     *
     * This one was tasty. Checks if players have collided or moved past
     * each other, i.e. moved over each other. Prevents such an action if
     * necessary.
     *
     * @param player The player to check collision for.
     *
     * @param directions The map from ids to directions of the last frame.
     */
    private void _checkPlayerCollision(Player player,
                                       Map<String, Direction> directions)
    {
        for (Player other : _players)
        {
            if (other == player) continue;

            // If one moved onto the other, either because one player
            // stood still and the other moved onto him, or because
            // they both moved onto the same point at the same time.
            if (other.point().equals(player.point()))
            {
                // Only one of them may have moved.
                if (directions.containsKey(player.id())) player.goBack();

                else other.goBack();

                break;
            }

            // If both moved, and they moved past each other such that
            // the point of the player is the previous point of other
            // and the point of other is the previous point of the player,
            // then undo both movements. Because both moved we can be sure
            // that neither previousPoint will be null.
            else if (directions.containsKey(player.id())          &&
                     directions.containsKey(other.id())           &&
                     player.point().equals(other.previousPoint()) &&
                     other.point().equals(player.previousPoint()))
            {
                player.goBack();
                other.goBack();

                break;
            }
        }
    }

    /**
     *
     * Handles all the events of the MysteryBox. Can't refactor that into
     * the MysteryBox class, as it performs too many internal modifications
     * on the level that shouldn't be accessible from the outside to another
     * class. Maybe in C++ we would declare the MysteryBox a friend class.
     *
     * @param mysteryBox The mystery-box instance to reveal.
     *
     * @param player The player who interacted with the mystery-box.
     */
    private void _handleMysteryBox(MysteryBox mysteryBox, Player player)
    {
        // Needs the gui to show a message box and unrender
        // Needs the region to unrender
        mysteryBox.reveal(_gui, _page.region());

        _grid.remove(mysteryBox);

        switch (mysteryBox.event())
        {
            case EMPTY:
                break;

            case HEAL:
            {
                if (! player.hasFullHealth()) player.heal();

                else MysteryBox.showMessage(
                        _gui,
                        "But you already have full health!"
                );

                break;
            }

            case INJURE:
                player.injure();
                break;

            case NEW_KEY:
                if (_generate(Element.Kind.KEY)) ++_totalKeys;
                break;

            case NEW_STATIC_OBSTACLE:
                _generate(Element.Kind.STATIC_OBSTACLE);
                break;

            case NEW_DYNAMIC_OBSTACLE:
                _generate(Element.Kind.DYNAMIC_OBSTACLE);
                break;

            case NEW_MYSTERY_BOX:
                _generate(Element.Kind.MYSTERY_BOX);
                break;

            case NEW_WALL:
                _generate(Element.Kind.WALL);
                break;

            case REMOVE_DYNAMIC_OBSTACLE:
                _remove(Element.Kind.DYNAMIC_OBSTACLE);
                break;

            case REMOVE_STATIC_OBSTACLE:
                _remove(Element.Kind.STATIC_OBSTACLE);
                break;

            case LOSE_KEY:
            {
                if (_keysCollected > 0)
                {
                    _generate(Element.Kind.KEY);

                    --_keysCollected;
                }

                else MysteryBox.showMessage(
                        _gui,
                        "But you have not collected any yet!"
                );
            }
        }

        // After the message boxes...
        redraw();
    }

    /**
     *
     * Handles generation of a new element due to a MysteryBox.
     * Takes care of the case when the generated element is on
     * the position of the player, in which case the player is
     * asked to go back. Also handles the situation when the
     * level is too full to generate a new element.
     *
     * @param kind The kind of element to generate.
     *
     * @return true if the element could be generated,
     *         false if the level was too full.
     */
    private boolean _generate(Element.Kind kind)
    {
        Element element;

        if((element = _grid.generate(kind, _theme)) != null)
        {
            for (Player player : _players)
            {
                if (element.point().equals(player.point()))
                {
                    // Edge cases, edge cases, edge cases. Gotta love them.
                    if (player.canGoBack()) player.goBack();

                    else break; // too full
                }
            }

            return true;
        }

        MysteryBox.showMessage(_gui, "But there is no space left!");

        return false;
    }

    /**
     *
     * Attempts to remove an element of a certain kind from the grid.
     *
     * @param kind The kind of element to attempt to remove.
     *
     * @return true if the removal was possible, else false.
     */
    private boolean _remove(Element.Kind kind)
    {
        if(_grid.remove(kind) != null) return true;

        else
        {
            MysteryBox.showMessage(_gui, "But there are none!");

            return false;
        }
    }

    /**
     *
     * Performs all the necessary operations to kill a player, e.g.
     * moving the player from the list of alive players to that of
     * dead players.
     *
     * @param player The player to kill.
     */
    private void _kill(Player player)
    {
        assert(_players.contains(player));
        assert(_IDMap.containsKey(player.id()));
        assert(player.isDead());

        _players.remove(player);

        _deadPlayers.add(player);

        _IDMap.remove(player.id());
    }

    /**
     * Checks if the screen and grid needs to be resized.
     */
    private void _checkResize()
    {
        if (! _screen.resizePending()) return;

        _screen.refresh();

        // Necessary because there is some weird bug in lanterna
        // that causes unoccupied space on the screen to be displayed
        // with green Xs instead of just the default background.
        _clear(_screen);

        _grid.resize(pageSize());

        _page = _grid.fetchPageOf(_players.get(0));

        _page.render(_screen);
    }

    /**
     *
     * Fixes the bug in lanterna that causes unoccupied space
     * on the screen to be displayed with green Xs instead
     * of just the default background.
     *
     * @param screen The screen to clear.
     */
    private void _clear(Screen screen)
    {
        ScreenWriter writer = new ScreenWriter(screen);

        writer.fillScreen(' ');
    }

    private Theme _theme;

    private String _name;

    private LevelSize _levelSize;

    private int _keysCollected;

    private int _totalKeys;

    private List<Profile> _hidden;

    private GUIScreen _gui;

    private Screen _screen;

    private Map<String, Player> _IDMap;

    private List<Player> _players;

    private List<Player> _deadPlayers;

    private PageGrid _grid;

    private Page _page;

    private boolean _won;

    private StatusBar _statusBar;

    private Difficulty _difficulty;

    private int _frameCount;
}