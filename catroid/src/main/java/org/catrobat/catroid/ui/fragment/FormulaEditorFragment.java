/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserBrickParameter;
import org.catrobat.catroid.content.commands.OnFormulaChangedListener;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaKeyboardAdapter;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.dialogs.FormulaEditorComputeDialog;
import org.catrobat.catroid.ui.dialogs.FormulaEditorIntroDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.NewStringDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;
import org.catrobat.catroid.ui.recyclerview.fragment.CategoryListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.DataListFragment;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;

import static org.catrobat.catroid.utils.SnackbarUtil.wasHintAlreadyShown;

public class FormulaEditorFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener,
		DataListFragment.FormulaEditorDataInterface,
		NewItemInterface<String> {

	private static final String TAG = FormulaEditorFragment.class.getSimpleName();

	private static final int SET_FORMULA_ON_CREATE_VIEW = 0;
	private static final int SET_FORMULA_ON_SWITCH_EDIT_TEXT = 1;
	private static final int TIME_WINDOW = 2000;
	public static final int REQUEST_GPS = 1;

	public static final String FORMULA_EDITOR_FRAGMENT_TAG = FormulaEditorFragment.class.getSimpleName();
	public static final String FORMULA_BRICK_BUNDLE_ARGUMENT = "formula_brick";
	public static final String BRICKFIELD_BUNDLE_ARGUMENT = "brick_field";

	private Context context;
	private static FormulaEditorEditText formulaEditorEditText;
	private TableLayout formulaEditorKeyboard;
	private static LinearLayout formulaEditorBrick;
	private static View brickView;
	private View fragmentView;

	private static FormulaBrick formulaBrick;
	private FormulaBrick clonedFormulaBrick;
	private static Brick.BrickField currentBrickField;
	private static Formula currentFormula;
	private Menu currentMenu;
	private FormulaElement formulaElementForComputeDialog;

	private long[] confirmSwitchEditTextTimeStamp = {0, 0};
	private int confirmSwitchEditTextCounter = 0;
	private static OnFormulaChangedListener onFormulaChangedListener;
	private boolean hasFormulaBeenChanged = false;

	private String previousActionBarTitle = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(!ViewConfiguration.get(getActivity()).hasPermanentMenuKey());

		ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		previousActionBarTitle = actionBar.getTitle().toString();
		actionBar.setTitle(R.string.formula_editor_title);

		onFormulaChangedListener = (OnFormulaChangedListener) getFragmentManager()
				.findFragmentByTag(ScriptFragment.TAG);

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

	private void cloneFormulaBrick(FormulaBrick formulaBrick) {
		try {
			clonedFormulaBrick = (FormulaBrick) formulaBrick.clone();
			clonedFormulaBrick.setCommentedOut(formulaBrick.isCommentedOut());
		} catch (CloneNotSupportedException exception) {
			Log.e(TAG, "Clone not supported", exception);
			onUserDismiss();
		}
	}

	private static void showFragment(View view, FormulaBrick formulaBrick, Brick.BrickField brickField, boolean showCustomView) {
		Activity activity = (Activity) view.getContext();

		FormulaEditorFragment formulaEditorFragment = (FormulaEditorFragment) activity.getFragmentManager()
				.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG);

		if (formulaEditorFragment == null) {
			formulaEditorFragment = new FormulaEditorFragment();
			formulaEditorFragment.showCustomView = showCustomView;
			Bundle bundle = new Bundle();
			bundle.putSerializable(FORMULA_BRICK_BUNDLE_ARGUMENT, formulaBrick);
			bundle.putString(BRICKFIELD_BUNDLE_ARGUMENT, brickField.name());
			formulaEditorFragment.setArguments(bundle);

			activity.getFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, formulaEditorFragment, FORMULA_EDITOR_FRAGMENT_TAG)
					.addToBackStack(FORMULA_EDITOR_FRAGMENT_TAG)
					.commit();

			BottomBar.hideBottomBar(activity);
		} else {
			refreshUserBrickParameterValuesIfNecessary(formulaBrick, brickField, formulaEditorFragment);
			formulaEditorFragment.showCustomView = false;
			formulaEditorFragment.updateBrickView();
			formulaEditorFragment.setInputFormula(brickField, SET_FORMULA_ON_SWITCH_EDIT_TEXT);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (SnackbarUtil.areHintsEnabled(this.getActivity())
				&& !wasHintAlreadyShown(getActivity(), getActivity().getResources()
				.getResourceName(R.string.formula_editor_intro_title_formula_editor))) {
			new FormulaEditorIntroDialog(this, R.style.stage_dialog).show();
		}
	}

	public static boolean saveFormulaForUserBrickParameterChange() {
		InternFormulaParser formulaToParse = formulaEditorEditText.getFormulaParser();
		FormulaElement formulaParseTree = formulaToParse.parseFormula();

		switch (formulaToParse.getErrorTokenIndex()) {
			case InternFormulaParser.PARSER_OK:
				currentFormula.setRoot(formulaParseTree);
				if (onFormulaChangedListener != null) {
					onFormulaChangedListener.onFormulaChanged(formulaBrick, currentBrickField, currentFormula);
				}
				if (formulaEditorBrick != null) {
					currentFormula.refreshTextField(brickView);
				}
				formulaEditorEditText.formulaSaved();
				return true;
		}
		return false;
	}

	private static void refreshUserBrickParameterValuesIfNecessary(FormulaBrick formulaBrick, Brick.BrickField
			brickField, FormulaEditorFragment formulaEditorFragment) {
		if (formulaBrick instanceof UserBrickParameter) {
			saveFormulaForUserBrickParameterChange();
			updateUserBricksIfNecessary();
			formulaEditorFragment.updateBrickViewAndFormula(formulaBrick, brickField);
			updateUserBricksIfNecessary();
		}
	}

	private boolean showCustomView = false;

	public static void showFragment(View view, FormulaBrick formulaBrick, Brick.BrickField brickField) {
		showFragment(view, formulaBrick, brickField, false);
	}

	public static void showCustomFragment(View view, FormulaBrick formulaBrick, Brick.BrickField brickField) {
		showFragment(view, formulaBrick, brickField, true);
	}

	public static void overwriteFormula(View view, Formula newFormula) {

		Activity activity = (Activity) view.getContext();

		FormulaEditorFragment formulaEditorFragment = (FormulaEditorFragment) activity.getFragmentManager()
				.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG);

		if (formulaEditorFragment == null) {
			return;
		}

		formulaEditorEditText.overwriteCurrentFormula(newFormula.getInternFormulaState());
	}

	public static void changeInputField(View view, Brick.BrickField brickField) {

		Activity activity = (Activity) view.getContext();

		FormulaEditorFragment formulaEditorFragment = (FormulaEditorFragment) activity.getFragmentManager()
				.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG);

		if (formulaEditorFragment == null) {
			return;
		}
		formulaEditorFragment.setInputFormula(brickField, SET_FORMULA_ON_SWITCH_EDIT_TEXT);
	}

	public void updateBrickView() {
		View newBrickView = getBrickOrCustomView();
		setBrickViewSafe(newBrickView, true);
		fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
		handleCustomView();
	}

	private void setBrickViewSafe(View newBrickView, boolean removeAllViews) {
		if (newBrickView != null) {
			if (newBrickView.getParent() != null) {
				((LinearLayout) newBrickView.getParent()).removeView(newBrickView);
			}

			if (removeAllViews && formulaEditorBrick.getChildCount() > 0) {
				formulaEditorBrick.removeAllViews();
			}

			formulaEditorBrick.addView(newBrickView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
			brickView = newBrickView;
		}
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
		refreshFormulaPreviewString(currentFormula.getTrimmedFormulaString(getActivity()));
		formulaEditorEditText.endEdit();
		currentFormula.prepareToRemove();

		getFragmentManager().popBackStack();

		BottomBar.showBottomBar(getActivity());
		BottomBar.showPlayButton(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		fragmentView = inflater.inflate(R.layout.fragment_formula_editor, container, false);
		fragmentView.setFocusableInTouchMode(true);
		fragmentView.requestFocus();

		context = getActivity();

		brickView = getBrickOrCustomView();
		formulaEditorBrick = (LinearLayout) fragmentView.findViewById(R.id.formula_editor_brick_space);
		setBrickViewSafe(brickView, false);

		formulaEditorEditText = (FormulaEditorEditText) fragmentView.findViewById(R.id.formula_editor_edit_field);

		formulaEditorKeyboard = (TableLayout) fragmentView.findViewById(R.id.formula_editor_keyboardview);
		formulaEditorEditText.init(this);

		fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(this);

		setInputFormula(currentBrickField, SET_FORMULA_ON_CREATE_VIEW);

		setHasOptionsMenu(true);
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.formula_editor_title);

		handleCustomView();

		return fragmentView;
	}

	private void handleCustomView() {
		if (showCustomView) {
			formulaEditorEditText.setVisibility(View.GONE);
			formulaEditorKeyboard.setVisibility(View.GONE);
		} else {
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

		getView().requestFocus();
		View.OnTouchListener touchListener = new View.OnTouchListener() {
			private Handler handler;
			private Runnable deleteAction;

			private boolean handleLongClick(final View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (handler == null) {
						return true;
					}
					handler.removeCallbacks(deleteAction);
					handler = null;
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					deleteAction = new Runnable() {
						@Override
						public void run() {
							handler.postDelayed(this, 100);
							if (formulaEditorEditText.isThereSomethingToDelete()) {
								formulaEditorEditText.handleKeyEvent(view.getId(), "");
							}
						}
					};

					if (handler != null) {
						return true;
					}
					handler = new Handler();
					handler.postDelayed(deleteAction, 400);
				}
				return true;
			}

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					updateButtonsOnKeyboardAndInvalidateOptionsMenu();
					view.setPressed(false);
					handleLongClick(view, event);
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
							if ((formulaElement.getRequiredResources() & Brick.SENSOR_GPS) > 0 && !SensorHandler
									.gpsAvailable()) {
								formulaElementForComputeDialog = formulaElement;
								Intent checkIntent = new Intent();
								checkIntent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivityForResult(checkIntent, REQUEST_GPS);
							} else {
								showComputeDialog(formulaElement);
							}

							return true;
						case R.id.formula_editor_keyboard_function:
							showCategoryListFragment(CategoryListFragment.FUNCTION_TAG,
									R.string.formula_editor_functions);
							return true;
						case R.id.formula_editor_keyboard_logic:
							showCategoryListFragment(CategoryListFragment.LOGIC_TAG,
									R.string.formula_editor_logic);
							return true;
						case R.id.formula_editor_keyboard_object:
							showCategoryListFragment(CategoryListFragment.OBJECT_TAG,
									R.string.formula_editor_choose_object_variable);
							return true;
						case R.id.formula_editor_keyboard_sensors:
							showCategoryListFragment(CategoryListFragment.SENSOR_TAG,
									R.string.formula_editor_device);
							return true;
						case R.id.formula_editor_keyboard_data:
							showDataFragment();
							return true;
						case R.id.formula_editor_keyboard_ok:
							endFormulaEditor();
							return true;
						case R.id.formula_editor_keyboard_string:
							showNewStringDialog();
							return true;
						case R.id.formula_editor_keyboard_delete:
							formulaEditorEditText.handleKeyEvent(view.getId(), "");
							return handleLongClick(view, event);
						default:
							formulaEditorEditText.handleKeyEvent(view.getId(), "");
							return true;
					}
				}
				return false;
			}
		};

		for (int index = 0; index < formulaEditorKeyboard.getChildCount(); index++) {
			View tableRow = formulaEditorKeyboard.getChildAt(index);
			if (tableRow instanceof TableRow) {
				TableRow row = (TableRow) tableRow;
				for (int indexRow = 0; indexRow < row.getChildCount(); indexRow++) {
					row.getChildAt(indexRow).setOnTouchListener(touchListener);
				}
			}
		}

		updateButtonsOnKeyboardAndInvalidateOptionsMenu();

		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(previousActionBarTitle);
	}

	private void showNewStringDialog() {
		NewStringDialogFragment dialog = new NewStringDialogFragment(getSelectedFormulaText(), this);
		dialog.show(getFragmentManager(), NewStringDialogFragment.TAG);
	}

	@Override
	public void addItem(String string) {
		String previousString = getSelectedFormulaText();
		if (previousString != null && !previousString.matches("\\s*")) {
			overrideSelectedText(string);
		} else {
			addStringToActiveFormula(string);
		}
		updateButtonsOnKeyboardAndInvalidateOptionsMenu();
	}

	private void showComputeDialog(FormulaElement formulaElement) {
		if (formulaElement == null) {
			return;
		}
		Formula formulaToCompute = new Formula(formulaElement);
		FormulaEditorComputeDialog computeDialog = new FormulaEditorComputeDialog(context);
		computeDialog.setFormula(formulaToCompute);
		computeDialog.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_GPS && resultCode == Activity.RESULT_CANCELED && SensorHandler.gpsAvailable()) {
			showComputeDialog(formulaElementForComputeDialog);
		} else {
			showToast(R.string.error_gps_not_available, true);
		}
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

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_formulaeditor, menu);
		super.onCreateOptionsMenu(menu, inflater);
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
				if (undo != null) {
					undo.setIcon(R.drawable.icon_undo_disabled);
					undo.setEnabled(false);
				}

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
			case InternFormulaParser.PARSER_OK:
				return saveValidFormula(formulaParseTree);
			case InternFormulaParser.PARSER_STACK_OVERFLOW:
				return checkReturnWithoutSaving(InternFormulaParser.PARSER_STACK_OVERFLOW);
			case InternFormulaParser.PARSER_NO_INPUT:
				if (Brick.BrickField.isExpectingStringValue(currentBrickField)) {
					return saveValidFormula(new FormulaElement(FormulaElement.ElementType.STRING, "", null));
				}
				// fallthrough
			default:
				formulaEditorEditText.setParseErrorCursorAndSelection();
				return checkReturnWithoutSaving(InternFormulaParser.PARSER_INPUT_SYNTAX_ERROR);
		}
	}

	private boolean saveValidFormula(FormulaElement formulaElement) {
		currentFormula.setRoot(formulaElement);
		if (onFormulaChangedListener != null) {
			onFormulaChangedListener.onFormulaChanged(formulaBrick, currentBrickField, currentFormula);
		}
		if (formulaEditorBrick != null) {
			currentFormula.refreshTextField(brickView);
		}
		formulaEditorEditText.formulaSaved();
		hasFormulaBeenChanged = true;
		return true;
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
				case InternFormulaParser.PARSER_INPUT_SYNTAX_ERROR:
					showToast(R.string.formula_editor_parse_fail, true);
					break;
				case InternFormulaParser.PARSER_STACK_OVERFLOW:
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

	public void promptSave() {
		if (hasFormulaBeenChanged) {
			ToastUtil.showSuccess(getActivity(), R.string.formula_editor_changes_saved);
			hasFormulaBeenChanged = false;
		}
		exitFormulaEditorFragment();
	}

	private void exitFormulaEditorFragment() {
		if (formulaEditorEditText.hasChanges()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.formula_editor_discard_changes_dialog_title)
					.setMessage(R.string.formula_editor_discard_changes_dialog_message)
					.setNegativeButton(R.string.no, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							ToastUtil.showError(getActivity(), R.string.formula_editor_changes_discarded);
							currentFormula.setDisplayText(null);
							onUserDismiss();
						}
					})
					.setPositiveButton(R.string.yes, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (saveFormulaIfPossible()) {
								ToastUtil.showSuccess(getActivity(), R.string.formula_editor_changes_saved);
								hasFormulaBeenChanged = false;
								onUserDismiss();
							}
						}
					})
					.create()
					.show();
		} else {
			onUserDismiss();
		}
	}

	private void endFormulaEditor() {
		if (formulaEditorEditText.hasChanges()) {
			if (saveFormulaIfPossible()) {
				showToast(R.string.formula_editor_changes_saved, false);
				hasFormulaBeenChanged = false;
				updateUserBricksIfNecessary();
				onUserDismiss();
			}
		} else {
			onUserDismiss();
		}
	}

	private static void updateUserBricksIfNecessary() {
		if (formulaBrick instanceof UserBrickParameter) {
			UserBrick userBrick = ((UserBrickParameter) formulaBrick).getParent();
			userBrick.updateUserBrickParametersAndVariables();
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

	private void showCategoryListFragment(String tag, int actionbarResId) {
		CategoryListFragment fragment = new CategoryListFragment();
		Bundle bundle = new Bundle();
		bundle.putString(CategoryListFragment.ACTION_BAR_TITLE_BUNDLE_ARGUMENT,
				getActivity().getString(actionbarResId));
		bundle.putString(CategoryListFragment.FRAGMENT_TAG_BUNDLE_ARGUMENT, tag);
		fragment.setArguments(bundle);

		getFragmentManager().beginTransaction()
				.hide(getFragmentManager().findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG))
				.add(R.id.fragment_container, fragment, tag)
				.addToBackStack(tag)
				.commit();
	}

	private void showDataFragment() {
		DataListFragment fragment = new DataListFragment();
		fragment.setFormulaEditorDataInterface(this);
		getFragmentManager().beginTransaction()
				.hide(getFragmentManager().findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG))
				.add(R.id.fragment_container, fragment, DataListFragment.TAG)
				.addToBackStack(DataListFragment.TAG)
				.commit();
	}

	@Override
	public void onDataItemSelected(UserData item) {
		if (item instanceof UserVariable) {
			addUserVariableToActiveFormula(item.getName());
		} else if (item instanceof UserList) {
			addUserListToActiveFormula(item.getName());
		}
	}

	@Override
	public void onVariableRenamed(String previousName, String newName) {
		formulaEditorEditText.updateVariableReferences(previousName, newName);
	}

	@Override
	public void onListRenamed(String previousName, String newName) {
		formulaEditorEditText.updateListReferences(previousName, newName);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		fragmentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		Rect brickRect = new Rect();
		Rect keyboardRec = new Rect();
		formulaEditorBrick.getGlobalVisibleRect(brickRect);
		formulaEditorKeyboard.getGlobalVisibleRect(keyboardRec);

		//FormulaEditorIntroUtil.prepareIntro(fragmentView);
	}

	public void addResourceToActiveFormula(int resource) {
		formulaEditorEditText.handleKeyEvent(resource, "");
		if (resource == R.string.formula_editor_function_collides_with_edge || resource == R.string
				.formula_editor_function_touched) {
			ProjectManager.getInstance().getCurrentSprite().createCollisionPolygons();
		}
	}

	public void addUserListToActiveFormula(String userListName) {
		formulaEditorEditText.handleKeyEvent(InternFormulaKeyboardAdapter.FORMULA_EDITOR_USER_LIST_RESOURCE_ID,
				userListName);
	}

	public void addUserVariableToActiveFormula(String userVariableName) {
		formulaEditorEditText.handleKeyEvent(InternFormulaKeyboardAdapter.FORMULA_EDITOR_USER_VARIABLE_RESOURCE_ID,
				userVariableName);
	}

	public void addCollideFormulaToActiveFormula(String spriteName) {
		formulaEditorEditText.handleKeyEvent(InternFormulaKeyboardAdapter.FORMULA_EDITOR_COLLIDE_RESOURCE_ID,
				spriteName);
	}

	public void addStringToActiveFormula(String string) {
		formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_string, string);
	}

	public String getSelectedFormulaText() {
		return formulaEditorEditText.getSelectedTextFromInternFormula();
	}

	public void overrideSelectedText(String string) {
		formulaEditorEditText.overrideSelectedText(string);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (!hidden) {
			((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.formula_editor_title);
			BottomBar.hideBottomBar(getActivity());
			updateButtonsOnKeyboardAndInvalidateOptionsMenu();
		}
	}

	public void updateButtonsOnKeyboardAndInvalidateOptionsMenu() {
		getActivity().invalidateOptionsMenu();

		ImageButton backspaceOnKeyboard = (ImageButton) getActivity().findViewById(R.id.formula_editor_keyboard_delete);
		if (!formulaEditorEditText.isThereSomethingToDelete()) {
			backspaceOnKeyboard.setAlpha(255 / 3);
			backspaceOnKeyboard.setEnabled(false);
		} else {
			backspaceOnKeyboard.setAlpha(255);
			backspaceOnKeyboard.setEnabled(true);
		}
	}
}
