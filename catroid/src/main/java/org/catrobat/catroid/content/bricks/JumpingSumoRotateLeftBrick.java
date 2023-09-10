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
import org.catrobat.catroid.io.catlang.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils;

import androidx.annotation.NonNull;

@CatrobatLanguageBrick(command = "Turn Jumping Sumo")
public class JumpingSumoRotateLeftBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public JumpingSumoRotateLeftBrick() {
		addAllowedBrickField(BrickField.JUMPING_SUMO_ROTATE, R.id.brick_jumping_sumo_change_left_variable_edit_text, "degrees");
	}

	public JumpingSumoRotateLeftBrick(double degree) {
		this(new Formula(degree));
	}

	public JumpingSumoRotateLeftBrick(Formula formula) {
		this();
		setFormulaWithBrickField(BrickField.JUMPING_SUMO_ROTATE, formula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_jumping_sumo_rotate_left;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
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
				.append(" (direction: (left), ");
		appendCatrobatLanguageArguments(catrobatLanguage);
		catrobatLanguage.append(");\n");
		return catrobatLanguage.toString();
	}
}
