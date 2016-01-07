package capstone.ui;

import com.googlecode.lanterna.gui.dialog.MessageBox;

/**
 * A utility class that is a NamedWidget with a ValidatedTextBox, i.e.
 * a Label with a TextBox next to each other.
 */
public class InputWidget extends NamedWidget<ValidatedTextBox>
{
    /**
     *
     * Creates a new InputWidget with the given title
     * for the TextBox and a validation string.
     *
     * The width of the TextBox is defaulted (see
     * the ValidatedTextBox class for how much).
     *
     * @param title The title for the TextBox.
     *
     * @param validation The validation string for the TextBox.
     */
    public InputWidget(String title, String validation)
    {
        super(title, new ValidatedTextBox(validation));
    }

    /**
     *
     * Creates a new InputWidget with the given title
     * for the TextBox and a validation string. The
     * width of the TextBox is specified by the width
     * parameter.
     *
     * @param title The title for the TextBox.
     *
     * @param validation The validation string for the TextBox.
     *
     * @param width The width of the TextBox.
     */
    public InputWidget(String title, String validation, int width)
    {
        super(title, new ValidatedTextBox(validation, width));
    }

    /**
     *
     * Check if the input in the TextBox is valid and if not,
     * shows a MessageBox with a descriptive message describing
     * the error.
     *
     * @return True if the input in the TextBox is valid, else false.
     */
    public boolean validate()
    {
        boolean valid = _widget.isValid();

        if (! valid)
        {
            String message = String.format(
                    "\nThe input for '%s' was not valid!",
                    _label.getText()
            );

            MessageBox.showMessageBox(
                    _panel.getParent().getWindow().getOwner(),
                    "Invalid Input!",
                    message
            );
        }

        return valid;
    }

    /**
     * @return True if the input in the TextBox is valid, else false.
     */
    public boolean isValid()
    {
        return _widget.isValid();
    }

    /**
     * @return The text in the TextBox.
     */
    public String text()
    {
        return _widget.text();
    }
}
