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
package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserList;

import java.util.ArrayList;

public class InsertItemIntoUserListAction extends TemporalAction {

	private Scope scope;
	private Formula formulaIndexToInsert;
	private Formula formulaItemToInsert;

	private UserList userList;

	@Override
	protected void update(float percent) {
		if (userList == null) {
			return;
		}

		Object value = formulaItemToInsert == null ? Double.valueOf(0d)
				: formulaItemToInsert.interpretObject(scope);
		int indexToInsert;

		try {
			indexToInsert = formulaIndexToInsert == null ? 1
					: formulaIndexToInsert.interpretInteger(scope);
		} catch (InterpretationException interpretationException) {
			indexToInsert = 1;
		}

		indexToInsert--;

		if (indexToInsert > userList.getValue().size() || indexToInsert < 0) {
			return;
		}

		((ArrayList<Object>) userList.getValue()).add(indexToInsert, value);
	}

	public void setUserList(UserList userVariable) {
		this.userList = userVariable;
	}

	public void setFormulaIndexToInsert(Formula formulaIndexToInsert) {
		this.formulaIndexToInsert = formulaIndexToInsert;
	}

	public void setFormulaItemToInsert(Formula formulaItemToInsert) {
		this.formulaItemToInsert = formulaItemToInsert;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
}
