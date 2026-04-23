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
package org.catrobat.catroid.content.actions.conditional;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class GlideToAction extends TemporalAction {

	private float startXValue;
	private float startYValue;
	private float currentXValue;
	private float currentYValue;
	private Formula endX;
	private Formula endY;
	protected Scope scope;
	private Formula duration;
	private float durationValue;
	private float endXValue;
	private float endYValue;

	private float velocityXValue = 0;
	private float velocityYValue = 0;

	private boolean restart = false;

	@Override
	protected void begin() {
		Float durationInterpretation;
		Float endXInterpretation = 0f;
		Float endYInterpretation = 0f;

		try {
			durationInterpretation = duration == null ? Float.valueOf(0f)
					: duration.interpretFloat(scope);
		} catch (InterpretationException interpretationException) {
			durationInterpretation = 0f;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}

		try {
			endXInterpretation = endX == null ? Float.valueOf(0f) : endX.interpretFloat(scope);
		} catch (InterpretationException interpretationException) {
			durationInterpretation = 0f;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}

		try {
			endYInterpretation = endY == null ? Float.valueOf(0f) : endY.interpretFloat(scope);
		} catch (InterpretationException interpretationException) {
			durationInterpretation = 0f;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}

		if (!restart) {
			if (duration != null) {
				super.setDuration(durationInterpretation);
				durationValue = durationInterpretation;
			}
			endXValue = endXInterpretation;
			endYValue = endYInterpretation;
		}
		restart = false;

		startXValue = scope.getSprite().look.getXInUserInterfaceDimensionUnit();
		startYValue = scope.getSprite().look.getYInUserInterfaceDimensionUnit();
		currentXValue = startXValue;
		currentYValue = startYValue;
		if (startXValue == endXInterpretation && startYValue == endYInterpretation) {
			super.finish();
		}
		if (velocityXValue == 0 && velocityYValue == 0 && durationValue != 0) {
			velocityXValue = (endXValue - startXValue) / durationValue;
			velocityYValue = (endYValue - startYValue) / durationValue;
		}

		scope.getSprite().setGlidingVelocityX(velocityXValue);
		scope.getSprite().setGlidingVelocityY(velocityYValue);
		scope.getSprite().setGliding(true);
	}

	@Override
	protected void update(float percent) {
		float deltaX = scope.getSprite().look.getXInUserInterfaceDimensionUnit() - currentXValue;
		float deltaY = scope.getSprite().look.getYInUserInterfaceDimensionUnit() - currentYValue;
		if ((-0.1f > deltaX || deltaX > 0.1f) || (-0.1f > deltaY || deltaY > 0.1f)) {
			restart = true;
			setDuration(getDuration() - getTime());
			restart();
		} else {
			currentXValue = startXValue + (endXValue - startXValue) * percent;
			currentYValue = startYValue + (endYValue - startYValue) * percent;
			scope.getSprite().look.setPositionInUserInterfaceDimensionUnit(currentXValue, currentYValue);
		}
	}

	@Override
	protected void end() {
		scope.getSprite().setGliding(false);
		scope.getSprite().setGlidingVelocityX(0);
		scope.getSprite().setGlidingVelocityY(0);
	}

	public void setDuration(Formula duration) {
		this.duration = duration;
	}

	public void setPosition(Formula x, Formula y) {
		endX = x;
		endY = y;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
}
