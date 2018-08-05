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
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.adapter.UserListAdapterWrapper;

import java.util.List;

public class ReplaceItemInUserListBrick extends UserListBrick {

	private static final long serialVersionUID = 1L;

	public ReplaceItemInUserListBrick(double value, Integer indexToReplace) {
		this(new Formula(value), new Formula(indexToReplace));
	}

	public ReplaceItemInUserListBrick(Formula userListFormulaValueToInsert, Formula userListFormulaIndexToReplace, UserList userList) {
		this(userListFormulaValueToInsert, userListFormulaIndexToReplace);
		this.userList = userList;
	}

	public ReplaceItemInUserListBrick(Formula userListFormulaValueToInsert, Formula userListFormulaIndexToReplace) {
		addAllowedBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_VALUE, R.id.brick_replace_item_in_userlist_value_edit_text);
		addAllowedBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_INDEX, R.id.brick_replace_item_in_userlist_at_index_edit_text);
		setFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_VALUE, userListFormulaValueToInsert);
		setFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_INDEX, userListFormulaIndexToReplace);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_replace_item_in_userlist;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		Spinner userListSpinner = prototypeView.findViewById(R.id.replace_item_in_userlist_spinner);

		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer()
				.createDataAdapter(context, ProjectManager.getInstance().getCurrentSprite());

		UserListAdapterWrapper userListAdapterWrapper = new UserListAdapterWrapper(context, dataAdapter);

		userListAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);
		userListSpinner.setAdapter(userListAdapterWrapper);
		setSpinnerSelection(userListSpinner, null);

		return prototypeView;
	}

	@Override
	public View getView(final Context context) {
		super.getView(context);

		Spinner userListSpinner = view.findViewById(R.id.replace_item_in_userlist_spinner);
		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer()
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
	public void onNewList(UserList userList) {
		Spinner spinner = view.findViewById(R.id.replace_item_in_userlist_spinner);
		setSpinnerSelection(spinner, userList);
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createReplaceItemInUserListAction(sprite,
				getFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_INDEX),
				getFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_VALUE), userList));
		return null;
	}
}
