package capstone.utility;

import capstone.data.Theme;
import capstone.element.Element;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PageGrid
{
    public static class Location extends AbstractPair<Index, Point>
    {
        public Location(Index index, Point point)
        {
            super(index, point);
        }

        public Location(Location other)
        {
            super(other);
        }

        public Index index()
        {
            return super.first();
        }

        public Point point()
        {
            return super.second();
        }
    }

    public PageGrid(LevelSize levelSize,
                    TerminalSize terminalSize)
    {
        assert(levelSize != null);
        assert(terminalSize != null);

        _grid = new ArrayList<>();

        _levelSize = levelSize;
        _terminalSize = terminalSize;

        _dimensions = _computeDimensions(levelSize, terminalSize);

        _redistribute(_grid, _newGrid(_dimensions, terminalSize));

        _currentIndex = new Index(0, 0);
    }

    public PageGrid(LevelSize levelSize,
                    TerminalSize terminalSize,
                    Collection<Element> elements)
    {
        this(levelSize, terminalSize);

        elements.forEach(this::add);

        _currentIndex = new Index(0, 0);
    }

    public void add(Element element)
    {
        assert(element != null);

        Page page = getPageOf(element);

        assert(page != null);

        page.add(element);

        ++_numberOfElements;
    }

    public void add(Index index, Element element)
    {
        assert(index != null);
        assert(element != null);
        assert(pageIndexOf(element).equals(index));

        _grid.get(index.row()).get(index.column()).add(element);
    }

    public void remove(Element element)
    {
        assert(element != null);

        Page page = getPageOf(element);

        assert(page != null);

        page.remove(element);

        --_numberOfElements;
    }

    public Element generate(Element.Kind kind, Theme theme)
    {
        Location location = findFreeSpace();

        if (location == null) return null;

        Element element = Element.Create(kind, location.point(), theme);

        add(location.index(), element);

        return element;
    }

    public Element remove(Element.Kind kind)
    {
        Location location = locationOf(kind);

        assert(location != null);

        Page page = get(location.index());

        assert(page != null);

        Element element = page.at(location.point());

        assert(element != null);

        page.remove(element);

        --_numberOfElements;

        return element;
    }

    public Element find(Element.Kind kind)
    {
        Location location = locationOf(kind);

        if (location == null) return null;

        Page page = get(location.index());

        assert(page != null);

        return page.at(location.point());
    }

    public Location locationOf(Element.Kind kind)
    {
        for (int row = 0; row < height(); ++row)
        {
            for (int column = 0; column < width(); ++column)
            {
                Page page = get(column, row);

                Collection<Element> collection = page.elements(kind);

                if (! collection.isEmpty())
                {
                    return new Location(
                            new Index(column, row),
                            collection.iterator().next().point()
                    );
                }
            }
        }

        return null;
    }

    public Location findFreeSpace()
    {
        Region level = new Region(
                _levelSize.getColumns() - 1, // size is not not inclusive
                _levelSize.getRows()    - 1  // but a region is
        );

        for (int row = 0; row < height(); ++row)
        {
            for (int column = 0; column < width(); ++column)
            {
                Point point = get(column, row).freePoint(level);

                if (point != null)
                {
                    return new Location(
                            new Index(column, row),
                            point
                    );
                }
            }
        }

        return null;
    }

    public Page fetch(int column, int row)
    {
        return fetch(new Index(column, row));
    }

    public Page fetch(Index index)
    {
        assert(index != null);

        assert(index.row() < _grid.size());
        assert(index.column() < _grid.get(index.row()).size());

        _currentIndex = index;

        // Invalidate
        _currentPage = null;

        return get(index);
    }

    public Page get(int column, int row)
    {
        return _grid.get(row).get(column);
    }

    public Page get(Index index)
    {
        return get(index.column(), index.row());
    }

    public Page follow(Element element)
    {
        Page page = currentPage();

        assert(page != null);

        page = _followHorizontally(element, page);

        assert(page != null);

        page = _followVertically(element, page);

        assert(page != null);

        return page;
    }

    public Page fetchAbove()
    {
        if (_currentIndex.row() > 0)
        {
            return fetch(_currentIndex.above());
        }

        else return null;
    }

    public Page fetchBelow()
    {
        if (_currentIndex.row() + 1 < height())
        {
            return fetch(_currentIndex.below());
        }

        else return null;
    }

    public Page fetchLeft()
    {
        if (_currentIndex.column() > 0)
        {
            return fetch(_currentIndex.left());
        }

        else return null;
    }

    public Page fetchRight()
    {
        if (_currentIndex.column() + 1 < width())
        {
            return fetch(_currentIndex.right());
        }

        else return null;
    }

    public Page getAbove()
    {
        if (_currentIndex.row() > 0) {
            return get(_currentIndex.above());
        }

        else return null;
    }

    public Page getBelow()
    {
        if (_currentIndex.row() + 1 < height()) {
            return get(_currentIndex.below());
        }

        else return null;
    }

    public Page getLeft()
    {
        if (_currentIndex.column() > 0) {
            return get(_currentIndex.left());
        }

        else return null;
    }

    public Page getRight()
    {
        if (_currentIndex.column() + 1 < width()) {
            return get(_currentIndex.right());
        }

        else return null;
    }

    public void resize(LevelSize levelSize)
    {
        assert(levelSize != null);

        Dimensions dimensions =
                _computeDimensions(levelSize, _terminalSize);

        int comparison = dimensions.second().compareTo(height());

        if (comparison == -1)
        {
            _shrinkLevelSizeForHeight(levelSize.getRows(), dimensions);
        }

        else if (comparison == +1)
        {
            _growLevelSizeForHeight(dimensions);
        }

        comparison = dimensions.first().compareTo(width());

        if (comparison == -1)
        {
            _shrinkLevelSizeForWidth(levelSize.getColumns(), dimensions);
        }

        else if (comparison == +1)
        {
            _growLevelSizeForWidth(dimensions);
        }

        assert(_grid.size() == dimensions.second());

        for (List<Page> row : _grid)
        {
            assert(row.size() == dimensions.first());
        }

        _levelSize = levelSize;

        _currentIndex = new Index(0, 0);
        _currentPage = null;
    }

    public void resize(TerminalSize terminalSize)
    {
        resize(_levelSize, terminalSize);
    }

    public void resize(LevelSize levelSize, TerminalSize terminalSize)
    {
        assert(terminalSize != null);

        _levelSize = levelSize;
        _terminalSize = terminalSize;

        if (_greater(_levelSize, _terminalSize))
        {
            _dimensions = _computeDimensions(levelSize, terminalSize);

            _redistribute(_grid, _newGrid(_dimensions, terminalSize));
        }
    }

    public Index pageIndexOf(Element element)
    {
        assert(element != null);

        return pageIndexAt(element.point());
    }

    public Index pageIndexAt(Point point)
    {
        int column = point.x() / _terminalSize.getColumns();

        if (column >= width()) return null;

        int row = point.y() / _terminalSize.getRows();

        if (row >= height()) return null;

        return new Index(column, row);
    }

    public Page fetchPageOf(Element element)
    {
        return fetchPageAt(element.point());
    }

    public Page fetchPageAt(Point point)
    {
        Index index = pageIndexAt(point);

        if (index == null) return null;

        _currentIndex = index;

        return fetch(index);
    }

    public Page getPageOf(Element element)
    {
        return getPageAt(element.point());
    }

    public Page getPageAt(Point point)
    {
        Index index = pageIndexAt(point);

        if (index == null) return null;

        return get(index);
    }

    public Index currentIndex()
    {
        assert(_currentIndex != null);

        return _currentIndex;
    }

    public Page currentPage()
    {
        if (_currentPage == null)
        {
            _currentPage = fetch(_currentIndex);
        }

        return _currentPage;
    }

    public int numberOfElements()
    {
        return _numberOfElements;
    }

    public int numberOfPages()
    {
        return width() * height();
    }

    public int levelWidth()
    {
        return _levelSize.getColumns();
    }

    public int levelHeight()
    {
        return _levelSize.getRows();
    }

    public LevelSize levelSize()
    {
        return _levelSize;
    }

    public int terminalWidth()
    {
        return _terminalSize.getColumns();
    }

    public int terminalHeight()
    {
        return _terminalSize.getRows();
    }

    public TerminalSize terminalSize()
    {
        return _terminalSize;
    }

    public int width()
    {
        return _dimensions.width();
    }

    public int height()
    {
        return _dimensions.height();
    }

    public Dimensions dimensions()
    {
        if (_dimensions == null)
        {
            _dimensions = new Dimensions(width(), height());
        }

        return _dimensions;
    }

    public int capacity()
    {
        return levelHeight() * levelWidth();
    }

    public boolean isEmpty()
    {
        return _numberOfElements == 0;
    }

    public boolean isFull()
    {
        return _numberOfElements == capacity();
    }

    public boolean isPerfectFit()
    {
        if (_levelSize.getColumns() % _terminalSize.getColumns() != 0)
        {
            return false;
        }

        if (_levelSize.getRows() % _terminalSize.getRows() != 0)
        {
            return false;
        }

        return true;
    }

    public boolean isRaggedFit()
    {
        return ! isPerfectFit();
    }

    public Collection<Page> pages()
    {
        Collection<Page> pages = new ArrayList<>();

        for (List<Page> row : _grid) row.forEach(pages::add);

        return pages;
    }

    private static Dimensions
    _computeDimensions(LevelSize levelSize, TerminalSize terminalSize)
    {
        double dimension = ((double)levelSize.getColumns()) /
                           terminalSize.getColumns();

        int width = (int) Math.ceil(dimension);

        dimension = ((double)levelSize.getRows()) / terminalSize.getRows();

        int height = (int) Math.ceil(dimension);

        return new Dimensions(width, height);
    }

    private void _shrinkLevelSizeForWidth(int columns, Dimensions dimensions)
    {
        boolean ragged = columns % _terminalSize.getColumns() != 0;

        for (int i = 0; i < height(); ++i)
        {
            List<Page> row = _grid.get(i);

            for (int j = dimensions.first(); j < width(); ++j)
            {
                row.remove(j);
            }

            if (ragged)
            {
                Iterator<Element> iterator =
                        row.get(dimensions.second() - 1).iterator();

                while(iterator.hasNext())
                {
                    if (iterator.next().point().x() >= columns)
                    {
                        iterator.remove();
                    }
                }
            }
        }

        _dimensions.first(dimensions.first());
    }

    private void _growLevelSizeForWidth(Dimensions dimensions)
    {
        int numberOfNewColumns = dimensions.first() - width();

        assert(numberOfNewColumns > 0);

        final int columns = _terminalSize.getColumns();

        for (int i = 0; i < height(); ++i)
        {
            List<Page> row = _grid.get(i);

            for (int j = 0; j < numberOfNewColumns; ++j) {
                Region left = row.get(row.size() - 1).region();

                Point southWest = new Point(left.southEast().right());
                Point northEast = new Point(
                        left.southEast().x() + columns,
                        left.northEast().y()
                );

                row.add(new Page(new Region(southWest, northEast)));
            }
        }

        _dimensions.first(dimensions.first());
    }

    private void _shrinkLevelSizeForHeight(int rows, Dimensions dimensions)
    {
        for (int i = height() - 1; i >= dimensions.second(); --i)
        {
            _grid.remove(i);
        }

        if (rows % _terminalSize.getRows() != 0)
        {
            for (Page page : _grid.get(_grid.size() - 1))
            {
                Iterator<Element> iterator = page.iterator();

                while(iterator.hasNext())
                {
                    if (iterator.next().point().y() >= rows)
                    {
                        iterator.remove();
                    }
                }
            }
        }

        _dimensions.second(dimensions.second());
    }

    private void _growLevelSizeForHeight(Dimensions dimensions)
    {
        int numberOfNewRows = dimensions.second() - height();

        assert(numberOfNewRows > 0);

        final int columns = _terminalSize.getColumns();
        final int rows = _terminalSize.getRows();

        int offset = height() * rows;

        for (int i = 0; i < numberOfNewRows; ++i, offset += rows)
        {
            List<Page> row = new ArrayList<>();

            Point initialSouthWest = new Point(0, offset + rows - 1);
            Point initialNorthEast = new Point(columns - 1, offset);

            row.add(new Page(new Region(initialSouthWest, initialNorthEast)));

            for (int j = 1; j < width(); ++j)
            {
                Region left = row.get(row.size() - 1).region();

                Point southWest = new Point(left.southEast().right());
                Point northEast = new Point(
                        left.southEast().x() + columns,
                        left.northEast().y()
                );

                row.add(new Page(new Region(southWest, northEast)));
            }

            _grid.add(row);
        }

        _dimensions.second(dimensions.second());
    }

    private List<List<Page>> _newGrid(Dimensions dimension,
                                      TerminalSize terminalSize)
    {
        final int columns = terminalSize.getColumns();
        final int rows = terminalSize.getRows();

        List<List<Page>> grid = new ArrayList<>(dimension.second());

        int yOffset = 0;

        for (int i = 0; i < dimension.second(); ++i)
        {
            List<Page> row = new ArrayList<>(dimension.first());

            int xOffset = 0;

            for (int j = 0; j < dimension.first(); ++j)
            {
                Region region = new Region(
                    new Point(xOffset, yOffset + rows - 1),
                    new Point(xOffset + columns - 1, yOffset)
                );

                row.add(new Page(region));

                xOffset += columns;
            }

            yOffset += rows;

            grid.add(row);
        }

        return grid;
    }

    private void _redistribute(List<List<Page>> oldGrid,
                               List<List<Page>> newGrid)
    {
        _grid = newGrid;

        _numberOfElements = 0;

        for (List<Page> row : oldGrid)
        {
            for (Page page : row)
            {
                page.forEach(this::add);
            }
        }
    }

    private Page _followHorizontally(Element element, Page page)
    {
        if (element.point().x() < page.region().southWest().x() &&
            _currentIndex.column() > 0)
        {
            return fetchLeft();
        }

        if (element.point().x() > page.region().northEast().x() &&
            _currentIndex.column() + 1 < width())
        {
            return fetchRight();
        }

        return page;
    }

    private Page _followVertically(Element element, Page page)
    {
        if (element.point().y() > page.region().southWest().y() &&
            _currentIndex.row() + 1 < height())
        {
            return fetchBelow();
        }

        if (element.point().y() < page.region().northEast().y() &&
            _currentIndex.row() > 0)
        {
            return fetchAbove();
        }

        return page;
    }

    private boolean _greater(LevelSize levelSize, TerminalSize terminalSize)
    {
        if (levelSize.getColumns() > terminalSize.getColumns()) return true;

        if (levelSize.getRows() > terminalSize.getRows()) return true;

        return false;
    }

    private LevelSize _levelSize;

    private TerminalSize _terminalSize;

    private List<List<Page>> _grid;

    private Dimensions _dimensions;

    
    private int _numberOfElements;

    private Index _currentIndex;

    private Page _currentPage;
}
