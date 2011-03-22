package at.tugraz.ist.catroid.ui.dialogs.brickdialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;

public class EditIntegerDialog extends EditDialog implements OnClickListener {
	private int value;

	public EditIntegerDialog(Context context, EditText referencedEditText, int value) {
		super(context, referencedEditText);
		this.value = value;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		editText.setText(String.valueOf(value));
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		Button closeButton = (Button) findViewById(R.id.dialogEditTextSubmit);
		closeButton.setOnClickListener(this);
	}

	public int getValue() {
		return value;
	}

	public int getRefernecedEditTextId() {
		return referencedEditText.getId();
	}

	public void onClick(View v) {
		if (v.getId() == referencedEditText.getId()) {
			show();
		} else {
		    value = Integer.parseInt(editText.getText().toString());
			dismiss();
		}
	}
}
