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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.adapter.UserListAdapterWrapper;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class ReplaceItemInUserListBrick extends UserListBrick {

	private static final long serialVersionUID = 1L;

	public ReplaceItemInUserListBrick(Formula userListFormulaValueToInsert, Formula userListFormulaIndexToReplace, UserList userList) {
		initializeBrickFields(userListFormulaValueToInsert, userListFormulaIndexToReplace);
		this.userList = userList;
	}

	public ReplaceItemInUserListBrick(double value, Integer indexToReplace) {
		initializeBrickFields(new Formula(value), new Formula(indexToReplace));
	}

	private void initializeBrickFields(Formula userListFormulaValueToInsert, Formula userListFormulaIndexToReplace) {
		addAllowedBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_VALUE);
		addAllowedBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_INDEX);
		setFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_VALUE, userListFormulaValueToInsert);
		setFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_INDEX, userListFormulaIndexToReplace);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createReplaceItemInUserListAction(sprite,
				getFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_INDEX),
				getFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_VALUE), userList));
		return null;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_replace_item_in_userlist, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_replace_item_in_userlist_checkbox);
		TextView textFieldValue = (TextView) view.findViewById(R.id.brick_replace_item_in_userlist_value_edit_text);
		getFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_VALUE).setTextFieldId(R.id.brick_replace_item_in_userlist_value_edit_text);
		getFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_VALUE).refreshTextField(view);
		textFieldValue.setOnClickListener(this);

		TextView textFieldIndex = (TextView) view.findViewById(R.id.brick_replace_item_in_userlist_at_index_edit_text);
		getFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_INDEX).setTextFieldId(R.id.brick_replace_item_in_userlist_at_index_edit_text);
		getFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_INDEX).refreshTextField(view);
		textFieldIndex.setOnClickListener(this);

		Spinner userListSpinner = (Spinner) view.findViewById(R.id.replace_item_in_userlist_spinner);
		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentScene().getDataContainer()
				.createDataAdapter(context, ProjectManager.getInstance().getCurrentSprite());
		UserListAdapterWrapper userListAdapterWrapper = new UserListAdapterWrapper(context, dataAdapter);
		userListAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);

		userListSpinner.setAdapter(userListAdapterWrapper);

		setSpinnerSelection(userListSpinner, null);

		userListSpinner.setOnTouchListener(createSpinnerOnTouchListener());
		userListSpinner.setOnItemSelectedListener(createListSpinnerItemSelectedListener());
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_replace_item_in_userlist, null);
		Spinner userListSpinner = (Spinner) prototypeView.findViewById(R.id.replace_item_in_userlist_spinner);

		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentScene().getDataContainer()
				.createDataAdapter(context, ProjectManager.getInstance().getCurrentSprite());

		UserListAdapterWrapper userListAdapterWrapper = new UserListAdapterWrapper(context, dataAdapter);

		userListAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);
		userListSpinner.setAdapter(userListAdapterWrapper);
		setSpinnerSelection(userListSpinner, null);

		TextView textViewValueToInsert = (TextView) prototypeView
				.findViewById(R.id.brick_replace_item_in_userlist_value_edit_text);
		textViewValueToInsert.setText(formatNumberForPrototypeView(BrickValues.REPLACE_ITEM_IN_USERLIST_VALUE));
		TextView textViewInsertIndex = (TextView) prototypeView
				.findViewById(R.id.brick_replace_item_in_userlist_at_index_edit_text);
		textViewInsertIndex.setText(formatNumberForPrototypeView(BrickValues.REPLACE_ITEM_IN_USERLIST_INDEX));

		return prototypeView;
	}

	@Override
	public void onNewList(UserList userList) {
		Spinner spinner = view.findViewById(R.id.replace_item_in_userlist_spinner);
		setSpinnerSelection(spinner, userList);
	}

	@Override
	public Brick clone() {
		ReplaceItemInUserListBrick clonedBrick = new ReplaceItemInUserListBrick(getFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_VALUE).clone(), getFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_INDEX).clone(), userList);
		clonedBrick.setBackPackedData(new BackPackedListData(backPackedData));
		return clonedBrick;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_replace_item_in_userlist_value_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.REPLACE_ITEM_IN_USERLIST_VALUE);
				break;

			case R.id.brick_replace_item_in_userlist_at_index_edit_text:
			default:
				FormulaEditorFragment.showFragment(view, this, BrickField.REPLACE_ITEM_IN_USERLIST_INDEX);
				break;
		}
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
		super.updateUserListReference(into, from);
	}
}
