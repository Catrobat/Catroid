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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class RaspiIfLogicBeginBrick extends IfLogicBeginBrick {

	private static final long serialVersionUID = 1L;

	public RaspiIfLogicBeginBrick() {
		super();
	}

	public RaspiIfLogicBeginBrick(Formula formula) {
		super(formula);
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(SOCKET_RASPI);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_raspi_if_begin_if;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		ScriptSequenceAction ifSequence = (ScriptSequenceAction) ActionFactory.createScriptSequenceAction(sequence.getScript());
		ScriptSequenceAction elseSequence = (ScriptSequenceAction) ActionFactory.createScriptSequenceAction(sequence.getScript());

		for (Brick brick : ifBranchBricks) {
			if (!brick.isCommentedOut()) {
				brick.addActionToSequence(sprite, ifSequence);
			}
		}

		for (Brick brick : elseBranchBricks) {
			if (!brick.isCommentedOut()) {
				brick.addActionToSequence(sprite, elseSequence);
			}
		}

		Action action = sprite.getActionFactory()
				.createRaspiIfLogicActionAction(sprite, sequence,
						getFormulaWithBrickField(BrickField.IF_CONDITION), ifSequence, elseSequence);

		sequence.addAction(action);
	}
}
