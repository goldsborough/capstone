package capstone.utility;

import capstone.element.DynamicObstacle;
import capstone.element.Element;
import com.googlecode.lanterna.screen.Screen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by petergoldsborough on 12/31/15.
 */
public class Page implements Iterable<Element>
{
    public Page(Region region)
    {
        assert(region != null);

        _elements = new HashMap<>();

        _region = region;

        _setupLists();
    }

    public Page(Region region, Collection<Element> elements)
    {
        this(region);

        assert(elements != null);

        _elements = new HashMap<>();

        _setupLists();

        elements.forEach(this::add);
    }

    public void update()
    {
        update(null);
    }

    public void update(Screen screen)
    {
        for (Element element : _dynamicObstacles)
        {
            DynamicObstacle obstacle = (DynamicObstacle) element;

            if (screen != null) obstacle.unrender(screen, _region);

            _moveDynamicObstacle(obstacle);

            if (screen != null) obstacle.render(screen, _region);
        }
    }

    public void render(Screen screen)
    {
        for (Element element : elements())
        {
            element.render(screen, _region);
        }
    }

    public void add(Element element)
    {
        assert(element != null);
        assert(_region.contains(element.point()));
        assert(! _elements.containsKey(element.point()));

        _elements.put(element.point(), element);

        _listOf(element).add(element);
    }


    public void remove(Element element)
    {
        assert(element != null);
        assert(_elements.containsKey(element.point()));

        _listOf(element).remove(element);

        _elements.remove(element.point());
    }

    public void remove(Point point)
    {
        assert(point != null);
        assert(_elements.containsKey(point));

        Element element = at(point);

        _listOf(element).remove(element);

        _elements.remove(point);
    }

    public void clear()
    {
        _elements.clear();
    }


    public Element at(Point point)
    {
        assert(point != null);

        return _elements.get(point);
    }

    public boolean hasAt(Point point)
    {
        assert(point != null);

        return _elements.containsKey(point);
    }

    public boolean contains(Element element)
    {
        assert(element != null);

        Element found = _elements.get(element.point());

        return found != null && found.equals(element);
    }


    public boolean isOutside(Element element)
    {
        return isOutside(element.point());
    }

    public boolean isOutside(Point point)
    {
        return ! _region.contains(point);
    }


    public boolean isInside(Element element)
    {
        return isInside(element.point());
    }

    public boolean isInside(Point point)
    {
        return _region.contains(point);
    }


    public Region region()
    {
        return _region;
    }

    public void region(Region region)
    {
        assert(region != null);

        _region = region;
    }


    public Collection<Element> elements()
    {
        return Collections.unmodifiableCollection(_elements.values());
    }

    public Collection<Element> elements(Element.Kind kind)
    {
        return Collections.unmodifiableCollection(_listOf(kind));
    }

    public Collection<Point> positions()
    {
        return Collections.unmodifiableCollection(_elements.keySet());
    }

    public Map<Point, Element> map()
    {
        return Collections.unmodifiableMap(_elements);
    }

    public Iterator<Element> iterator()
    {
        return elements().iterator();
    }


    public Collection<Element> walls()
    {
        return Collections.unmodifiableCollection(_walls);
    }

    public Collection<Element> entrances()
    {
        return Collections.unmodifiableCollection(_entrances);
    }

    public Collection<Element> exits()
    {
        return Collections.unmodifiableCollection(_exits);
    }

    public Collection<Element> keys()
    {
        return Collections.unmodifiableCollection(_keys);
    }

    public Collection<Element> dynamicObstacles()
    {
        return Collections.unmodifiableCollection(_dynamicObstacles);
    }

    public Collection<Element> staticObstacles()
    {
        return Collections.unmodifiableCollection(_staticObstacles);
    }

    public Collection<Element> mysteryBoxes()
    {
        return Collections.unmodifiableCollection(_mysteryBoxes);
    }

    public int amountOfFreeSpace()
    {
        return capacity() - size();
    }

    public Point freePoint(Region level)
    {
        Point origin = _region.northWest();

        int width = Math.min(_region.width(), level.width());

        int height = Math.min(_region.height(), level.height());

        for (int x = 0; x < width; ++x)
        {
            for (int y = 0; y < height; ++y)
            {
                Point point = new Point(origin).move(x, y);

                if (! hasAt(point)) return point;
            }
        }

        return null;
    }

    public Collection<Point> freeSpace()
    {
        Collection<Point> free = new ArrayList<>();

        Point point = new Point(0, 0);

        for (int x = 0; x <= _region.width(); ++x)
        {
            point.x(x);

            for (int y = 0; y <= _region.height(); ++y)
            {
                point.y(y);

                if (! hasAt(point)) free.add(new Point(point));
            }
        }

        return free;
    }

    public int size()
    {
        return _elements.size();
    }

    public int capacity()
    {
        return _region.area();
    }

    public boolean isEmpty()
    {
        return _elements.isEmpty();
    }

    public boolean isFull()
    {
        return size() == capacity();
    }

    private List<Element> _listOf(Element element) {
        return _listOf(element.kind());
    }

    private List<Element> _listOf(Element.Kind kind)
    {
        assert(kind != null);

        switch (kind)
        {
            case WALL:
                return _walls;

            case ENTRANCE:
                return _entrances;

            case EXIT:
                return _exits;

            case KEY:
                return _keys;

            case STATIC_OBSTACLE:
                return _staticObstacles;

            case DYNAMIC_OBSTACLE:
                return _dynamicObstacles;

            case MYSTERY_BOX:
                return _mysteryBoxes;
        }

        throw new IllegalArgumentException();
    }

    private void _setupLists()
    {
        _walls = new ArrayList<>();

        _entrances = new ArrayList<>();

        _exits = new ArrayList<>();

        _keys = new ArrayList<>();

        _dynamicObstacles = new ArrayList<>();

        _staticObstacles = new ArrayList<>();

        _mysteryBoxes = new ArrayList<>();
    }

    private void _moveDynamicObstacle(DynamicObstacle obstacle)
    {
        _elements.remove(obstacle.point());

        obstacle.update(_region, _elements.keySet());

        _elements.put(obstacle.point(), obstacle);
    }


    private Map<Point, Element> _elements;

    private Region _region;


    private List<Element> _walls;

    private List<Element> _entrances;

    private List<Element> _exits;

    private List<Element> _keys;

    private List<Element> _dynamicObstacles;

    private List<Element> _staticObstacles;

    private List<Element> _mysteryBoxes;
}
