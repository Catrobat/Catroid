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
package org.catrobat.catroid.ui.fragment;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.ui.dialogs.FormulaEditorComputeDialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class FormulaEditorFragment extends SherlockFragment implements OnKeyListener {

	private static final int PARSER_OK = -1;
	private static final int PARSER_STACK_OVERFLOW = -2;
	private static final int PARSER_INPUT_SYNTAX_ERROR = -3;

	private static final int SET_FORMULA_ON_CREATE_VIEW = 0;
	private static final int SET_FORMULA_ON_SWITCH_EDIT_TEXT = 1;
	private static final int TIME_WINDOW = 2000;

	public static final String FORMULA_EDITOR_FRAGMENT_TAG = "formula_editor_fragment";
	private static final int MAX_BUTTON_LINES = 1;

	private Context context;

	private Brick currentBrick;
	private Formula currentFormula;
	private FormulaEditorEditText formulaEditorEditText;
	private LinearLayout formulaEditorKeyboard;
	private LinearLayout formulaEditorBrick;
	private View brickView;
	private long[] confirmBackTimeStamp = { 0, 0 };
	private long[] confirmSwitchEditTextTimeStamp = { 0, 0 };
	private int confirmBackCounter = 0;
	private int confirmSwitchEditTextCounter = 0;

	public boolean restoreInstance = false;

	//	public FormulaEditorFragment() {
	//
	//	}

	public FormulaEditorFragment(Brick brick, Formula formula) {
		currentBrick = brick;
		currentFormula = formula;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			restoreInstance = savedInstanceState.getBoolean("restoreInstance");
		}
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.formula_editor_title));
	}

	@Override
	public void onSaveInstanceState(Bundle saveInstanceState) {
		saveInstanceState.putBoolean("restoreInstance", true);
		super.onSaveInstanceState(saveInstanceState);
	}

	public static void showFragment(View view, Brick brick, Formula formula) {

		SherlockFragmentActivity activity = null;
		activity = (SherlockFragmentActivity) view.getContext();

		FormulaEditorFragment formulaEditorDialog = (FormulaEditorFragment) activity.getSupportFragmentManager()
				.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG);

		if (formulaEditorDialog == null) {
			formulaEditorDialog = new FormulaEditorFragment(brick, formula);
			FragmentManager fragmentManager = activity.getSupportFragmentManager();
			FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
			fragTransaction.replace(R.id.script_fragment_container, formulaEditorDialog, FORMULA_EDITOR_FRAGMENT_TAG);

			fragTransaction.commit();
			activity.getSupportActionBar().setTitle(ProjectManager.INSTANCE.getCurrentSprite().getName());
			activity.findViewById(R.id.bottom_bar).setVisibility(View.GONE);

		} else {
			formulaEditorDialog.setInputFormula(formula, SET_FORMULA_ON_SWITCH_EDIT_TEXT);
		}

	}

	private void onUserDismiss() {
		formulaEditorEditText.endEdit();
		currentFormula.prepareToRemove();

		SherlockFragmentActivity activity = getSherlockActivity();
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
		ScriptFragment scriptFragment = (ScriptFragment) fragmentManager
				.findFragmentById(R.id.fragment_script_relative_layout);
		fragTransaction.replace(R.id.script_fragment_container, scriptFragment);
		fragTransaction.commit();
		activity.getSupportActionBar().setTitle(ProjectManager.getInstance().getCurrentSprite().getName());

		getSherlockActivity().findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View fragmentView = inflater.inflate(R.layout.fragment_formula_editor, container, false);
		fragmentView.setFocusableInTouchMode(true);
		fragmentView.requestFocus();

		context = getActivity();
		formulaEditorBrick = (LinearLayout) fragmentView.findViewById(R.id.formula_editor_brick_space);
		if (formulaEditorBrick != null) {
			brickView = currentBrick.getView(context, 0, null);
			formulaEditorBrick.addView(brickView);
		}

		formulaEditorEditText = (FormulaEditorEditText) fragmentView.findViewById(R.id.formula_editor_edit_field);
		if (formulaEditorBrick != null) {
			formulaEditorBrick.measure(0, 0);
		}

		formulaEditorKeyboard = (LinearLayout) fragmentView.findViewById(R.id.formula_editor_keyboardview);

		if (formulaEditorBrick != null) {
			formulaEditorEditText.init(this, formulaEditorBrick.getMeasuredHeight(), formulaEditorKeyboard);
		} else {
			formulaEditorEditText.init(this, 0, formulaEditorKeyboard);
		}
		setInputFormula(currentFormula, SET_FORMULA_ON_CREATE_VIEW);

		return fragmentView;
	}

	@Override
	public void onStart() {
		formulaEditorKeyboard.setClickable(true);
		getView().requestFocus();
		View.OnTouchListener touchListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				Log.i("info", "viewId: " + view.getId());
				if (event.getAction() == MotionEvent.ACTION_UP) {
					view.setBackgroundResource(R.drawable.brick_blue);
					view.setPressed(false);
					return true;
				}

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					view.setBackgroundResource(R.drawable.ic_paintroid_logo);
					view.setPressed(true);

					switch (view.getId()) {
						case R.id.formula_editor_keyboard_compute:
							InternFormulaParser internFormulaParser = formulaEditorEditText.getFormulaParser();
							FormulaElement formulaElement = internFormulaParser.parseFormula();
							if (formulaElement == null) {
								if (internFormulaParser.getErrorTokenIndex() >= 0) {
									formulaEditorEditText.setParseErrorCursorAndSelection();
								}
								return false;
							}
							Formula formulaToCompute = new Formula(formulaElement);
							FormulaEditorComputeDialog computeDialog = new FormulaEditorComputeDialog(context);
							computeDialog.setFormula(formulaToCompute);
							computeDialog.show();
							return true;
						case R.id.formula_editor_keyboard_undo:
							formulaEditorEditText.undo();
							return true;
						case R.id.formula_editor_keyboard_redo:
							formulaEditorEditText.redo();
							return true;
						case R.id.formula_editor_keyboard_math:
							showFormulaEditorListFragment(FormulaEditorListFragment.MATH_TAG,
									R.string.formula_editor_math);
							return true;
						case R.id.formula_editor_keyboard_logic:
							showFormulaEditorListFragment(FormulaEditorListFragment.LOGIC_TAG,
									R.string.formula_editor_logic);
							return true;
						case R.id.formula_editor_keyboard_object:
							showFormulaEditorListFragment(FormulaEditorListFragment.OBJECT_TAG,
									R.string.formula_editor_choose_look_variable);
							return true;
						case R.id.formula_editor_keyboard_sensors:
							showFormulaEditorListFragment(FormulaEditorListFragment.SENSOR_TAG,
									R.string.formula_editor_sensors);
							return true;
						case R.id.formula_editor_keyboard_variables:
							showFormulaEditorVariableListFragment(FormulaEditorVariableListFragment.VARIABLE_TAG,
									R.string.formula_editor_variables);
							return true;
						default:
							formulaEditorEditText.handleKeyEvent(view.getId(), "");
							return true;
					}

				}
				return false;
			}
		};

		for (int index = 0; index < formulaEditorKeyboard.getChildCount(); index++) {
			LinearLayout child = (LinearLayout) formulaEditorKeyboard.getChildAt(index);
			for (int nestedIndex = 0; nestedIndex < child.getChildCount(); nestedIndex++) {
				Button key = (Button) child.getChildAt(nestedIndex);
				key.setOnTouchListener(touchListener);
				key.setBackgroundResource(R.drawable.brick_blue);
				key.setLines(MAX_BUTTON_LINES);
				key.setEllipsize(TruncateAt.END);
			}
		}
		super.onStart();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.formula_editor_title));

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case android.R.id.home:
				endFormulaEditor();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		Log.i("", "FEF.onContextItemSelected");
		return super.onContextItemSelected(item);
	}

	public void setInputFormula(Formula newFormula, int mode) {

		int orientation = getResources().getConfiguration().orientation;

		switch (mode) {
			case SET_FORMULA_ON_CREATE_VIEW:
				currentFormula.removeTextFieldHighlighting(brickView, orientation);
				formulaEditorEditText.enterNewFormula(currentFormula.getInternFormulaState());
				currentFormula.highlightTextField(brickView, orientation);
				refreshFormulaPreviewString();
				break;
			case SET_FORMULA_ON_SWITCH_EDIT_TEXT:

				if (currentFormula == newFormula && formulaEditorEditText.hasChanges()) {
					formulaEditorEditText.quickSelect();
					break;
				}
				if (formulaEditorEditText.hasChanges()) {
					confirmSwitchEditTextTimeStamp[0] = confirmSwitchEditTextTimeStamp[1];
					confirmSwitchEditTextTimeStamp[1] = System.currentTimeMillis();
					confirmSwitchEditTextCounter++;
					if (!saveFormulaIfPossible()) {
						return;
					}
				}
				if (currentFormula != null) {
					currentFormula.refreshTextField(brickView);
				}

				formulaEditorEditText.endEdit();
				currentFormula.removeTextFieldHighlighting(brickView, orientation);
				currentFormula = newFormula;
				currentFormula.highlightTextField(brickView, orientation);
				formulaEditorEditText.enterNewFormula(newFormula.getInternFormulaState());
				break;
			default:
				break;
		}
	}

	public boolean saveFormulaIfPossible() {
		InternFormulaParser formulaToParse = formulaEditorEditText.getFormulaParser();
		FormulaElement formulaParseTree = formulaToParse.parseFormula();
		int err = formulaToParse.getErrorTokenIndex();
		switch (err) {
			case PARSER_OK:
				currentFormula.setRoot(formulaParseTree);
				if (formulaEditorBrick != null) {
					currentFormula.refreshTextField(brickView);
				}
				formulaEditorEditText.formulaSaved();
				showToast(R.string.formula_editor_changes_saved);
				return true;
			case PARSER_STACK_OVERFLOW:
				return checkReturnWithoutSaving(PARSER_STACK_OVERFLOW);
			default:
				formulaEditorEditText.setParseErrorCursorAndSelection();
				return checkReturnWithoutSaving(PARSER_INPUT_SYNTAX_ERROR);
		}
	}

	private boolean checkReturnWithoutSaving(int errorType) {
		Log.i("info", "confirmBackCounter=" + confirmBackCounter + " "
				+ (System.currentTimeMillis() <= confirmBackTimeStamp[0] + TIME_WINDOW)
				+ " confirmSwitchEditTextCounter=" + confirmSwitchEditTextCounter + " "
				+ (System.currentTimeMillis() <= confirmSwitchEditTextTimeStamp[0] + TIME_WINDOW));

		if (((System.currentTimeMillis() <= confirmBackTimeStamp[0] + TIME_WINDOW) && (confirmBackCounter > 1))
				|| ((System.currentTimeMillis() <= confirmSwitchEditTextTimeStamp[0] + TIME_WINDOW) && (confirmSwitchEditTextCounter > 1))) {
			confirmSwitchEditTextTimeStamp[0] = 0;
			confirmSwitchEditTextTimeStamp[1] = 0;
			confirmSwitchEditTextCounter = 0;
			confirmBackTimeStamp[0] = 0;
			confirmBackTimeStamp[1] = 0;
			confirmBackCounter = 0;
			showToast(R.string.formula_editor_changes_discarded);
			return true;
		} else {
			switch (errorType) {
				case PARSER_INPUT_SYNTAX_ERROR:
					showToast(R.string.formula_editor_parse_fail);
					break;
				case PARSER_STACK_OVERFLOW:
					showToast(R.string.formula_editor_parse_fail_formula_too_long);
					break;
			}
			return false;
		}

	}

	public void showToast(int ressourceId) {
		Toast.makeText(context, getString(ressourceId), Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		Log.i("info", "onKey() in FE-Fragment! keyCode: " + keyCode);
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				confirmBackTimeStamp[0] = confirmBackTimeStamp[1];
				confirmBackTimeStamp[1] = System.currentTimeMillis();
				confirmBackCounter++;
				endFormulaEditor();
				return true;
		}

		return false;
	}

	public void endFormulaEditor() {

		if (formulaEditorEditText.hasChanges()) {
			if (saveFormulaIfPossible()) {
				onUserDismiss();
			}
		} else {
			onUserDismiss();
		}

	}

	public void refreshFormulaPreviewString() {
		currentFormula.refreshTextField(brickView, formulaEditorEditText.getText().toString(),
				formulaEditorEditText.getAbsoluteCursorPosition());

	}

	private void showFormulaEditorListFragment(String tag, int actionbarResId) {
		FragmentManager fragmentManager = ((SherlockFragmentActivity) context).getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(tag);

		if (fragment == null) {
			fragment = new FormulaEditorListFragment(formulaEditorEditText, context.getString(actionbarResId), tag);
		}
		((FormulaEditorListFragment) fragment).showFragment(context);
	}

	private void showFormulaEditorVariableListFragment(String tag, int actionbarResId) {
		FragmentManager fragmentManager = ((SherlockFragmentActivity) context).getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(tag);

		if (fragment == null) {
			fragment = new FormulaEditorVariableListFragment(formulaEditorEditText, context.getString(actionbarResId),
					tag);
		}
		((FormulaEditorVariableListFragment) fragment).showFragment(context);
	}

}
