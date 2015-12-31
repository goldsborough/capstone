package capstone.gui;

import com.googlecode.lanterna.gui.component.TextBox;

import java.util.regex.Pattern;

/**
 * Created by petergoldsborough on 12/30/15.
 */
public class ValidatedTextBox extends Widget
{
    public ValidatedTextBox(String validation)
    {
        this(validation, 20);
    }

    public ValidatedTextBox(String validation, int width)
    {
        _pattern = Pattern.compile(validation);

        _textBox = new TextBox("", width);

        super.add(_textBox);
    }

    public boolean isValid()
    {
        return _pattern.matcher(text()).matches();
    }

    public String text()
    {
        return _textBox.getText();
    }

    public TextBox textBox()
    {
        return _textBox;
    }

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
