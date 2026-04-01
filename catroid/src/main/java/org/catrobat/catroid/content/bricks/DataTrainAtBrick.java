/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

public class DataTrainAtBrick extends VisualPlacementBrick {

	private static final long serialVersionUID = 1L;

	public DataTrainAtBrick() {
		addAllowedBrickField(BrickField.ASK_CHAT_QUESTION, R.id.brick_datatrain_at_edit_text_x);
		addAllowedBrickField(BrickField.RESPONSE_CHAT_QUESTION, R.id.brick_datatrain_at_edit_text_y);
	}

	public DataTrainAtBrick(String ASK_CHAT_QUESTION, String RESPONSE_CHAT_QUESTION) {
		this(new Formula(ASK_CHAT_QUESTION), new Formula(RESPONSE_CHAT_QUESTION));
	}

	public DataTrainAtBrick(Formula ASK_CHAT_QUESTION, Formula RESPONSE_CHAT_QUESTION) {
		this();
		setFormulaWithBrickField(BrickField.ASK_CHAT_QUESTION, ASK_CHAT_QUESTION);
		setFormulaWithBrickField(BrickField.RESPONSE_CHAT_QUESTION, RESPONSE_CHAT_QUESTION);
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.ASK_CHAT_QUESTION;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_data_train_at;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createTrainChatQuestionAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.ASK_CHAT_QUESTION),
				getFormulaWithBrickField(BrickField.RESPONSE_CHAT_QUESTION)));
	}

	@Override
	public BrickField getXBrickField() {
		return BrickField.ASK_CHAT_QUESTION;
	}

	@Override
	public BrickField getYBrickField() {
		return BrickField.RESPONSE_CHAT_QUESTION;
	}

	@Override
	public int getXEditTextId() {
		return R.id.brick_datatrain_at_edit_text_x;
	}

	@Override
	public int getYEditTextId() {
		return R.id.brick_datatrain_at_edit_text_y;
	}
}