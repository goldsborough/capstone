package capstone.utility;

import capstone.data.Theme;
import capstone.element.Element;
import capstone.element.Player;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This wondrous data-structure (c) Peter Goldsborough enables constant-time
 * fetching of level-pages (regions that fit onto the screen). Let me explain
 * my thought process. So we have two problems: first, we would like to be
 * able to check in constant time if one element collides with anther. Great,
 * hashmap, done. But wait, we also need to be able to fetch a certain set
 * of elements that fit onto the screen whenever the player moves outside
 * of the region of the current page. Ok, a hashtable would mean O(N) iteration
 * to select the elements that fit into the screen. Life is too short for
 * O(N) iteration. So of-course, a tree. Elements are sorted by x-coordinates.
 * O(lg N) lookup. Sweet. To fetch the next page, we do two O(lg N) lookups
 * to find the lower and upper bound of the x-range of the new region within
 * the elements. Then we extract that x-range, do a O(X lg X) sort by
 * y-coordinate where X is the number of elements in the x-range. Then two
 * more O(lg X) to find the lower and upper bound of the y-range within that
 * x-range. Bingo, we have our page. Ok, so, O(2lgN + X lg X + 2lg X) will
 * reduce to either O(lg N) or O(X lg X) depending on the page dimensions.
 * That sounds good. But wait, there's more: pre-processing. Of course!
 * We know the size of the terminal (the page size) and the size of the level.
 * So we can calculate the page size by ceil-dividing (i.e. dividing and then
 * taking the ceiling) of levelSize / terminalSize for x and y individually
 * to get the height and width of the grid we need. Then, given the collection
 * of elements, which is available to us upon construction of the PageGrid in
 * most cases, we simply iterate over the elements and put each one in the
 * page it belongs in. So when we have a page, what is the complexity of
 * loading a another page, i.e. figuring out what elements belong into the
 * region the player is entering? O(1). That's a 1 ;)
 *
 * Of course, there is one caveat of pre-processing: it's not very dynamic.
 * Resizing the screen means re-calculating the grid and re-distributing the
 * entire collection of elements in most cases (optimizations can be made for
 * changes to the size of the level, never for changes to the size of the
 * terminal). But it's also a manageable O(N) expense and of course it happens
 * extremely rarely, so this solution should be magnificent in most cases.
 *
 * Regarding the first of the two problems state above, lookup of elements: the
 * pages themselves are of course only abstractions over hashtables, so it's
 * constant time all day long.
 */
public class PageGrid
{
    /**
     * A Location inside the grid consisting of a page-index into the
     * grid and point within the page at that index.
     */
    public static class Location extends AbstractPair<Index, Point>
    {
        /**
         *
         * Constructs a Location instance.
         *
         * @param index The index of the page within the grid.
         *
         * @param point The point on that page.
         */
        public Location(Index index, Point point)
        {
            super(index, point);
        }

        /**
         *
         * Copy-constructor.
         *
         * @param other The location to copy.
         */
        public Location(Location other)
        {
            super(other);
        }

        /**
         * @return The index of the page within the grid.
         */
        public Index index()
        {
            return super.first();
        }

        /**
         * @return The point of on the page.
         */
        public Point point()
        {
            return super.second();
        }
    }

    /**
     *
     * Constructs an empty PageGrid containing no elements, with the
     * dimensions as calculated from the level and terminal sizes passed.
     *
     * @param levelSize The size of the whole level.
     *
     * @param terminalSize The size of the terminal.
     */
    public PageGrid(LevelSize levelSize, TerminalSize terminalSize)
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

    /**
     *
     * Constructs a PageGrid, with the dimensions as calculated from the
     * level and terminal sizes passed, and then adds all the elements
     * passed in the collection.
     *
     * @param levelSize The size of the whole level.
     *
     * @param terminalSize The size of the terminal.
     *
     * @param elements The collection of elements to add.
     */
    public PageGrid(LevelSize levelSize,
                    TerminalSize terminalSize,
                    Collection<Element> elements)
    {
        this(levelSize, terminalSize);

        elements.forEach(this::add);

        _currentIndex = new Index(0, 0);
    }

    /**
     *
     * Adds an element to the grid.
     *
     * The element is mapped to the page it corresponds to
     * (if it is a valid element) and added to that page.
     *
     * @param element The element to add.
     */
    public void add(Element element)
    {
        assert(element != null);

        Page page = getPageOf(element);

        assert(page != null);

        page.add(element);

        ++_numberOfElements;
    }

    /**
     *
     * Adds an element to the page at the index.
     *
     *
     * The page will throw for elements
     * outside of its region, so beware.
     *
     * @param index The index of the page in the grid.
     *
     * @param element The element to add.
     */
    public void add(Index index, Element element)
    {
        assert(index != null);
        assert(element != null);
        assert(pageIndexOf(element).equals(index));

        _grid.get(index.row()).get(index.column()).add(element);
    }

    /**
     *
     * Attempts to remove the element from the grid.
     *
     * @param element The element to remove.
     */
    public void remove(Element element)
    {
        assert(element != null);

        Page page = getPageOf(element);

        assert(page != null);

        page.remove(element);

        --_numberOfElements;
    }

    /**
     *
     * Attempts to generate an element of the given kind somewhere on the grid.
     *
     * The them is necessary to construct the
     * element if a free point is found on a page.
     *
     * @param kind The kind of element to construct.
     *
     * @param theme The theme containing the representation for the kind.
     *
     * @return The element generated if there was free space on one of
     *         the pages, else null if there was no more free space anywhere.
     */
    public Element generate(Element.Kind kind, Theme theme)
    {
        Location location = findFreePoint();

        if (location == null) return null;

        Element element = Element.Create(kind, location.point(), theme);

        // Add the element to that page
        add(location.index(), element);

        return element;
    }

    /**
     *
     * Attempts to find and remove an element of that kind.
     *
     * @param kind The kind to attempt to find and remove.
     *
     * @return The element that was removed, if one was found on a
     *         page somewhere on the grid, else null if there was
     *         no element of the given kind on any page.
     */
    public Element remove(Element.Kind kind)
    {
        Location location = locationOf(kind);

        if (location == null) return null;

        Page page = get(location.index());

        assert(page != null);

        Element element = page.at(location.point());

        assert(element != null);

        page.remove(element);

        --_numberOfElements;

        return element;
    }

    /**
     *
     * Attempts to find an element of the given kind.
     *
     * Use locationOf() to get a location instead of an element.
     *
     * @param kind The kind to look for.
     *
     * @return An element of the kind if one was found on any page,
     *         else null if no such element was found on any page.
     *
     * @see PageGrid#locationOf
     */
    public Element find(Element.Kind kind)
    {
        Location location = locationOf(kind);

        if (location == null) return null;

        Page page = get(location.index());

        assert(page != null);

        return page.at(location.point());
    }

    /**
     *
     * Attempts to find the location of an element of the given kind.
     *
     * Use find() to get an actual element and not just a location.
     *
     * @param kind The kind to look for.
     *
     * @return The location of an element of the kind if one was found on
     *         any page, else null if no such element was found on any page.
     *
     * @see PageGrid#find
     */
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

    /**
     *
     * Attempts to find the Location of a free
     * point somewhere on a page in the grid.
     *
     * @return The Location of a free point somewhere on a page in the grid.
     */
    public Location findFreePoint()
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
                    return new Location(new Index(column, row), point);
                }
            }
        }

        return null;
    }

    /**
     *
     * Resizes the grid for a new LevelSize.
     *
     * This operation can be much much more efficient than the resizing
     * operations involving a change in the terminal size, as a change
     * in the levelSize may only mean removing or adding a few pages on
     * the outer edges of the grid, without expensive re-distribution of
     * elements.
     *
     * @param levelSize The new levelSize.
     */
    public void resize(LevelSize levelSize)
    {
        assert(levelSize != null);

        // Recompute the dimensions of the grid
        Dimensions dimensions =
                _computeDimensions(levelSize, _terminalSize);

        // Check if we need to add/remove pages at the bottom
        _resizeVertically(levelSize, dimensions);

        // Check if we need to add/remove pages on the right
        _resizeHorizontally(levelSize, dimensions);

        // Just assertions
        assert(_grid.size() == dimensions.height());

        for (List<Page> row : _grid)
        {
            assert(row.size() == dimensions.width());
        }

        _levelSize = levelSize;

        _currentIndex = new Index(0, 0);
        _currentPage = null;
    }

    /**
     *
     * Resizes the terminal according to a new terminal size.
     *
     * Calls resize() with the current levelSize and the terminalSize passed.
     *
     * @param terminalSize The new terminal size.
     */
    public void resize(TerminalSize terminalSize)
    {
        resize(_levelSize, terminalSize);
    }

    /**
     *
     * Resizes the terminal according to a new terminal size and level size.
     *
     * This operation is the most expensive in the grid, as it requires
     * creating an entirely new grid of pages and redistributing all the
     * elements in the old grid into that new grid. It's a linear-time
     * operation, but requires some temporary extra space because two
     * grids exist until the old one has been emptied into the new one.
     *
     * @param terminalSize The new terminal size.
     */
    public void resize(LevelSize levelSize, TerminalSize terminalSize)
    {
        assert(terminalSize != null);

        _levelSize = levelSize;
        _terminalSize = terminalSize;

        // Sweet optimization for when there is only one page.
        if (_greater(_levelSize, _terminalSize))
        {
            // get new width and height of the grid
            _dimensions = _computeDimensions(levelSize, terminalSize);

            _redistribute(_grid, _newGrid(_dimensions, terminalSize));
        }
    }

    /**
     *
     * Fetches a page at the given column and row.
     *
     * Fetching means returning the page and also
     * updating the current-index of the grid.
     * Use one of the `get` methods to only get
     * a page without modifying the current-index.
     *
     *
     * @param column The column in the grid of the page to fetch.
     *
     * @param row The row in the grid of the page to fetch.
     *
     * @return The page fetched.
     *
     * @see PageGrid#fetch(int, int)
     */
    public Page fetch(int column, int row)
    {
        return fetch(new Index(column, row));
    }

    /**
     *
     * Fetches a page at the given index.
     *
     * Fetching means returning the page and also
     * updating the current-index of the grid.
     * Use one of the `get` methods to only get
     * a page without modifying the current-index.
     *
     *
     * @param index The index to fetch the page at.
     *
     * @return The page fetched.
     *
     * @see PageGrid#get(int, int)
     */
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

    /**
     *
     * Gets a page at the given index.
     *
     * Getting means returning the page without
     * updating the current-index of the grid.
     * Use one of the `fetch` methods to get
     * a page and also update the current-index
     * to that page.
     *
     * @param column The column in the grid of the page to get.
     *
     * @param row The row in the grid of the page to get.
     *
     * @return The page fetched.
     *
     * @see PageGrid#fetch(int, int)
     */
    public Page get(int column, int row)
    {
        return _grid.get(row).get(column);
    }

    /**
     *
     * Gets a page at the given index.
     *
     * Getting means returning the page without
     * updating the current-index of the grid.
     * Use one of the `fetch` methods to get
     * a page and also update the current-index
     * to that page.
     *
     *
     * @param index The index to get the page at.
     *
     * @return The page fetched.
     *
     * @see PageGrid#fetch(int, int)
     */
    public Page get(Index index)
    {
        return get(index.column(), index.row());
    }

    /**
     *
     * Utility method to follow a player.
     * Equivalent to a call to fetchPageOf().
     *
     * @param player The player to follow.
     *
     * @return The page that was fetched.
     */
    public Page follow(Player player)
    {
        return fetchPageOf(player);
    }

    /**
     *
     * Fetches the page above the current one, if any.
     *
     * Fetching means returning the page and also
     * updating the current-index of the grid.
     * Use one of the `get` methods to only get
     * a page without modifying the current-index.
     *
     * @return The page fetched if there is one above, else null.
     *
     * @see PageGrid#getAbove
     */
    public Page fetchAbove()
    {
        if (_currentIndex.row() > 0)
        {
            return fetch(_currentIndex.above());
        }

        else return null;
    }

    /**
     *
     * Fetches the page below the current one, if any.
     *
     * Fetching means returning the page and also
     * updating the current-index of the grid.
     * Use one of the `get` methods to only get
     * a page without modifying the current-index.
     *
     * @return The page fetched if there is one below, else null.
     *
     * @see PageGrid#getBelow
     */
    public Page fetchBelow()
    {
        if (_currentIndex.row() + 1 < height())
        {
            return fetch(_currentIndex.below());
        }

        else return null;
    }

    /**
     *
     * Fetches the page left of the current one, if any.
     *
     * Fetching means returning the page and also
     * updating the current-index of the grid.
     * Use one of the `get` methods to only get
     * a page without modifying the current-index.
     *
     * @return The page fetched if there is one to the left, else null.
     *
     * @see PageGrid#getLeft
     */
    public Page fetchLeft()
    {
        if (_currentIndex.column() > 0)
        {
            return fetch(_currentIndex.left());
        }

        else return null;
    }

    /**
     *
     * Fetches the page right of the current one, if any.
     *
     * Fetching means returning the page and also
     * updating the current-index of the grid.
     * Use one of the `get` methods to only get
     * a page without modifying the current-index.
     *
     * @return The page fetched if there is one to the right, else null.
     *
     * @see PageGrid#getRight
     */
    public Page fetchRight()
    {
        if (_currentIndex.column() + 1 < width())
        {
            return fetch(_currentIndex.right());
        }

        else return null;
    }

    /**
     *
     * Gets the page above the current one, if any.
     *
     * Getting means returning the page without
     * updating the current-index of the grid.
     * Use one of the `fetch` methods to get
     * a page and also update the current-index
     * to that page.
     *
     * @return The page retrieved if there is one above, else null.
     *
     * @see PageGrid#fetchAbove
     */
    public Page getAbove()
    {
        if (_currentIndex.row() > 0)
        {
            return get(_currentIndex.above());
        }

        else return null;
    }

    /**
     *
     * Gets the page below the current one, if any.
     *
     * Getting means returning the page without
     * updating the current-index of the grid.
     * Use one of the `fetch` methods to get
     * a page and also update the current-index
     * to that page.
     *
     * @return The page retrieved if there is one below, else null.
     *
     * @see PageGrid#fetchBelow
     */
    public Page getBelow()
    {
        if (_currentIndex.row() + 1 < height())
        {
            return get(_currentIndex.below());
        }

        else return null;
    }

    /**
     *
     * Gets the page of left the current one, if any.
     *
     * Getting means returning the page without
     * updating the current-index of the grid.
     * Use one of the `fetch` methods to get
     * a page and also update the current-index
     * to that page.
     *
     * @return The page retrieved if there is one to the left, else null.
     *
     * @see PageGrid#fetchLeft
     */
    public Page getLeft()
    {
        if (_currentIndex.column() > 0) {
            return get(_currentIndex.left());
        }

        else return null;
    }

    /**
     *
     * Gets the page of right the current one, if any.
     *
     * Getting means returning the page without
     * updating the current-index of the grid.
     * Use one of the `fetch` methods to get
     * a page and also update the current-index
     * to that page.
     *
     * @return The page retrieved if there is one to the right, else null.
     *
     * @see PageGrid#fetchRight
     */
    public Page getRight()
    {
        if (_currentIndex.column() + 1 < width()) {
            return get(_currentIndex.right());
        }

        else return null;
    }

    /**
     *
     * Returns the index of the page the element's point is contained in.
     *
     * @param element The element to return the page-index of.
     *
     * @return The index of the page the element's point is contained in
     *         if the point is contained in the grid, else null.
     */
    public Index pageIndexOf(Element element)
    {
        assert(element != null);

        return pageIndexAt(element.point());
    }

    /**
     *
     * Returns the index of the page the point is contained.
     *
     * @param point The point to attempt to return the page-index.
     *
     * @return The index of the page the point is contained in
     *         if the point is contained in the grid, else null.
     */
    public Index pageIndexAt(Point point)
    {
        int column = point.x() / _terminalSize.getColumns();

        if (column >= width()) return null;

        int row = point.y() / _terminalSize.getRows();

        if (row >= height()) return null;

        return new Index(column, row);
    }

    /**
     *
     * Fetches the page the element is contained in.
     *
     * Fetching means returning the page and also
     * updating the current-index of the grid.
     * Use one of the `get` methods to only get
     * a page without modifying the current-index.
     *
     * @param element The element to fetch the page for.
     *
     * @return The page the element is contained in, if
     *         such a page exists in the grid, else null.
     *
     * @see PageGrid#getPageOf
     */
    public Page fetchPageOf(Element element)
    {
        return fetchPageAt(element.point());
    }

    /**
     *
     * Fetches the page the point is contained in.
     *
     * Fetching means returning the page and also
     * updating the current-index of the grid.
     * Use one of the `get` methods to only get
     * a page without modifying the current-index.
     *
     * @param point The point to fetch the page for.
     *
     * @return The page the point is contained in, if
     *         such a page exists in the grid, else null.
     *
     * @see PageGrid#getPageAt
     */
    public Page fetchPageAt(Point point)
    {
        Index index = pageIndexAt(point);

        if (index == null) return null;

        _currentIndex = index;

        return fetch(index);
    }

    /**
     *
     * Gets the page the element is contained in.
     *
     * Getting means returning the page without
     * updating the current-index of the grid.
     * Use one of the `fetch` methods to get
     * a page and also update the current-index
     * to that page.
     *
     * @param element The element to get the page for.
     *
     * @return The page the point is contained in, if
     *         such a page exists in the grid, else null.
     *
     * @see PageGrid#fetchPageOf
     */

    public Page getPageOf(Element element)
    {
        return getPageAt(element.point());
    }

    /**
     *
     * Gets the page the point is contained in.
     *
     * Getting means returning the page without
     * updating the current-index of the grid.
     * Use one of the `fetch` methods to get
     * a page and also update the current-index
     * to that page.
     *
     * @param point The point to get the page for.
     *
     * @return The page the point is contained in, if
     *         such a page exists in the grid, else null.
     *
     * @see PageGrid#fetchPageAt
     */
    public Page getPageAt(Point point)
    {
        Index index = pageIndexAt(point);

        if (index == null) return null;

        return get(index);
    }

    /**
     * @return The index of the "current" page of the grid.
     */
    public Index currentIndex()
    {
        assert(_currentIndex != null);

        return _currentIndex;
    }

    /**
     * @return The current page of the grid.
     */
    public Page currentPage()
    {
        if (_currentPage == null)
        {
            _currentPage = fetch(_currentIndex);
        }

        return _currentPage;
    }

    /**
     * @return The combined number of elements of all pages in the grid.
     */
    public int numberOfElements()
    {
        return _numberOfElements;
    }

    /**
     * @return The number pages contained in the grid.
     */
    public int numberOfPages()
    {
        return width() * height();
    }

    /**
     * @return The width (number of columns) of the level assumed by the grid.
     */
    public int levelWidth()
    {
        return _levelSize.getColumns();
    }

    /**
     * @return The height (number of rows) of the level assumed by the grid.
     */
    public int levelHeight()
    {
        return _levelSize.getRows();
    }

    /**
     * @return The LevelSize assumed by the grid.
     */
    public LevelSize levelSize()
    {
        return _levelSize;
    }

    /**
     * @return The width (number of columns) of the terminal assumed by the grid.
     */
    public int terminalWidth()
    {
        return _terminalSize.getColumns();
    }

    /**
     * @return The width (number of rows) of the terminal assumed by the grid.
     */
    public int terminalHeight()
    {
        return _terminalSize.getRows();
    }

    /**
     * @return The TerminalSize assumed by the grid.
     */
    public TerminalSize terminalSize()
    {
        return _terminalSize;
    }

    /**
     * @return The width (number of pages) of the grid.
     */
    public int width()
    {
        return _dimensions.width();
    }

    /**
     * @return The height (number of pages) of the grid.
     */
    public int height()
    {
        return _dimensions.height();
    }

    /**
     * @return The width and height (number of pages) of
     *         the grid, as a Dimensions object.
     */
    public Dimensions dimensions()
    {
        if (_dimensions == null)
        {
            _dimensions = new Dimensions(width(), height());
        }

        return _dimensions;
    }

    /**
     * @return The combined capacity of all pages in the
     *         grid, i.e. the area of the level.
     */
    public int capacity()
    {
        return levelHeight() * levelWidth();
    }

    /**
     * @return Whether the grid is empty.
     */
    public boolean isEmpty()
    {
        return _numberOfElements == 0;
    }

    /**
     * @return Whether the grid is full, i.e. whether
     *         there is no free space left on any page.
     */
    public boolean isFull()
    {
        return _numberOfElements == capacity();
    }

    /**
     * @return Whether the outer-most pages fit the entire terminal, i.e.
     *         if the level's width is an integer multiple of the terminal's
     *         width and the level's height is an intetger multiple of the
     *         terminal's height.
     */
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

    /**
     * @return The opposite of isPerfectFit().
     */
    public boolean isRaggedFit()
    {
        return ! isPerfectFit();
    }

    /**
     * @return A collection of all the pages contained in the grid.
     */
    public Collection<Page> pages()
    {
        Collection<Page> pages = new ArrayList<>();

        for (List<Page> row : _grid) row.forEach(pages::add);

        return pages;
    }

    /**
     *
     * Computes the dimensions resulting from the given
     * levelSize and terminalSize.
     *
     * @param levelSize The size of the level.
     *
     * @param terminalSize The size of the terminal.
     *
     * @return The resulting Dimensions object.
     */
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

    /**
     *
     * Performs the horizontal shrinking of the grid
     * on a level-size-only resize-event.
     *
     * This is one of the optimizations that can be performed for only resizing
     * the level. i.e. if the levelSize shrinks we only need to remove the
     * pages the level is no longer contained in, we don't need to redistribute
     * any elements.
     *
     * @param columns The new number of columns in the level.
     *
     * @param dimensions The dimensions computed for new levelSize
     *                   and existing terminalSize.
     */
    private void _shrinkLevelSizeForWidth(int columns, Dimensions dimensions)
    {
        boolean ragged = columns % _terminalSize.getColumns() != 0;

        // Iterate over the rows in the grid
        for (int i = 0; i < height(); ++i)
        {
            List<Page> row = _grid.get(i);

            // Start at the boundary of the dimensions and remove
            // all invalidated pages going to the right
            for (int j = dimensions.width(), w = width(); j < w; ++j)
            {
                row.remove(j);
            }

            // If the level is now ragged, i.e. the terminalSize is not
            // an divisor of the levelSize, then we also have to remove
            // the elements that are contained in the outer pages which
            // the level is only partially contained in, but that are
            // not contained in the level any longer.
            if (ragged)
            {
                Iterator<Element> iterator =
                        row.get(dimensions.height() - 1).iterator();

                while(iterator.hasNext())
                {
                    if (iterator.next().point().x() >= columns)
                    {
                        iterator.remove();
                    }
                }
            }
        }

        _dimensions.width(dimensions.width());
    }

    /**
     *
     * Performs the horizontal growing of the grid
     * on a level-size-only resize-event.
     *
     * This is one of the optimizations that can be performed for
     * only resizing the level. i.e. if the levelSize grows we only
     * need to add some new pages.
     *
     * @param dimensions The dimensions computed for new levelSize
     *                   and existing terminalSize.
     */
    private void _growLevelSizeForWidth(Dimensions dimensions)
    {
        int numberOfNewColumns = dimensions.width() - width();

        // Else we wouldn't have come here
        assert(numberOfNewColumns > 0);

        final int columns = _terminalSize.getColumns();

        // Iterate over the rows of the grid
        for (int i = 0; i < height(); ++i)
        {
            List<Page> row = _grid.get(i);

            // Add pages to the right
            for (int j = 0; j < numberOfNewColumns; ++j)
            {
                // Just computing the regions for the new pages
                Region left = row.get(row.size() - 1).region();

                Point southWest = new Point(left.southEast().right());
                Point northEast = new Point(
                        left.southEast().x() + columns,
                        left.northEast().y()
                );

                row.add(new Page(new Region(southWest, northEast)));
            }
        }

        _dimensions.width(dimensions.width());
    }

    /**
     *
     * Performs the vertical shrinking of the grid
     * on a level-size-only resize-event.
     *
     * This is one of the optimizations that can be performed for only resizing
     * the level. i.e. if the levelSize shrinks we only need to remove the
     * pages the level is no longer contained in, we don't need to redistribute
     * any elements.
     *
     * @param rows The new number of rows in the level.
     *
     * @param dimensions The dimensions computed for new levelSize
     *                   and existing terminalSize.
     */
    private void _shrinkLevelSizeForHeight(int rows, Dimensions dimensions)
    {
        // Move backwards for more efficient removal in an ArrayList
        for (int i = height() - 1; i >= dimensions.height(); --i)
        {
            _grid.remove(i);
        }

        // Same raggedness check as in _shrinkLevelSizeForWidth
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

        _dimensions.height(dimensions.height());
    }

    /**
     *
     * Performs the vertical growing of the grid
     * on a level-size-only resize-event.
     *
     * This is one of the optimizations that can be performed for
     * only resizing the level. i.e. if the levelSize grows we only
     * need to add some new pages.
     *
     * @param dimensions The dimensions computed for new levelSize
     *                   and existing terminalSize.
     */
    private void _growLevelSizeForHeight(Dimensions dimensions)
    {
        int numberOfNewRows = dimensions.height() - height();

        assert(numberOfNewRows > 0);

        final int columns = _terminalSize.getColumns();
        final int rows    = _terminalSize.getRows();

        int offset = height() * rows;

        // Add this many rows
        for (int i = 0; i < numberOfNewRows; ++i, offset += rows)
        {
            // Takes some work to create an entire row
            List<Page> row = new ArrayList<>();

            // Left-most page in the row
            Point initialSouthWest = new Point(0, offset + rows - 1);
            Point initialNorthEast = new Point(columns - 1, offset);

            row.add(new Page(new Region(initialSouthWest, initialNorthEast)));

            // Pages after that can use the previous page
            // (to their left) to calculate their own region.
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

        _dimensions.height(dimensions.height());
    }

    /**
     *
     * Creates a new grid given the dimensions and the terminalSize.
     *
     * The terminalSize is necessary to calculate the regions of the pages.
     *
     * @param dimension The width and height (in number of
     *                  pages) the new grid should have.
     *
     * @param terminalSize The size of the terminal the
     *                     pages will be displayed on.
     *
     * @return A grid with the specified dimensions.
     */
    private List<List<Page>> _newGrid(Dimensions dimension,
                                      TerminalSize terminalSize)
    {
        final int columns = terminalSize.getColumns();
        final int rows = terminalSize.getRows();

        List<List<Page>> grid = new ArrayList<>(dimension.height());

        int yOffset = 0;

        // For every row
        for (int i = 0; i < dimension.height(); ++i)
        {
            // Create a new row of pages
            List<Page> row = new ArrayList<>(dimension.width());

            int xOffset = 0;

            // Fill the row
            for (int j = 0; j < dimension.width(); ++j)
            {
                // Create the region with the x and y offsets so far
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


    /**
     *
     * Redistributes elements from an old grid to a new grid.
     *
     * @param oldGrid The source grid.
     *
     * @param newGrid The destination grid.
     */
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

    /**
     *
     * Handles vertical resizing (growing/shrinking)
     * for a level-size-only resize-event.
     *
     * @param levelSize The new levelSize.
     *
     * @param dimensions The computed new dimensions.
     */
    private void _resizeVertically(LevelSize levelSize, Dimensions dimensions)
    {
        int comparison = dimensions.height().compareTo(height());

        if (comparison == -1)
        {
            _shrinkLevelSizeForHeight(levelSize.getRows(), dimensions);
        }

        else if (comparison == +1)
        {
            _growLevelSizeForHeight(dimensions);
        }
    }

    /**
     *
     * Handles horizontal resizing (growing/shrinking)
     * for a level-size-only resize-event.
     *
     * @param levelSize The new levelSize.
     *
     * @param dimensions The computed new dimensions.
     */
    private void _resizeHorizontally(LevelSize levelSize, Dimensions dimensions)
    {
        int comparison = dimensions.width().compareTo(width());

        if (comparison == -1)
        {
            _shrinkLevelSizeForWidth(levelSize.getColumns(), dimensions);
        }

        else if (comparison == +1)
        {
            _growLevelSizeForWidth(dimensions);
        }
    }

    /**
     *
     * Utility method to determine if a levelSize
     * is greater than a terminalSize.
     *
     * @param levelSize The levelSize that should be compared.
     *
     * @param terminalSize The terminalSize that should be compared.
     *
     * @return True if the levelSize has more columns or
     *         rows than the terminalSize, else false.
     */
    private static boolean _greater(LevelSize levelSize,
                                    TerminalSize terminalSize)
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