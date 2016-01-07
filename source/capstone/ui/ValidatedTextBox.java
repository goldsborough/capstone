package capstone.ui;

import com.googlecode.lanterna.gui.component.TextBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A thin TextBox-wrapper that can check if its input
 * matches a certain regular-expression pattern.
 */
public class ValidatedTextBox extends Widget
{
    /**
     *
     * Constructs the ValidatedTextBox with
     * a validation regular-expression string.
     *
     * Defaults the width to 20 characters.
     *
     * @param validation The validation regular-expression string.
     */
    public ValidatedTextBox(String validation)
    {
        this(validation, 20);
    }

    /**
     *
     * Constructs the ValidatedTextBox with a validation
     * regular-expression string and a width for the TextBox.
     *
     * @param validation The validation regular-expression string.
     *
     * @param width The width of the TextBox, in columns.
     */
    public ValidatedTextBox(String validation, int width)
    {
        _pattern = Pattern.compile(validation);

        this.textBox(new TextBox("", width));
    }

    /**
     * @return True if the current text in the TextBox matches
     *         the validation pattern, else false.
     */
    public boolean isValid()
    {
        return _pattern.matcher(text()).matches();
    }

    /**
     * @return The current text in the TextBox.
     */
    public String text()
    {
        return _textBox.getText();
    }

    /**
     * @return A collection of the groups matched by the validation
     *         regular-expression pattern on the current text.
     */
    public Collection<String> groups()
    {
        // Get the matcher
        Matcher matcher = _pattern.matcher(text());

        // Match the whole string
        if (! matcher.matches()) return null;

        // Collect the Groups
        Collection<String> strings = new ArrayList<>();

        // Group 0 is the full match, so the groups start at 1
        // and go inclusively to the groupCount
        for (int i = 1; i <= matcher.groupCount(); ++i)
        {
            strings.add(matcher.group(i));
        }

        return strings;
    }

    /**
     * @return The concatenation of all the groups matched by the validation
     *         regular-expression pattern on the current text.
     */
    public String concatenatedGroups()
    {
        // Get the matcher
        Matcher matcher = _pattern.matcher(text());

        // Match the whole string
        if (matcher.matches()) return null;

        StringBuilder builder = new StringBuilder();

        // Group 0 is the full match, so the groups start at 1
        // and go inclusively to the groupCount
        for (int i = 1; i <= matcher.groupCount(); ++i)
        {
            builder.append(matcher.group(i));
        }

        return builder.toString();
    }

    /**
     * @return The underlying TextBox.
     */
    public TextBox textBox()
    {
        return _textBox;
    }

    /**
     *
     * Sets the underlying TextBox.
     *
     * This does not modify the validation string.
     *
     * @param textBox The new underlying TextBox.
     */
    public void textBox(TextBox textBox)
    {
        assert(textBox != null);

        super.removeAllComponents();

        _textBox = textBox;

        super.add(_textBox);
    }

    private Pattern _pattern;

    private TextBox _textBox;
}
