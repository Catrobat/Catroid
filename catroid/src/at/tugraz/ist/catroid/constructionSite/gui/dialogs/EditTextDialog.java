package at.tugraz.ist.catroid.constructionSite.gui.dialogs;

import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;

public class EditTextDialog extends Dialog implements OnClickListener {

	private EditText mListEditText;
	private EditText mLocalEditText;
	private Button closeButton;
	private Integer intValueReference;
	private Double doubleValueReference;
	private int inputType;
	

	private void init(EditText brickEditText) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_edit_text);
		mLocalEditText = (EditText) findViewById(R.id.dialogEditText);
		this.mListEditText = brickEditText;
		if(intValueReference != null)
			mLocalEditText.setText(intValueReference + "");
		else
			mLocalEditText.setText(doubleValueReference + "");
		closeButton = (Button) findViewById(R.id.dialogEditTextSubmit);
		closeButton.setOnClickListener(this);
	}
	
	public EditTextDialog(Context context, EditText brickEditText, Integer valueReference) {
		super(context);
		this.intValueReference = valueReference;
		this.doubleValueReference = null;

		init(brickEditText);
	}
	
	public EditTextDialog(Context context, EditText brickEditText, Double valueReference) {
		super(context);
		this.doubleValueReference = valueReference;
		this.intValueReference = null;
		
		init(brickEditText);
	}

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
		mLocalEditText.requestFocus();
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

	}

	@Override
	public void onBackPressed() {
		saveContent();
		super.onBackPressed();
	}

	@Override
	public void cancel() {
		saveContent();
		super.cancel();
	}

	private void saveContent() {
		if(intValueReference != null)
			intValueReference = Integer.parseInt(mLocalEditText.getText().toString());
		else
			doubleValueReference = Double.parseDouble(mLocalEditText.getText().toString());
		
		mListEditText.setText(mLocalEditText.getText().toString());
	}

	public void onClick(View v) {
		show();
		if (v.getId() == R.id.dialogEditTextSubmit) {
			Log.i("EditTextDialog", "in onClickListener");
			cancel();
		}

	}

}
