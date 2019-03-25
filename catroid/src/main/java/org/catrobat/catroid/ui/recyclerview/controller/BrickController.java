/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.ControlStructureBrick;
import org.catrobat.catroid.content.bricks.IfElseLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public final class BrickController {

	private static final String TAG = BrickController.class.getSimpleName();

	@Nullable
	public Script getScriptThatContainsBrick(Brick brick, @NonNull Iterable<Script> scripts) {
		for (Script script : scripts) {
			if (script.containsBrick(brick) || script.getScriptBrick().equals(brick)) {
				return script;
			}
		}
		return null;
	}

	public List<Brick> getBricksInControlStructure(ControlStructureBrick controlStructureBrick, @NonNull List<Brick> brickList) {
		if (!brickList.contains(controlStructureBrick)) {
			throw new IllegalArgumentException("Brick: " + controlStructureBrick.getClass() + " is not in list.");
		}

		List<Brick> bricksInControlStructure = new ArrayList<>();
		Brick start = controlStructureBrick.getFirstBrick();
		Brick end = controlStructureBrick.getLastBrick();

		boolean inControlStructure = false;
		for (Brick brick : brickList) {
			if (brick.equals(start)) {
				inControlStructure = true;
			}
			if (inControlStructure) {
				bricksInControlStructure.add(brick);
				if (brick.equals(end)) {
					break;
				}
			}
		}
		return bricksInControlStructure;
	}

	public List<BrickBaseType> getBricksToMove(BrickBaseType brick, @NonNull List<Brick> brickList) {
		boolean isFirstBrickOfANestedConstruct = brick instanceof ControlStructureBrick
				&& brick.equals(((ControlStructureBrick) brick).getFirstBrick());

		List<BrickBaseType> bricksToMove = new ArrayList<>();
		if (isFirstBrickOfANestedConstruct) {
			List<Brick> bricksInControlStructure = getBricksInControlStructure((ControlStructureBrick) brick, brickList);
			for (Brick brickInControlStructure : bricksInControlStructure) {
				bricksToMove.add((BrickBaseType) brickInControlStructure);
			}
		} else if (brick instanceof ScriptBrick) {
			bricksToMove.add(brick);
			List<Brick> bricksInScript = ((ScriptBrick) brick).getScript().getBrickList();
			for (Brick brickInScript : bricksInScript) {
				bricksToMove.add((BrickBaseType) brickInScript);
			}
		} else {
			bricksToMove.add(brick);
		}
		return bricksToMove;
	}

	public List<Brick> initialize(Brick brick) {
		List<Brick> bricks = new ArrayList<>();
		bricks.add(brick);

		if (brick instanceof ControlStructureBrick) {
			if (brick instanceof LoopBeginBrick) {
				LoopBeginBrick beginBrick = (LoopBeginBrick) brick;
				LoopEndBrick endBrick = new LoopEndBrick(beginBrick);
				beginBrick.setLoopEndBrick(endBrick);
				bricks.add(endBrick);
			}
			if (brick instanceof IfThenLogicBeginBrick) {
				IfThenLogicBeginBrick beginBrick = (IfThenLogicBeginBrick) brick;
				IfThenLogicEndBrick endBrick = new IfThenLogicEndBrick(beginBrick);
				beginBrick.setIfThenEndBrick(endBrick);
				bricks.add(endBrick);
			}
			if (brick instanceof IfElseLogicBeginBrick) {
				IfElseLogicBeginBrick beginBrick = (IfElseLogicBeginBrick) brick;
				IfLogicElseBrick elseBrick = new IfLogicElseBrick(beginBrick);
				IfLogicEndBrick endBrick = new IfLogicEndBrick(beginBrick, elseBrick);
				beginBrick.setIfElseBrick(elseBrick);
				beginBrick.setIfEndBrick(endBrick);
				elseBrick.setIfEndBrick(endBrick);
				bricks.add(elseBrick);
				bricks.add(endBrick);
			}
		}
		return bricks;
	}

	public void copy(@NonNull Iterable<Brick> bricksToCopy, Sprite parent) {
		PeekingIterator<Brick> iterator = Iterators.peekingIterator(bricksToCopy.iterator());

		while (iterator.hasNext()) {
			Brick brick = iterator.next();
			Script script = getScriptThatContainsBrick(brick, parent.getScriptList());
			if (script == null) {
				continue;
			}

			if (brick instanceof ScriptBrick) {
				try {
					parent.addScript(script.clone());
				} catch (CloneNotSupportedException e) {
					Log.e(TAG, Log.getStackTraceString(e));
				}
				while (iterator.hasNext() && script.getBrickList().contains(iterator.peek())) {
					iterator.next();
				}
			} else if (brick instanceof ControlStructureBrick) {
				List<Brick> brickList = script.getBrickList();
				List<Brick> bricksInControlStructure =
						getBricksInControlStructure((ControlStructureBrick) brick, brickList);

				try {
					script.addBricks(clone(bricksInControlStructure));
				} catch (CloneNotSupportedException e) {
					Log.e(TAG, Log.getStackTraceString(e));
				}
				while (iterator.hasNext() && script.getBrickList().contains(iterator.peek())) {
					iterator.next();
				}
			} else {
				try {
					script.addBrick(brick.clone());
				} catch (CloneNotSupportedException e) {
					Log.e(TAG, Log.getStackTraceString(e));
				}
			}
		}
	}

	public void delete(@NonNull Iterable<Brick> bricksToDelete, Sprite parent) {
		PeekingIterator<Brick> iterator = Iterators.peekingIterator(bricksToDelete.iterator());

		while (iterator.hasNext()) {
			Brick brick = iterator.next();
			Script script = getScriptThatContainsBrick(brick, parent.getScriptList());
			if (script == null) {
				continue;
			}
			if (brick instanceof ScriptBrick) {
				parent.removeScript(script);
				while (iterator.hasNext() && script.getBrickList().contains(iterator.peek())) {
					iterator.next();
				}
			} else {
				script.removeBrick(brick);
			}
		}
	}

	public List<Brick> clone(@NonNull List<Brick> bricksToClone) throws CloneNotSupportedException {
		List<Brick> clones = new ArrayList<>();

		for (Brick brick : bricksToClone) {
			if (brick instanceof ScriptBrick) {
				throw new IllegalArgumentException("Do not clone Scripts via this method. Get the Script from the "
						+ "ScriptBrick and clone from there.");
			}
			clones.add(brick.clone());
		}
		for (Brick brick : bricksToClone) {
			if (brick instanceof LoopBeginBrick) {
				int begin = bricksToClone.indexOf(brick);
				int end = bricksToClone.indexOf(((LoopBeginBrick) brick).getLoopEndBrick());

				if (end == -1) {
					continue;
				}

				LoopBeginBrick beginBrick = (LoopBeginBrick) clones.get(begin);
				LoopEndBrick endBrick = (LoopEndBrick) clones.get(end);

				beginBrick.setLoopEndBrick(endBrick);
				endBrick.setLoopBeginBrick(beginBrick);
			}
			if (brick instanceof IfThenLogicBeginBrick) {
				int begin = bricksToClone.indexOf(brick);
				int end = bricksToClone.indexOf(((IfThenLogicBeginBrick) brick).getIfThenEndBrick());

				IfThenLogicBeginBrick beginBrick = (IfThenLogicBeginBrick) clones.get(begin);
				IfThenLogicEndBrick endBrick = (IfThenLogicEndBrick) clones.get(end);

				beginBrick.setIfThenEndBrick(endBrick);
				endBrick.setIfThenBeginBrick(beginBrick);
			} else if (brick instanceof IfElseLogicBeginBrick) {
				int begin = bricksToClone.indexOf(brick);
				int middle = bricksToClone.indexOf(((IfElseLogicBeginBrick) brick).getIfElseBrick());
				int end = bricksToClone.indexOf(((IfElseLogicBeginBrick) brick).getIfEndBrick());

				if (middle == -1 || end == -1) {
					continue;
				}

				IfElseLogicBeginBrick beginBrick = (IfElseLogicBeginBrick) clones.get(begin);
				IfLogicElseBrick elseBrick = (IfLogicElseBrick) clones.get(middle);
				IfLogicEndBrick endBrick = (IfLogicEndBrick) clones.get(end);

				beginBrick.setIfElseBrick(elseBrick);
				beginBrick.setIfEndBrick(endBrick);
				elseBrick.setIfBeginBrick(beginBrick);
				elseBrick.setIfEndBrick(endBrick);
				endBrick.setIfBeginBrick(beginBrick);
				endBrick.setIfElseBrick(elseBrick);
			}
		}
		return clones;
	}

	public boolean setControlBrickReferences(List<Brick> bricks) {
		setIfElseBrickReferences(bricks);
		setIfThenBrickReferences(bricks);
		setLoopBrickReferences(bricks);
		for (ListIterator<Brick> iterator = bricks.listIterator(); iterator.hasNext(); ) {
			Brick brick = iterator.next();
			if (brick instanceof ControlStructureBrick) {
				ControlStructureBrick controlStructureBrick = ((ControlStructureBrick) brick);
				if (hasInvalidReference(controlStructureBrick, bricks)) {
					iterator.remove();
				}
			}
		}
		return true;
	}

	private boolean hasInvalidReference(ControlStructureBrick brick, List<Brick> bricks) {
		List<Brick> brickParts = brick.getAllParts();
		if (brickParts.contains(null)) {
			return true;
		}
		for (Brick brickPart : brickParts) {
			if (!(bricks.contains(brickPart))) {
				return true;
			}
		}
		return false;
	}

	private void setIfElseBrickReferences(List<Brick> bricksToCheck) {
		ArrayDeque<IfLogicBeginBrick> beginBricks = new ArrayDeque<>();

		for (ListIterator<Brick> iterator = bricksToCheck.listIterator(); iterator.hasNext(); ) {
			Brick brick = iterator.next();
			if (brick instanceof IfElseLogicBeginBrick) {
				beginBricks.push((IfLogicBeginBrick) brick);
			}
			if (beginBricks.isEmpty() && (brick instanceof IfLogicElseBrick || brick instanceof IfLogicEndBrick)) {
				iterator.remove();
			}
		}

		List<Brick> bricksWithInvalidReferences = new ArrayList<>();
		List<Brick> bricks = new ArrayList<>(bricksToCheck);

		while (!beginBricks.isEmpty()) {
			bricksWithInvalidReferences.addAll(setElseAndEndBrickReferences(beginBricks.pop(), bricks));
		}

		bricksToCheck.removeAll(bricksWithInvalidReferences);
	}

	private List<Brick> setElseAndEndBrickReferences(IfLogicBeginBrick beginBrick, List<Brick> bricks) {
		List<Brick> bricksWithInvalidReferences = new ArrayList<>();
		List<Brick> bricksInControlStructure = new ArrayList<>();

		IfLogicElseBrick elseBrick = null;
		IfLogicEndBrick endBrick;

		for (ListIterator<Brick> iterator = bricks.listIterator(bricks.indexOf(beginBrick)); iterator.hasNext(); ) {
			Brick brick = iterator.next();
			bricksInControlStructure.add(brick);

			if (brick instanceof IfLogicElseBrick) {
				if (elseBrick == null) {
					elseBrick = (IfLogicElseBrick) brick;
				} else {
					bricksWithInvalidReferences.add(brick);
					bricksInControlStructure.remove(brick);
					iterator.remove();
				}
			}
			if (brick instanceof IfLogicEndBrick) {
				if (elseBrick == null) {
					bricksWithInvalidReferences.add(beginBrick);
					bricksInControlStructure.clear();
					break;
				} else {
					endBrick = (IfLogicEndBrick) brick;
					beginBrick.setIfElseBrick(elseBrick);
					beginBrick.setIfEndBrick(endBrick);
					elseBrick.setIfBeginBrick(beginBrick);
					elseBrick.setIfEndBrick(endBrick);
					endBrick.setIfBeginBrick(beginBrick);
					endBrick.setIfElseBrick(elseBrick);
					break;
				}
			}
		}

		bricks.removeAll(bricksInControlStructure);
		bricks.removeAll(bricksWithInvalidReferences);
		return bricksWithInvalidReferences;
	}

	private void setIfThenBrickReferences(List<Brick> bricksToCheck) {
		ArrayDeque<IfThenLogicBeginBrick> beginBricks = new ArrayDeque<>();

		for (ListIterator<Brick> iterator = bricksToCheck.listIterator(); iterator.hasNext(); ) {
			Brick brick = iterator.next();
			if (brick instanceof IfThenLogicBeginBrick) {
				beginBricks.push((IfThenLogicBeginBrick) brick);
			}
			if (beginBricks.isEmpty() && brick instanceof IfThenLogicEndBrick) {
				iterator.remove();
			}
		}

		List<Brick> bricksWithInvalidReferences = new ArrayList<>();
		List<Brick> bricks = new ArrayList<>(bricksToCheck);

		while (!beginBricks.isEmpty()) {
			bricksWithInvalidReferences.addAll(setIfThenEndBrickReferences(beginBricks.pop(), bricks));
		}

		bricksToCheck.removeAll(bricksWithInvalidReferences);
	}

	private List<Brick> setIfThenEndBrickReferences(IfThenLogicBeginBrick beginBrick, List<Brick> bricks) {
		List<Brick> bricksWithInvalidReferences = new ArrayList<>();
		List<Brick> bricksInControlStructure = new ArrayList<>();

		IfThenLogicEndBrick endBrick;

		for (ListIterator<Brick> iterator = bricks.listIterator(bricks.indexOf(beginBrick)); iterator.hasNext(); ) {
			Brick brick = iterator.next();
			bricksInControlStructure.add(brick);

			if (brick instanceof IfThenLogicEndBrick) {
				endBrick = (IfThenLogicEndBrick) brick;
				beginBrick.setIfThenEndBrick(endBrick);
				endBrick.setIfThenBeginBrick(beginBrick);
				bricks.removeAll(bricksInControlStructure);
				break;
			}
		}

		bricks.removeAll(bricksWithInvalidReferences);
		return bricksWithInvalidReferences;
	}

	private void setLoopBrickReferences(List<Brick> bricksToCheck) {
		ArrayDeque<LoopBeginBrick> beginBricks = new ArrayDeque<>();

		for (ListIterator<Brick> iterator = bricksToCheck.listIterator(); iterator.hasNext(); ) {
			Brick brick = iterator.next();
			if (brick instanceof LoopBeginBrick) {
				beginBricks.push((LoopBeginBrick) brick);
			}
			if (beginBricks.isEmpty() && brick instanceof LoopEndBrick) {
				iterator.remove();
			}
		}

		List<Brick> bricksWithInvalidReferences = new ArrayList<>();
		List<Brick> bricks = new ArrayList<>(bricksToCheck);

		while (!beginBricks.isEmpty()) {
			bricksWithInvalidReferences.addAll(setLoopEndBrickReferences(beginBricks.pop(), bricks));
		}

		bricksToCheck.removeAll(bricksWithInvalidReferences);
	}

	private List<Brick> setLoopEndBrickReferences(LoopBeginBrick beginBrick, List<Brick> bricks) {
		List<Brick> bricksWithInvalidReferences = new ArrayList<>();
		List<Brick> bricksInControlStructure = new ArrayList<>();

		LoopEndBrick endBrick;

		for (ListIterator<Brick> iterator = bricks.listIterator(bricks.indexOf(beginBrick)); iterator.hasNext(); ) {
			Brick brick = iterator.next();
			bricksInControlStructure.add(brick);

			if (brick instanceof LoopEndBrick) {
				endBrick = (LoopEndBrick) brick;
				beginBrick.setLoopEndBrick(endBrick);
				endBrick.setLoopBeginBrick(beginBrick);
				bricks.removeAll(bricksInControlStructure);
				break;
			}
		}

		bricks.removeAll(bricksWithInvalidReferences);
		return bricksWithInvalidReferences;
	}
}
