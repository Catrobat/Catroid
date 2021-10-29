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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.FillTatamiContourBrick;
import org.catrobat.catroid.embroidery.DSTStitchCommand;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.stage.StageActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FillTatamiContourAction extends TemporalAction {

	private FillTatamiContourBrick.Direction direction;
	private FillTatamiContourBrick.Style style;
	private List<Pair<Integer, Integer>> listOfAllContourCoordinates = new ArrayList<Pair<Integer, Integer>>();
	private Formula width;
	private Scope scope;
	private Sprite sprite;
	private final int contourStitchGap = 15;
	private final int zigZagSize = 16;

	private interface StyleFunction {
		int getStyle(boolean randomizeOffsetForFirstInLine);
	}

	private static class Random6To14 implements StyleFunction {
		@Override
		public int getStyle(boolean randomizeOffsetForFirstInLine) {
			Random random = new Random();
			if (randomizeOffsetForFirstInLine) {
				return random.nextInt(10) + 1;
			}
			random = new Random();
			return random.nextInt(5) + 6;
		}
	}

	private static class Regular8 implements StyleFunction {
		@Override
		public int getStyle(boolean randomizeOffsetForFirstInLine) {
			if (randomizeOffsetForFirstInLine) {
				Random random = new Random();
				return random.nextInt(8) + 1;
			}
			return 8;
		}
	}

	private static class Regular10 implements StyleFunction {
		@Override
		public int getStyle(boolean randomizeOffsetForFirstInLine) {
			if (randomizeOffsetForFirstInLine) {
				Random random = new Random();
				return random.nextInt(10) + 1;
			}
			return 10;
		}
	}

	private static class Regular12 implements StyleFunction {
		@Override
		public int getStyle(boolean randomizeOffsetForFirstInLine) {
			if (randomizeOffsetForFirstInLine) {
				Random random = new Random();
				return random.nextInt(12) + 1;
			}
			return 12;
		}
	}

	@Override
	protected void update(float percent) {
		if (scope == null) {
			return;
		}
		sprite = scope.getSprite();

		List<Pair<Integer, Integer>> coordinates = sprite.getTatamiContour().getCoordinates();
		if (coordinates.size() < 1) {
			return;
		}

		int widthInterpretation = 1;
		try {
			if (width != null) {
				widthInterpretation = width.interpretInteger(scope);
				if (widthInterpretation < 1) {
					return;
				}
			}
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
			return;
		}

		for (int i = 0; i < coordinates.size() - 1; i++) {
			interpolateStitches(coordinates.get(i).first, coordinates.get(i).second,
					coordinates.get(i + 1).first, coordinates.get(i + 1).second);
		}
		interpolateStitches(coordinates.get(coordinates.size() - 1).first,
				coordinates.get(coordinates.size() - 1).second, coordinates.get(0).first, coordinates.get(0).second);

		if (listOfAllContourCoordinates.size() < 1) {
			return;
		}

		sortAllCoordinates();
		Pair<Integer, Integer> minValues = getMinValues(listOfAllContourCoordinates);
		Pair<Integer, Integer> maxValues = getMaxValues(listOfAllContourCoordinates);

		List<Pair<Integer, Integer>> insideContourCoordinates = generateInsideCoordinates(minValues, maxValues);
		addUnderlayStitch(insideContourCoordinates, minValues, maxValues);

		boolean directionLeftToRight = false;
		if (direction == FillTatamiContourBrick.Direction.LEFT) {
			directionLeftToRight = true;
		}
		StyleFunction styleFunction = new Random6To14();
		if (style == FillTatamiContourBrick.Style.REGULAR_8) {
			styleFunction = new Regular8();
		} else if (style == FillTatamiContourBrick.Style.REGULAR_10) {
			styleFunction = new Regular10();
		} else if (style == FillTatamiContourBrick.Style.REGULAR_12) {
			styleFunction = new Regular12();
		}
		fillContour(insideContourCoordinates, directionLeftToRight, maxValues, widthInterpretation, styleFunction);
	}

	private void interpolateStitches(int startX, int startY, int endX, int endY) {
		Sprite sprite = scope.getSprite();

		StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(startX, startY,
				sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));

		double distance = Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));

		int interpolationCount = (int) distance;

		for (int count = 0; count < interpolationCount; count++) {
			float splitFactor = (float) count / interpolationCount;
			int x = interpolate(startX, endX, splitFactor);
			int y = interpolate(startY, endY, splitFactor);
			listOfAllContourCoordinates.add(new Pair<>(x, y));
			if (count % contourStitchGap == 0) {
				StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(x, y,
						sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
			}
		}

		List<Pair<Integer, Integer>> originalList = new ArrayList<>(listOfAllContourCoordinates);
		for (int i = 0; i < originalList.size(); i++) {
			Pair<Integer, Integer> pairToRemove = new Pair<>(originalList.get(i).first - 1, originalList.get(i).second);
			if (originalList.contains(pairToRemove)) {
				listOfAllContourCoordinates.remove(pairToRemove);
			}
		}
	}

	private int interpolate(int endValue, int startValue, float percentage) {
		return Math.round(startValue + percentage * (endValue - startValue));
	}

	private List<Pair<Integer, Integer>> generateInsideCoordinates(Pair<Integer, Integer> minValues, Pair<Integer, Integer> maxValues) {
		List<Pair<Integer, Integer>> insideContourCoordinates = new ArrayList<>();
		for (int y = maxValues.second; y > minValues.second; y--) {

			while (y < listOfAllContourCoordinates.get(0).second) {
				listOfAllContourCoordinates.remove(0);
			}

			boolean insideContour = false;
			for (int x = minValues.first - 1; x < maxValues.first + 1; x++) {
				if (listOfAllContourCoordinates.size() == 0) {
					break;
				}

				if (listOfAllContourCoordinates.get(0).second < y) {
					break;
				} else {
					while (listOfAllContourCoordinates.get(0).second == y && x > listOfAllContourCoordinates.get(0).first) {
						listOfAllContourCoordinates.remove(0);
					}
				}

				if (insideContour) {
					insideContourCoordinates.add(new Pair<>(x, y));
					if (listOfAllContourCoordinates.get(0).equals(new Pair<>(x, y))) {
						insideContour = false;
						listOfAllContourCoordinates.remove(0);
					}
				} else {
					if (listOfAllContourCoordinates.size() > 1 && listOfAllContourCoordinates.get(0).equals(new Pair<>(x, y))
							&& !listOfAllContourCoordinates.get(1).equals(new Pair<>(x + 1, y))) {
						insideContour = true;
						listOfAllContourCoordinates.remove(0);
					}
				}
			}
			if (listOfAllContourCoordinates.size() == 0) {
				break;
			}
		}

		return insideContourCoordinates;
	}

	private void addUnderlayStitch(List<Pair<Integer, Integer>> insideContourCoordinates,
			Pair<Integer, Integer> minValues, Pair<Integer, Integer> maxValues) {
		boolean leftToRight = true;
		for (int y = maxValues.second - 1; y >= minValues.second; y -= zigZagSize) {
			List<Pair<Integer, Integer>> upperLine = getLine(y, insideContourCoordinates);
			List<Pair<Integer, Integer>> lowerLine = getLine(y - zigZagSize, insideContourCoordinates);

			if (upperLine != null && lowerLine != null) {
				if (leftToRight) {
					int currentX = upperLine.get(0).first;

					int lowerLineIndex = 0;
					while (currentX < lowerLine.get(lowerLineIndex).first) {
						currentX++;
					}
					while (upperLine.size() > 0 && lowerLine.size() > 0) {
						while (upperLine.size() > 0 && upperLine.get(0).first < currentX) {
							upperLine.remove(0);
						}
						if (upperLine.size() == 0) {
							break;
						}
						if (insideContourCoordinates.contains(new Pair<>(currentX, y))) {
							StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(
									currentX, y, sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
						}
						if (currentX + zigZagSize > lowerLine.get(lowerLine.size() - 1).first) {
							break;
						}
						if (insideContourCoordinates.contains(new Pair<>(currentX + zigZagSize, y - zigZagSize))) {
							StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(
									currentX + zigZagSize, y - zigZagSize,
									sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
						}
						currentX += zigZagSize * 2;
					}
					leftToRight = false;
				} else {
					int currentX = upperLine.get(upperLine.size() - 1).first;

					int lowerLineIndex = lowerLine.size() - 1;
					while (currentX > lowerLine.get(lowerLineIndex).first) {
						currentX--;
					}
					while (upperLine.size() > 0 && lowerLine.size() > 0) {
						while (upperLine.size() > 0 && upperLine.get(upperLine.size() - 1).first > currentX) {
							upperLine.remove(upperLine.size() - 1);
						}
						if (upperLine.size() == 0) {
							break;
						}
						if (insideContourCoordinates.contains(new Pair<>(currentX, y))) {
							StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(
									currentX, y, sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
						}
						if (currentX - zigZagSize < lowerLine.get(0).first) {
							break;
						}
						if (insideContourCoordinates.contains(new Pair<>(currentX - zigZagSize, y - zigZagSize))) {
							StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(
									currentX - zigZagSize, y - zigZagSize,
									sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
						}
						currentX -= zigZagSize * 2;
					}
					leftToRight = true;
				}
			}
		}
	}

	private void fillContour(List<Pair<Integer, Integer>> insideContourCoordinates, boolean leftToRight,
			Pair<Integer, Integer> maxValues, int integerWidth, StyleFunction styleFunction) {
		boolean lastLeftToRight = !leftToRight;

		int currentY = maxValues.second;
		while (!insideContourCoordinates.isEmpty()) {
			if (currentY < insideContourCoordinates.get(insideContourCoordinates.size() - 1).second) {
				currentY = insideContourCoordinates.get(0).second;
			}

			List<Pair<Integer, Integer>> line = getLine(currentY, insideContourCoordinates);
			if (line != null) {
				boolean randomizeOffsetForFirstInLine = true;
				int currentX = line.get(0).first;
				if (!lastLeftToRight) {
					while (true) {
						StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(
								currentX, currentY, sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
						currentX += styleFunction.getStyle(randomizeOffsetForFirstInLine);
						randomizeOffsetForFirstInLine = false;
						if (!line.contains(new Pair<>(currentX, currentY))) {
							removeCoordinatesByInterval(insideContourCoordinates, line.get(0).first, currentX, currentY, currentY - integerWidth);
							break;
						}
					}
					lastLeftToRight = true;
				} else {
					while (line.contains(new Pair<>(currentX, currentY))) {
						currentX++;
					}
					int startX = currentX - 1;
					while (true) {
						StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(
								currentX, currentY, sprite.look.getZIndex(), sprite, sprite.getEmbroideryThreadColor()));
						currentX -= styleFunction.getStyle(randomizeOffsetForFirstInLine);
						randomizeOffsetForFirstInLine = false;
						if (!line.contains(new Pair<>(currentX, currentY))) {
							removeCoordinatesByInterval(insideContourCoordinates, line.get(0).first, startX, currentY, currentY - integerWidth);
							break;
						}
					}
					lastLeftToRight = false;
				}
			}
			currentY -= integerWidth;
		}
	}

	private void removeCoordinatesByInterval(List<Pair<Integer, Integer>> insideContourCoordinates,
			int startX, int endX, int startY, int endY) {
		for (int i = 0; i < insideContourCoordinates.size(); i++) {
			if (insideContourCoordinates.get(i).second > startY) {
				continue;
			}
			if (insideContourCoordinates.get(i).second <= endY) {
				break;
			}
			int currentY = insideContourCoordinates.get(i).second;
			while (i < insideContourCoordinates.size() && insideContourCoordinates.get(i).second == currentY
					&& insideContourCoordinates.get(i).first >= startX && insideContourCoordinates.get(i).first <= endX) {
				insideContourCoordinates.remove(i);
			}
			while (i < insideContourCoordinates.size() && insideContourCoordinates.get(i).second == currentY) {
				i++;
			}
		}
	}

	private List<Pair<Integer, Integer>> getLine(int yCoordinate, List<Pair<Integer, Integer>> allCoordinates) {
		List<Pair<Integer, Integer>> line = new ArrayList<>();
		for (int i = 0; i < allCoordinates.size(); i++) {
			if (allCoordinates.get(i).second == yCoordinate) {
				while (i < allCoordinates.size() && allCoordinates.get(i).second == yCoordinate) {
					line.add(allCoordinates.get(i));
					i++;
				}
				return line;
			}
		}
		return null;
	}

	private void sortAllCoordinates() {
		for (int i = 0; i < listOfAllContourCoordinates.size(); i++) {
			for (int j = 0; j < listOfAllContourCoordinates.size(); j++) {
				if (comparePairs(listOfAllContourCoordinates.get(i), listOfAllContourCoordinates.get(j)) > 0) {
					Collections.swap(listOfAllContourCoordinates, i, j);
				}
			}
		}
	}

	private Pair<Integer, Integer> getMinValues(List<Pair<Integer, Integer>> coordinates) {
		int minX = Integer.MAX_VALUE;
		for (int i = 0; i < coordinates.size(); i++) {
			if (coordinates.get(i).first < minX) {
				minX = Math.round(coordinates.get(i).first);
			}
		}
		return new Pair<>(minX, Math.round(coordinates.get(coordinates.size() - 1).second));
	}

	private Pair<Integer, Integer> getMaxValues(List<Pair<Integer, Integer>> coordinates) {
		int maxX = Integer.MIN_VALUE;
		for (int i = 0; i < coordinates.size(); i++) {
			if (coordinates.get(i).first > maxX) {
				maxX = Math.round(coordinates.get(i).first);
			}
		}
		return new Pair<>(maxX, Math.round(coordinates.get(0).second));
	}

	private int comparePairs(Pair<Integer, Integer> pair1, Pair<Integer, Integer> pair2) {
		int result = Integer.compare(pair1.second, pair2.second);
		if (result == 0) {
			result = -Integer.compare(pair1.first, pair2.first);
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
