/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public abstract class JumpingSumoRotateBrick extends FormulaBrick {
	private static final String TAG = JumpingSumoRotateBrick.class.getSimpleName();

	protected transient View prototypeView;
	private static final long serialVersionUID = 1L;


	public JumpingSumoRotateBrick() {
		addAllowedBrickField(BrickField.JUMPING_SUMO_ROTATE);
	}


	public JumpingSumoRotateBrick(float degree) {
		initializeBrickFields(new Formula(degree));
	}

	public JumpingSumoRotateBrick(Formula degree) {
		initializeBrickFields(degree);
	}

	private void initializeBrickFields(Formula degree) {
		addAllowedBrickField(BrickField.JUMPING_SUMO_ROTATE);
		setFormulaWithBrickField(BrickField.JUMPING_SUMO_ROTATE, degree);
	}

	public void setPower(Formula degree) {
		setFormulaWithBrickField(BrickField.JUMPING_SUMO_ROTATE, degree);
	}

	@Override
	public abstract List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence);

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_jumping_sumo_rotate, null);
		TextView label = (TextView) prototypeView.findViewById(R.id.brick_jumping_sumo_rotate_label);
		label.setText(getBrickLabel(prototypeView));
		TextView textDegree = (TextView) prototypeView.findViewById(R.id.brick_jumping_sumo_rotate_value);
		textDegree.setText(String.valueOf(BrickValues.JUMPING_SUMO_ROTATE_DEFAULT_DEGREE));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_jumping_sumo_rotate_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textRotateLabel = (TextView) view.findViewById(R.id.brick_jumping_sumo_rotate_label);

			TextView editDegree = (TextView) view.findViewById(R.id.brick_jumping_sumo_rotate_value);

			textRotateLabel.setTextColor(textRotateLabel.getTextColors().withAlpha(alphaValue));

			editDegree.setTextColor(editDegree.getTextColors().withAlpha(alphaValue));
			editDegree.getBackground().setAlpha(alphaValue);

			this.alphaValue = alphaValue;
		}
		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		switch (view.getId()) {
			case R.id.brick_jumping_sumo_rotate_value:
				FormulaEditorFragment.showFragment(view, this, BrickField.JUMPING_SUMO_ROTATE);
				break;

			default:
				return;
		}
	}

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.JUMPING_SUMO;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.JUMPING_SUMO_ROTATE);
	}

	protected abstract String getBrickLabel(View view);
}
