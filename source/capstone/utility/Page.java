package capstone.utility;

import capstone.element.DynamicObstacle;
import capstone.element.Element;
import com.googlecode.lanterna.screen.Screen;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
    }

    public Page(Region region, Collection<Element> elements)
    {
        this(region);

        assert(elements != null);

        elements.forEach(this::add);
    }

    public void update()
    {
        for (Element element : elements())
        {
            if (element.kind() == Element.Kind.DYNAMIC_OBSTACLE)
            {
                ((DynamicObstacle) element).update();
            }
        }
    }

    public void add(Element element)
    {
        assert(element != null);
        assert(! _elements.containsKey(element.point()));

        _elements.put(element.point(), element);
    }


    public void remove(Element element)
    {
        assert(element != null);

        remove(element.point());
    }

    public void remove(Point point)
    {
        assert(point != null);

        _elements.get(point).unrender();

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


    public Collection<Element> elements()
    {
        return _elements.values();
    }

    public Collection<Point> positions()
    {
        return _elements.keySet();
    }

    public Map<Point, Element> map()
    {
        return _elements;
    }

    public Iterator<Element> iterator()
    {
        return elements().iterator();
    }


    public int size()
    {
        return _elements.size();
    }

    public boolean isEmpty()
    {
        return _elements.isEmpty();
    }



    private Map<Point, Element> _elements;

    private Region _region;
}
