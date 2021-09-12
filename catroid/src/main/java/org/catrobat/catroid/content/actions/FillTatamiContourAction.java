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

package org.catrobat.catroid.content.actions;

import android.util.Log;
import android.util.Pair;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.FillTatamiContourBrick;
import org.catrobat.catroid.embroidery.DSTStitchCommand;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.stage.StageActivity;

import java.util.List;

public class FillTatamiContourAction extends TemporalAction {

	private FillTatamiContourBrick.Direction direction;
	private FillTatamiContourBrick.Style style;
	private Formula width;
	private Scope scope;

	@Override
	protected void update(float percent) {
		if (scope == null) {
			return;
		}
		Sprite sprite = scope.getSprite();

		List<Pair<Float, Float>> coordinates = sprite.getTatamiContour().getCoordinates();

		float widthInterpretation = 0;
		try {
			if (width != null) {
				widthInterpretation = width.interpretFloat(scope);
			}
		} catch (InterpretationException interpretationException) {
			widthInterpretation = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}

		for (int i = 0; i < coordinates.size() - 1; i++) {
			interpolateStitches(coordinates.get(i).first, coordinates.get(i).second,
					coordinates.get(i + 1).first, coordinates.get(i + 1).second);
		}

		interpolateStitches(coordinates.get(coordinates.size() - 1).first,
				coordinates.get(coordinates.size() - 1).second, coordinates.get(0).first, coordinates.get(0).second);

		System.out.println(coordinates);
	}

	private void interpolateStitches(float startX, float startY, float endX, float endY) {
		Sprite sprite = scope.getSprite();

		StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(startX, startY,
				sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));

		double distance = Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
		int interpolationCount = (int) distance / 10;

		for (int count = 1; count <= interpolationCount; count++) {
			float splitFactor = (float) count / interpolationCount;
			float x = interpolate(startX, endX, splitFactor);
			float y = interpolate(startY, endY, splitFactor);
			StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(x, y,
					sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
		}
	}

	private float interpolate(float endValue, float startValue, float percentage) {
		return (float) Math.round(startValue + percentage * (endValue - startValue));
	}

	public void setDirection(FillTatamiContourBrick.Direction direction) {
		this.direction = direction;
	}

	public void setStyle(FillTatamiContourBrick.Style style) {
		this.style = style;
	}

	public void setWidth(Formula width) {
		this.width = width;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
}
