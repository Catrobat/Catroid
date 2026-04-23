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

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class SetVelocityBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public SetVelocityBrick() {
		addAllowedBrickField(BrickField.PHYSICS_VELOCITY_X, R.id.brick_set_velocity_edit_text_x);
		addAllowedBrickField(BrickField.PHYSICS_VELOCITY_Y, R.id.brick_set_velocity_edit_text_y);
	}

	public SetVelocityBrick(Vector2 velocity) {
		this(new Formula(velocity.x), new Formula(velocity.y));
	}

	public SetVelocityBrick(Formula velocityX, Formula velocityY) {
		this();
		setFormulaWithBrickField(BrickField.PHYSICS_VELOCITY_X, velocityX);
		setFormulaWithBrickField(BrickField.PHYSICS_VELOCITY_Y, velocityY);
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(PHYSICS);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.PHYSICS_VELOCITY_X;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_physics_set_velocity;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetVelocityAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.PHYSICS_VELOCITY_X),
				getFormulaWithBrickField(BrickField.PHYSICS_VELOCITY_Y)));
	}
}
