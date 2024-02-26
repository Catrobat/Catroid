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

import android.content.Context;
import android.view.View;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@CatrobatLanguageBrick(command = "Go to")
public class GoToBrick extends BrickBaseType implements BrickSpinner.OnItemSelectedListener<Sprite> {

	private static final long serialVersionUID = 1L;
	private static final String TARGET_CATLANG_PARAMETER_NAME = "target";

	private static final BiMap<Integer, String> CATLANG_SPINNER_VALUES = HashBiMap.create(new HashMap<Integer, String>()
	{{
		put(BrickValues.GO_TO_TOUCH_POSITION, "touch position");
		put(BrickValues.GO_TO_RANDOM_POSITION, "random position");
	}});

	private Sprite destinationSprite;
	private int spinnerSelection;

	public GoToBrick() {
	}

	public GoToBrick(Sprite destinationSprite) {
		this.destinationSprite = destinationSprite;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_go_to;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		List<Nameable> items = new ArrayList<>();
		items.add(new StringOption(context.getString(R.string.brick_go_to_touch_position)));
		items.add(new StringOption(context.getString(R.string.brick_go_to_random_position)));
		items.addAll(ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList());
		items.remove(ProjectManager.getInstance().getCurrentlyEditedScene().getBackgroundSprite());
		items.remove(ProjectManager.getInstance().getCurrentSprite());

		BrickSpinner<Sprite> spinner = new BrickSpinner<>(R.id.brick_go_to_spinner, view, items);
		spinner.setOnItemSelectedListener(this);
		if (spinnerSelection == BrickValues.GO_TO_TOUCH_POSITION) {
			spinner.setSelection(0);
		}
		if (spinnerSelection == BrickValues.GO_TO_RANDOM_POSITION) {
			spinner.setSelection(1);
		}
		if (spinnerSelection == BrickValues.GO_TO_OTHER_SPRITE_POSITION) {
			spinner.setSelection(destinationSprite);
		}
		return view;
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
		Context context = view.getContext();

		if (string.equals(context.getString(R.string.brick_go_to_touch_position))) {
			spinnerSelection = BrickValues.GO_TO_TOUCH_POSITION;
		}

		if (string.equals(context.getString(R.string.brick_go_to_random_position))) {
			spinnerSelection = BrickValues.GO_TO_RANDOM_POSITION;
		}
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable Sprite item) {
		spinnerSelection = BrickValues.GO_TO_OTHER_SPRITE_POSITION;
		destinationSprite = item;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createGoToAction(sprite, destinationSprite, spinnerSelection));
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(TARGET_CATLANG_PARAMETER_NAME)) {
			String currentSelectionString = "";
			if (CATLANG_SPINNER_VALUES.containsKey(spinnerSelection)) {
				currentSelectionString = CATLANG_SPINNER_VALUES.get(spinnerSelection);
			} else if (destinationSprite != null) {
				currentSelectionString = CatrobatLanguageUtils.formatActorOrObject(destinationSprite.getName());
			}
			return CatrobatLanguageUtils.getCatlangArgumentTuple(name, currentSelectionString);
		}
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(TARGET_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);
		if (arguments.containsKey(TARGET_CATLANG_PARAMETER_NAME)) {
			String target = arguments.get(TARGET_CATLANG_PARAMETER_NAME);
			BiMap<String, Integer> spinnerValues = CATLANG_SPINNER_VALUES.inverse();
			if (spinnerValues.containsKey(target)) {
				spinnerSelection = spinnerValues.get(target);
			} else {
				destinationSprite = scene.getSprite(target);
				if (destinationSprite == null) {
					throw new CatrobatLanguageParsingException("Target sprite '" + target + "' not found.");
				}
				spinnerSelection = BrickValues.GO_TO_OTHER_SPRITE_POSITION;
			}
		}
	}
}
