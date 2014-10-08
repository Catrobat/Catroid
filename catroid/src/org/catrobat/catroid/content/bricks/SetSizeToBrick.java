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
import android.view.View.OnClickListener;
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

public class SetSizeToBrick extends BrickBaseType implements OnClickListener, FormulaBrick {
	private static final long serialVersionUID = 1L;
	private Formula size;

	private transient View prototypeView;

	public SetSizeToBrick(Sprite sprite, double sizeValue) {
		this.sprite = sprite;
		size = new Formula(sizeValue);
	}

	public SetSizeToBrick(Sprite sprite, Formula size) {
		this.sprite = sprite;
		this.size = size;
	}

	public SetSizeToBrick() {

	}

	@Override
	public Formula getFormula() {
		return size;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		SetSizeToBrick copyBrick = (SetSizeToBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_set_size_to, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_set_size_to_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});
		TextView text = (TextView) view.findViewById(R.id.brick_set_size_to_prototype_text_view);
		TextView edit = (TextView) view.findViewById(R.id.brick_set_size_to_edit_text);
		size.setTextFieldId(R.id.brick_set_size_to_edit_text);
		size.refreshTextField(view);
		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);
		edit.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_set_size_to, null);
		TextView textSetSizeTo = (TextView) prototypeView.findViewById(R.id.brick_set_size_to_prototype_text_view);
		textSetSizeTo.setText(String.valueOf(size.interpretDouble(sprite)));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new SetSizeToBrick(getSprite(), size.clone());
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_set_size_to_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textSize = (TextView) view.findViewById(R.id.brick_set_size_to_label);
			TextView textPercent = (TextView) view.findViewById(R.id.brick_set_size_to_percent);
			TextView editSize = (TextView) view.findViewById(R.id.brick_set_size_to_edit_text);
			textSize.setTextColor(textSize.getTextColors().withAlpha(alphaValue));
			textPercent.setTextColor(textPercent.getTextColors().withAlpha(alphaValue));
			editSize.setTextColor(editSize.getTextColors().withAlpha(alphaValue));
			editSize.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, size);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.setSizeTo(sprite, size));
		return null;
	}
}
