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

package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.VisibleForTesting;

public class PhiroIfLogicBeginBrick extends BrickBaseType implements CompositeBrick {

	private static final long serialVersionUID = 1L;

	private int sensorSpinnerPosition = 0;

	private transient ElseBrick elseBrick = new ElseBrick(this);
	private transient EndBrick endBrick = new EndBrick(this);

	private List<Brick> ifBranchBricks = new ArrayList<>();
	private List<Brick> elseBranchBricks = new ArrayList<>();

	@Override
	public boolean hasSecondaryList() {
		return true;
	}

	@Override
	public List<Brick> getNestedBricks() {
		return ifBranchBricks;
	}

	@Override
	public List<Brick> getSecondaryNestedBricks() {
		return elseBranchBricks;
	}

	public boolean addBrickToIfBranch(Brick brick) {
		return ifBranchBricks.add(brick);
	}

	public boolean addBrickToElseBranch(Brick brick) {
		return elseBranchBricks.add(brick);
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		for (Brick brick : ifBranchBricks) {
			brick.setCommentedOut(commentedOut);
		}
		for (Brick brick : elseBranchBricks) {
			brick.setCommentedOut(commentedOut);
		}
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		PhiroIfLogicBeginBrick clone = (PhiroIfLogicBeginBrick) super.clone();
		clone.elseBrick = new ElseBrick(clone);
		clone.endBrick = new EndBrick(clone);
		clone.ifBranchBricks = new ArrayList<>();
		clone.elseBranchBricks = new ArrayList<>();

		for (Brick brick : ifBranchBricks) {
			clone.addBrickToIfBranch(brick.clone());
		}
		for (Brick brick : elseBranchBricks) {
			clone.addBrickToElseBranch(brick.clone());
		}
		return clone;
	}

	@Override
	public boolean consistsOfMultipleParts() {
		return true;
	}

	@Override
	public List<Brick> getAllParts() {
		List<Brick> bricks = new ArrayList<>();
		bricks.add(this);
		bricks.add(elseBrick);
		bricks.add(endBrick);
		return bricks;
	}

	@Override
	public void addToFlatList(List<Brick> bricks) {
		super.addToFlatList(bricks);
		for (Brick brick : ifBranchBricks) {
			brick.addToFlatList(bricks);
		}
		bricks.add(elseBrick);
		for (Brick brick : elseBranchBricks) {
			brick.addToFlatList(bricks);
		}
		bricks.add(endBrick);
	}

	@Override
	public void setParent(Brick parent) {
		super.setParent(parent);
		for (Brick brick : ifBranchBricks) {
			brick.setParent(this);
		}
		for (Brick brick : elseBranchBricks) {
			brick.setParent(elseBrick);
		}
	}

	@Override
	public List<Brick> getDragAndDropTargetList() {
		return ifBranchBricks;
	}

	@Override
	public boolean removeChild(Brick brick) {
		if (ifBranchBricks.remove(brick)) {
			return true;
		}
		if (elseBranchBricks.remove(brick)) {
			return true;
		}
		for (Brick childBrick : ifBranchBricks) {
			if (childBrick.removeChild(brick)) {
				return true;
			}
		}
		for (Brick childBrick : elseBranchBricks) {
			if (childBrick.removeChild(brick)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_phiro_if_sensor;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		Spinner spinner = view.findViewById(R.id.brick_phiro_sensor_action_spinner);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(view.getContext(),
				R.array.brick_phiro_select_sensor_spinner,
				android.R.layout.simple_spinner_item);

		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(spinnerAdapter);
		spinner.setSelection(sensorSpinnerPosition);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				sensorSpinnerPosition = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		return view;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(BLUETOOTH_PHIRO);
		super.addRequiredResources(requiredResourcesSet);
		for (Brick brick : ifBranchBricks) {
			brick.addRequiredResources(requiredResourcesSet);
		}

		for (Brick brick : elseBranchBricks) {
			brick.addRequiredResources(requiredResourcesSet);
		}
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		ScriptSequenceAction ifSequence = (ScriptSequenceAction) ActionFactory.createScriptSequenceAction(sequence.getScript());
		ScriptSequenceAction elseSequence = (ScriptSequenceAction) ActionFactory.createScriptSequenceAction(sequence.getScript());

		for (Brick brick : ifBranchBricks) {
			if (!brick.isCommentedOut()) {
				brick.addActionToSequence(sprite, ifSequence);
			}
		}

		for (Brick brick : elseBranchBricks) {
			if (!brick.isCommentedOut()) {
				brick.addActionToSequence(sprite, elseSequence);
			}
		}

		Action action = sprite.getActionFactory()
				.createPhiroSendSelectedSensorAction(sprite, sensorSpinnerPosition, ifSequence, elseSequence);

		sequence.addAction(action);
	}

	@VisibleForTesting
	public static class ElseBrick extends BrickBaseType {

		ElseBrick(PhiroIfLogicBeginBrick ifBrick) {
			parent = ifBrick;
		}

		@Override
		public boolean isCommentedOut() {
			return parent.isCommentedOut();
		}

		@Override
		public boolean consistsOfMultipleParts() {
			return true;
		}

		@Override
		public List<Brick> getAllParts() {
			return parent.getAllParts();
		}

		@Override
		public void addToFlatList(List<Brick> bricks) {
			parent.addToFlatList(bricks);
		}

		@Override
		public List<Brick> getDragAndDropTargetList() {
			return ((PhiroIfLogicBeginBrick) parent).elseBranchBricks;
		}

		@Override
		public int getPositionInDragAndDropTargetList() {
			return -1;
		}

		@Override
		public int getViewResource() {
			return R.layout.brick_if_else;
		}

		@Override
		public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		}
	}

	@VisibleForTesting
	public static class EndBrick extends BrickBaseType {

		EndBrick(PhiroIfLogicBeginBrick ifBrick) {
			parent = ifBrick;
		}

		@Override
		public boolean isCommentedOut() {
			return parent.isCommentedOut();
		}

		@Override
		public boolean consistsOfMultipleParts() {
			return true;
		}

		@Override
		public List<Brick> getAllParts() {
			return parent.getAllParts();
		}

		@Override
		public void addToFlatList(List<Brick> bricks) {
			parent.addToFlatList(bricks);
		}

		@Override
		public List<Brick> getDragAndDropTargetList() {
			return parent.getParent().getDragAndDropTargetList();
		}

		@Override
		public int getPositionInDragAndDropTargetList() {
			return parent.getParent().getDragAndDropTargetList().indexOf(parent);
		}

		@Override
		public int getViewResource() {
			return R.layout.brick_if_end_if;
		}

		@Override
		public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		}
	}
}
