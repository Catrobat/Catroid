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
import org.catrobat.catroid.utils.Utils;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class WaitBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private Formula timeToWaitInSeconds;
	private Sprite sprite;

	private transient View view;

	public WaitBrick(Sprite sprite, int timeToWaitInMillisecondsValue) {
		this.sprite = sprite;
		timeToWaitInSeconds = new Formula(timeToWaitInMillisecondsValue / 1000.0);
	}

	public WaitBrick(Sprite sprite, Formula timeToWaitInSecondsFormula) {
		this.sprite = sprite;
		this.timeToWaitInSeconds = timeToWaitInSecondsFormula;
	}

	public WaitBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		view = View.inflate(context, R.layout.brick_wait, null);

		TextView text = (TextView) view.findViewById(R.id.brick_wait_prototype_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_wait_edit_text);
		timeToWaitInSeconds.setTextFieldId(R.id.brick_wait_edit_text);
		timeToWaitInSeconds.refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_wait_second_text_view);

		if (timeToWaitInSeconds.isSingleNumberFormula()) {
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.convertDoubleToPluralInteger(timeToWaitInSeconds.interpretFloat(sprite))));
		} else {

			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);
		edit.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_wait, null);
		TextView times = (TextView) view.findViewById(R.id.brick_wait_second_text_view);
		if (timeToWaitInSeconds.isSingleNumberFormula()) {
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.convertDoubleToPluralInteger(timeToWaitInSeconds.interpretFloat(sprite))));
		} else {

			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}
		return view;
	}

	@Override
	public Brick clone() {
		return new WaitBrick(getSprite(), timeToWaitInSeconds);
	}

	@Override
	public void onClick(View view) {
		FormulaEditorFragment.showFragment(view, this, timeToWaitInSeconds);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.delay(sprite, timeToWaitInSeconds));
		return null;
	}
}
