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
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class ChangeBrightnessByNBrick extends BrickBaseType implements OnClickListener {
	private static final long serialVersionUID = 1L;
	private Formula changeBrightness;

	private transient View prototypeView;

	public ChangeBrightnessByNBrick() {

	}

	public ChangeBrightnessByNBrick(Sprite sprite, double changeBrightnessValue) {
		this.sprite = sprite;
		changeBrightness = new Formula(changeBrightnessValue);
	}

	public ChangeBrightnessByNBrick(Sprite sprite, Formula changeBrightness) {
		this.sprite = sprite;
		this.changeBrightness = changeBrightness;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		ChangeBrightnessByNBrick copyBrick = (ChangeBrightnessByNBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_change_brightness, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_change_brightness_checkbox);
		final Brick brickInstance = this;

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});
		TextView textX = (TextView) view.findViewById(R.id.brick_change_brightness_prototype_text_view);
		EditText editX = (EditText) view.findViewById(R.id.brick_change_brightness_edit_text);
		changeBrightness.setTextFieldId(R.id.brick_change_brightness_edit_text);
		changeBrightness.refreshTextField(view);

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);

		editX.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_change_brightness, null);
		TextView textChangeBrightness = (TextView) prototypeView
				.findViewById(R.id.brick_change_brightness_prototype_text_view);
		textChangeBrightness.setText(String.valueOf(changeBrightness.interpretFloat(sprite)));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new ChangeBrightnessByNBrick(getSprite(), changeBrightness.clone());
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_change_brightness_layout);
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
		FormulaEditorFragment.showFragment(view, this, changeBrightness);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {

		sequence.addAction(ExtendedActions.changeBrightnessByN(sprite, changeBrightness));
		return null;
	}
}
