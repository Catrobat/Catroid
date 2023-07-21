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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.io.catlang.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static org.catrobat.catroid.content.Look.ROTATION_STYLE_LEFT_RIGHT_ONLY;

@CatrobatLanguageBrick(command = "Set")
public class SetRotationStyleBrick extends BrickBaseType implements
		BrickSpinner.OnItemSelectedListener<SetRotationStyleBrick.RotationStyleOption>, UpdateableSpinnerBrick {

	private static final long serialVersionUID = 1L;

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

	@NonNull
	@Override
	public String serializeToCatrobatLanguage(int indentionLevel) {
		return getCatrobatLanguageSpinnerCall(indentionLevel, "rotation style", selection);
	}

	@Override
	protected String getCatrobatLanguageSpinnerValue(int spinnerIndex) {
		switch (spinnerIndex) {
			case ROTATION_STYLE_LEFT_RIGHT_ONLY:
				return "left-right only";
			case Look.ROTATION_STYLE_ALL_AROUND:
				return "all-around";
			case Look.ROTATION_STYLE_NONE:
				return "don't rotate";
			default:
				throw new IllegalStateException("Invalid spinner selection: " + selection);
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

