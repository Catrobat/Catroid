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

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class FormulaEditorVariableListFragment extends SherlockListFragment implements Dialog.OnKeyListener {

	String mTag;
	public static final String VARIABLE_TAG = "variableFragment";
	List<String> mItems;
	private FormulaEditorEditText mFormulaEditorEditText;
	private String mActionBarTitle;
	private com.actionbarsherlock.view.ActionMode mContextActionMode;
	private boolean mInContextMode;
	private int mDeleteIndex;
	private RadioButton leftDialogRadioButton;
	private RadioButton rightDialogRadioButton;
	private Dialog dialogNewVariable;

	public FormulaEditorVariableListFragment(FormulaEditorEditText formulaEditorEditText, String actionBarTitle,
			String fragmentTag) {
		mFormulaEditorEditText = formulaEditorEditText;
		mActionBarTitle = actionBarTitle;
		mTag = fragmentTag;
		mContextActionMode = null;
		mDeleteIndex = -1;
		mInContextMode = false;
		mItems = new ArrayList<String>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		String currentSpriteName = currentSprite.getName();
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		UserVariablesContainer userVariableContainer = currentProject.getUserVariables();
		List<UserVariable> userVariables = userVariableContainer.getUserVariables(currentSpriteName);
		for (UserVariable userVariable : userVariables) {
			mItems.add(userVariable.toString());
		}
		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mItems));

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_formula_editor_variablelist, container, false);
		return fragmentView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_formulaeditor_variablelist, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		if (!mInContextMode) {
			Log.i("info", "FEVLF.onCreateContextMenu()");
			super.onCreateContextMenu(menu, view, menuInfo);
			getSherlockActivity().getMenuInflater().inflate(R.menu.menu_formulaeditor_variablelist, menu);
		}

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		super.onPrepareOptionsMenu(menu);
		//		menu.removeItem(R.id.menu_add);
		//		menu.removeItem(R.id.menu_start);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(mActionBarTitle);
		actionBar.setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		Log.i("info", "FEVLF.onLISTItemClick()");
		if (mInContextMode) {
			String title = countCheckedListItems() + " "
					+ getString(R.string.formula_editor_variable_context_action_item_selected);
			mContextActionMode.setTitle(title);
		} else {
			mFormulaEditorEditText.handleKeyEvent(0, "" + mItems.get(position));
			KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
			onKey(null, keyEvent.getKeyCode(), keyEvent);
		}

	}

	@Override
	public void onStart() {

		this.registerForContextMenu(getListView());
		getListView().setLongClickable(true);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
				Log.i("info", "onItemLongClick()");
				if (!mInContextMode) {
					mDeleteIndex = position;
					//					arg0.setPressed(true);
					//					arg0	.setBackgroundResource(R.color.backbrown);
					getSherlockActivity().openContextMenu(getListView());
					return true;
				}
				return false;
			}
		});

		getListView().setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Log.i("info", "FEVLFonLongClick");
				return false;
			}
		});

		Button bottomBar = (Button) getSherlockActivity().findViewById(R.id.formula_editor_variable_list_bottom_bar);
		bottomBar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				dialogNewVariable = new AlertDialog.Builder(getActivity())
						.setView(
								LayoutInflater.from(getActivity()).inflate(
										R.layout.dialog_formula_editor_variable_name, null))
						.setTitle("Variable name ?").setNegativeButton(R.string.cancel_button, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}

						}).setPositiveButton(R.string.ok, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								EditText dialogEdittext = (EditText) dialogNewVariable
										.findViewById(R.id.dialog_formula_editor_variable_name_edit_text);

								String editTextString = dialogEdittext.getText().toString();
								if (leftDialogRadioButton.isChecked()) {
									if (ProjectManager
											.getInstance()
											.getCurrentProject()
											.getUserVariables()
											.getUserVariable(editTextString,
													ProjectManager.getInstance().getCurrentSprite().getName()) != null) {

										Toast.makeText(getActivity(), R.string.formula_editor_existing_user_variable,
												Toast.LENGTH_LONG).show();

									} else {
										ProjectManager.getInstance().getCurrentProject().getUserVariables()
												.addProjectUserVariable(editTextString, 5.0);//TODO value
										mItems.add(editTextString);
									}
								} else if (rightDialogRadioButton.isChecked()) {
									ProjectManager.getInstance().getCurrentProject().getUserVariables()
											.addSpriteUserVariable(editTextString, 5.0); //TODO value
									mItems.add(editTextString);
								}

								setListAdapter(new ArrayAdapter<String>(getActivity(),
										android.R.layout.simple_list_item_1, mItems));

							}
						}).create();

				dialogNewVariable.setOnShowListener(new OnShowListener() {
					@Override
					public void onShow(DialogInterface dialog) {
						Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
						positiveButton.setClickable(false);
						positiveButton.setTextColor(getResources().getColorStateList(R.color.gray));

						InputMethodManager imm = (InputMethodManager) getSherlockActivity().getSystemService(
								Context.INPUT_METHOD_SERVICE);

						EditText dialogEdittext = (EditText) dialogNewVariable
								.findViewById(R.id.dialog_formula_editor_variable_name_edit_text);

						imm.showSoftInput(dialogEdittext, InputMethodManager.SHOW_IMPLICIT);

						dialogEdittext.addTextChangedListener(new TextWatcher() {

							@Override
							public void onTextChanged(CharSequence s, int start, int before, int count) {

							}

							@Override
							public void beforeTextChanged(CharSequence s, int start, int count, int after) {

							}

							@SuppressLint("ShowToast")
							@Override
							public void afterTextChanged(Editable editable) {
								EditText dialogEdittext = (EditText) dialogNewVariable
										.findViewById(R.id.dialog_formula_editor_variable_name_edit_text);
								String editTextString = editable.toString();

								Button positiveButton = ((AlertDialog) dialogNewVariable)
										.getButton(AlertDialog.BUTTON_POSITIVE);

								if (ProjectManager
										.getInstance()
										.getCurrentProject()
										.getUserVariables()
										.getUserVariable(editTextString,
												ProjectManager.getInstance().getCurrentSprite().getName()) != null) {

									Toast toast = Toast.makeText(getActivity(),
											R.string.formula_editor_existing_user_variable, Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();

									positiveButton.setClickable(false);
									positiveButton.setTextColor(getResources().getColorStateList(R.color.gray));

									dialogEdittext.setBackgroundColor(getResources().getColor(R.color.solid_red));

								} else {

									dialogEdittext.setBackgroundColor(getResources().getColor(R.color.transparent));
									positiveButton.setClickable(true);
									positiveButton.setTextColor(getResources().getColorStateList(R.color.solid_black));
								}
							}
						});

					}
				});

				dialogNewVariable.setCanceledOnTouchOutside(true);
				dialogNewVariable.show();
				leftDialogRadioButton = (RadioButton) dialogNewVariable
						.findViewById(R.id.dialog_formula_editor_variable_name_radio_button_left);
				leftDialogRadioButton.setChecked(true);
				rightDialogRadioButton = (RadioButton) dialogNewVariable
						.findViewById(R.id.dialog_formula_editor_variable_name_radio_button_right);
				View.OnClickListener radioButtonListener = new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						RadioGroup radioGroup = (RadioGroup) dialogNewVariable
								.findViewById(R.id.dialog_formula_editor_variable_name_radio_group);
						switch (view.getId()) {
							case R.id.dialog_formula_editor_variable_name_radio_button_left:
								if (!leftDialogRadioButton.isChecked()) {
									radioGroup.clearCheck();
									leftDialogRadioButton.setChecked(true);
								}
								break;
							case R.id.dialog_formula_editor_variable_name_radio_button_right:
								if (!rightDialogRadioButton.isChecked()) {
									radioGroup.clearCheck();
									rightDialogRadioButton.setChecked(true);
								}
								break;
						}

					}
				};
				leftDialogRadioButton.setOnClickListener(radioButtonListener);
				rightDialogRadioButton.setOnClickListener(radioButtonListener);
			}
		});

		super.onStart();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_delete:
				mInContextMode = true;
				mContextActionMode = getSherlockActivity().startActionMode(mContextModeCallback);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		Log.i("info", "FEVLF.onContextItemSelected");
		switch (item.getItemId()) {
			case R.id.menu_delete:
				if (!mItems.isEmpty()) {
					ProjectManager.getInstance().getCurrentProject().getUserVariables()
							.deleteUserVariableByName(mItems.remove(mDeleteIndex));
					setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mItems));
				}
				return true;
			default:
				return super.onContextItemSelected(item);
		}

	}

	public void showFragment(Context context) {
		FragmentActivity activity = (FragmentActivity) context;
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();

		Fragment formulaEditorFragment = fragmentManager
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		fragTransaction.hide(formulaEditorFragment);
		fragTransaction.add(android.R.id.tabhost, this, FormulaEditorVariableListFragment.VARIABLE_TAG);
		fragTransaction.commit();

	}

	@Override
	public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
		Log.i("info", "onKey() in FE-ListFragment! keyCode: " + keyCode);
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i("info", "KEYCODE_BACK pressed in FE-ListFragment!");
				FragmentTransaction fragTransaction = getSherlockActivity().getSupportFragmentManager()
						.beginTransaction();
				fragTransaction.remove(this);
				fragTransaction.show(getSherlockActivity().getSupportFragmentManager().findFragmentByTag(
						FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG));
				fragTransaction.commit();
				return true;
			default:
				break;
		}
		return false;
	}

	private ActionMode.Callback mContextModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.setTitle("0 " + getString(R.string.formula_editor_variable_context_action_item_selected));
			menu.removeItem(R.id.menu_delete);
			setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice,
					mItems));

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			//				case R.id.menu_delete:
			//					Log.i("info", "mContextModeCallback.nActionItemClicked()");
			//					// TODO
			//					mode.finish();
			//					return true;

				default:
					Log.i("info", (String) item.getTitle());
					return false;
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mContextActionMode = null;
			mInContextMode = false;
			SparseBooleanArray checkedItemPositions = getListView().getCheckedItemPositions();
			Log.i("info", "checkedItemPositions.length(): " + checkedItemPositions.size());

			for (int index = 0; index < mItems.size(); index++) {
				if (checkedItemPositions.get(index) == true) {
					ProjectManager.getInstance().getCurrentProject().getUserVariables()
							.deleteUserVariableByName(mItems.get(index));
				}
			}

			mItems.clear();
			Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
			String currentSpriteName = currentSprite.getName();
			Project currentProject = ProjectManager.getInstance().getCurrentProject();
			UserVariablesContainer userVariableContainer = currentProject.getUserVariables();
			List<UserVariable> userVariables = userVariableContainer.getUserVariables(currentSpriteName);
			for (UserVariable userVariable : userVariables) {
				mItems.add(userVariable.toString());
			}
			setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mItems));

		}

	};

	private int countCheckedListItems() {
		int count = 0;
		SparseBooleanArray checkedItemPositions = getListView().getCheckedItemPositions();
		for (int index = 0; index < getListView().getCount(); index++) {
			if (checkedItemPositions.get(index) == true) {
				count++;
			}
		}
		return count;
	}

}
