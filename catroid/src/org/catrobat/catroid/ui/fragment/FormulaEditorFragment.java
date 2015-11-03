/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.commands.OnFormulaChangedListener;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaKeyboardAdapter;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.FormulaEditorComputeDialog;
import org.catrobat.catroid.ui.dialogs.NewStringDialog;
import org.catrobat.catroid.utils.ToastUtil;

public class FormulaEditorFragment extends SherlockFragment implements OnKeyListener,
		ViewTreeObserver.OnGlobalLayoutListener {
	private static final String TAG = FormulaEditorFragment.class.getSimpleName();

	private static final int PARSER_OK = -1;
	private static final int PARSER_STACK_OVERFLOW = -2;
	private static final int PARSER_INPUT_SYNTAX_ERROR = -3;

	private static final int SET_FORMULA_ON_CREATE_VIEW = 0;
	private static final int SET_FORMULA_ON_SWITCH_EDIT_TEXT = 1;
	private static final int TIME_WINDOW = 2000;

	public static final String FORMULA_EDITOR_FRAGMENT_TAG = FormulaEditorFragment.class.getSimpleName();
	public static final String FORMULA_BRICK_BUNDLE_ARGUMENT = "formula_brick";
	public static final String BRICKFIELD_BUNDLE_ARGUMENT = "brick_field";

	private Context context;
	private FormulaEditorEditText formulaEditorEditText;
	private LinearLayout formulaEditorKeyboard;
	private ImageButton formulaEditorFieldDeleteButton;
	private LinearLayout formulaEditorBrick;
	private View brickView;
	private View fragmentView;

	private FormulaBrick formulaBrick;
	private FormulaBrick clonedFormulaBrick;
	private Brick.BrickField currentBrickField;
	private Formula currentFormula;
	private Menu currentMenu;

	private long[] confirmSwitchEditTextTimeStamp = { 0, 0 };
	private int confirmSwitchEditTextCounter = 0;
	private CharSequence previousActionBarTitle;
	private VariableOrUserListDeletedReceiver variableOrUserListDeletedReceiver;
	private OnFormulaChangedListener onFormulaChangedListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setUpActionBar();
		onFormulaChangedListener = (OnFormulaChangedListener) ((ScriptActivity) getActivity())
				.getFragment(ScriptActivity.FRAGMENT_SCRIPTS);

		formulaBrick = (FormulaBrick) getArguments().getSerializable(FORMULA_BRICK_BUNDLE_ARGUMENT);
		currentBrickField = Brick.BrickField.valueOf(getArguments().getString(BRICKFIELD_BUNDLE_ARGUMENT));
		cloneFormulaBrick(formulaBrick);
		currentFormula = clonedFormulaBrick.getFormulaWithBrickField(currentBrickField);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	private void setUpActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		previousActionBarTitle = ProjectManager.getInstance().getCurrentSprite().getName();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.formula_editor_title);
	}

	private void resetActionBar() {
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(previousActionBarTitle);
	}

	private void cloneFormulaBrick(FormulaBrick formulaBrick) {
		try {
			clonedFormulaBrick = (FormulaBrick) formulaBrick.clone();
		} catch (CloneNotSupportedException exception) {
			Log.e(TAG, "Clone not supported", exception);
			onUserDismiss();
		}
	}

	private static void showFragment(View view, FormulaBrick formulaBrick, Brick.BrickField brickField, boolean showCustomView) {

		SherlockFragmentActivity activity = (SherlockFragmentActivity) view.getContext();

		FormulaEditorFragment formulaEditorFragment = (FormulaEditorFragment) activity.getSupportFragmentManager()
				.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG);

		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();

		if (formulaEditorFragment == null) {
			formulaEditorFragment = new FormulaEditorFragment();
			formulaEditorFragment.showCustomView = showCustomView;
			Bundle bundle = new Bundle();
			bundle.putSerializable(FORMULA_BRICK_BUNDLE_ARGUMENT, formulaBrick);
			bundle.putString(BRICKFIELD_BUNDLE_ARGUMENT, brickField.name());
			formulaEditorFragment.setArguments(bundle);

			fragTransaction.add(R.id.script_fragment_container, formulaEditorFragment, FORMULA_EDITOR_FRAGMENT_TAG);
			fragTransaction.hide(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
			fragTransaction.show(formulaEditorFragment);
			BottomBar.hideBottomBar(activity);
		} else if (formulaEditorFragment.isHidden()) {
			formulaEditorFragment.showCustomView = showCustomView;
			formulaEditorFragment.updateBrickViewAndFormula(formulaBrick, brickField);
			fragTransaction.hide(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
			fragTransaction.show(formulaEditorFragment);
			BottomBar.hideBottomBar(activity);
		} else {
			formulaEditorFragment.showCustomView = false;
			formulaEditorFragment.updateBrickView();
			formulaEditorFragment.setInputFormula(brickField, SET_FORMULA_ON_SWITCH_EDIT_TEXT);
		}
		fragTransaction.commit();
	}

	private boolean showCustomView = false;

	public static void showFragment(View view, FormulaBrick formulaBrick, Brick.BrickField brickField) {
		showFragment(view, formulaBrick, brickField, false);
	}

	public static void showCustomFragment(View view, FormulaBrick formulaBrick, Brick.BrickField brickField) {
		showFragment(view, formulaBrick, brickField, true);
	}

	public static void overwriteFormula(View view, Formula newFormula) {

		SherlockFragmentActivity activity = (SherlockFragmentActivity) view.getContext();

		FormulaEditorFragment formulaEditorFragment = (FormulaEditorFragment) activity.getSupportFragmentManager()
				.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG);

		if (formulaEditorFragment == null) {
			return;
		}

		formulaEditorFragment.formulaEditorEditText.overwriteCurrentFormula(newFormula.getInternFormulaState());
	}

	public static void changeInputField(View view, Brick.BrickField brickField) {

		SherlockFragmentActivity activity = (SherlockFragmentActivity) view.getContext();

		FormulaEditorFragment formulaEditorFragment = (FormulaEditorFragment) activity.getSupportFragmentManager()
				.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG);

		if (formulaEditorFragment == null) {
			return;
		}
		formulaEditorFragment.setInputFormula(brickField, SET_FORMULA_ON_SWITCH_EDIT_TEXT);
	}

	public void updateBrickView() {
		formulaEditorBrick.removeAllViews();

		View newBrickView = getBrickOrCustomView();

		formulaEditorBrick.addView(newBrickView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

		brickView = newBrickView;

		fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(this);

		handleCustomView();
	}

	private void updateBrickViewAndFormula(FormulaBrick newBrick, Brick.BrickField brickField) {
		formulaBrick = newBrick;
		cloneFormulaBrick(newBrick);
		updateBrickView();
		currentBrickField = brickField;
		currentFormula = clonedFormulaBrick.getFormulaWithBrickField(brickField);
		setInputFormula(currentBrickField, SET_FORMULA_ON_CREATE_VIEW);
		fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
		updateButtonsOnKeyboardAndInvalidateOptionsMenu();
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

		resetActionBar();

		BottomBar.showBottomBar(activity);
		BottomBar.showPlayButton(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		fragmentView = inflater.inflate(R.layout.fragment_formula_editor, container, false);
		fragmentView.setFocusableInTouchMode(true);
		fragmentView.requestFocus();

		formulaEditorFieldDeleteButton = (ImageButton) fragmentView.findViewById(R.id.formula_editor_edit_field_clear);

		context = getActivity();

		brickView = getBrickOrCustomView();
		formulaEditorBrick = (LinearLayout) fragmentView.findViewById(R.id.formula_editor_brick_space);
		formulaEditorBrick.addView(brickView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

		formulaEditorEditText = (FormulaEditorEditText) fragmentView.findViewById(R.id.formula_editor_edit_field);

		formulaEditorKeyboard = (LinearLayout) fragmentView.findViewById(R.id.formula_editor_keyboardview);
		formulaEditorEditText.init(this);

		fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(this);

		setInputFormula(currentBrickField, SET_FORMULA_ON_CREATE_VIEW);

		setHasOptionsMenu(true);
		setUpActionBar();

		handleCustomView();

		return fragmentView;
	}

	private void handleCustomView() {
		if (showCustomView) {
			formulaEditorFieldDeleteButton.setVisibility(View.GONE);
			formulaEditorEditText.setVisibility(View.GONE);
			formulaEditorKeyboard.setVisibility(View.GONE);
		} else {
			formulaEditorFieldDeleteButton.setVisibility(View.VISIBLE);
			formulaEditorEditText.setVisibility(View.VISIBLE);
			formulaEditorKeyboard.setVisibility(View.VISIBLE);
		}
	}

	private View getBrickOrCustomView() {
		if (showCustomView) {
			return clonedFormulaBrick.getCustomView(context, 0, null);
		} else {
			return clonedFormulaBrick.getView(context, 0, null);
		}
	}

	@Override
	public void onStart() {
		formulaEditorKeyboard.setClickable(true);
		formulaEditorFieldDeleteButton.setClickable(true);

		getView().requestFocus();
		View.OnTouchListener touchListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					updateButtonsOnKeyboardAndInvalidateOptionsMenu();
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
						case R.id.formula_editor_keyboard_function:
							showFormulaEditorListFragment(FormulaEditorListFragment.FUNCTION_TAG,
									R.string.formula_editor_math);
							return true;
						case R.id.formula_editor_keyboard_logic:
							showFormulaEditorListFragment(FormulaEditorListFragment.LOGIC_TAG,
									R.string.formula_editor_logic);
							return true;
						case R.id.formula_editor_keyboard_object:
							showFormulaEditorListFragment(FormulaEditorListFragment.OBJECT_TAG,
									R.string.formula_editor_choose_object_variable);
							return true;
						case R.id.formula_editor_keyboard_sensors:
							showFormulaEditorListFragment(FormulaEditorListFragment.SENSOR_TAG,
									R.string.formula_editor_sensors);
							return true;
						case R.id.formula_editor_keyboard_data:
							showFormulaEditorDataFragment(FormulaEditorDataFragment.USER_DATA_TAG,
									R.string.formula_editor_data);
							return true;
						case R.id.formula_editor_keyboard_ok:
							endFormulaEditor();
							return true;
						case R.id.formula_editor_keyboard_string:
							FragmentManager fragmentManager = ((SherlockFragmentActivity) context)
									.getSupportFragmentManager();
							Fragment dialogFragment = fragmentManager
									.findFragmentByTag(NewStringDialog.DIALOG_FRAGMENT_TAG);

							if (dialogFragment == null) {
								dialogFragment = NewStringDialog.newInstance();
							}

							((NewStringDialog) dialogFragment).show(fragmentManager,
									NewStringDialog.DIALOG_FRAGMENT_TAG);
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
		formulaEditorFieldDeleteButton.setOnTouchListener(touchListener);

		updateButtonsOnKeyboardAndInvalidateOptionsMenu();

		super.onStart();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		currentMenu = menu;

		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}

		MenuItem undo = menu.findItem(R.id.menu_undo);
		if (formulaEditorEditText == null || !formulaEditorEditText.getHistory().undoIsPossible()) {
			undo.setIcon(R.drawable.icon_undo_disabled);
			undo.setEnabled(false);
		} else {
			undo.setIcon(R.drawable.icon_undo);
			undo.setEnabled(true);
		}

		MenuItem redo = menu.findItem(R.id.menu_redo);
		if (formulaEditorEditText == null || !formulaEditorEditText.getHistory().redoIsPossible()) {
			redo.setIcon(R.drawable.icon_redo_disabled);
			redo.setEnabled(false);
		} else {
			redo.setIcon(R.drawable.icon_redo);
			redo.setEnabled(true);
		}

		menu.findItem(R.id.menu_undo).setVisible(true);
		menu.findItem(R.id.menu_redo).setVisible(true);
		getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.formula_editor_title));

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_formulaeditor, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	private void setInputFormula(Brick.BrickField brickField, int mode) {

		switch (mode) {
			case SET_FORMULA_ON_CREATE_VIEW:
				formulaEditorEditText.enterNewFormula(currentFormula.getInternFormulaState());
				currentFormula.highlightTextField(brickView);
				refreshFormulaPreviewString();
				break;

			case SET_FORMULA_ON_SWITCH_EDIT_TEXT:
				Formula newFormula = clonedFormulaBrick.getFormulaWithBrickField(brickField);
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
				MenuItem undo = currentMenu.findItem(R.id.menu_undo);
				undo.setIcon(R.drawable.icon_undo_disabled);
				undo.setEnabled(false);

				MenuItem redo = currentMenu.findItem(R.id.menu_redo);
				redo.setIcon(R.drawable.icon_redo_disabled);
				redo.setEnabled(false);

				formulaEditorEditText.endEdit();
				currentBrickField = brickField;
				currentFormula = newFormula;
				formulaEditorEditText.enterNewFormula(newFormula.getInternFormulaState());
				refreshFormulaPreviewString();
				break;
			default:
				break;
		}
	}

	public boolean saveFormulaIfPossible() {
		InternFormulaParser formulaToParse = formulaEditorEditText.getFormulaParser();
		FormulaElement formulaParseTree = formulaToParse.parseFormula();

		switch (formulaToParse.getErrorTokenIndex()) {
			case PARSER_OK:
				currentFormula.setRoot(formulaParseTree);
				if (onFormulaChangedListener != null) {
					onFormulaChangedListener.onFormulaChanged(formulaBrick, currentBrickField, currentFormula);
				}
				if (formulaEditorBrick != null) {
					currentFormula.refreshTextField(brickView);
				}
				formulaEditorEditText.formulaSaved();
				showToast(R.string.formula_editor_changes_saved, false);
				return true;
			case PARSER_STACK_OVERFLOW:
				return checkReturnWithoutSaving(PARSER_STACK_OVERFLOW);
			default:
				formulaEditorEditText.setParseErrorCursorAndSelection();
				return checkReturnWithoutSaving(PARSER_INPUT_SYNTAX_ERROR);
		}
	}

	private boolean checkReturnWithoutSaving(int errorType) {
		if ((System.currentTimeMillis() <= confirmSwitchEditTextTimeStamp[0] + TIME_WINDOW)
				&& (confirmSwitchEditTextCounter > 1)) {
			confirmSwitchEditTextTimeStamp[0] = 0;
			confirmSwitchEditTextTimeStamp[1] = 0;
			confirmSwitchEditTextCounter = 0;
			currentFormula.setDisplayText(null);
			showToast(R.string.formula_editor_changes_discarded, false);
			return true;
		} else {
			switch (errorType) {
				case PARSER_INPUT_SYNTAX_ERROR:
					showToast(R.string.formula_editor_parse_fail, true);
					break;
				case PARSER_STACK_OVERFLOW:
					showToast(R.string.formula_editor_parse_fail_formula_too_long, true);
					break;
			}
			return false;
		}
	}

	/*
	 * TODO Remove Toasts from this class and replace them with something useful
	 * This is a hack more than anything else. We shouldn't use Toasts if we're going to change the message all the time
	 */
	private void showToast(int resourceId, boolean error) {
		if (error) {
			ToastUtil.showError(getActivity().getApplicationContext(), resourceId);
		} else {
			ToastUtil.showSuccess(getActivity().getApplicationContext(), resourceId);
		}
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (formulaEditorEditText.hasChanges()) {
					AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
					builder.setTitle(R.string.formula_editor_discard_changes_dialog_title)
							.setMessage(R.string.formula_editor_discard_changes_dialog_message)
							.setNegativeButton(R.string.no, new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

									showToast(R.string.formula_editor_changes_discarded, false);
									currentFormula.setDisplayText(null);
									onUserDismiss();
								}
							})
							.setPositiveButton(R.string.yes, new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (saveFormulaIfPossible()) {
										onUserDismiss();
									}
								}
							}).create().show();
				} else {
					onUserDismiss();
				}
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
		refreshFormulaPreviewString(formulaEditorEditText.getStringFromInternFormula());
	}

	public void refreshFormulaPreviewString(String newString) {
		currentFormula.setDisplayText(newString);
		updateBrickView();
		currentFormula.refreshTextField(brickView, newString);
		if (!showCustomView) {
			currentFormula.highlightTextField(brickView);
		}
	}

	private void showFormulaEditorListFragment(String tag, int actionbarResId) {
		FragmentManager fragmentManager = ((SherlockFragmentActivity) context).getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(tag);

		if (fragment == null) {
			fragment = new FormulaEditorListFragment();
			Bundle bundle = new Bundle();
			bundle.putString(FormulaEditorListFragment.ACTION_BAR_TITLE_BUNDLE_ARGUMENT,
					context.getString(actionbarResId));
			bundle.putString(FormulaEditorListFragment.FRAGMENT_TAG_BUNDLE_ARGUMENT, tag);
			fragment.setArguments(bundle);
			fragmentManager.beginTransaction().add(R.id.script_fragment_container, fragment, tag).commit();
		}
		((FormulaEditorListFragment) fragment).showFragment(context);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_undo:
				formulaEditorEditText.undo();
				break;
			case R.id.menu_redo:
				formulaEditorEditText.redo();
				break;
		}
		updateButtonsOnKeyboardAndInvalidateOptionsMenu();
		return super.onOptionsItemSelected(item);
	}

	private void showFormulaEditorDataFragment(String tag, int actionbarResId) {
		FragmentManager fragmentManager = ((SherlockFragmentActivity) context).getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(tag);

		if (fragment == null) {
			if (getActivity().getClass().equals(ScriptActivity.class)) {
				fragment = new FormulaEditorDataFragment(false);
			} else {
				fragment = new FormulaEditorDataFragment(true);
			}
			Bundle bundle = new Bundle();
			bundle.putString(FormulaEditorDataFragment.ACTION_BAR_TITLE_BUNDLE_ARGUMENT,
					context.getString(actionbarResId));
			bundle.putString(FormulaEditorDataFragment.FRAGMENT_TAG_BUNDLE_ARGUMENT, tag);
			fragment.setArguments(bundle);
			fragmentManager.beginTransaction().add(R.id.script_fragment_container, fragment, tag).commit();
		}
		((FormulaEditorDataFragment) fragment).setAddButtonListener(getSherlockActivity());
		((FormulaEditorDataFragment) fragment).showFragment(context);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		fragmentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		Rect brickRect = new Rect();
		Rect keyboardRec = new Rect();
		formulaEditorBrick.getGlobalVisibleRect(brickRect);
		formulaEditorKeyboard.getGlobalVisibleRect(keyboardRec);
		formulaEditorEditText.setMaxHeight(keyboardRec.top - brickRect.bottom);
	}

	public void addResourceToActiveFormula(int resource) {
		formulaEditorEditText.handleKeyEvent(resource, "");
	}

	public void addUserListToActiveFormula(String userListName) {
		formulaEditorEditText.handleKeyEvent(InternFormulaKeyboardAdapter.FORMULA_EDITOR_USER_LIST_RESOURCE_ID,
				userListName);
	}

	public void addUserVariableToActiveFormula(String userVariableName) {
		formulaEditorEditText.handleKeyEvent(InternFormulaKeyboardAdapter.FORMULA_EDITOR_USER_VARIABLE_RESOURCE_ID,
				userVariableName);
	}

	public void addStringToActiveFormula(String string) {
		formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_string, string);
	}

	private class VariableOrUserListDeletedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(ScriptActivity.ACTION_VARIABLE_DELETED)
					|| intent.getAction().equals(ScriptActivity.ACTION_USERLIST_DELETED)) {
				updateBrickView();
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (variableOrUserListDeletedReceiver != null) {
			getActivity().unregisterReceiver(variableOrUserListDeletedReceiver);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (variableOrUserListDeletedReceiver == null) {
			variableOrUserListDeletedReceiver = new VariableOrUserListDeletedReceiver();
		}

		IntentFilter filterVariableDeleted = new IntentFilter(ScriptActivity.ACTION_VARIABLE_DELETED);
		BottomBar.hideBottomBar(getSherlockActivity());
		filterVariableDeleted.addAction(ScriptActivity.ACTION_USERLIST_DELETED);
		getActivity().registerReceiver(variableOrUserListDeletedReceiver, filterVariableDeleted);
	}

	public void updateButtonsOnKeyboardAndInvalidateOptionsMenu() {
		getSherlockActivity().invalidateOptionsMenu();

		ImageButton backspaceEditText = (ImageButton) getSherlockActivity().findViewById(R.id.formula_editor_edit_field_clear);
		ImageButton backspaceOnKeyboard = (ImageButton) getSherlockActivity().findViewById(R.id.formula_editor_keyboard_delete);
		if (!formulaEditorEditText.isThereSomethingToDelete()) {
			backspaceEditText.setImageResource(R.drawable.icon_backspace_disabled);
			backspaceEditText.setEnabled(false);
			backspaceOnKeyboard.setAlpha(255 / 3);
			backspaceOnKeyboard.setEnabled(false);
		} else {
			backspaceEditText.setImageResource(R.drawable.icon_backspace);
			backspaceEditText.setEnabled(true);
			backspaceOnKeyboard.setAlpha(255);
			backspaceOnKeyboard.setEnabled(true);
		}
	}
}
