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

public class SetGravityBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public SetGravityBrick() {
		addAllowedBrickField(BrickField.PHYSICS_GRAVITY_X, R.id.brick_set_gravity_edit_text_x);
		addAllowedBrickField(BrickField.PHYSICS_GRAVITY_Y, R.id.brick_set_gravity_edit_text_y);
	}

	public SetGravityBrick(Vector2 gravity) {
		this(new Formula(gravity.x), new Formula(gravity.y));
	}

	public SetGravityBrick(Formula gravityX, Formula gravityY) {
		addAllowedBrickField(BrickField.PHYSICS_GRAVITY_X, R.id.brick_set_gravity_edit_text_x);
		addAllowedBrickField(BrickField.PHYSICS_GRAVITY_Y, R.id.brick_set_gravity_edit_text_y);
		setFormulaWithBrickField(BrickField.PHYSICS_GRAVITY_X, gravityX);
		setFormulaWithBrickField(BrickField.PHYSICS_GRAVITY_Y, gravityY);
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(PHYSICS);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_physics_set_gravity;
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.PHYSICS_GRAVITY_X;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetGravityAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.PHYSICS_GRAVITY_X),
				getFormulaWithBrickField(BrickField.PHYSICS_GRAVITY_Y)));
	}
}
