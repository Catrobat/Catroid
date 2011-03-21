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

public class EditDoubleDialog extends EditBrickDialog implements OnClickListener {

    private PrimitiveWrapper<Double> doubleValueReference;

    public EditDoubleDialog(Context context, EditText editText, PrimitiveWrapper<Double> wrapper) {
        super(context, editText);
        doubleValueReference = wrapper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localEditText = (EditText) findViewById(R.id.dialogEditText);
        localEditText.setText(doubleValueReference.getValue().intValue() + "");
        localEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        Button closeButton = (Button) findViewById(R.id.dialogEditTextSubmit);
        closeButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == editText.getId()) {
            show();
        } else if (v.getId() == R.id.dialogEditTextSubmit) {
            try {
                doubleValueReference.setValue(Double.parseDouble(localEditText.getText().toString()));
                dismiss();
            } catch (Exception e) {
                doubleValueReference.setValue(0.0);
                dismiss();
            }
        }
    }
}
