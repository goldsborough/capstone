# Capstone Project

## Building

You can compile the source as usual with `javac` and `java`:

`javac -cp lib/lanterna-2.1.9.jar:source source/Main.java`

`java -cp lib/lanterna-2.1.9.jar:source Main`

or optionally run the `run.py` script, which gives you the option to build the project with the `-b/--build` switch and then run it either in the terminal via `-t/--terminal` or in a SWING GUI using `-g/--graphical`. For example, it's this simple to build and run:

`python run.py -bg`

You can also easily import the project into NetBeans (`nbproject`) or Intellij (`.idea`).

## Testing

Test coverage for non-UI classes (especially any utility/data classes) is quite high. Tests are contained in the `test` folder and can be executed with `JUnit` and `HamCrest`, both included in `test/lib`.

Note that you will want to run the tests with the `-Djava.awt.headless=true`, as some tests require opening a GUI screen temporarily. Passing that flag will prevent it from showing, without affecting the test results.

## Documentation

The code has 100% documentation coverage. Generated JavaDoc is available in the `docs` folder and can be re-generated with any JavaDoc tool.
