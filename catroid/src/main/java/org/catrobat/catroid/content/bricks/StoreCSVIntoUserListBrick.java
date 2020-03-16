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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;

public class StoreCSVIntoUserListBrick extends UserListBrick {

	private static final long serialVersionUID = 1L;

	public StoreCSVIntoUserListBrick() {
		addAllowedBrickField(BrickField.STORE_CSV_INTO_USERLIST_COLUMN,
				R.id.brick_store_csv_into_userlist_column_edit_text);
		addAllowedBrickField(BrickField.STORE_CSV_INTO_USERLIST_CSV,
				R.id.brick_store_csv_into_userlist_csv_edit_text);
	}

	public StoreCSVIntoUserListBrick(Formula userListFormulaColumnToInsert, Formula userListFormulaCSVToInsert, UserList userList) {
		this(userListFormulaColumnToInsert, userListFormulaCSVToInsert);
		this.userList = userList;
	}

	public StoreCSVIntoUserListBrick(Integer column, String csv) {
		this(new Formula(column), new Formula(csv));
	}

	public StoreCSVIntoUserListBrick(Formula userListFormulaColumnToInsert,
			Formula userListFormulaCSVToInsert) {
		this();
		setFormulaWithBrickField(BrickField.STORE_CSV_INTO_USERLIST_COLUMN,
				userListFormulaColumnToInsert);
		setFormulaWithBrickField(BrickField.STORE_CSV_INTO_USERLIST_CSV,
				userListFormulaCSVToInsert);
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.STORE_CSV_INTO_USERLIST_COLUMN;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_store_csv_into_userlist;
	}

	@Override
	protected int getSpinnerId() {
		return R.id.brick_store_csv_into_userlist_spinner;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createStoreCSVIntoUserListAction(sprite,
				getFormulaWithBrickField(BrickField.STORE_CSV_INTO_USERLIST_COLUMN),
				getFormulaWithBrickField(BrickField.STORE_CSV_INTO_USERLIST_CSV), userList));
	}
}
