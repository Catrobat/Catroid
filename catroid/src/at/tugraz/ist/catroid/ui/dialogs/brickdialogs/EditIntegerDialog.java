package at.tugraz.ist.catroid.ui.dialogs.brickdialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.entities.PrimitiveWrapper;

public class EditIntegerDialog extends Dialog implements OnClickListener {

    private PrimitiveWrapper<Integer> intValueReference;
    private EditText localEditText;
    private EditText editText;

    public EditIntegerDialog(Context context, EditText editText) {
        super(context);
        this.editText = editText;
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_text);
        setCanceledOnTouchOutside(true);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        localEditText = (EditText) findViewById(R.id.dialogEditText);
        localEditText.setText(intValueReference.getValue().intValue() + "");
        Button closeButton = (Button) findViewById(R.id.dialogEditTextSubmit);
        closeButton.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }

    public void onClick(View v) {
        if (v.getId() == editText.getId()) {
            show();
        } else if (v.getId() == R.id.dialogEditTextSubmit) {
            cancel();
        }
    }

    public void setInteger(PrimitiveWrapper<Integer> intValue) {
        intValueReference = intValue;
    }

    @Override
    public void cancel() {
        intValueReference.setValue(Integer.parseInt(localEditText.getText().toString()));
        super.cancel();
    }
}
