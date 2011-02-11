package at.tugraz.ist.catroid.constructionSite.gui.dialogs;

import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
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

	private void init() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_edit_text);
		localEditText = (EditText) findViewById(R.id.dialogEditText);
		
		if (intValueReference != null)
			localEditText.setText(intValueReference.getValue().intValue() + "");
		else if (doubleValueReference != null)
			localEditText.setText(doubleValueReference.getValue().doubleValue() + "");
		else if (longValueReference != null)
			localEditText.setText(longValueReference.getValue().longValue() + "");
		
		closeButton = (Button) findViewById(R.id.dialogEditTextSubmit);
		closeButton.setOnClickListener(this);
	}
	
	public EditTextDialog(Context context, EditText brickEditText, BaseAdapter adapter) {
		super(context);
		this.mListEditText = brickEditText;
		this.adapter = adapter;
	}
	
	public void setInteger(PrimitiveWrapper<Integer> valueReference) {
		this.intValueReference = valueReference;
		this.doubleValueReference = null;
		this.longValueReference   = null;
		init();
	}
	
	public void setDouble(PrimitiveWrapper<Double> valueReference) {
		this.doubleValueReference = valueReference;
		this.intValueReference  = null;
		this.longValueReference = null;
		init();
	}
	
	public void setLong(PrimitiveWrapper<Long> valueReference) {
		this.longValueReference = valueReference;
		this.doubleValueReference = null;
		this.intValueReference    = null;
		init();
	}
	
//	public EditTextDialog(Context context, EditText brickEditText, PrimitiveWrapper<Double> valueReference) {
//		super(context);
//		this.doubleValueReference = valueReference;
//		this.intValueReference = null;
//		
//		init(brickEditText);
//	}

	public void show(HashMap<String, String> brickMap, EditText text) {
		/*
		mBrickMap = brickMap;
		mListEditText = text;
		String tag = (String) text.getTag();

		// allow decimal numbers only in wait dialog
		if (tag.equals(getContext().getString(
				R.string.constructional_brick_wait_edit_text_tag)))
			mLocalEditText.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL
					| InputType.TYPE_NUMBER_FLAG_SIGNED);
		else
			mLocalEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

		if (tag.equals(getContext().getString(
				R.string.constructional_brick_go_to_y_tag)))
			isValue1 = true;
		else
			isValue1 = false;
		if (tag.equals(getContext().getString(
				R.string.constructional_brick_go_to_x_tag))
				|| tag.equals(getContext().getString(
						R.string.constructional_brick_go_to_y_tag))) {
			mLocalEditText.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_SIGNED);
		}
		if (isValue1)
			mLocalEditText.setText(brickMap.get(BrickDefine.BRICK_VALUE_1));
		else
			mLocalEditText.setText(brickMap.get(BrickDefine.BRICK_VALUE));
		*/
		super.show();
		localEditText.requestFocus();
		this.getWindow().setSoftInputMode(
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
		if (intValueReference != null)
			intValueReference.setValue(Integer.parseInt(localEditText.getText().toString()));
		else if (doubleValueReference != null)
			doubleValueReference.setValue(Double.parseDouble(localEditText.getText().toString()));
		else if (longValueReference != null)
			longValueReference.setValue(Long.parseLong(localEditText.getText().toString()));

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
