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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class SetFrictionBrick extends BrickBaseType implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private Formula friction;

	private transient View prototypeView;

	public SetFrictionBrick() {
	}

	public SetFrictionBrick(Sprite sprite, float friction) {
		this.sprite = sprite;
		this.friction = new Formula(friction);
	}

	//	public SetFrictionBrick(Sprite sprite, Formula friction) {
	//		this.sprite = sprite;
	//		this.friction = friction;
	//	}

	@Override
	public int getRequiredResources() {
		return PHYSIC;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		SetFrictionBrick copyBrick = (SetFrictionBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_physics_set_friction, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_set_friction_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView text = (TextView) view.findViewById(R.id.brick_set_friction_prototype_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_set_friction_edit_text);

		friction.setTextFieldId(R.id.brick_set_friction_edit_text);
		friction.refreshTextField(view);

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);
		edit.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_physics_set_friction, null);
		TextView textXPosition = (TextView) prototypeView.findViewById(R.id.brick_set_friction_prototype_text_view);
		textXPosition.setText(String.valueOf(friction.interpretInteger(sprite)));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_set_friction_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);

		TextView textView = (TextView) view.findViewById(R.id.brick_set_friction_text_view);
		EditText editText = (EditText) view.findViewById(R.id.brick_set_friction_edit_text);
		textView.setTextColor(textView.getTextColors().withAlpha(alphaValue));
		editText.setTextColor(editText.getTextColors().withAlpha(alphaValue));
		editText.getBackground().setAlpha(alphaValue);

		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public Brick clone() {
		return new SetFrictionBrick(getSprite(), friction.clone().interpretFloat(sprite));
	}

	@Override
	public void onClick(final View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, friction);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		//		sequence.addAction(ExtendedActions.setFriction(sprite, physicsObject, friction));
		sequence.addAction(sprite.getActionFactory().createSetFrictionAction(sprite, friction));
		return null;
	}
}