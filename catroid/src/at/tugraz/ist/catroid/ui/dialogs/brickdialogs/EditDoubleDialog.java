package at.tugraz.ist.catroid.ui.dialogs.brickdialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;

public class EditDoubleDialog extends EditDialog implements OnClickListener {
    private double value;

    public EditDoubleDialog(Context context, EditText referencedEditText, double value) {
        super(context, referencedEditText);
        this.value = value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        editText.setText(String.valueOf(value));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        Button closeButton = (Button) findViewById(R.id.dialogEditTextSubmit);
        closeButton.setOnClickListener(this);
    }
    
    public double getValue() {
        return value;
    }

    public void onClick(View v) {
    	if (v.getId() == referencedEditText.getId()) {
            show();
        } else {
        	value = Double.parseDouble((editText.getText().toString()));
        	dismiss();
        }
    }
}
