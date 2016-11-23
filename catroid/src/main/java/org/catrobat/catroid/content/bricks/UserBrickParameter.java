/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

public class UserBrickParameter extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private UserScriptDefinitionBrickElement element;

	private transient TextView textView;
	private transient TextView prototypeView;

	private transient UserBrick parent;

	public UserBrickParameter(UserBrick parent, UserScriptDefinitionBrickElement element) {
		this.parent = parent;
		this.element = element;
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
		if (textView != null) {
			clonedBrick.getFormulaWithBrickField(BrickField.USER_BRICK).setTextFieldId(textView.getId());
		}
		clonedBrick.parent = parent;
		clonedBrick.element = element;
		clonedBrick.textView = textView;
		clonedBrick.prototypeView = prototypeView;
		return clonedBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		return parent.getView(context, brickId, adapter);
	}

	@Override
	public View getPrototypeView(Context context) {
		return null;
	}

	@Override
	public java.util.List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentScene().getDataContainer();
		String variableName = element.getText();

		sequence.addAction(sprite.getActionFactory().createSetVariableAction(sprite,
				getFormulaWithBrickField(BrickField.VARIABLE), dataContainer.getUserVariable(variableName, parent, sprite)));
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.USER_BRICK);
	}

	public void setParent(UserBrick parent) {
		this.parent = parent;
	}

	public TextView getTextView() {
		return textView;
	}

	public TextView getPrototypeView() {
		return prototypeView;
	}

	public UserScriptDefinitionBrickElement getElement() {
		return element;
	}

	public void setTextView(TextView textView) {
		this.textView = textView;
	}

	public void setPrototypeView(TextView prototypeView) {
		this.prototypeView = prototypeView;
	}

	public UserBrick getParent() {
		return parent;
	}

	public void setElement(UserScriptDefinitionBrickElement element) {
		this.element = element;
	}
}
