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
package org.catrobat.catroid.physics.content.bricks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class SetGravityBrick extends BrickBaseType implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private Formula gravityX;
	private Formula gravityY;

	private transient View prototypeView;

	public SetGravityBrick() {
	}

	public SetGravityBrick(Sprite sprite, Vector2 gravity) {
		this.sprite = sprite;
		this.gravityX = new Formula(gravity.x);
		this.gravityY = new Formula(gravity.y);
	}

	public SetGravityBrick(Sprite sprite, Formula gravityX, Formula gravityY) {
		this.sprite = sprite;
		this.gravityX = gravityX;
		this.gravityY = gravityY;
	}

	@Override
	public int getRequiredResources() {
		return PHYSIC;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		SetGravityBrick copyBrick = (SetGravityBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_physics_set_gravity, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_set_gravity_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textX = (TextView) view.findViewById(R.id.brick_set_gravity_prototype_text_view_x);
		EditText editX = (EditText) view.findViewById(R.id.brick_set_gravity_edit_text_x);
		gravityX.setTextFieldId(R.id.brick_set_gravity_edit_text_x);
		gravityX.refreshTextField(view);

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		editX.setOnClickListener(this);

		TextView textY = (TextView) view.findViewById(R.id.brick_set_gravity_prototype_text_view_y);
		EditText editY = (EditText) view.findViewById(R.id.brick_set_gravity_edit_text_y);
		gravityY.setTextFieldId(R.id.brick_set_gravity_edit_text_y);
		gravityY.refreshTextField(view);
		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);
		editY.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_physics_set_gravity, null);
		TextView textX = (TextView) prototypeView.findViewById(R.id.brick_set_gravity_prototype_text_view_x);
		textX.setText(String.valueOf(gravityX.interpretInteger(sprite)));
		TextView textY = (TextView) prototypeView.findViewById(R.id.brick_set_gravity_prototype_text_view_y);
		textY.setText(String.valueOf(gravityY.interpretInteger(sprite)));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new SetGravityBrick(getSprite(), gravityX.clone(), gravityY.clone());
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_set_gravity_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);

		TextView setGravityLabel = (TextView) view.findViewById(R.id.brick_set_gravity_label);
		TextView setGravityX = (TextView) view.findViewById(R.id.brick_set_gravity_x_textview);
		TextView setGravityY = (TextView) view.findViewById(R.id.brick_set_gravity_y_textview);
		TextView setGravityUnit = (TextView) view.findViewById(R.id.brick_set_gravity_unit);
		EditText editX = (EditText) view.findViewById(R.id.brick_set_gravity_edit_text_x);
		EditText editY = (EditText) view.findViewById(R.id.brick_set_gravity_edit_text_y);
		setGravityLabel.setTextColor(setGravityLabel.getTextColors().withAlpha(alphaValue));
		setGravityX.setTextColor(setGravityX.getTextColors().withAlpha(alphaValue));
		setGravityY.setTextColor(setGravityY.getTextColors().withAlpha(alphaValue));
		setGravityUnit.setTextColor(setGravityUnit.getTextColors().withAlpha(alphaValue));
		editX.setTextColor(editX.getTextColors().withAlpha(alphaValue));
		editX.getBackground().setAlpha(alphaValue);
		editY.setTextColor(editY.getTextColors().withAlpha(alphaValue));
		editY.getBackground().setAlpha(alphaValue);

		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		switch (view.getId()) {
			case R.id.brick_set_gravity_edit_text_x:
				FormulaEditorFragment.showFragment(view, this, gravityX);
				break;

			case R.id.brick_set_gravity_edit_text_y:
				FormulaEditorFragment.showFragment(view, this, gravityY);
				break;
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		//		sequence.addAction(ExtendedActions.setGravity(sprite, physicsWorld, gravityX, gravityY));
		sequence.addAction(sprite.getActionFactory().createSetGravityAction(sprite, gravityX, gravityY));
		return null;
	}
}