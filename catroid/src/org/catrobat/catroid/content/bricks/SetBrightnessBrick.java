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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class SetBrightnessBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private Formula brightness;
	private Sprite sprite;

	private transient View view;
	private transient View prototypeView;

	public SetBrightnessBrick() {

	}

	public SetBrightnessBrick(Sprite sprite, double brightnessValue) {
		this.sprite = sprite;
		brightness = new Formula(brightnessValue);
	}

	public SetBrightnessBrick(Sprite sprite, Formula brightness) {
		this.sprite = sprite;
		this.brightness = brightness;
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
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_set_brightness, null);

		TextView textX = (TextView) view.findViewById(R.id.brick_set_brightness_prototype_text_view);
		EditText editX = (EditText) view.findViewById(R.id.brick_set_brightness_edit_text);
		brightness.setTextFieldId(R.id.brick_set_brightness_edit_text);
		brightness.refreshTextField(view);
		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);

		editX.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_set_brightness, null);
		TextView textSetBrightness = (TextView) prototypeView
				.findViewById(R.id.brick_set_brightness_prototype_text_view);
		textSetBrightness.setText(String.valueOf(brightness.interpretFloat(sprite)));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new SetBrightnessBrick(getSprite(), brightness.clone());
	}

	@Override
	public void onClick(View view) {
		FormulaEditorFragment.showFragment(view, this, brightness);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.setBrightness(sprite, brightness));
		return null;
	}
}
