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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class SetVariableAction extends TemporalAction {

	private Sprite sprite;
	private Formula changeVariable;
	private UserVariable userVariable;

	@Override
	protected void update(float percent) {
		if (userVariable == null) {
			return;
		}
		Object value = changeVariable == null ? Double.valueOf(0d) : changeVariable.interpretObject(sprite);

		boolean isFirstLevelStringTree = false;
		if (changeVariable != null && changeVariable.getRoot().getElementType() == FormulaElement.ElementType.STRING) {
			isFirstLevelStringTree = true;
		}

		try {
			if (!isFirstLevelStringTree && value instanceof String) {
				value = Double.valueOf((String) value);
			}
		} catch (NumberFormatException numberFormatException) {
			Log.d(getClass().getSimpleName(), "Couldn't parse String", numberFormatException);
		}

		userVariable.setValue(value);
	}

	public void setUserVariable(UserVariable userVariable) {
		if (userVariable == null) {
			return;
		}
		this.userVariable = userVariable;
	}

	public void setChangeVariable(Formula changeVariable) {
		this.changeVariable = changeVariable;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
