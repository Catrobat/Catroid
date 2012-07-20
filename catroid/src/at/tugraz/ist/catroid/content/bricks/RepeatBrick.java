/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.ui.dialogs.FormulaEditorDialog;

public class RepeatBrick extends LoopBeginBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;
	private int timesToRepeat;

	private Formula timesToRepeatFormula;

	private transient Brick instance = null;
	private transient FormulaEditorDialog formulaEditor;
	public transient boolean editorActive = false;

	public RepeatBrick(Sprite sprite, int timesToRepeat) {
		this.sprite = sprite;
		this.timesToRepeat = timesToRepeat;

		timesToRepeatFormula = new Formula(Integer.toString(timesToRepeat), R.id.brick_repeat_edit_text);
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		timesToRepeat = timesToRepeatFormula.interpret().intValue();

		if (timesToRepeat <= 0) {
			Script script = loopEndBrick.getScript();
			script.setExecutingBrickIndex(script.getBrickList().indexOf(loopEndBrick));
			return;
		}
		loopEndBrick.setTimesToRepeat(timesToRepeat);
		super.setFirstStartTime();
	}

	@Override
	public Brick clone() {
		return new RepeatBrick(getSprite(), timesToRepeat);
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {

		if (instance == null) {
			instance = this;
		}

		if (timesToRepeatFormula == null) {
			timesToRepeatFormula = new Formula(Double.toString(timesToRepeat), R.id.brick_repeat_edit_text);
		}

		View view = View.inflate(context, R.layout.brick_repeat, null);

		TextView text = (TextView) view.findViewById(R.id.brick_repeat_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_repeat_edit_text);
		//		edit.setText(timesToRepeat + "");
		//		edit.setText(timesToRepeatFormula.getEditTextRepresentation());
		timesToRepeatFormula.refreshTextField(view);

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);

		edit.setOnClickListener(this);
		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_repeat, null);
	}

	public void onClick(View view) {
		final Context context = view.getContext();

		if (!editorActive) {
			editorActive = true;
			formulaEditor = new FormulaEditorDialog(context, instance);
			formulaEditor.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface editor) {

					//size = formulaEditor.getReturnValue();
					formulaEditor.dismiss();

					editorActive = false;
				}
			});
			formulaEditor.show();
		}

		formulaEditor.setInputFocusAndFormula(timesToRepeatFormula);

		//		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		//		final EditText input = new EditText(context);
		//		input.setText(String.valueOf(timesToRepeat));
		//		input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
		//				| InputType.TYPE_NUMBER_FLAG_SIGNED);
		//		input.setSelectAllOnFocus(true);
		//		dialog.setView(input);
		//		dialog.setOnCancelListener((OnCancelListener) context);
		//		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
		//			public void onClick(DialogInterface dialog, int which) {
		//				try {
		//					timesToRepeat = Integer.parseInt(input.getText().toString());
		//				} catch (NumberFormatException exception) {
		//					Toast.makeText(context, R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
		//				}
		//				dialog.cancel();
		//			}
		//		});
		//		dialog.setNeutralButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
		//			public void onClick(DialogInterface dialog, int which) {
		//				dialog.cancel();
		//			}
		//		});
		//
		//		AlertDialog finishedDialog = dialog.create();
		//		finishedDialog.setOnShowListener(Utils.getBrickDialogOnClickListener(context, input));
		//
		//		finishedDialog.show();

	}

}
