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
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;

import java.util.List;

public class UserBrickParameter extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private UserScriptDefinitionBrickElement element;

	private transient TextView prototypeView;

	private transient UserBrick parent;

	public UserBrickParameter() {
	}

	@Override
	public int getViewResource() {
		return parent.getViewResource();
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		return parent.getView(context);
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer();
		String variableName = element.getText();

		sequence.addAction(sprite.getActionFactory()
				.createSetVariableAction(sprite, getFormulaWithBrickField(BrickField.VARIABLE),
						dataContainer.getUserVariable(sprite, parent, variableName)));
		return null;
	}

	public TextView getPrototypeView() {
		return prototypeView;
	}

	public void setPrototypeView(TextView prototypeView) {
		this.prototypeView = prototypeView;
	}

	public UserScriptDefinitionBrickElement getElement() {
		return element;
	}

	public void setElement(UserScriptDefinitionBrickElement element) {
		this.element = element;
	}

	public UserBrick getParent() {
		return parent;
	}

	public void setParent(UserBrick parent) {
		this.parent = parent;
	}
}
