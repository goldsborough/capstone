# Notes for Level

## Basic Ideas

* Game: control main program flow.
	+ public Game(double frameRate)
	+ public Game() // Default frameRate of > 1 Hz
	+ public start() // creates and shows menu, asks user for name for highscore
		+ start() menu shows option to load and exit
	+ private handleKey()
	+ private pause() // creates and shows menu
	+ private load(path)
		+ calls loop()
	+ loop(): game loop,
		+ while (! level.done()), read input, show menu if `Esc`, else pass to level player movements
	+ private exit()
	+ private resume()
	+ private save() // saves session
	+ contains __profiles__, not players, because players are the UI (level) while profiles are the model (Game)
	+ There is a timer which times out upon the framerate, puts keypresses into a hashtable <player, key> in the meantime, then upon timeout passes that to the level and clears it.
	+ Map<ID, Direction>

* Menu: display options to the user.

Renders a menu onto the screen. Two APIs:
1. Supply things to show in the constructor. Get lastAction(), which returns a Menu.Action member. Optionally show/hide actions via `show()`/`hide()` methods.
2. Default construct Menu, call `show()` with two arguments: The action to show and a callback for when it is pressed. Moves switch logic from the Caller to the callee (the Menu). Menu.Action could also just be a string.

	+ public Menu(void)
	+ public Menu(things to show, no callbacks)
	+ public show(Menu.Action)
	+ public show(Menu.Action, Callback)
	+ public hide(Menu.Action) // unsets callback too

* Highscore
	+ Object to keep track of a highscore (time to complete a level).
	+ Manages resources in a .highscore file
	+ Internally a TreeMap<time, name> (use correct comparison function)
	+ Can insert new player with time
	+ Can delete names, though will be linear search
	+ newEntry(level, time, List<Players>)

* Level: Field that renders Elements.
	+ Contains hashmaps for various Elements:
		+ HashMap<Point, Wall>
		+ HashMap<Point, Obstacle>
		+ HashMap<Point, Key>
		+ HashMap<Point, MysteryBox>
	+ Decoding/handling of which object the player is colliding with can then be handled via the information of which HashMap the coordinate of the player is contained with, rather than having one HashMap<Point, Element> and then switch-casing on Element.Kind.
	+ full management of storing/loading .session files
	+ update(Map<ID, Direction>):
	+ done(): players dead or game won
	+ Level(Properties levelSpecification, List<Profile>)
	+ Level(Session session, List<Profile>)
	+ Loads themes from .theme files which contain maps
	+ has HashMap<Point, Element> for all items, when Players' Point hashes
	+ to existing Element, then call interact
	+ Additionally have ArrayList<Element> that needs to be updated (contains only references)

* Representation:
	+ Character
	+ Backgroundcolor
	+ Foregroundcolor

* Theme
	+ Contains HashMap<Kind, Representation>
	+ extends Data
	+ transports data and allows iteration

* Element: Basic renderable object.
	+ abstract
	+ Defined by:
		+ Point
		+ Character (to print)
		+ Kind (Enum member)
	+ Factory method fromKind
	+ Element(Kind, Point, Representation)
	+ static code(kind)
	+ serialize()/toString()
	+ representation()
	+ point()
	+ kind()
	+ abstract interact(Player): overridden by Elements, controls what happens
	  to a player when he/she interacts with this renderable.

* Player
	+ extends Element
	+ Direction Enum
	+ Player(Profile)
	+ up()
	+ down() // Called by game
	+ left()
	+ right()
	+ move(Direction)
	+ lives(): int
	+ heal()
	+ injure()
	+ isAlive()
	+ isDead()
	+ interact()
	+ id()
	+ contains a Profile object
	+ lives has a maximum and minimum (0)

* Profile
	+ real-name
	+ keymap
	+ times played
	+ date joined
	+ Element.Representation
	+ stored as .profile

* Wall
	+ extends Element
	+ uses '□' as character, by default

* Key
	+ extends Element
	+ uses '🔑' as character, by default

* MysteryBox
	+ extends Element
	+ uses '⍰' as character, by default
	+ reveal():
		+ returns Event object, contains a message string and an enum member:
		+ Nothing
		+ Heal (you get Freibier)
		+ Injure
		+ LoseKey
		+ NewKey
		+ NewDynamicObstacle
		+ NewStaticObstacle
		+ NewWall
		+ RemoveDynamicObstacle
		+ RemoveStaticObstacle
		+ NewMysteryBox
		+ UnlockDoor

* Door
	+ extends Element
	+ uses '🚪' as character, by default

* Point
	+ immutable
	+ x
	+ y
	+ Arithmetic Operations
	+ Comparison functions
	+ factory function for random

* Session:
	+ The name of the original level
	+ The serialization of the current level (essentially a new level, but with the keys already collected gone etc.)
	+ The number of keys collected
	+ The time elapsed
	+ Player positions: load only players that are registered for the game, put new players in new positions
	+ Session stores hashset of players, then iterate over arraylist passed from game object, linear lookup fuck yeah

* Stopwatch
	+ Timing a Game

* StaticObstacle: typedef for Element

* DynamicObstacle: extends Element with update() function

Players cannot move onto each other. At start, try to distribute them across entrances, but have them wait in the background if not enough entrances.

Resources:
* Profiles: player profiles
* Levels: level specifications
* Highscores: highscores for each level
* Sessions: stored sessions



## How I am extending

* Keep track of time
* Highscore for fastest time to win a level
* Optional: Timeout for level
* Mystery Objects that generate more keys, deduct a life or add one
* Functions for dynamic obstacles
* Multiple Keys

## TDD

1. Think about what the code (function) will do
2. Write test that will pass once the code does what it's supposed to
3. Run the test, see it fail
	* If you need to backtest, break old code, see the test fail, unbreak
4. Write the code
5. Run the test, see it pass (else modify code)

If you write testable code, you ensure that your code:
* is modular
* has decoupled design
* has methods of limited scope
* (it has to be easy to test)

Ultimately, you will have organized your thoughts and thus you will be faster in writing codes!

* Some tests are too trivial to be useful.
* Some things are too hard to test.

"Executable Specs"

* Have methods take arguments, even if just references and you pass internal state, rather than work with internal state implicitly. Easier to test.

* Unit test review:
	* `new` operators are difficult to test
	* global state is difficult to test
	* doing work in constructor is a bad idea, because you can only call it once for an object.

Think about software testing like hardware testing: if you fuck up hardware test, you cannot go back. You must think of software testing in the same way.

Could you reconstruct your code given only your tests?

Ruby talk: https://www.youtube.com/watch?v=HhwElTL-mdI
Google talk: https://www.youtube.com/watch?v=XcT4yYu_TTs
