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

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.utils.ShowTextUtils;

import static org.catrobat.catroid.common.Constants.THINK_BRICK;

public class ThinkForBubbleBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public ThinkForBubbleBrick() {
		addAllowedBrickField(BrickField.STRING, R.id.brick_for_bubble_edit_text_text);
		addAllowedBrickField(BrickField.DURATION_IN_SECONDS, R.id.brick_for_bubble_edit_text_duration);
	}

	public ThinkForBubbleBrick(String text, float durationInSecondsValue) {
		this(new Formula(text), new Formula(durationInSecondsValue));
	}

	public ThinkForBubbleBrick(Formula text, Formula formula) {
		this();
		setFormulaWithBrickField(BrickField.STRING, text);
		setFormulaWithBrickField(BrickField.DURATION_IN_SECONDS, formula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_think_for_bubble;
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.STRING;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		setSecondsLabel(view, BrickField.DURATION_IN_SECONDS);
		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createThinkSayForBubbleAction(sprite,
				sequence, new ShowTextUtils.AndroidStringProvider(CatroidApplication.getAppContext()),
				getFormulaWithBrickField(BrickField.STRING), THINK_BRICK));
		sequence.addAction(sprite.getActionFactory().createWaitForBubbleBrickAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS)));
	}
}
