/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.FormulaEditorComputeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class FormulaEditorFragment extends SherlockFragment implements OnKeyListener,
		ViewTreeObserver.OnGlobalLayoutListener {

	private static final int PARSER_OK = -1;
	private static final int PARSER_STACK_OVERFLOW = -2;
	private static final int PARSER_INPUT_SYNTAX_ERROR = -3;

	private static final int SET_FORMULA_ON_CREATE_VIEW = 0;
	private static final int SET_FORMULA_ON_SWITCH_EDIT_TEXT = 1;
	private static final int TIME_WINDOW = 2000;

	public static final String FORMULA_EDITOR_FRAGMENT_TAG = "formula_editor_fragment";

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
	private View fragmentView;
	private VariableDeletedReceiver variableDeletedReceiver;

	public FormulaEditorFragment(Brick brick, Formula formula) {
		currentBrick = brick;
		currentFormula = formula;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.formula_editor_title));

	}

	public static void showFragment(View view, Brick brick, Formula formula) {

		SherlockFragmentActivity activity = null;
		activity = (SherlockFragmentActivity) view.getContext();

		FormulaEditorFragment formulaEditorFragment = (FormulaEditorFragment) activity.getSupportFragmentManager()
				.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG);

		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();

		if (formulaEditorFragment == null) {
			formulaEditorFragment = new FormulaEditorFragment(brick, formula);
			fragTransaction.add(R.id.script_fragment_container, formulaEditorFragment, FORMULA_EDITOR_FRAGMENT_TAG);
			fragTransaction.hide(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
			fragTransaction.show(formulaEditorFragment);
			activity.findViewById(R.id.bottom_bar).setVisibility(View.GONE);
		} else if (formulaEditorFragment.isHidden()) {
			formulaEditorFragment.updateBrickViewAndFormula(brick, formula);
			fragTransaction.hide(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
			fragTransaction.show(formulaEditorFragment);
			activity.findViewById(R.id.bottom_bar).setVisibility(View.GONE);
		} else {
			formulaEditorFragment.setInputFormula(formula, SET_FORMULA_ON_SWITCH_EDIT_TEXT);
		}
		fragTransaction.commit();
	}

	public void updateBrickView() {
		updateBrickView(currentBrick);
	}

	private void updateBrickView(Brick newBrick) {
		currentBrick = newBrick;
		formulaEditorBrick.removeAllViews();
		View newBrickView = newBrick.getView(context, 0, null);
		formulaEditorBrick.addView(newBrickView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		brickView = newBrickView;
		fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(this);

	}

	private void updateBrickViewAndFormula(Brick newBrick, Formula newFormula) {
		updateBrickView(newBrick);
		currentFormula = newFormula;
		setInputFormula(newFormula, SET_FORMULA_ON_CREATE_VIEW);
		fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
		updateButtonViewOnKeyboard();
	}

	private void onUserDismiss() {
		formulaEditorEditText.endEdit();
		currentFormula.prepareToRemove();

		SherlockFragmentActivity activity = getSherlockActivity();
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
		fragTransaction.hide(this);
		fragTransaction.show(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
		fragTransaction.commit();
		activity.getSupportActionBar().setTitle(ProjectManager.getInstance().getCurrentSprite().getName());

		getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSherlockActivity().getSupportActionBar().setNavigationMode(
				com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_LIST);
		getSherlockActivity().findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		fragmentView = inflater.inflate(R.layout.fragment_formula_editor, container, false);
		fragmentView.setFocusableInTouchMode(true);
		fragmentView.requestFocus();

		context = getActivity();
		brickView = currentBrick.getView(context, 0, null);

		formulaEditorBrick = (LinearLayout) fragmentView.findViewById(R.id.formula_editor_brick_space);

		formulaEditorBrick.addView(brickView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

		formulaEditorEditText = (FormulaEditorEditText) fragmentView.findViewById(R.id.formula_editor_edit_field);

		formulaEditorKeyboard = (LinearLayout) fragmentView.findViewById(R.id.formula_editor_keyboardview);
		formulaEditorEditText.init(this);

		fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(this);

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
					updateButtonViewOnKeyboard();
					view.setPressed(false);
					return true;
				}

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

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
				View view = child.getChildAt(nestedIndex);
				view.setOnTouchListener(touchListener);
			}
		}

		updateButtonViewOnKeyboard();

		super.onStart();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}

		getSherlockActivity().getSupportActionBar().setNavigationMode(
				com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_STANDARD);
		getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.formula_editor_title));

		super.onPrepareOptionsMenu(menu);
	}

	private void setInputFormula(Formula newFormula, int mode) {

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

	private boolean saveFormulaIfPossible() {
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

	private void showToast(int ressourceId) {
		Toast.makeText(context, getString(ressourceId), Toast.LENGTH_LONG).show();
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

	private void endFormulaEditor() {
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
			fragmentManager.beginTransaction().add(R.id.script_fragment_container, fragment, tag).commit();
		}
		((FormulaEditorListFragment) fragment).showFragment(context);
	}

	private void showFormulaEditorVariableListFragment(String tag, int actionbarResId) {
		FragmentManager fragmentManager = ((SherlockFragmentActivity) context).getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(tag);

		if (fragment == null) {
			fragment = new FormulaEditorVariableListFragment(formulaEditorEditText, context.getString(actionbarResId),
					tag);
			fragmentManager.beginTransaction().add(R.id.script_fragment_container, fragment, tag).commit();
		}
		((FormulaEditorVariableListFragment) fragment).showFragment(context);
	}

	@Override
	public void onGlobalLayout() {
		fragmentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

		Rect brickRect = new Rect();
		Rect keyboardRec = new Rect();
		formulaEditorBrick.getGlobalVisibleRect(brickRect);
		formulaEditorKeyboard.getGlobalVisibleRect(keyboardRec);

		Log.e("info", "heights: " + brickRect.bottom + " | " + keyboardRec.top);
		formulaEditorEditText.setMaxHeight(keyboardRec.top - brickRect.bottom);

	}

	private class VariableDeletedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_VARIABLE_DELETED)) {
				updateBrickView(currentBrick);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (variableDeletedReceiver != null) {
			getActivity().unregisterReceiver(variableDeletedReceiver);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (variableDeletedReceiver == null) {
			variableDeletedReceiver = new VariableDeletedReceiver();
		}

		IntentFilter filterVariableDeleted = new IntentFilter(ScriptActivity.ACTION_VARIABLE_DELETED);
		getActivity().registerReceiver(variableDeletedReceiver, filterVariableDeleted);
	}

	public void updateButtonViewOnKeyboard() {

		ImageButton undo = (ImageButton) getSherlockActivity().findViewById(R.id.formula_editor_keyboard_undo);
		if (!formulaEditorEditText.getHistory().undoIsPossible()) {
			undo.setImageResource(R.drawable.icon_undo_disabled);
			undo.setClickable(false);
		} else {
			undo.setImageResource(R.drawable.icon_undo);
			undo.setClickable(true);
		}

		ImageButton redo = (ImageButton) getSherlockActivity().findViewById(R.id.formula_editor_keyboard_redo);
		if (!formulaEditorEditText.getHistory().redoIsPossible()) {
			redo.setImageResource(R.drawable.icon_redo_disabled);
			redo.setClickable(false);
		} else {
			redo.setImageResource(R.drawable.icon_redo);
			redo.setClickable(true);
		}

		ImageButton backspace = (ImageButton) getSherlockActivity().findViewById(R.id.formula_editor_keyboard_delete);
		if (!formulaEditorEditText.isThereSomethingToDelete()) {
			backspace.setImageResource(R.drawable.icon_backspace_disabled);
			backspace.setClickable(false);
		} else {
			backspace.setImageResource(R.drawable.icon_backspace);
			backspace.setClickable(true);
		}

	}
}
