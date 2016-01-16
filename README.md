# Capstone Project

## How I am extending

1.  Highscores!
2.  Difficulty Levels!
3.  Multiplayer (that should be worth a lot because it is a whole new level of
    requirements and required a LOT of effort)!
4.  Profiles!
5.  Profile Creation!
6.  Themes!
7.  Theme Creation!
8.  MysteryBoxes!
9.  Fancy Status Bar with lots of information that resizes for new players!
10. You can press 'Backspace'/'Delete' to get to the legend directly!
11. Pretty GUI Menus!
12. Tests!

## Building

You can compile the source as usual with `javac` and `java`:

`javac -cp lib/lanterna-2.1.9.jar:source source/Main.java`

`java -cp lib/lanterna-2.1.9.jar:source Main`

or optionally run the `run.py` script, which gives you the option to build the
project with the `-b/--build` switch and then run it either in the terminal via
`-t/--terminal` or in a SWING GUI using `-g/--graphical`. For example, it's this
simple to build and run:

`python run.py -bg`

You can also easily import the project into NetBeans (`nbproject`) or Intellij
(`.idea`).

## Testing

Test coverage for non-UI classes (especially any utility/data classes) is quite
high. Tests are contained in the `test` folder and can be executed with `JUnit`
and `HamCrest`, both included in `test/lib`.

Note that you will want to run the tests with the `-Djava.awt.headless=true`, as
some tests require opening a GUI screen temporarily. Passing that flag will
prevent it from showing, without affecting the test results.

## Documentation

The code has 100% documentation coverage. Generated JavaDoc is available in the
`docs` folder and can be re-generated with any JavaDoc tool.
