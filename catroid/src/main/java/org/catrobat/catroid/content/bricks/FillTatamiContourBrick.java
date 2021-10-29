/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import java.util.ArrayList;

public class FillTatamiContourBrick extends SpinnerWithFormulaBrickType {

	public enum Direction {
		LEFT,
		RIGHT
	}

	public enum Style {
		RANDOM,
		REGULAR_8,
		REGULAR_10,
		REGULAR_12
	}

	public FillTatamiContourBrick() {
		addAllowedBrickField(BrickField.TATAMI_WIDTH, R.id.brick_fill_tatami_contour_edit_text);
	}

	public FillTatamiContourBrick(int width) {
		this(new Formula(width));
	}

	public FillTatamiContourBrick(Formula width) {
		this();
		ArrayList<Integer> directions = new ArrayList<>();
		directions.add(R.string.brick_tatami_direction_left);
		directions.add(R.string.brick_tatami_direction_right);
		addSpinner(R.id.brick_fill_tatami_contour_direction_spinner, directions);

		ArrayList<Integer> styles = new ArrayList<>();
		styles.add(R.string.brick_tatami_style_random);
		styles.add(R.string.brick_tatami_style_regular8);
		styles.add(R.string.brick_tatami_style_regular10);
		styles.add(R.string.brick_tatami_style_regular12);
		addSpinner(R.id.brick_fill_tatami_contour_style_spinner, styles);

		setFormulaWithBrickField(BrickField.TATAMI_WIDTH, width);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_fill_tatami_contour;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		int directionSelection = getSpinnerSelection(R.id.brick_fill_tatami_contour_direction_spinner);
		int styleSelection = getSpinnerSelection(R.id.brick_fill_tatami_contour_style_spinner);

		Direction direction = Direction.LEFT;
		Style style = Style.RANDOM;

		if (directionSelection == R.string.brick_tatami_direction_left) {
			direction = Direction.LEFT;
		} else if (directionSelection == R.string.brick_tatami_direction_right) {
			direction = Direction.RIGHT;
		}

		if (styleSelection == R.string.brick_tatami_style_random) {
			style = Style.RANDOM;
		} else if (styleSelection == R.string.brick_tatami_style_regular8) {
			style = Style.REGULAR_8;
		} else if (styleSelection == R.string.brick_tatami_style_regular10) {
			style = Style.REGULAR_10;
		} else if (styleSelection == R.string.brick_tatami_style_regular12) {
			style = Style.REGULAR_12;
		}

		sequence.addAction(sprite.getActionFactory().createFillTatamiContourAction(sprite, sequence,
				direction, style, getFormulaWithBrickField(BrickField.TATAMI_WIDTH)));
	}
}
