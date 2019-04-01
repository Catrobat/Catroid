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
import android.support.annotation.Nullable;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.ArrayList;
import java.util.List;

public class ShowTextColorSizeAlignmentBrick extends UserVariableBrick {

	private static final long serialVersionUID = 1L;

	public static final int ALIGNMENT_STYLE_LEFT = 0;
	public static final int ALIGNMENT_STYLE_CENTERED = 1;
	public static final int ALIGNMENT_STYLE_RIGHT = 2;

	public int alignmentSelection = ALIGNMENT_STYLE_CENTERED;

	class AlignmentStyle implements Nameable {
		private String name;
		private int alignmentStyle;

		AlignmentStyle(String name, int alignmentStyle) {
			this.name = name;
			this.alignmentStyle = alignmentStyle;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}
	}

	public BrickField getDefaultBrickField() {
		return BrickField.X_POSITION;
	}

	public ShowTextColorSizeAlignmentBrick() {
		addAllowedBrickField(BrickField.X_POSITION, R.id.brick_show_variable_color_size_edit_text_x);
		addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_show_variable_color_size_edit_text_y);
		addAllowedBrickField(BrickField.SIZE, R.id.brick_show_variable_color_size_edit_relative_size);
		addAllowedBrickField(BrickField.COLOR, R.id.brick_show_variable_color_size_edit_color);
	}

	public ShowTextColorSizeAlignmentBrick(int xPosition, int yPosition, double size, String color) {
		this(new Formula(xPosition), new Formula(yPosition), new Formula(size), new Formula(color));
	}

	public ShowTextColorSizeAlignmentBrick(Formula xPosition, Formula yPosition, Formula size, Formula color) {
		this();
		setFormulaWithBrickField(BrickField.X_POSITION, xPosition);
		setFormulaWithBrickField(BrickField.Y_POSITION, yPosition);
		setFormulaWithBrickField(BrickField.SIZE, size);
		setFormulaWithBrickField(BrickField.COLOR, color);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_show_variable_color_size_alignment;
	}

	@Override
	protected int getSpinnerId() {
		return R.id.show_variable_color_size_spinner;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		List<Nameable> items = new ArrayList<>();
		items.add(new AlignmentStyle(context.getString(R.string.brick_show_variable_aligned_left),
				ALIGNMENT_STYLE_LEFT));
		items.add(new AlignmentStyle(context.getString(R.string.brick_show_variable_aligned_centered),
				ALIGNMENT_STYLE_CENTERED));
		items.add(new AlignmentStyle(context.getString(R.string.brick_show_variable_aligned_right),
				ALIGNMENT_STYLE_RIGHT));
		BrickSpinner<AlignmentStyle> spinner =
				new BrickSpinner<>(R.id.brick_show_variable_color_size_align_spinner, view, items);
		spinner.setSelection(alignmentSelection);
		spinner.setOnItemSelectedListener(new BrickSpinner.OnItemSelectedListener<AlignmentStyle>() {

			@Override
			public void onStringOptionSelected(String string) {
			}

			@Override
			public void onItemSelected(@Nullable AlignmentStyle item) {
				ShowTextColorSizeAlignmentBrick.this.alignmentSelection = item.alignmentStyle;
			}

			@Override
			public void onNewOptionSelected() {
			}
		});
		return view;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		if (userVariable == null || userVariable.getName() == null) {
			userVariable = new UserVariable("NoVariableSet", Constants.NO_VARIABLE_SELECTED);
			userVariable.setDummy(true);
		}

		sequence.addAction(sprite.getActionFactory().createShowVariableColorAndSizeAction(sprite,
				getFormulaWithBrickField(BrickField.X_POSITION),
				getFormulaWithBrickField(BrickField.Y_POSITION),
				getFormulaWithBrickField(BrickField.SIZE),
				getFormulaWithBrickField(BrickField.COLOR), userVariable, alignmentSelection));
		return null;
	}
}
