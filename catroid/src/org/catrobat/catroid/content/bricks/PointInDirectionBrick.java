/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.List;

public class PointInDirectionBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public static enum Direction {
		RIGHT(90), LEFT(-90), UP(0), DOWN(180);

		private double directionDegrees;

		private Direction(double degrees) {
			directionDegrees = degrees;
		}

		public double getDegrees() {
			return directionDegrees;
		}
	}

	public PointInDirectionBrick() {
		addAllowedBrickField(BrickField.DEGREES);
	}

	public PointInDirectionBrick(Direction direction) {
		initializeBrickFields(new Formula(direction.getDegrees()));
	}

	public PointInDirectionBrick(Formula direction) {
		initializeBrickFields(direction);
	}

	public PointInDirectionBrick(double direction) {
		initializeBrickFields(new Formula(direction));
	}

	private void initializeBrickFields(Formula direction) {
		addAllowedBrickField(BrickField.DEGREES);
		setFormulaWithBrickField(BrickField.DEGREES, direction);
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.pointInDirection(sprite, getFormulaWithBrickField(BrickField.DEGREES)));
		return null;
	}
}
