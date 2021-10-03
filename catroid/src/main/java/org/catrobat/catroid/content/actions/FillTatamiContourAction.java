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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.FillTatamiContourBrick;
import org.catrobat.catroid.embroidery.DSTStitchCommand;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.stage.StageActivity;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.catrobat.catroid.common.ScreenValues.SCREEN_WIDTH;

public class FillTatamiContourAction extends TemporalAction {

	private FillTatamiContourBrick.Direction direction;
	private FillTatamiContourBrick.Style style;
	private List<Pair<Float, Float>> listOfAllCoordinates = new ArrayList<Pair<Float, Float>>();
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

		sprite.setEmbroideryThreadColor(Color.RED);

		for (int i = 0; i < coordinates.size() - 1; i++) {
			interpolateStitches(coordinates.get(i).first, coordinates.get(i).second,
					coordinates.get(i + 1).first, coordinates.get(i + 1).second);
		}
		interpolateStitches(coordinates.get(coordinates.size() - 1).first,
				coordinates.get(coordinates.size() - 1).second, coordinates.get(0).first, coordinates.get(0).second);

		sprite.setEmbroideryThreadColor(Color.BLUE);

		sortAllCoordinates();

		fillContour();
	}

	private void interpolateStitches(float startX, float startY, float endX, float endY) {
		Sprite sprite = scope.getSprite();

		StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(startX, startY,
				sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));

		double distance = Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));

		int interpolationCount = (int) distance;

		for (int count = 0; count < interpolationCount; count++) {
			float splitFactor = (float) count / interpolationCount;
			float x = interpolate(startX, endX, splitFactor);
			float y = interpolate(startY, endY, splitFactor);
			listOfAllCoordinates.add(new Pair<>(x, y));
			if(count % 10 == 0) {
				StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(x, y,
						sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
			}
		}
	}

	private float interpolate(float endValue, float startValue, float percentage) {
		return (float) Math.round(startValue + percentage * (endValue - startValue));
	}

	//TODO remove comments
	/*
	This method needs the list of all coordinates to be sorted by higher to lower y and then from
	lower to higher x. As done by the sortAllCoordinates method. It iterates over all relevant
	screen pixels starting from the top of the screen and working down row by row.
	The insideContour boolean keeps track of the current fill status. If true we are currently
	inside the shape and need to set stitches to fill it. The lastContained boolean indicates
	when true if the last processed point was already an edge.
	Whenever the y value of the first point is greater than the current y value it can safely
	removed because it is irrelevant. The same holds true whenever the x value is too low.
	 */
	private void fillContour() {
		Sprite sprite = scope.getSprite();

		for (int y = Math.round(listOfAllCoordinates.get(0).second);
				y > Math.round(listOfAllCoordinates.get(listOfAllCoordinates.size() - 1).second); y--) {

			//Remove irrelevant y values
			while (y < listOfAllCoordinates.get(0).second) {
				listOfAllCoordinates.remove(0);
			}

			boolean insideContour = false;
			boolean lastContained = false;
			for (int x = -SCREEN_WIDTH / 2; x < SCREEN_WIDTH / 2; x++) {
				if (listOfAllCoordinates.size() == 0) {
					break;
				}

				if (listOfAllCoordinates.get(0).second < y) {
					//Whenever this is true the next point is already in the next line so we can
					// break out of the x for loop
					break;
				} else {
					//Remove irrelevant x values
					while (listOfAllCoordinates.get(0).second == y &&
							x > listOfAllCoordinates.get(0).first) {
						listOfAllCoordinates.remove(0);
					}
				}

				if (listOfAllCoordinates.get(0).equals(new Pair<>((float) x, (float) y))) {
					if (lastContained) {
						//When the last was already contained we are at an edge and not in contour
						insideContour = false;
					} else {
						//Otherwise we are in the contour
						insideContour ^= true;
					}
					lastContained = true;
					//Remove the first element of the sorted list because we processed it
					listOfAllCoordinates.remove(0);
				} else {
					lastContained = false;
				}

				if (insideContour) {
					StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(x, y,
							sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
				}
			}
			if (listOfAllCoordinates.size() == 0) {
				break;
			}
		}
	}

	private void sortAllCoordinates() {
		for (int i = 0; i < listOfAllCoordinates.size(); i++) {
			for (int j = 0; j < listOfAllCoordinates.size(); j++) {
				if (comparePairs(listOfAllCoordinates.get(i), listOfAllCoordinates.get(j)) > 0) {
					Collections.swap(listOfAllCoordinates, i, j);
				}
			}
		}
	}

	private int comparePairs(Pair<Float, Float> pair1, Pair<Float, Float> pair2) {
		int result = Float.compare(pair1.second, pair2.second);
		if (result == 0) {
			result = -Float.compare(pair1.first, pair2.first);
		}
		return result;
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
