/*
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.adapter.UserListAdapterWrapper;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class DeleteItemOfUserListBrick extends UserListBrick {

	private static final long serialVersionUID = 1L;
	private transient AdapterView<?> adapterView;

	public DeleteItemOfUserListBrick() {
		addAllowedBrickField(BrickField.LIST_DELETE_ITEM);
	}

	public DeleteItemOfUserListBrick(Formula userListFormula, UserList userList) {
		initializeBrickFields(userListFormula);
		this.userList = userList;
	}

	public DeleteItemOfUserListBrick(Integer value) {
		initializeBrickFields(new Formula(value));
	}

	private void initializeBrickFields(Formula listAddItemFormula) {
		addAllowedBrickField(BrickField.LIST_DELETE_ITEM);
		setFormulaWithBrickField(BrickField.LIST_DELETE_ITEM, listAddItemFormula);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.deleteItemOfUserList(sprite, getFormulaWithBrickField(BrickField.LIST_DELETE_ITEM), userList));
		return null;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_delete_item_of_userlist, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_delete_item_of_userlist_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView prototypeText = (TextView) view.findViewById(R.id.brick_delete_item_of_userlist_prototype_view);
		TextView textField = (TextView) view.findViewById(R.id.brick_delete_item_of_userlist_edit_text);
		prototypeText.setVisibility(View.GONE);
		getFormulaWithBrickField(BrickField.LIST_DELETE_ITEM).setTextFieldId(R.id.brick_delete_item_of_userlist_edit_text);
		getFormulaWithBrickField(BrickField.LIST_DELETE_ITEM).refreshTextField(view);
		textField.setVisibility(View.VISIBLE);
		textField.setOnClickListener(this);

		Spinner userListSpinner = (Spinner) view.findViewById(R.id.delete_item_of_userlist_spinner);
		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentProject().getDataContainer()
				.createDataAdapter(context, ProjectManager.getInstance().getCurrentSprite());
		UserListAdapterWrapper userListAdapterWrapper = new UserListAdapterWrapper(context, dataAdapter);
		userListAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);

		userListSpinner.setAdapter(userListAdapterWrapper);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			userListSpinner.setClickable(true);
			userListSpinner.setEnabled(true);
		} else {
			userListSpinner.setClickable(false);
			userListSpinner.setFocusable(false);
		}

		setSpinnerSelection(userListSpinner, null);

		userListSpinner.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						&& (((Spinner) view).getSelectedItemPosition() == 0 && ((Spinner) view).getAdapter().getCount() == 1)) {
					NewDataDialog dialog = new NewDataDialog((Spinner) view, NewDataDialog.DialogType.USER_LIST);
					dialog.addUserListDialogListener(DeleteItemOfUserListBrick.this);
					dialog.show(((SherlockFragmentActivity) view.getContext()).getSupportFragmentManager(),
							NewDataDialog.DIALOG_FRAGMENT_TAG);
					return true;
				}

				return false;
			}
		});
		userListSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 && ((UserListAdapterWrapper) parent.getAdapter()).isTouchInDropDownView()) {
					NewDataDialog dialog = new NewDataDialog((Spinner) parent, NewDataDialog.DialogType.USER_LIST);
					dialog.addUserListDialogListener(DeleteItemOfUserListBrick.this);
					dialog.show(((SherlockFragmentActivity) view.getContext()).getSupportFragmentManager(),
							NewDataDialog.DIALOG_FRAGMENT_TAG);
				}
				((UserListAdapterWrapper) parent.getAdapter()).resetIsTouchInDropDownView();
				userList = (UserList) parent.getItemAtPosition(position);
				adapterView = parent;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				userList = null;
			}
		});

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_delete_item_of_userlist, null);
		Spinner userListSpinner = (Spinner) prototypeView.findViewById(R.id.delete_item_of_userlist_spinner);
		userListSpinner.setFocusableInTouchMode(false);
		userListSpinner.setFocusable(false);
		userListSpinner.setEnabled(false);
		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentProject().getDataContainer()
				.createDataAdapter(context, ProjectManager.getInstance().getCurrentSprite());

		UserListAdapterWrapper userListAdapterWrapper = new UserListAdapterWrapper(context, dataAdapter);

		userListAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);
		userListSpinner.setAdapter(userListAdapterWrapper);
		setSpinnerSelection(userListSpinner, null);

		TextView textAddItemToList = (TextView) prototypeView
				.findViewById(R.id.brick_delete_item_of_userlist_prototype_view);
		textAddItemToList.setText(String.valueOf(BrickValues.DELETE_ITEM_OF_USERLIST));

		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			TextView textAddItemToList = (TextView) view.findViewById(R.id.brick_delete_item_of_userlist_label);
			TextView textTo = (TextView) view.findViewById(R.id.brick_delete_item_of_userlist_textview);
			TextView editVariable = (TextView) view.findViewById(R.id.brick_delete_item_of_userlist_edit_text);
			Spinner userListSpinner = (Spinner) view.findViewById(R.id.delete_item_of_userlist_spinner);

			ColorStateList color = textAddItemToList.getTextColors().withAlpha(alphaValue);
			userListSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}
			textAddItemToList.setTextColor(textAddItemToList.getTextColors().withAlpha(alphaValue));
			textTo.setTextColor(textTo.getTextColors().withAlpha(alphaValue));
			editVariable.setTextColor(editVariable.getTextColors().withAlpha(alphaValue));
			editVariable.getBackground().setAlpha(alphaValue);

			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public Brick clone() {
		DeleteItemOfUserListBrick clonedBrick = new DeleteItemOfUserListBrick(getFormulaWithBrickField(BrickField.LIST_DELETE_ITEM).clone(), userList);
		return clonedBrick;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.LIST_DELETE_ITEM);
	}
}
