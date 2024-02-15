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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static org.catrobat.catroid.content.Look.ROTATION_STYLE_LEFT_RIGHT_ONLY;
import static org.catrobat.catroid.content.Look.ROTATION_STYLE_NONE;

@CatrobatLanguageBrick(command = "Set")
public class SetRotationStyleBrick extends BrickBaseType implements
		BrickSpinner.OnItemSelectedListener<SetRotationStyleBrick.RotationStyleOption>, UpdateableSpinnerBrick {

	private static final long serialVersionUID = 1L;
	private static final String ROTATION_STYLE_CATLANG_PARAMETER_NAME = "rotation style";
	private static final BiMap<Integer, String> CATLANG_SPINNER_VALUES = HashBiMap.create(new HashMap<Integer, String>()
	{{
		put(ROTATION_STYLE_LEFT_RIGHT_ONLY, "left-right only");
		put(Look.ROTATION_STYLE_ALL_AROUND, "all-around");
		put(ROTATION_STYLE_NONE, "don't rotate");
	}});

	@Look.RotationStyle
	private int selection;

	private transient BrickSpinner<RotationStyleOption> spinner;

	public SetRotationStyleBrick() {
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_set_rotation_style;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		List<Nameable> items = new ArrayList<>();
		items.add(new RotationStyleOption(context.getString(R.string.brick_set_rotation_style_lr),
				ROTATION_STYLE_LEFT_RIGHT_ONLY));
		items.add(new RotationStyleOption(context.getString(R.string.brick_set_rotation_style_normal),
				Look.ROTATION_STYLE_ALL_AROUND));
		items.add(new RotationStyleOption(context.getString(R.string.brick_set_rotation_style_no),
				Look.ROTATION_STYLE_NONE));

		BrickSpinner<RotationStyleOption> spinner = new BrickSpinner<>(R.id.brick_set_rotation_style_spinner,
				view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(selection);
		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetRotationStyleAction(sprite, selection));
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable RotationStyleOption item) {
		selection = item != null ? item.getRotationStyle() : 0;
	}

	@Override
	public void updateSelectedItem(Context context, int spinnerId, String itemName, int itemIndex) {
		if (spinner != null) {
			spinner.setSelection(itemName);
		}
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(ROTATION_STYLE_CATLANG_PARAMETER_NAME)) {
			return CatrobatLanguageUtils.getCatlangArgumentTuple(name, CATLANG_SPINNER_VALUES.get(selection));
		}
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(ROTATION_STYLE_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);
		String rotationStyle = arguments.get(ROTATION_STYLE_CATLANG_PARAMETER_NAME);
		if (rotationStyle != null) {
			Integer selectedRotationStyle = CATLANG_SPINNER_VALUES.inverse().get(rotationStyle);
			if (selectedRotationStyle != null) {
				selection = selectedRotationStyle;
			} else {
				throw new CatrobatLanguageParsingException("Invalid rotation style: " + rotationStyle);
			}
		}

	}

	class RotationStyleOption implements Nameable {

		private String name;

		@Look.RotationStyle
		private int rotationStyle;

		RotationStyleOption(String name, @Look.RotationStyle int rotationStyle) {
			this.name = name;
			this.rotationStyle = rotationStyle;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		int getRotationStyle() {
			return rotationStyle;
		}
	}
}

