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
package org.catrobat.catroid.content.bricks;

import android.widget.AdapterView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;

import java.util.List;

public class AddItemToUserListBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;
	private UserList userList;

	public AddItemToUserListBrick() {
		addAllowedBrickField(BrickField.LIST_ADD_ITEM);
	}

	public AddItemToUserListBrick(Formula userListFormula, UserList userList) {
		initializeBrickFields(userListFormula);
		this.userList = userList;
	}

	public AddItemToUserListBrick(double value) {
		initializeBrickFields(new Formula(value));
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.addItemToUserList(sprite, getFormulaWithBrickField(BrickField.LIST_ADD_ITEM), userList));
		return null;
	}

	@Override
	public Brick clone() {
		AddItemToUserListBrick clonedBrick = new AddItemToUserListBrick(getFormulaWithBrickField(BrickField.LIST_ADD_ITEM).clone(), userList);
		return clonedBrick;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject == null) {
			throw new RuntimeException("The current project must be set before cloning it");
		}

		AddItemToUserListBrick copyBrick = (AddItemToUserListBrick) clone();
		copyBrick.userList = currentProject.getDataContainer().getUserList(userList.getName(), sprite);
		return copyBrick;
	}

	private void initializeBrickFields(Formula listAddItemFormula) {
		addAllowedBrickField(BrickField.LIST_ADD_ITEM);
		setFormulaWithBrickField(BrickField.LIST_ADD_ITEM, listAddItemFormula);
	}

	public void setUserList(UserList userList) {
		this.userList = userList;
	}

	public UserList getUserList() {
		return userList;
	}
}
