package at.tugraz.ist.catroid.ui.dialogs.brickdialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.entities.PrimitiveWrapper;

public class EditIntegerDialog extends EditBrickDialog implements OnClickListener {

    private PrimitiveWrapper<Integer> intValueReference;

    public EditIntegerDialog(Context context, EditText editText, PrimitiveWrapper<Integer> wrapper) {
        super(context, editText);
        intValueReference = wrapper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localEditText.setText(intValueReference.getValue().intValue() + "");
        localEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        Button closeButton = (Button) findViewById(R.id.dialogEditTextSubmit);
        closeButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == editText.getId()) {
            show();
        } else if (v.getId() == R.id.dialogEditTextSubmit) {
            try {
                intValueReference.setValue(Integer.parseInt(localEditText.getText().toString()));
                dismiss();
            } catch (Exception e) {
                intValueReference.setValue(0);
                dismiss();
            }
        }
    }
}
