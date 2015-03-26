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

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

public class UserBrickParameter extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public int parameterIndex;
	public String variableName;
	public transient TextView textView;
	public transient TextView prototypeView;

	private transient UserBrick parent;

	public UserBrickParameter(UserBrick parent) {
		this.parent = parent;
		addAllowedBrickField(BrickField.USER_BRICK);
		setFormulaWithBrickField(BrickField.USER_BRICK, new Formula(0));
	}

	public UserBrickParameter(Formula parameter) {
		addAllowedBrickField(BrickField.USER_BRICK);
		setFormulaWithBrickField(BrickField.USER_BRICK, parameter);
	}

	@Override
	public UserBrickParameter clone() {
		UserBrickParameter clonedBrick = new UserBrickParameter(getFormulaWithBrickField(
				BrickField.USER_BRICK).clone());
		clonedBrick.getFormulaWithBrickField(BrickField.USER_BRICK).setTextFieldId(textView.getId());
		clonedBrick.parent = parent;
		clonedBrick.parameterIndex = parameterIndex;
		clonedBrick.variableName = variableName;
		clonedBrick.textView = textView;
		clonedBrick.prototypeView = prototypeView;
		return clonedBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		return parent.getView(context, brickId, adapter);
	}

	@Override
	public java.util.List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.setVariable(sprite, getFormulaWithBrickField(BrickField.VARIABLE),
				ProjectManager.getInstance().getCurrentProject().getDataContainer().getUserVariable(variableName, sprite)));
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.USER_BRICK);
	}
}
