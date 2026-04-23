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

public class RaspiSendDigitalValueBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public RaspiSendDigitalValueBrick() {
		addAllowedBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER, R.id.brick_raspi_set_digital_pin_edit_text);
		addAllowedBrickField(BrickField.RASPI_DIGITAL_PIN_VALUE, R.id.brick_raspi_set_digital_value_edit_text);
	}

	public RaspiSendDigitalValueBrick(int pinNumber, int pinValue) {
		this(new Formula(pinNumber), new Formula(pinValue));
	}

	public RaspiSendDigitalValueBrick(Formula pinNumber, Formula pinValue) {
		this();
		setFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER, pinNumber);
		setFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_VALUE, pinValue);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_raspi_send_digital;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(SOCKET_RASPI);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.RASPI_DIGITAL_PIN_NUMBER;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSendDigitalRaspiValueAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_NUMBER),
				getFormulaWithBrickField(BrickField.RASPI_DIGITAL_PIN_VALUE)));
	}
}
