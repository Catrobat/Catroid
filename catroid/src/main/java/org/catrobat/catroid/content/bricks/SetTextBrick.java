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

import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class SetTextBrick extends FormulaBrick implements View.OnClickListener {

	private static final long serialVersionUID = 1L;

	public SetTextBrick() {
		addAllowedBrickField(BrickField.X_DESTINATION, R.id.brick_set_text_edit_text_x);
		addAllowedBrickField(BrickField.Y_DESTINATION, R.id.brick_set_text_edit_text_y);
		addAllowedBrickField(BrickField.STRING, R.id.brick_set_text_edit_text);
	}

	public SetTextBrick(int xDestinationValue, int yDestinationValue, String text) {
		this(new Formula(xDestinationValue), new Formula(yDestinationValue), new Formula(text));
	}

	public SetTextBrick(Formula xDestination, Formula yDestination, Formula text) {
		this();
		setFormulaWithBrickField(BrickField.X_DESTINATION, xDestination);
		setFormulaWithBrickField(BrickField.Y_DESTINATION, yDestination);
		setFormulaWithBrickField(BrickField.STRING, text);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_set_text;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory()
				.createSetTextAction(sprite, sequence, getFormulaWithBrickField(BrickField.X_DESTINATION),
						getFormulaWithBrickField(BrickField.Y_DESTINATION),
						getFormulaWithBrickField(BrickField.STRING)));
	}
}
