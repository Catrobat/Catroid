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

import android.view.View;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.LinkedList;
import java.util.List;

public class RaspiIfLogicBeginBrick extends IfLogicBeginBrick {

	private static final long serialVersionUID = 1L;

	public RaspiIfLogicBeginBrick() {
	}

	public RaspiIfLogicBeginBrick(int condition) {
		super(condition);
	}

	@Override
	public int getRequiredResources() {
		return SOCKET_RASPI;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_raspi_if_begin_if;
	}

	@Override
	public void onViewCreated(View view) {
		TextView ifBeginTextView = view.findViewById(R.id.brick_raspi_if_begin_edit_text);
		getFormulaWithBrickField(BrickField.IF_CONDITION).setTextFieldId(R.id.brick_raspi_if_begin_edit_text);
		getFormulaWithBrickField(BrickField.IF_CONDITION).refreshTextField(view);
		ifBeginTextView.setOnClickListener(this);
	}

	@Override
	public void onPrototypeViewCreated(View prototypeView) {
		TextView textIfBegin = prototypeView.findViewById(R.id.brick_raspi_if_begin_edit_text);
		textIfBegin.setText(formatNumberForPrototypeView(BrickValues.RASPI_DIGITAL_INITIAL_PIN_NUMBER));
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		ScriptSequenceAction ifAction = (ScriptSequenceAction) ActionFactory.eventSequence(sequence.getScript());
		ScriptSequenceAction elseAction = (ScriptSequenceAction) ActionFactory.eventSequence(sequence.getScript());

		Action action = sprite.getActionFactory().createRaspiIfLogicActionAction(sprite,
				getFormulaWithBrickField(BrickField.IF_CONDITION), ifAction, elseAction);

		sequence.addAction(action);

		LinkedList<ScriptSequenceAction> returnActionList = new LinkedList<>();
		returnActionList.add(elseAction);
		returnActionList.add(ifAction);

		return returnActionList;
	}
}
