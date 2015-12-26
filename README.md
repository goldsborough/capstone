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
	+ private save()
	+ contains players
	+ handles frame-rate delay, then calls level.update() in the main loop

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
	+ Object to keep track of highscores (time to complete a level).
	+ Manages resources in .highscore files
	+ Internally a TreeMap<points, name> for each level (use correct comparison function)
	+ And a HashMap<level_name, TreeMap<time, name>> for all levels
	+ Can insert new player with time
	+ Can delete names, though will be linear search
	+ newEntry(time, Players...) // variadic

* Level: Field that renders entities.
	+ Contains hashmaps for various Renderables:
		+ HashMap<Point, Wall>
		+ HashMap<Point, Obstacle>
		+ HashMap<Point, Key>
		+ HashMap<Point, MysteryBox>
	+ Decoding/handling of which object the player is colliding with can then be handled via the information of which HashMap the coordinate of the player is contained with, rather than having one HashMap<Point, Renderable> and then switch-casing on Renderable.Kind.
	+ full management of storing/loading
	+ Contains references to players (non-owning pointers)
	+ update(): Compares player positions (updated by Game) with

* Renderable: Basic renderable object.
	+ abstract
	+ Defined by:
		+ Point
		+ Character
		+ Kind (Enum member)
	+ Nested class Representation (Character, Color)
	+ Renderable(Point, Kind)
	+ Renderable(Point, Code)
	+ Renderable(Point, Kind, Representation)
	+ static kind(code)
	+ static code(kind)
	+ static private HashMap<Kind, Representation>
	+ serialize()/toString()
	+ character()
	+ color()
	+ Loads representation to a static HashMap<Kind, Representation>

* Player
	+ extends Renderable
	+ Direction Enum
	+ Player(id)
	+ up()
	+ down() // Called by game
	+ left()
	+ right()
	+ move(Direction)
	+ lives(): int
	+ heal()
	+ injure()
	+ reset() // for new level
	+ contains a Profile object

* Profile
	+ real-name
	+ keymap
	+ personal highscore
	+ times played
	+ date joined
	+ stored as .profile

* Wall
	+ extends Renderable
	+ uses '□' as character, by default

* Key
	+ extends Renderable
	+ uses '🔑' as character, by default

* MysteryBox
	+ extends Renderable
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
	+ extends Renderable
	+ uses '🚪' as character, by default

* Point
	+ immutable
	+ x
	+ y
	+ Arithmetic Operations
	+ Comparison functions
	+ factory function for random

## How I am extending

* Keep track of time
* Highscore for fastest time to win a level
* Optional: Timeout for level
* Mystery Objects that generate more keys, deduct a life or add one
* Functions for dynamic obstacles
* Multiple Keys
