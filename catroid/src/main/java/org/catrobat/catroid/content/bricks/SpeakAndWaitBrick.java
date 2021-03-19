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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.actions.SpeakAction;
import org.catrobat.catroid.formulaeditor.Formula;

import java.io.File;

public class SpeakAndWaitBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private File speechFile = null;

	public SpeakAndWaitBrick() {
		addAllowedBrickField(BrickField.SPEAK, R.id.brick_speak_and_wait_edit_text);
	}

	public SpeakAndWaitBrick(String text) {
		this(new Formula(text));
	}

	public SpeakAndWaitBrick(Formula formula) {
		this();
		setFormulaWithBrickField(BrickField.SPEAK, formula);
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(TEXT_TO_SPEECH);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_speak_and_wait;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {

		Formula text = getFormulaWithBrickField(BrickField.SPEAK);
		sequence.addAction(sprite.getActionFactory()
				.createSpeakAction(sprite, sequence, text));

		sequence.addAction(sprite.getActionFactory().createWaitForSoundAction(sprite, sequence,
				new Formula(getDurationOfSpokenText(sprite, sequence,
						text)), speechFile.getAbsolutePath()));
	}

	private float getDurationOfSpokenText(Sprite sprite, SequenceAction sequence, Formula text) {

		SpeakAction action = (SpeakAction) sprite.getActionFactory()
				.createSpeakAction(sprite, sequence, text);
		action.setDetermineLength(true);

		action.act(1.0f);
		speechFile = action.getSpeechFile();
		return action.getLengthOfText() / 1000;
	}
}
