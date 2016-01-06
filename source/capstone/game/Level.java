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

public class Level
{
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

    public void update(Map<String, Direction> directions)
    {
        _checkResize();

        _updatePage();

        Page old = _page;

        _movePlayers(directions);

        _evaluatePlayers(directions);

        if (_page != old)
        {
            _screen.clear();
            _page.render(_screen);
        }

        _statusBar.draw();

        _screen.refresh();
    }

    public String name()
    {
        return _name;
    }

    public boolean isDone()
    {
        return hasWon() || hasLost();
    }

    public boolean hasWon()
    {
        return _won;
    }

    public boolean hasLost()
    {
        return _players.isEmpty() && _hidden.isEmpty();
    }

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

    public void resize()
    {
        _grid.resize(_screen.getTerminalSize());

        _screen.refresh();
    }

    public Theme theme()
    {
        return _theme;
    }

    public void Theme(Theme theme)
    {
        assert(theme != null);

        _theme = theme;

        _grid.pages().forEach(page -> page.render(_screen));

        _screen.refresh();
    }

    public List<Player> players()
    {
        List<Player> players = new ArrayList<>(_players);

        players.addAll(_deadPlayers);

        return players;
    }

    public List<Profile> hidden()
    {
        return Collections.unmodifiableList(_hidden);
    }

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

    public int keysCollected()
    {
        return _keysCollected;
    }

    public int totalKeys()
    {
        return _totalKeys;
    }

    public PageGrid grid()
    {
        return _grid;
    }

    public Page currentPage()
    {
        return _page;
    }

    public GUIScreen gui()
    {
        return _gui;
    }

    public void gui(GUIScreen gui)
    {
        assert(gui != null);

        _gui = gui;

        _screen = _gui.getScreen();
    }

    public Screen screen()
    {
        return _screen;
    }

    public LevelSize size()
    {
        return _levelSize;
    }

    public Difficulty difficulty()
    {
        return _difficulty;
    }

    public void store() throws  IOException
    {
        new LevelBuilder(this).store();
    }

    private void _updatePage()
    {
        if (_frameCount++ >= _difficulty.delay())
        {
            _page.update(_screen);

            _frameCount = 0;
        }
    }

    private void _movePlayers(Map<String, Direction> directions)
    {
        for (String id : directions.keySet())
        {
            Player player = _IDMap.get(id);

            // Happens when hidden players press a key
            if (player == null) continue;

            _move(player, directions.get(id));
        }
    }

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

    private void _unhidePlayer(Point point)
    {
        assert(! _hidden.isEmpty());

        Iterator<Profile> iterator = _hidden.iterator();

        Player player = new Player(point, iterator.next());

        _IDMap.put(player.id(), player);

        _players.add(player);

        iterator.remove();
    }

    private void _evaluatePlayers(Map<String, Direction> directions)
    {
        for (int i = 0; i < _players.size(); )
        {
            Player player = _players.get(i);

            _evaluate(player);

            if (player.isAlive()) ++i;
        }

        for (Player player : _players)
        {
            if (_page.isInside(player))
            {
                _checkPlayerCollision(player, directions);

                player.render(_screen, _page.region());
            }
        }
    }

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

        if (player.isDead()) _kill(player);

        _page = _grid.follow(player);
    }

    private void _checkPlayerCollision(Player player,
                                       Map<String, Direction> directions)
    {
        for (Player other : _players)
        {
            if (other == player) continue;

            // If one moved onto the other (one movement at a time)
            if (other.point().equals(player.point()))
            {
                if (directions.containsKey(player.id())) player.goBack();

                else other.goBack();

                break;
            }

            // If both moved, and they moved past each other such that
            // the point of the player is the previous point of other
            // and the point of other is the previouss point of the player
            // then undo both movements, because both moved we can be sure
            // that neither previousPoints will be null.
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

    private void _handleMysteryBox(MysteryBox mysteryBox, Player player)
    {
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
                if (_generate(Element.Kind.KEY, player)) ++_totalKeys;
                break;

            case NEW_STATIC_OBSTACLE:
                _generate(Element.Kind.STATIC_OBSTACLE, player);
                break;

            case NEW_DYNAMIC_OBSTACLE:
                _generate(Element.Kind.DYNAMIC_OBSTACLE, player);
                break;

            case NEW_MYSTERY_BOX:
                _generate(Element.Kind.MYSTERY_BOX, player);
                break;

            case NEW_WALL:
                _generate(Element.Kind.WALL, player);
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
                    _generate(Element.Kind.KEY, player);

                    --_keysCollected;
                }

                else MysteryBox.showMessage(
                        _gui,
                        "But you have not collected any yet!"
                );
            }
        }

        redraw();
    }

    private boolean _generate(Element.Kind kind, Player player)
    {
        Element element;

        if((element = _grid.generate(kind, _theme)) != null)
        {
            if (element.point().equals(player.point())) player.goBack();

            return true;
        }

        else
        {
            MysteryBox.showMessage(_gui, "But there is no space left!");

            return false;
        }
    }

    private boolean _remove(Element.Kind kind)
    {
        if(_grid.remove(kind) != null) return true;

        else
        {
            MysteryBox.showMessage(_gui, "But there are none!");

            return false;
        }
    }

    private void _kill(Player player)
    {
        assert(_players.contains(player));
        assert(_IDMap.containsKey(player.id()));
        assert(player.isDead());

        _players.remove(player);

        _deadPlayers.add(player);

        _IDMap.remove(player.id());
    }

    private void _checkResize()
    {
        if (! _screen.resizePending()) return;

        _screen.refresh();

        _clear(_screen);

        _grid.resize(pageSize());

        _page = _grid.fetchPageOf(_players.get(0));

        _page.render(_screen);
    }

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

    private List<Element> _freeEntrances;
}