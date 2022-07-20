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
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.strategy.ShowColorPickerFormulaEditorStrategy;
import org.catrobat.catroid.content.strategy.ShowFormulaEditorStrategy;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.common.Conversions;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT_ALIGNMENT;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT_COLOR;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT_SIZE;
import static org.catrobat.catroid.utils.ShowTextUtils.ALIGNMENT_STYLE_CENTERED;
import static org.catrobat.catroid.utils.ShowTextUtils.ALIGNMENT_STYLE_LEFT;
import static org.catrobat.catroid.utils.ShowTextUtils.ALIGNMENT_STYLE_RIGHT;
import static org.catrobat.catroid.utils.ShowTextUtils.convertColorToString;
import static org.catrobat.catroid.utils.ShowTextUtils.isValidColorString;

public class ShowTextColorSizeAlignmentBrick extends UserVariableBrickWithVisualPlacement {

	private static final long serialVersionUID = 1L;

	public int alignmentSelection = ALIGNMENT_STYLE_CENTERED;

	private final transient ShowFormulaEditorStrategy showFormulaEditorStrategy;

	public ShowTextColorSizeAlignmentBrick() {
		addAllowedBrickField(BrickField.X_POSITION, R.id.brick_show_variable_color_size_edit_text_x);
		addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_show_variable_color_size_edit_text_y);
		addAllowedBrickField(BrickField.SIZE, R.id.brick_show_variable_color_size_edit_relative_size);
		addAllowedBrickField(BrickField.COLOR, R.id.brick_show_variable_color_size_edit_color);

		showFormulaEditorStrategy = new ShowColorPickerFormulaEditorStrategy();
	}

	public ShowTextColorSizeAlignmentBrick(int xPosition, int yPosition, double size, String color) {
		this(new Formula(xPosition), new Formula(yPosition), new Formula(size), new Formula(color));
	}

	private ShowTextColorSizeAlignmentBrick(Formula xPosition, Formula yPosition, Formula size, Formula color) {
		this();
		setFormulaWithBrickField(BrickField.X_POSITION, xPosition);
		setFormulaWithBrickField(BrickField.Y_POSITION, yPosition);
		setFormulaWithBrickField(BrickField.SIZE, size);
		setFormulaWithBrickField(BrickField.COLOR, color);
	}

	public BrickField getDefaultBrickField() {
		return BrickField.X_POSITION;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		if (view.getId() == R.id.brick_show_variable_color_size_edit_color && isValidColorString(getColor())) {
			ShowFormulaEditorStrategy.Callback callback = new ShowTextColorSizeAlignmentBrickCallback(view);
			showFormulaEditorStrategy.showFormulaEditorToEditFormula(view, callback);
		} else {
			superShowFormulaEditor(view);
		}
	}

	private void superShowFormulaEditor(View view) {
		super.showFormulaEditorToEditFormula(view);
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
		items.add(new AlignmentStyle(context.getString(R.string.brick_show_variable_aligned_left), ALIGNMENT_STYLE_LEFT));
		items.add(new AlignmentStyle(context.getString(R.string.brick_show_variable_aligned_centered), ALIGNMENT_STYLE_CENTERED));
		items.add(new AlignmentStyle(context.getString(R.string.brick_show_variable_aligned_right), ALIGNMENT_STYLE_RIGHT));
		BrickSpinner<AlignmentStyle> spinner =
				new BrickSpinner<>(R.id.brick_show_variable_color_size_align_spinner, view, items);
		spinner.setSelection(alignmentSelection);
		spinner.setOnItemSelectedListener(new BrickSpinner.OnItemSelectedListener<AlignmentStyle>() {

			@Override
			public void onStringOptionSelected(Integer spinnerId, String string) {
			}

			@Override
			public void onItemSelected(Integer spinnerId, @Nullable AlignmentStyle item) {
				if (item != null) {
					alignmentSelection = item.alignmentStyle;
				}
			}

			@Override
			public void onEditOptionSelected(Integer spinnerId) {
			}

			@Override
			public void onNewOptionSelected(Integer spinnerId) {
			}
		});
		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		if (userVariable == null || userVariable.getName() == null) {
			userVariable = new UserVariable("NoVariableSet",
					CatroidApplication.getAppContext().getString(R.string.no_variable_selected));
			userVariable.setDummy(true);
		}

		sequence.addAction(sprite.getActionFactory().createShowVariableColorAndSizeAction(sprite,
				sequence, getFormulaWithBrickField(BrickField.X_POSITION),
				getFormulaWithBrickField(BrickField.Y_POSITION),
				getFormulaWithBrickField(BrickField.SIZE),
				getFormulaWithBrickField(BrickField.COLOR), userVariable,
				alignmentSelection,
				new AndroidStringProvider(CatroidApplication.getAppContext())));
	}

	private String getColor() {
		return getFormulaWithBrickField(BrickField.COLOR).getRoot().getValue();
	}

	private float sanitizeTextSize() {
		Formula sizeFormula = getFormulaWithBrickField(BrickField.SIZE);
		float size = 1.0f;

		if (sizeFormula.getRoot().getElementType() == ElementType.NUMBER) {
			size = Float.parseFloat(sizeFormula.getRoot().getValue()) / 100.f;
		}
		if (size < 0.1f) {
			size = 1.0f;
		}

		return size;
	}

	@Override
	public int getXEditTextId() {
		return R.id.brick_show_variable_color_size_edit_text_x;
	}

	@Override
	public int getYEditTextId() {
		return R.id.brick_show_variable_color_size_edit_text_y;
	}

	@Override
	public Intent generateIntentForVisualPlacement(BrickField brickFieldX, BrickField brickFieldY) {
		Intent intent = super.generateIntentForVisualPlacement(brickFieldX, brickFieldY);
		intent.putExtra(EXTRA_TEXT_COLOR, getColor());
		intent.putExtra(EXTRA_TEXT_SIZE, sanitizeTextSize());
		intent.putExtra(EXTRA_TEXT_ALIGNMENT, alignmentSelection);
		return intent;
	}

	private static class AlignmentStyle implements Nameable {
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

	private final class ShowTextColorSizeAlignmentBrickCallback implements ShowFormulaEditorStrategy.Callback {
		private View view;

		private ShowTextColorSizeAlignmentBrickCallback(View view) {
			this.view = view;
		}

		@Override
		public void showFormulaEditor(View view) {
			superShowFormulaEditor(view);
		}

		@Override
		public void setValue(int value) {
			String colorString = convertColorToString(value);
			setFormulaWithBrickField(BrickField.COLOR, new Formula(colorString));

			AppCompatActivity activity = UiUtils.getActivityFromView(view);
			Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
			notifyDataSetChanged(activity);

			if (currentFragment instanceof FormulaEditorFragment) {
				((FormulaEditorFragment) currentFragment).updateFragmentAfterColorPicker();
			}
		}

		@Override
		public int getValue() {
			if (!isColorBrickFieldAString()) {
				return Color.BLACK;
			}
			String stringValue = getColorBrickFieldStringValue();
			return Conversions.tryParseColor(stringValue);
		}

		private boolean isColorBrickFieldAString() {
			return getColorFormulaElement().getElementType() == ElementType.STRING;
		}

		private String getColorBrickFieldStringValue() {
			return getColorFormulaElement().getValue();
		}

		private FormulaElement getColorFormulaElement() {
			return getFormulaWithBrickField(BrickField.COLOR).getRoot();
		}
	}
}
