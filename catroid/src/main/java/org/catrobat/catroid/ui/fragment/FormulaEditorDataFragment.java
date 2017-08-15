/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.TrackingConstants;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.UserBrickScriptActivity;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;
import org.catrobat.catroid.ui.dialogs.NewDataDialog.NewUserListDialogListener;
import org.catrobat.catroid.ui.dialogs.RenameVariableDialog;
import org.catrobat.catroid.utils.DividerUtil;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.utils.Utils;

public class FormulaEditorDataFragment extends ListFragment implements Dialog.OnKeyListener,
		DataAdapter.OnCheckedChangeListener, DataAdapter.OnListItemClickListener, NewUserListDialogListener, NewDataDialog.NewVariableDialogListener {
	private static final String TAG = FormulaEditorDataFragment.class.getSimpleName();

	public static final String USER_DATA_TAG = "userDataFragment";
	public static final String ACTION_BAR_TITLE_BUNDLE_ARGUMENT = "actionBarTitle";
	public static final String FRAGMENT_TAG_BUNDLE_ARGUMENT = "fragmentTag";

	private String actionBarTitle;
	private ActionMode contextActionMode;
	private View selectAllActionModeButton;
	private boolean inContextMode;
	private int index;
	private DataAdapter adapter;

	public FormulaEditorDataFragment() {
		contextActionMode = null;
		index = -1;
		inContextMode = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "FormulaEditorData onresume()");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		initializeDataAdapter();

		this.actionBarTitle = getArguments().getString(ACTION_BAR_TITLE_BUNDLE_ARGUMENT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_formula_editor_data_list, container, false);
		container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				getListView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
				DividerUtil.setDivider(getActivity(), getListView());
				TextSizeUtil.enlargeViewGroup((ViewGroup) getView());
			}
		});
		return view;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		if (!inContextMode) {
			super.onCreateContextMenu(menu, view, menuInfo);
			getActivity().getMenuInflater().inflate(R.menu.context_menu_formulaeditor_userlist, menu);
			boolean visible = !(getActivity() instanceof UserBrickScriptActivity);
			menu.findItem(R.id.context_formula_editor_userlist_delete).setVisible(visible);
			menu.findItem(R.id.context_formula_editor_userlist_rename).setVisible(visible);
		}
		TextSizeUtil.enlargeOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_formulaeditor_data_fragment, menu);
		TextSizeUtil.enlargeOptionsMenu(menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}
		boolean deleteVisible = !(getActivity() instanceof UserBrickScriptActivity);
		menu.findItem(R.id.formula_editor_data_item_delete).setVisible(deleteVisible);

		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().getActionBar().setTitle(actionBarTitle);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

		TextSizeUtil.enlargeOptionsMenu(menu);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				closeFormulaEditorDataFragment();
				return true;

			case R.id.formula_editor_data_item_delete:
				inContextMode = true;
				contextActionMode = getActivity().startActionMode(contextModeCallback);
				return true;
		}
		TextSizeUtil.enlargeOptionsItem(item);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(int position) {
		if (!inContextMode) {
			FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getActivity()
					.getFragmentManager().findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
			if (formulaEditor != null) {
				Object itemToAdd = adapter.getItem(position);
				if (itemToAdd instanceof UserVariable) {
					formulaEditor.addUserVariableToActiveFormula(((UserVariable) itemToAdd).getName());
				} else if (itemToAdd instanceof UserList) {
					formulaEditor.addUserListToActiveFormula(((UserList) itemToAdd).getName());
				}
				formulaEditor.updateButtonsOnKeyboardAndInvalidateOptionsMenu();
			}
			KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
			onKey(null, keyEvent.getKeyCode(), keyEvent);
		}
	}

	@Override
	public void onCheckedChange() {
		if (!inContextMode) {
			return;
		}

		updateActionModeTitle();
		UtilUi.setSelectAllActionModeButtonVisibility(selectAllActionModeButton,
				adapter.getCount() > 0 && adapter.getAmountOfCheckedItems() != adapter.getCount());
	}

	private void updateActionModeTitle() {
		String title = adapter.getAmountOfCheckedItems()
				+ " "
				+ getActivity().getResources().getQuantityString(
				R.plurals.formula_editor_data_fragment_context_action_item_selected,
				adapter.getAmountOfCheckedItems());

		contextActionMode.setTitle(title);

		TextSizeUtil.enlargeActionMode(contextActionMode);
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
					index = position;
					getActivity().openContextMenu(getListView());
					return true;
				}
				return false;
			}
		});

		setAddButtonListener(getActivity());

		adapter.notifyDataSetChanged();

		super.onStart();
	}

	public void setAddButtonListener(final Activity activity) {
		ImageButton buttonAdd = (ImageButton) activity.findViewById(R.id.button_add);
		buttonAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				NewDataDialog dialog = new NewDataDialog(NewDataDialog.DialogType.SHOW_LIST_CHECKBOX);
				dialog.addUserListDialogListener(FormulaEditorDataFragment.this);
				dialog.addVariableDialogListener(FormulaEditorDataFragment.this);
				dialog.show(activity.getFragmentManager(), NewDataDialog.DIALOG_FRAGMENT_TAG);
			}
		});
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		switch (item.getItemId()) {
			case R.id.context_formula_editor_userlist_delete:
				if (!adapter.isEmpty()) {
					Object itemToDelete = adapter.getItem(index);
					if (itemToDelete instanceof UserList) {
						ProjectManager.getInstance().getCurrentScene().getDataContainer()
								.deleteUserListByName(getNameOfItemInAdapter(index));
						adapter.notifyDataSetChanged();
						getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_USERLIST_DELETED));
					} else {
						final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

						final TextView textView = new TextView(getActivity());
						textView.setText(R.string.deletion_alert_text);
						textView.setPadding(50, 10, 50, 10);
						TextSizeUtil.enlargeTextView(textView);
						alertDialog.setTitle(R.string.deletion_alert_title);
						alertDialog.setView(textView);

						alertDialog.setPositiveButton(R.string.deletion_alert_yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										ProjectManager.getInstance().getCurrentScene().getDataContainer().deleteUserVariableByName(getNameOfItemInAdapter(index));
										adapter.notifyDataSetChanged();
										getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_VARIABLE_DELETED));
									}
								});
						alertDialog.setNegativeButton(R.string.deletion_alert_no, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						});

						AlertDialog alert = alertDialog.create();

						alert.setOnShowListener(new DialogInterface.OnShowListener() {
							@Override
							public void onShow(DialogInterface dialog) {
								Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
								if (positiveButton != null) {
									TextSizeUtil.enlargeButtonText(positiveButton);
								}
								Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
								if (negativeButton != null) {
									TextSizeUtil.enlargeButtonText(negativeButton);
								}
							}
						});

						alert.show();
					}
				}
				TextSizeUtil.enlargeOptionsItem(item);
				return true;
			case R.id.context_formula_editor_userlist_rename:
				Object itemToRename = adapter.getItem(index);
				RenameVariableDialog dialog;
				if (itemToRename instanceof UserVariable) {
					dialog = new RenameVariableDialog((UserVariable) itemToRename, adapter, RenameVariableDialog
							.DialogType.USER_VARIABLE);
				} else if (itemToRename instanceof UserList) {
					dialog = new RenameVariableDialog((UserList) itemToRename, adapter, RenameVariableDialog
							.DialogType.USER_LIST);
				} else {
					return false;
				}
				dialog.show(getActivity().getFragmentManager(), RenameVariableDialog.DIALOG_FRAGMENT_TAG);
				TextSizeUtil.enlargeOptionsItem(item);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onFinishNewUserListDialog(Spinner spinnerToUpdate, UserList userList) {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onFinishNewVariableDialog(Spinner spinnerToUpdate, UserVariable newUserVariable) {
		adapter.notifyDataSetChanged();
	}

	public void showFragment(Context context) {
		Activity activity = (Activity) context;
		FragmentManager fragmentManager = activity.getFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();

		Fragment formulaEditorFragment = fragmentManager
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		fragTransaction.hide(formulaEditorFragment);

		BottomBar.showBottomBar(activity);
		BottomBar.hidePlayButton(activity);

		fragTransaction.show(this);
		fragTransaction.commit();

		if (adapter != null) {
			initializeDataAdapter();
		}
	}

	private void initializeDataAdapter() {
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		UserBrick currentUserBrick = ProjectManager.getInstance().getCurrentUserBrick();
		DataContainer dataContainer = currentScene.getDataContainer();
		adapter = dataContainer.createDataAdapter(getActivity(), currentUserBrick, currentSprite);
		setListAdapter(adapter);
		adapter.setOnCheckedChangeListener(this);
		adapter.setOnListItemClickListener(this);
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				closeFormulaEditorDataFragment();
				return true;
			default:
				break;
		}
		return false;
	}

	private void closeFormulaEditorDataFragment() {
		BottomBar.hideBottomBar(getActivity());
		((ScriptActivity) getActivity()).updateHandleAddButtonClickListener();

		FragmentTransaction fragmentTransaction = getActivity().getFragmentManager()
				.beginTransaction();
		fragmentTransaction.hide(this);
		FormulaEditorFragment formulaEditorFragment = (FormulaEditorFragment) getActivity()
				.getFragmentManager().findFragmentByTag(
						FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		formulaEditorFragment.updateBrickView();
		fragmentTransaction.show(formulaEditorFragment);
		fragmentTransaction.commit();
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAllActionModeButton = UtilUi.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode, menu);
		selectAllActionModeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				for (int position = 0; position < adapter.getCount(); position++) {
					adapter.addCheckedItem(position);
				}
				adapter.notifyDataSetChanged();
				onCheckedChange();
			}
		});
	}

	private ActionMode.Callback contextModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			adapter.setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			adapter.notifyDataSetChanged();
			mode.setTitle("0 "
					+ getActivity().getResources().getQuantityString(
					R.plurals.formula_editor_data_fragment_context_action_item_selected, 0));
			BottomBar.hideBottomBar(getActivity());
			addSelectAllActionModeButton(mode, menu);

			TextSizeUtil.enlargeActionMode(mode);
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
			final DataContainer dataContainer = ProjectManager.getInstance().getCurrentScene().getDataContainer();
			if (!adapter.isEmpty()) {

				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

				final TextView textView = new TextView(getActivity());
				textView.setText(R.string.deletion_alert_text);
				textView.setPadding(50, 10, 50, 10);
				TextSizeUtil.enlargeTextView(textView);
				alertDialog.setTitle(R.string.deletion_alert_title);
				alertDialog.setView(textView);

				alertDialog.setPositiveButton(R.string.deletion_alert_yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								for (UserList list : adapter.getCheckedUserLists()) {
									DataContainer.DataType type = dataContainer.getTypeOfUserVariable(list.getName(),
											ProjectManager.getInstance().getCurrentSprite());
									String typeString = type == DataContainer.DataType.USER_VARIABLE_PROJECT
											? TrackingConstants.GLOBAL : TrackingConstants.LOCAL;

									Utils.getTrackingUtilProxy().trackData(list.getName(), typeString, TrackingConstants.DELETE_LIST);
									dataContainer.deleteUserListByName(list.getName());
								}
								for (UserVariable variable : adapter.getCheckedUserVariables()) {
									DataContainer.DataType type = dataContainer.getTypeOfUserVariable(variable.getName(),
											ProjectManager.getInstance().getCurrentSprite());
									String typeString = type == DataContainer.DataType.USER_VARIABLE_PROJECT
											? TrackingConstants.GLOBAL : TrackingConstants.LOCAL;

									Utils.getTrackingUtilProxy().trackData(variable.getName(), typeString, TrackingConstants.DELETE_VARIABLE);
									dataContainer.deleteUserVariableByName(variable.getName());
								}

								adapter.notifyDataSetChanged();
								getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_USERLIST_DELETED));
								getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_VARIABLE_DELETED));

								adapter.setSelectMode(ListView.CHOICE_MODE_NONE);
								contextActionMode = null;
								inContextMode = false;
								getActivity().findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
							}
						});
				alertDialog.setNegativeButton(R.string.deletion_alert_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						adapter.setSelectMode(ListView.CHOICE_MODE_NONE);
						contextActionMode = null;
						inContextMode = false;
						getActivity().findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
					}
				});

				AlertDialog alert = alertDialog.create();

				alert.setOnShowListener(new DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialog) {
						Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
						if (positiveButton != null) {
							TextSizeUtil.enlargeButtonText(positiveButton);
						}
						Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
						if (negativeButton != null) {
							TextSizeUtil.enlargeButtonText(negativeButton);
						}
					}
				});

				alert.show();
			}
		}
	};

	private String getNameOfItemInAdapter(int position) {
		Object item = adapter.getItem(position);
		if (item instanceof UserList) {
			return ((UserList) item).getName();
		} else if (item instanceof UserVariable) {
			return ((UserVariable) item).getName();
		}

		return null;
	}
}
