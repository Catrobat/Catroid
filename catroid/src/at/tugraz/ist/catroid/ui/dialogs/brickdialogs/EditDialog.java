package at.tugraz.ist.catroid.ui.dialogs.brickdialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;

public class EditDialog extends Dialog {

    protected EditText editText;
    protected EditText referencedEditText;

    public EditDialog(Context context, EditText referencedEditText) {
        super(context);
        this.referencedEditText = referencedEditText;
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_text);
        setCanceledOnTouchOutside(true);
        editText = (EditText) findViewById(R.id.dialogEditText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }
}
