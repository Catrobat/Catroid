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

public class SetMassBrick extends BrickBaseType implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private Formula mass;

	private transient View prototypeView;

	public SetMassBrick() {
	}

	public SetMassBrick(Sprite sprite, float mass) {
		this.sprite = sprite;
		this.mass = new Formula(mass);
	}

	public SetMassBrick(Sprite sprite, Formula mass) {
		this.sprite = sprite;
		this.mass = mass;
	}

	@Override
	public int getRequiredResources() {
		return PHYSIC;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		SetMassBrick copyBrick = (SetMassBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_physics_set_mass, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_set_mass_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView text = (TextView) view.findViewById(R.id.brick_set_mass_prototype_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_set_mass_edit_text);

		mass.setTextFieldId(R.id.brick_set_mass_edit_text);
		mass.refreshTextField(view);

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);
		edit.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_physics_set_mass, null);
		TextView textXPosition = (TextView) prototypeView.findViewById(R.id.brick_set_mass_prototype_text_view);
		textXPosition.setText(String.valueOf(mass.interpretInteger(sprite)));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_set_mass_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);

		TextView textX = (TextView) view.findViewById(R.id.brick_set_mass_text_view);
		EditText editX = (EditText) view.findViewById(R.id.brick_set_mass_edit_text);
		textX.setTextColor(textX.getTextColors().withAlpha(alphaValue));
		editX.setTextColor(editX.getTextColors().withAlpha(alphaValue));
		editX.getBackground().setAlpha(alphaValue);

		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public Brick clone() {
		return new SetMassBrick(getSprite(), mass.clone());
	}

	@Override
	public void onClick(final View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, mass);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		//		sequence.addAction(ExtendedActions.setMass(sprite, physicsObject, mass));
		sequence.addAction(sprite.getActionFactory().createSetMassAction(sprite, mass));
		return null;
	}
}
