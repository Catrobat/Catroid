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

public class GlideToBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private Formula xDestination;
	private Formula yDestination;
	private Formula durationInSeconds;
	private Sprite sprite;

	private transient View view;

	public GlideToBrick() {

	}

	public GlideToBrick(Sprite sprite, int xDestinationValue, int yDestinationValue, int durationInMilliSecondsValue) {
		this.sprite = sprite;

		xDestination = new Formula(xDestinationValue);
		yDestination = new Formula(yDestinationValue);
		durationInSeconds = new Formula(durationInMilliSecondsValue / 1000.0);
	}

	public GlideToBrick(Sprite sprite, Formula xDestination, Formula yDestination, Formula durationInSeconds) {
		this.sprite = sprite;

		this.xDestination = xDestination;
		this.yDestination = yDestination;
		this.durationInSeconds = durationInSeconds;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	public int getDurationInMilliSeconds() {
		return durationInSeconds.interpretInteger(sprite);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_glide_to, null);

		TextView textX = (TextView) view.findViewById(R.id.brick_glide_to_prototype_text_view_x);
		EditText editX = (EditText) view.findViewById(R.id.brick_glide_to_edit_text_x);
		xDestination.setTextFieldId(R.id.brick_glide_to_edit_text_x);
		xDestination.refreshTextField(view);
		editX.setOnClickListener(this);

		TextView textY = (TextView) view.findViewById(R.id.brick_glide_to_prototype_text_view_y);
		EditText editY = (EditText) view.findViewById(R.id.brick_glide_to_edit_text_y);
		yDestination.setTextFieldId(R.id.brick_glide_to_edit_text_y);
		yDestination.refreshTextField(view);
		editY.setOnClickListener(this);

		TextView textDuration = (TextView) view.findViewById(R.id.brick_glide_to_prototype_text_view_duration);
		EditText editDuration = (EditText) view.findViewById(R.id.brick_glide_to_edit_text_duration);
		durationInSeconds.setTextFieldId(R.id.brick_glide_to_edit_text_duration);
		durationInSeconds.refreshTextField(view);

		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);
		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_glide_to, null);
	}

	@Override
	public Brick clone() {
		return new GlideToBrick(getSprite(), xDestination, yDestination, durationInSeconds);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.brick_glide_to_edit_text_x:
				FormulaEditorFragment.showFragment(view, this, xDestination);
				break;

			case R.id.brick_glide_to_edit_text_y:
				FormulaEditorFragment.showFragment(view, this, yDestination);
				break;

			case R.id.brick_glide_to_edit_text_duration:
				FormulaEditorFragment.showFragment(view, this, durationInSeconds);
				break;
		}

	}

	@Override
	public SequenceAction addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.glideTo(sprite, xDestination, yDestination, durationInSeconds));
		return null;
	}
}
