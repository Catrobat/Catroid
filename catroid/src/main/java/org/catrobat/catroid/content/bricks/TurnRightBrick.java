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
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;

@CatrobatLanguageBrick(command = "Turn")
public class TurnRightBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public TurnRightBrick() {
		addAllowedBrickField(BrickField.TURN_RIGHT_DEGREES, R.id.brick_turn_right_edit_text, "degrees");
	}

	public TurnRightBrick(double degreesValue) {
		this(new Formula(degreesValue));
	}

	public TurnRightBrick(Formula formula) {
		addAllowedBrickField(BrickField.TURN_RIGHT_DEGREES, R.id.brick_turn_right_edit_text, "degrees");
		setFormulaWithBrickField(BrickField.TURN_RIGHT_DEGREES, formula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_turn_right;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory()
				.createTurnRightAction(sprite, sequence,
						getFormulaWithBrickField(BrickField.TURN_RIGHT_DEGREES)));
	}

	@NonNull
	@Override
	public String serializeToCatrobatLanguage(int indentionLevel) {
		String indention = CatrobatLanguageUtils.getIndention(indentionLevel);

		StringBuilder catrobatLanguage = new StringBuilder(60);
		catrobatLanguage.append(indention);

		if (commentedOut) {
			catrobatLanguage.append("// ");
		}

		catrobatLanguage.append(getCatrobatLanguageCommand())
				.append(" (direction: (right), ");
		appendCatrobatLanguageArguments(catrobatLanguage);
		catrobatLanguage.append(");\n");
		return catrobatLanguage.toString();
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add("direction");
		return requiredArguments;
	}
}
