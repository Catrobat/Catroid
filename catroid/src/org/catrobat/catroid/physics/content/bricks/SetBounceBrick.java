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

public class SetBounceBrick extends BrickBaseType implements OnClickListener, Cloneable{
	private static final long serialVersionUID = 1L;

	private Formula bounceFactor;

	private transient View prototypeView;

	public SetBounceBrick() {
	}

	public SetBounceBrick(Sprite sprite, float bounceFactor) {
		this.sprite = sprite;
		this.bounceFactor = new Formula(bounceFactor);
	}

	public SetBounceBrick(Sprite sprite, Formula bounceFactor) {
		this.sprite = sprite;
		this.bounceFactor = bounceFactor;
	}

	@Override
	public int getRequiredResources() {
		return PHYSIC;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		SetBounceBrick copyBrick = (SetBounceBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_physics_set_bounce_factor, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_set_bounce_factor_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView text = (TextView) view.findViewById(R.id.brick_set_bounce_factor_prototype_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_set_bounce_factor_edit_text);

		bounceFactor.setTextFieldId(R.id.brick_set_bounce_factor_edit_text);
		bounceFactor.refreshTextField(view);

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);
		edit.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_physics_set_bounce_factor, null);
		TextView textXPosition = (TextView) prototypeView
				.findViewById(R.id.brick_set_bounce_factor_prototype_text_view);
		textXPosition.setText(String.valueOf((bounceFactor.interpretInteger(sprite))));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_set_bounce_factor_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);

		TextView textView = (TextView) view.findViewById(R.id.brick_set_bounce_factor_prototype_text_view);
		EditText editText = (EditText) view.findViewById(R.id.brick_set_bounce_factor_edit_text);
		textView.setTextColor(textView.getTextColors().withAlpha(alphaValue));
		editText.setTextColor(editText.getTextColors().withAlpha(alphaValue));
		editText.getBackground().setAlpha(alphaValue);

		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public Brick clone() {
		return new SetBounceBrick(getSprite(), bounceFactor.clone());
	}

	@Override
	public void onClick(final View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, bounceFactor);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		//		sequence.addAction(ExtendedActions.setBounceFactor(sprite, physicsObject, bounceFactor));
		sequence.addAction(sprite.getActionFactory().createSetBounceFactorAction(sprite, bounceFactor));
		return null;
	}
}