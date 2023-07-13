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

import android.view.View.OnClickListener;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.utils.ShowTextUtils;

import static org.catrobat.catroid.common.Constants.THINK_BRICK;

public class ThinkBubbleBrick extends FormulaBrick implements OnClickListener {

	private static final long serialVersionUID = 1L;

	public ThinkBubbleBrick() {
		addAllowedBrickField(BrickField.STRING, R.id.brick_bubble_edit_text);
	}

	public ThinkBubbleBrick(String text) {
		this(new Formula(text));
	}

	public ThinkBubbleBrick(Formula formula) {
		this();
		setFormulaWithBrickField(BrickField.STRING, formula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_think_bubble;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createThinkSayBubbleAction(sprite, sequence,
				new ShowTextUtils.AndroidStringProvider(CatroidApplication.getAppContext()), getFormulaWithBrickField(BrickField.STRING),
				THINK_BRICK));
	}
}
