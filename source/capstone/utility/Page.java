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
 * A Page contains the elements that fit onto the screen. It provides
 * kick-ass constant-time collision-lookup which is required in the Level
 * to determine if a player is colliding with an element. It also has a
 * pretty neat interface to perform a variety of operations on the elements
 * contained in the page.
 */
public class Page implements Iterable<Element>
{
    /**
     *
     * Constructs an empty page with no elements. The region specifies the
     * region (of the level) of the page. All elements the page holds will
     * be inside this region.
     *
     * @param region The region of the level this page represents.
     */
    public Page(Region region)
    {
        assert(region != null);

        _elements = new HashMap<>();

        _region = region;

        _setupLists();
    }

    /**
     *
     * Sets up the page with the region of the level this page represents,
     * and already adss the collection of elements to this page.
     *
     * @param region The region of the level this page represents.
     *
     * @param elements The elements to add to the page initially.
     */
    public Page(Region region, Collection<Element> elements)
    {
        this(region);

        assert(elements != null);

        _elements = new HashMap<>();

        _setupLists();

        elements.forEach(this::add);
    }

    /**
     * Updates the page without performing (un)rendering operations.
     *
     * Mostly required by tests, but has potential real-world applicability.
     *
     */
    public void update()
    {
        update(null);
    }

    /**
     *
     * Updates the dynamic elements of the page and renders them at
     * their updated positions onto the screen.
     *
     * @param screen The Screen to perform the (un)rendering operations on.
     */
    public void update(Screen screen)
    {
        for (Element element : _dynamicObstacles)
        {
            // Unfortunately we have to cast here.
            DynamicObstacle obstacle = (DynamicObstacle) element;

            if (screen != null) obstacle.unrender(screen, _region);

            _moveDynamicObstacle(obstacle);

            if (screen != null) obstacle.render(screen, _region);
        }
    }

    /**
     *
     * Renders all the elements contained in the page onto the screen.
     *
     * @param screen The screen to render the element onto.
     */
    public void render(Screen screen)
    {
        for (Element element : elements())
        {
            element.render(screen, _region);
        }
    }

    /**
     *
     * Adds an element to the page.
     *
     * The element must be not-null, must be contained in the region of
     * the page and must be at a location not yet occupied by another
     * element on the page.
     *
     * @param element The element to add.
     */
    public void add(Element element)
    {
        assert(element != null);
        assert(_region.contains(element.point()));
        assert(! _elements.containsKey(element.point()));

        _elements.put(element.point(), element);

        _listOf(element).add(element);
    }


    /**
     *
     * Removes an element from the page.
     *
     * @param element The element to remove.
     */
    public void remove(Element element)
    {
        assert(element != null);
        assert(_elements.containsKey(element.point()));

        _listOf(element).remove(element);

        _elements.remove(element.point());
    }

    /**
     *
     * Removes the element positioned at that point on the page.
     *
     * @param point The point of the element to remove. Must be really present.
     */
    public void remove(Point point)
    {
        assert(point != null);
        assert(_elements.containsKey(point));

        Element element = at(point);

        _listOf(element).remove(element);

        _elements.remove(point);
    }

    /**
     * Removes all elements from the page.
     */
    public void clear()
    {
        _elements.clear();

        _walls.clear();

        _entrances.clear();

        _exits.clear();

        _keys.clear();

        _dynamicObstacles.clear();

        _staticObstacles.clear();

        _mysteryBoxes.clear();
    }


    /**
     *
     * Returns the element at that point on the page, if any.
     *
     * @param point The point to attempt to find an element at.
     *
     * @return The element positioned at that point on the page if any,
     *         else none if no such element exists.
     */
    public Element at(Point point)
    {
        assert(point != null);

        return _elements.get(point);
    }

    /**
     *
     * Test whether there is an element on the page at that point.
     *
     * @param point The point to attempt to find an element at.
     *
     * @return True if there is an element at that
     *         point on the page, else false.
     */
    public boolean hasAt(Point point)
    {
        assert(point != null);

        return _elements.containsKey(point);
    }

    /**
     *
     * Test whether the page contains the element.
     *
     * @param element The element to look for.
     *
     * @return True if there is an element at the point of the element
     *         and the element found equals the element passed as argument.
     */
    public boolean contains(Element element)
    {
        assert(element != null);

        Element found = _elements.get(element.point());

        return found != null && found.equals(element);
    }


    /**
     *
     * Test whether an element is outside the region
     * of the level the page represents.
     *
     * Note that region boundaries are inclusive.
     *
     * @param element The element to test for.
     *
     * @return True if the element is outside
     *         the region of the page, else false.
     */
    public boolean isOutside(Element element)
    {
        assert(element != null);

        return isOutside(element.point());
    }

    /**
     *
     * Test whether a point is outside the region
     * of the level the page represents.
     *
     * Note that region boundaries are inclusive.
     *
     * @param point The point to test for.
     *
     * @return True if the point is outside the
     *         region of the page, else false.
     */
    public boolean isOutside(Point point)
    {
        assert(point != null);

        return ! _region.contains(point);
    }


    /**
     *
     * Test whether an element is inside the region
     * of the level the page represents.
     *
     * Note that region boundaries are inclusive.
     *
     * @param element The element to test for.
     *
     * @return True if the element is inside
     *         the region of the page, else false.
     */
    public boolean isInside(Element element)
    {
        assert(element != null);

        return isInside(element.point());
    }

    /**
     *
     * Test whether a point is inside the region
     * of the level the page represents.
     *
     * Note that region boundaries are inclusive.
     *
     * @param point The point to test for.
     *
     * @return True if the point is inside the
     *         region of the page, else false.
     */
    public boolean isInside(Point point)
    {
        assert(point != null);

        return _region.contains(point);
    }


    /**
     * @return The region of the level this page represents.
     */
    public Region region()
    {
        return _region;
    }

    /**
     *
     * Sets the region of the level this page represents.
     *
     * @param region The new region for the page.
     */
    public void region(Region region)
    {
        assert(region != null);

        _region = region;
    }


    /**
     * @return All the elements contained in the page.
     */
    public Collection<Element> elements()
    {
        return Collections.unmodifiableCollection(_elements.values());
    }

    /**
     * @param kind The kind of element to return all elements for.
     *
     * @return All elements of the given kind contained in the page.
     */
    public Collection<Element> elements(Element.Kind kind)
    {
        return Collections.unmodifiableCollection(_listOf(kind));
    }

    /**
     * @return All the positions of elements contained in the page.
     */
    public Collection<Point> positions()
    {
        return Collections.unmodifiableCollection(_elements.keySet());
    }

    /**
     * @return A map from all points to their
     *         respective elements contained on the page.
     */
    public Map<Point, Element> map()
    {
        return Collections.unmodifiableMap(_elements);
    }

    /**
     * @return An iterator over all elements contained in the page.
     */
    public Iterator<Element> iterator()
    {
        return elements().iterator();
    }


    /**
     * @return All walls contained in the page.
     */
    public Collection<Element> walls()
    {
        return Collections.unmodifiableCollection(_walls);
    }

    /**
     * @return All entrances contained in the page.
     */
    public Collection<Element> entrances()
    {
        return Collections.unmodifiableCollection(_entrances);
    }

    /**
     * @return All exits contained in the page.
     */
    public Collection<Element> exits()
    {
        return Collections.unmodifiableCollection(_exits);
    }

    /**
     * @return All keys contained in the page.
     */
    public Collection<Element> keys()
    {
        return Collections.unmodifiableCollection(_keys);
    }

    /**
     * @return All dynamic obstacles contained in the page.
     */
    public Collection<Element> dynamicObstacles()
    {
        return Collections.unmodifiableCollection(_dynamicObstacles);
    }

    /**
     * @return All static obstacles contained in the page.
     */
    public Collection<Element> staticObstacles()
    {
        return Collections.unmodifiableCollection(_staticObstacles);
    }

    /**
     * @return All mystery boxes contained in the page.
     */
    public Collection<Element> mysteryBoxes()
    {
        return Collections.unmodifiableCollection(_mysteryBoxes);
    }

    /**
     * @return How many free spots there are on the page.
     */
    public int amountOfFreeSpace()
    {
        return capacity() - size();
    }

    /**
     *
     * Searches the page for a free point (not occupied by an element).
     *
     * Note that the region of the level must be passed because it
     * can be that the level is smaller than the page size, in which case
     * looking for a free point in the region of the page could result in
     * a point that is contained in the page, but not in the level which
     * is a useless result. Therefore the minimum area between the page's
     * region and the level's region is searched.
     *
     * @param levelRegion The region of the level.
     *
     * @return A free point if one was found, else null.
     *
     * * @see Page#freeSpace
     */
    public Point freePoint(Region levelRegion)
    {
        Point origin = _region.northWest();

        int width = Math.min(_region.width(), levelRegion.width());

        int height = Math.min(_region.height(), levelRegion.height());

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

    /**
     *
     * The reason for the levelRegion parameter is
     * the same as for the freePoint() method.
     *
     * @param levelRegion The region of the level.
     *
     * @return All the free points on the page.
     *
     * @see Page#freePoint
     */
    public Collection<Point> freeSpace(Region levelRegion)
    {
        Collection<Point> free = new ArrayList<>();

        Point point = new Point(0, 0);

        int width = Math.min(_region.width(), levelRegion.width());

        int height = Math.min(_region.height(), levelRegion.height());

        for (int x = 0; x < width; ++x)
        {
            point.x(x);

            for (int y = 0; y < height; ++y)
            {
                point.y(y);

                if (! hasAt(point)) free.add(new Point(point));
            }
        }

        return free;
    }

    /**
     * @return The number of elements contained in the page.
     */
    public int size()
    {
        return _elements.size();
    }

    /**
     * @return The maximum number of elements the page can hold.
     *         Basically the area of the region.
     */
    public int capacity()
    {
        return _region.area();
    }

    /**
     * @return True if the page contains no elements, else false.
     */
    public boolean isEmpty()
    {
        return _elements.isEmpty();
    }

    /**
     * @return True if there is no more free space on the page.
     */
    public boolean isFull()
    {
        return size() == capacity();
    }

    /**
     * @param element The element whose kind to get the list for.
     *
     * @return The list of elements of the same type as the element passed.
     */
    private List<Element> _listOf(Element element)
    {
        return _listOf(element.kind());
    }

    /**
     * @param kind The kind of element to get the list for.
     *
     * @return The list of elements of that kind.
     */
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

    /**
     * Initializes all the lists for all the kinds of elements.
     */
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

    /**
     *
     * Handles updating the position of a dynamic obstacle.
     *
     * @param obstacle The obstacle to move.
     */
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
