/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;

import org.catrobat.catroid.common.BrickValues;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class SetGhostEffectBrick extends FormulaBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	public SetGhostEffectBrick() {
		addAllowedBrickField(BrickField.TRANSPARENCY);
	}

	public SetGhostEffectBrick(double ghostEffectValue) {
		initializeBrickFields(new Formula(ghostEffectValue));
	}

	public SetGhostEffectBrick(Formula transparency) {
		initializeBrickFields(transparency);
	}

	private void initializeBrickFields(Formula transparency) {
		addAllowedBrickField(BrickField.TRANSPARENCY);
		setFormulaWithBrickField(BrickField.TRANSPARENCY, transparency);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.TRANSPARENCY).getRequiredResources();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_set_ghost_effect, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_set_ghost_effect_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textX = (TextView) view.findViewById(R.id.brick_set_ghost_effect_to_prototype_text_view);
		TextView editX = (TextView) view.findViewById(R.id.brick_set_ghost_effect_to_edit_text);
		getFormulaWithBrickField(BrickField.TRANSPARENCY).setTextFieldId(R.id.brick_set_ghost_effect_to_edit_text);
		getFormulaWithBrickField(BrickField.TRANSPARENCY).refreshTextField(view);
		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);

		editX.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_set_ghost_effect, null);
		TextView textSetGhostEffect = (TextView) prototypeView
				.findViewById(R.id.brick_set_ghost_effect_to_prototype_text_view);
        textSetGhostEffect.setText(String.valueOf(BrickValues.SET_GHOST_EFFECT));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_set_ghost_effect_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textGhostLabel = (TextView) view.findViewById(R.id.brick_set_ghost_effect_label);
			TextView textGhostTo = (TextView) view.findViewById(R.id.brick_set_ghost_effect_to);
			TextView textPercent = (TextView) view.findViewById(R.id.brick_set_ghost_effect_percent);
			TextView editGhostEffect = (TextView) view.findViewById(R.id.brick_set_ghost_effect_to_edit_text);
			textGhostLabel.setTextColor(textGhostLabel.getTextColors().withAlpha(alphaValue));
			textGhostTo.setTextColor(textGhostTo.getTextColors().withAlpha(alphaValue));
			textPercent.setTextColor(textPercent.getTextColors().withAlpha(alphaValue));
			editGhostEffect.setTextColor(editGhostEffect.getTextColors().withAlpha(alphaValue));
			editGhostEffect.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, getFormulaWithBrickField(BrickField.TRANSPARENCY));
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.setGhostEffect(sprite, getFormulaWithBrickField(BrickField.TRANSPARENCY)));
		return null;
	}
}
