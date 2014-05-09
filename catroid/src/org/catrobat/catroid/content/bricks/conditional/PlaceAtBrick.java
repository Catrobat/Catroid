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
package org.catrobat.catroid.content.bricks.conditional;

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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class PlaceAtBrick extends BrickBaseType implements OnClickListener, FormulaBrick {
	private static final long serialVersionUID = 1L;
	private Formula xPosition;
	private Formula yPosition;

	private transient View prototypeView;

	public PlaceAtBrick() {

	}

	public PlaceAtBrick(Sprite sprite, int xPositionValue, int yPositionValue) {
		this.sprite = sprite;

		xPosition = new Formula(xPositionValue);
		yPosition = new Formula(yPositionValue);
	}

	public PlaceAtBrick(Sprite sprite, Formula xPosition, Formula yPosition) {
		this.sprite = sprite;

		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}

	@Override
	public Formula getFormula() {
		return xPosition;
	}

	public void setXPosition(Formula xPosition) {
		this.xPosition = xPosition;
	}

	public void setYPosition(Formula yPosition) {
		this.yPosition = yPosition;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		PlaceAtBrick copyBrick = (PlaceAtBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_place_at, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_place_at_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textX = (TextView) view.findViewById(R.id.brick_place_at_prototype_text_view_x);
		TextView editX = (TextView) view.findViewById(R.id.brick_place_at_edit_text_x);
		xPosition.setTextFieldId(R.id.brick_place_at_edit_text_x);
		xPosition.refreshTextField(view);

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		editX.setOnClickListener(this);

		TextView textY = (TextView) view.findViewById(R.id.brick_place_at_prototype_text_view_y);
		TextView editY = (TextView) view.findViewById(R.id.brick_place_at_edit_text_y);
		yPosition.setTextFieldId(R.id.brick_place_at_edit_text_y);
		yPosition.refreshTextField(view);
		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);
		editY.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_place_at, null);
		TextView textX = (TextView) prototypeView.findViewById(R.id.brick_place_at_prototype_text_view_x);
		textX.setText(String.valueOf(xPosition.interpretInteger(sprite)));
		TextView textY = (TextView) prototypeView.findViewById(R.id.brick_place_at_prototype_text_view_y);
		textY.setText(String.valueOf(yPosition.interpretInteger(sprite)));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new PlaceAtBrick(getSprite(), xPosition.clone(), yPosition.clone());
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_place_at_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView placeAtLabel = (TextView) view.findViewById(R.id.brick_place_at_label);
			TextView placeAtX = (TextView) view.findViewById(R.id.brick_place_at_x_textview);
			TextView placeAtY = (TextView) view.findViewById(R.id.brick_place_at_y_textview);
			TextView editX = (TextView) view.findViewById(R.id.brick_place_at_edit_text_x);
			TextView editY = (TextView) view.findViewById(R.id.brick_place_at_edit_text_y);
			placeAtLabel.setTextColor(placeAtLabel.getTextColors().withAlpha(alphaValue));
			placeAtX.setTextColor(placeAtX.getTextColors().withAlpha(alphaValue));
			placeAtY.setTextColor(placeAtY.getTextColors().withAlpha(alphaValue));
			editX.setTextColor(editX.getTextColors().withAlpha(alphaValue));
			editX.getBackground().setAlpha(alphaValue);
			editY.setTextColor(editY.getTextColors().withAlpha(alphaValue));
			editY.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		switch (view.getId()) {
			case R.id.brick_place_at_edit_text_x:
				FormulaEditorFragment.showFragment(view, this, xPosition);
				break;

			case R.id.brick_place_at_edit_text_y:
				FormulaEditorFragment.showFragment(view, this, yPosition);
				break;
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		//		sequence.addAction(ExtendedActions.placeAt(sprite, xPosition, yPosition));
		sequence.addAction(sprite.getActionFactory().createPlaceAtAction(sprite, xPosition, yPosition)); // TODO[physics]
		return null;
	}
}
