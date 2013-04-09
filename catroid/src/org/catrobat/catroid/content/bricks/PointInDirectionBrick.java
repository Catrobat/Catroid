/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class PointInDirectionBrick extends BrickBaseType implements View.OnClickListener {

	private static final long serialVersionUID = 1L;

	private Formula degrees;

	private transient Direction direction;
	private transient EditText setAngleEditText;
	private transient View prototypeView;

	public static enum Direction {
		DIRECTION_RIGHT(90), DIRECTION_LEFT(-90), DIRECTION_UP(0), DIRECTION_DOWN(180);

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
		this.direction = direction;
		this.degrees = new Formula(direction.getDegrees());
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
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
		setAngleEditText = (EditText) view.findViewById(R.id.brick_point_in_direction_edit_text);

		degrees.setTextFieldId(R.id.brick_point_in_direction_edit_text);
		degrees.refreshTextField(view);

		setAngleTextView.setVisibility(View.GONE);
		setAngleEditText.setVisibility(View.VISIBLE);

		setAngleEditText.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_point_in_direction, null);
		TextView setAngleTextView = (TextView) prototypeView
				.findViewById(R.id.brick_point_in_direction_prototype_text_view);
		setAngleTextView.setText(String.valueOf(degrees.interpretFloat(sprite)));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new PointInDirectionBrick(getSprite(), direction);
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_point_in_direction_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
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
