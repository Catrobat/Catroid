/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

public class ReplaceItemInUserListBrick extends UserListBrick {

	private static final long serialVersionUID = 1L;

	public ReplaceItemInUserListBrick() {
		addAllowedBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_VALUE,
				R.id.brick_replace_item_in_userlist_value_edit_text);
		addAllowedBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_INDEX,
				R.id.brick_replace_item_in_userlist_at_index_edit_text);
	}

	public ReplaceItemInUserListBrick(double value, Integer indexToReplace) {
		this(new Formula(value), new Formula(indexToReplace));
	}

	public ReplaceItemInUserListBrick(Formula valueFormula, Formula indexFormula, UserList userList) {
		this(valueFormula, indexFormula);
		this.userList = userList;
	}

	public ReplaceItemInUserListBrick(Formula valueFormula, Formula indexFormula) {
		this();
		setFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_VALUE, valueFormula);
		setFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_INDEX, indexFormula);
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.REPLACE_ITEM_IN_USERLIST_INDEX;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_replace_item_in_userlist;
	}

	@Override
	protected int getSpinnerId() {
		return R.id.replace_item_in_userlist_spinner;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createReplaceItemInUserListAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_INDEX),
				getFormulaWithBrickField(BrickField.REPLACE_ITEM_IN_USERLIST_VALUE), userList));
	}
}
