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
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.LinkedList;
import java.util.List;

public class RepeatBrick extends LoopBeginBrick implements OnClickListener, FormulaBrick {
	private static final long serialVersionUID = 1L;
	private Formula timesToRepeat;

	private transient View prototypeView;

	public RepeatBrick(Sprite sprite, int timesToRepeatValue) {
		this.sprite = sprite;
		timesToRepeat = new Formula(timesToRepeatValue);
	}

	public RepeatBrick(Sprite sprite, Formula timesToRepeat) {
		this.sprite = sprite;
		this.timesToRepeat = timesToRepeat;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public RepeatBrick() {

	}

	@Override
	public Formula getFormula() {
		return timesToRepeat;
	}

	@Override
	public Brick clone() {
		return new RepeatBrick(sprite, timesToRepeat.clone());
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_repeat, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_repeat_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				checked = !checked;
				if (!checked) {
					for (Brick currentBrick : adapter.getCheckedBricks()) {
						currentBrick.setCheckedBoolean(false);
					}
				}
				adapter.handleCheck(brickInstance, checked);
			}
		});

		TextView text = (TextView) view.findViewById(R.id.brick_repeat_prototype_text_view);
		TextView edit = (TextView) view.findViewById(R.id.brick_repeat_edit_text);
		timesToRepeat.setTextFieldId(R.id.brick_repeat_edit_text);
		timesToRepeat.refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_repeat_time_text_view);

		if (timesToRepeat.isSingleNumberFormula()) {
			times.setText(view.getResources().getQuantityString(R.plurals.time_plural,
					Utils.convertDoubleToPluralInteger(timesToRepeat.interpretDouble(sprite))));
		} else {

			// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
			// in hopefully all possible languages
			times.setText(view.getResources().getQuantityString(R.plurals.time_plural,
					Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
		}

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);

		edit.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_repeat, null);
		TextView textRepeat = (TextView) prototypeView.findViewById(R.id.brick_repeat_prototype_text_view);
		textRepeat.setText(String.valueOf(timesToRepeat.interpretInteger(sprite)));
		TextView times = (TextView) prototypeView.findViewById(R.id.brick_repeat_time_text_view);
		times.setText(context.getResources().getQuantityString(R.plurals.time_plural,
				Utils.convertDoubleToPluralInteger(timesToRepeat.interpretDouble(sprite))));
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_repeat_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView repeatLabel = (TextView) view.findViewById(R.id.brick_repeat_label);
			TextView editRepeat = (TextView) view.findViewById(R.id.brick_repeat_edit_text);
			TextView times = (TextView) view.findViewById(R.id.brick_repeat_time_text_view);
			repeatLabel.setTextColor(repeatLabel.getTextColors().withAlpha(alphaValue));
			times.setTextColor(times.getTextColors().withAlpha(alphaValue));
			editRepeat.setTextColor(editRepeat.getTextColors().withAlpha(alphaValue));
			editRepeat.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, timesToRepeat);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		SequenceAction repeatSequence = ExtendedActions.sequence();
		Action action = ExtendedActions.repeat(sprite, timesToRepeat, repeatSequence);
		sequence.addAction(action);
		LinkedList<SequenceAction> returnActionList = new LinkedList<SequenceAction>();
		returnActionList.add(repeatSequence);
		return returnActionList;
	}
}
