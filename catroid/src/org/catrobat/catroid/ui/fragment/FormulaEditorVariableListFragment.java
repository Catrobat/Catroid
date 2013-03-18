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
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
import org.catrobat.catroid.ui.adapter.UserVariableAdapter;

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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class FormulaEditorVariableListFragment extends SherlockListFragment implements Dialog.OnKeyListener,
		UserVariableAdapter.OnCheckedChangeListener, UserVariableAdapter.OnListItemClickListener {

	String mTag;
	public static final String VARIABLE_TAG = "variableFragment";
	private FormulaEditorEditText formulaEditorEditText;
	private String actionBarTitle;
	private com.actionbarsherlock.view.ActionMode contextActionMode;
	private boolean inContextMode;
	private int deleteIndex;
	private RadioButton leftDialogRadioButton;
	private RadioButton rightDialogRadioButton;
	private Dialog dialogNewVariable;
	private UserVariableAdapter adapter;

	public FormulaEditorVariableListFragment(FormulaEditorEditText formulaEditorEditText, String actionBarTitle,
			String fragmentTag) {
		this.formulaEditorEditText = formulaEditorEditText;
		this.actionBarTitle = actionBarTitle;
		mTag = fragmentTag;
		contextActionMode = null;
		deleteIndex = -1;
		inContextMode = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		initializeUserVariableAdapter();

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
		if (!inContextMode) {
			super.onCreateContextMenu(menu, view, menuInfo);
			getSherlockActivity().getMenuInflater().inflate(R.menu.menu_formulaeditor_variablelist, menu);
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.delete).setVisible(true);
		menu.findItem(R.id.copy).setVisible(false);
		menu.findItem(R.id.cut).setVisible(false);
		menu.findItem(R.id.show_details).setVisible(false);
		menu.findItem(R.id.insert_below).setVisible(false);
		menu.findItem(R.id.move).setVisible(false);
		menu.findItem(R.id.rename).setVisible(false);
		menu.findItem(R.id.show_details).setVisible(false);
		menu.findItem(R.id.settings).setVisible(false);

		getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSherlockActivity().getSupportActionBar().setTitle(actionBarTitle);
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public void onListItemClick(int position) {

		if (!inContextMode) {
			formulaEditorEditText.handleKeyEvent(0, "" + adapter.getItem(position).getName());
			KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
			onKey(null, keyEvent.getKeyCode(), keyEvent);
		}

	}

	@Override
	public void onCheckedChange() {
		if (inContextMode) {
			String title = adapter.getAmountOfCheckedItems() + " "
					+ getString(R.string.formula_editor_variable_context_action_item_selected);
			contextActionMode.setTitle(title);
		}
	}

	@Override
	public void onStart() {

		this.registerForContextMenu(getListView());
		getListView().setItemsCanFocus(true);
		getListView().setLongClickable(true);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
				if (!inContextMode) {
					deleteIndex = position;
					getSherlockActivity().openContextMenu(getListView());
					return true;
				}
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
													ProjectManager.getInstance().getCurrentSprite()) != null) {

										Toast.makeText(getActivity(), R.string.formula_editor_existing_user_variable,
												Toast.LENGTH_LONG).show();

									} else {
										ProjectManager.getInstance().getCurrentProject().getUserVariables()
												.addProjectUserVariable(editTextString, 0.0);
										adapter.notifyDataSetChanged();
									}
								} else if (rightDialogRadioButton.isChecked()) {
									ProjectManager.getInstance().getCurrentProject().getUserVariables()
											.addSpriteUserVariable(editTextString, 0.0);
									adapter.notifyDataSetChanged();
								}

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

							@Override
							public void afterTextChanged(Editable editable) {
								EditText dialogEdittext = (EditText) dialogNewVariable
										.findViewById(R.id.dialog_formula_editor_variable_name_edit_text);
								String editTextString = editable.toString();

								Button positiveButton = ((AlertDialog) dialogNewVariable)
										.getButton(AlertDialog.BUTTON_POSITIVE);

								Button negativeButton = ((AlertDialog) dialogNewVariable)
										.getButton(AlertDialog.BUTTON_NEGATIVE);

								if (ProjectManager
										.getInstance()
										.getCurrentProject()
										.getUserVariables()
										.getUserVariable(editTextString,
												ProjectManager.getInstance().getCurrentSprite()) != null) {

									Toast toast = Toast.makeText(getActivity(),
											R.string.formula_editor_existing_user_variable, Toast.LENGTH_LONG);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();

									positiveButton.setClickable(false);

									positiveButton.setTextColor(getResources().getColorStateList(R.color.gray));

									dialogEdittext.setBackgroundColor(getResources().getColor(R.color.solid_red));

								} else {

									dialogEdittext.setBackgroundColor(getResources().getColor(R.color.transparent));
									positiveButton.setClickable(true);
									positiveButton.setTextColor(negativeButton.getTextColors());
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
		adapter.notifyDataSetChanged();
		super.onStart();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_delete:
				inContextMode = true;
				contextActionMode = getSherlockActivity().startActionMode(mContextModeCallback);
				return true;
			case R.id.delete:
				inContextMode = true;
				contextActionMode = getSherlockActivity().startActionMode(mContextModeCallback);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		switch (item.getItemId()) {
			case R.id.menu_delete:
				if (!adapter.isEmpty()) {
					ProjectManager.getInstance().getCurrentProject().getUserVariables()
							.deleteUserVariableByName(adapter.getItem(deleteIndex).getName());
					adapter.notifyDataSetChanged();
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
		fragTransaction.show(this);
		fragTransaction.commit();
		if (adapter != null) {
			initializeUserVariableAdapter();
		}

	}

	private void initializeUserVariableAdapter() {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		UserVariablesContainer userVariableContainer = currentProject.getUserVariables();
		adapter = userVariableContainer.createUserVariableAdapter(getSherlockActivity(), currentSprite);
		setListAdapter(adapter);
		adapter.setOnCheckedChangeListener(this);
		adapter.setOnListItemClickListener(this);
	}

	@Override
	public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				FragmentTransaction fragTransaction = getSherlockActivity().getSupportFragmentManager()
						.beginTransaction();
				fragTransaction.hide(this);
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
			adapter.setSelectMode(Constants.MULTI_SELECT);
			adapter.notifyDataSetChanged();
			mode.setTitle("0 " + getString(R.string.formula_editor_variable_context_action_item_selected));
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			UserVariablesContainer userVariablesContainer = ProjectManager.getInstance().getCurrentProject()
					.getUserVariables();
			if (!adapter.isEmpty()) {
				for (UserVariable var : adapter.getCheckedUserVariables()) {
					userVariablesContainer.deleteUserVariableByName(var.getName());
				}
			}

			adapter.setSelectMode(Constants.SELECT_NONE);
			adapter.notifyDataSetChanged();
			contextActionMode = null;
			inContextMode = false;
		}
	};
}
