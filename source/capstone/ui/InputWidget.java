package capstone.ui;

import com.googlecode.lanterna.gui.dialog.MessageBox;

/**
 * Created by petergoldsborough on 12/30/15.
 */
public class InputWidget extends NamedWidget<ValidatedTextBox>
{
    public InputWidget(String title, String validation)
    {
        super(title, new ValidatedTextBox(validation));
    }

    public InputWidget(String title, String validation, int width)
    {
        super(title, new ValidatedTextBox(validation, width));
    }

    public InputWidget(String title,
                       ValidatedTextBox component)
    {
        super(title, component);
    }

    public boolean validate()
    {
        boolean valid = _widget.isValid();

        if (! valid)
        {
            MessageBox.showMessageBox(
                    _panel.getParent().getWindow().getOwner(),
                    "Invalid Input!",
                    String.format("\nThe input for '%s' was not valid!", _label.getText())
            );
        }

        return valid;
    }

    public boolean isValid()
    {
        return _widget.isValid();
    }

    public String text()
    {
        return _widget.text();
    }
}
