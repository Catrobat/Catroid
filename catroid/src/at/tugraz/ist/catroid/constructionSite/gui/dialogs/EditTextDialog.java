package at.tugraz.ist.catroid.constructionSite.gui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.entities.PrimitiveWrapper;

public class EditTextDialog extends Dialog implements OnClickListener {

	private EditText mListEditText;
	private EditText localEditText;
	private Button closeButton;
	private PrimitiveWrapper<Integer> intValueReference;
	private PrimitiveWrapper<Double>  doubleValueReference;
	private PrimitiveWrapper<Long>    longValueReference;
	private BaseAdapter adapter;
	private boolean useSigned;

	private void init() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_edit_text);
		localEditText = (EditText) findViewById(R.id.dialogEditText);
		
		if (intValueReference != null) {
            localEditText.setText(intValueReference.getValue().intValue() + "");
        } else if (doubleValueReference != null) {
            localEditText.setText(doubleValueReference.getValue().doubleValue() + "");
        } else if (longValueReference != null) {
            localEditText.setText(longValueReference.getValue().longValue() + "");
        }
		
		closeButton = (Button) findViewById(R.id.dialogEditTextSubmit);
		closeButton.setOnClickListener(this);
	}
	
	public EditTextDialog(Context context, EditText brickEditText, BaseAdapter adapter, boolean useSigned) {
		super(context);
		mListEditText = brickEditText;
		this.adapter = adapter;
		this.useSigned = useSigned;
	}
	
	public void setInteger(PrimitiveWrapper<Integer> valueReference) {
		intValueReference = valueReference;
		doubleValueReference = null;
		longValueReference   = null;
		init();
	}
	
	public void setDouble(PrimitiveWrapper<Double> valueReference) {
		doubleValueReference = valueReference;
		intValueReference  = null;
		longValueReference = null;
		init();
	}
	
	public void setLong(PrimitiveWrapper<Long> valueReference) {
		longValueReference = valueReference;
		doubleValueReference = null;
		intValueReference    = null;
		init();
	}
	

	@Override
	public void show() {
		super.show();
		
		int settings = 0;
		
		if(intValueReference != null || longValueReference != null) {
            settings |= InputType.TYPE_CLASS_NUMBER;
        } else if (doubleValueReference != null) {
            settings |= InputType.TYPE_NUMBER_FLAG_DECIMAL;
        }
		
		if(useSigned) {
            settings |= InputType.TYPE_NUMBER_FLAG_SIGNED;
        }
		
		localEditText.setInputType(settings);
		
		localEditText.requestFocus();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
		saveContent();
		
	}

	@Override
	public void cancel() {
		saveContent();
		super.cancel();
	}

	private void saveContent() {
		if (intValueReference != null) {
            intValueReference.setValue(Integer.parseInt(localEditText.getText().toString()));
        } else if (doubleValueReference != null) {
            doubleValueReference.setValue(Double.parseDouble(localEditText.getText().toString()));
        } else if (longValueReference != null) {
            longValueReference.setValue(Long.parseLong(localEditText.getText().toString()));
        }

		mListEditText.setText(localEditText.getText().toString());
		adapter.notifyDataSetChanged();
	}

	public void onClick(View v) {
		System.out.println("__onClick dialog");
		if (v.getId() == mListEditText.getId()) {
			show();
		} else if(v.getId() == R.id.dialogEditTextSubmit) {
			Log.i("EditTextDialog", "in onClickListener");
			cancel();
		}

	}

}
