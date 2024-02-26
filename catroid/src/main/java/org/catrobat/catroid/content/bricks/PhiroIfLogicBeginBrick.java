/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "If")
public class PhiroIfLogicBeginBrick extends BrickBaseType implements CompositeBrick {

	private static final long serialVersionUID = 1L;
	private static final String ACTIVATED_PHIRO_CATLANG_PARAMETER_NAME = "activated phiro";
	private static final BiMap<Integer, String> CATLANG_SPINNER_VALUES = HashBiMap.create(new HashMap<Integer, String>()
	{{
		put(0, "front left sensor");
		put(1, "front right sensor");
		put(2, "side left sensor");
		put(3, "side right sensor");
		put(4, "bottom left sensor");
		put(5, "bottom right sensor");
	}});

	private int sensorSpinnerPosition = 0;

	private transient ElseBrick elseBrick = new ElseBrick(this);
	private transient EndBrick endBrick = new EndBrick(this, R.layout.brick_if_end_if);

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

	@Override
	public Brick getSecondaryNestedBricksParent() {
		return elseBrick;
	}

	@Override
	public String getSecondaryBrickCommand() {
		return "else";
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
		clone.endBrick = new EndBrick(clone, R.layout.brick_if_end_if);
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
		spinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			sensorSpinnerPosition = position;
			return Unit.INSTANCE;
		}));
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
				.createPhiroSendSelectedSensorAction(sprite, sequence, sensorSpinnerPosition, ifSequence,
						elseSequence);

		sequence.addAction(action);
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(ACTIVATED_PHIRO_CATLANG_PARAMETER_NAME))
			return CatrobatLanguageUtils.getCatlangArgumentTuple(name, CATLANG_SPINNER_VALUES.get(sensorSpinnerPosition));
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(ACTIVATED_PHIRO_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);
		String sensorValue = arguments.get(ACTIVATED_PHIRO_CATLANG_PARAMETER_NAME);
		if (sensorValue == null)
			throw new CatrobatLanguageParsingException("No value found for parameter " + ACTIVATED_PHIRO_CATLANG_PARAMETER_NAME);
		Integer sensorIndex = CATLANG_SPINNER_VALUES.inverse().get(sensorValue);
		if (sensorIndex == null)
			throw new CatrobatLanguageParsingException("Unknown value " + sensorValue + " for parameter " + ACTIVATED_PHIRO_CATLANG_PARAMETER_NAME);
		sensorSpinnerPosition = sensorIndex;
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
}
