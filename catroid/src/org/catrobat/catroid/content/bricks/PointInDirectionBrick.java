/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class PointInDirectionBrick extends BrickBaseType implements View.OnClickListener, FormulaBrick {

	private static final long serialVersionUID = 1L;

	private Formula degrees;

	private transient View prototypeView;

	public static enum Direction {
		RIGHT(90), LEFT(-90), UP(0), DOWN(180);

		private double directionDegrees;

		private Direction(double degrees) {
			directionDegrees = degrees;
		}

		public double getDegrees() {
			return directionDegrees;
		}
	}

	public PointInDirectionBrick() {

	}

	public PointInDirectionBrick(Sprite sprite, Direction direction) {
		this.sprite = sprite;
		this.degrees = new Formula(direction.getDegrees());
	}

	public PointInDirectionBrick(Sprite sprite, Formula direction) {
		this.sprite = sprite;
		this.degrees = direction;
	}

	public PointInDirectionBrick(Sprite sprite, double direction) {
		this.sprite = sprite;
		this.degrees = new Formula(direction);
	}

	@Override
	public Formula getFormula() {
		return degrees;
	}

	@Override
	public int getRequiredResources() {
		return degrees.getRequiredResources();
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		PointInDirectionBrick copyBrick = (PointInDirectionBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_point_in_direction, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_point_in_direction_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView setAngleTextView = (TextView) view.findViewById(R.id.brick_point_in_direction_prototype_text_view);
		TextView setAngleTextField = (TextView) view.findViewById(R.id.brick_point_in_direction_edit_text);

		degrees.setTextFieldId(R.id.brick_point_in_direction_edit_text);
		degrees.refreshTextField(view);

		setAngleTextView.setVisibility(View.GONE);
		setAngleTextField.setVisibility(View.VISIBLE);

		setAngleTextField.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_point_in_direction, null);
		TextView setAngleTextView = (TextView) prototypeView
				.findViewById(R.id.brick_point_in_direction_prototype_text_view);
		setAngleTextView.setText(String.valueOf(degrees.interpretDouble(sprite)));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new PointInDirectionBrick(getSprite(), degrees.clone());
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_point_in_direction_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView pointInDirectionLabel = (TextView) view.findViewById(R.id.brick_point_in_direction_label);
			TextView pointInDirectionDegree = (TextView) view.findViewById(R.id.brick_point_in_direction_degree);
			TextView setAngleTextView = (TextView) view.findViewById(R.id.brick_point_in_direction_edit_text);
			pointInDirectionLabel.setTextColor(pointInDirectionLabel.getTextColors().withAlpha(alphaValue));
			pointInDirectionDegree.setTextColor(pointInDirectionDegree.getTextColors().withAlpha(alphaValue));
			setAngleTextView.setTextColor(setAngleTextView.getTextColors().withAlpha(alphaValue));
			setAngleTextView.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, degrees);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.pointInDirection(sprite, degrees));
		return null;
	}
}
