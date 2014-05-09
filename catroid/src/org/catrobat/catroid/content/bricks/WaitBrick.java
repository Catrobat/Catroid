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
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class WaitBrick extends BrickBaseType implements OnClickListener, FormulaBrick {
	private static final long serialVersionUID = 1L;
	private Formula timeToWaitInSeconds;

	private transient View prototypeView;

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
	public Formula getFormula() {
		return timeToWaitInSeconds;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public Formula getTimeToWait() {
		return timeToWaitInSeconds;
	}

	public void setTimeToWait(Formula timeToWaitInSeconds) {
		this.timeToWaitInSeconds = timeToWaitInSeconds;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		WaitBrick copyBrick = (WaitBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_wait, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_wait_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView text = (TextView) view.findViewById(R.id.brick_wait_prototype_text_view);
		TextView edit = (TextView) view.findViewById(R.id.brick_wait_edit_text);
		timeToWaitInSeconds.setTextFieldId(R.id.brick_wait_edit_text);
		timeToWaitInSeconds.refreshTextField(view);

		TextView times = (TextView) view.findViewById(R.id.brick_wait_second_text_view);

		if (timeToWaitInSeconds.isSingleNumberFormula()) {
			times.setText(view.getResources().getQuantityString(R.plurals.second_plural,
					Utils.convertDoubleToPluralInteger(timeToWaitInSeconds.interpretDouble(sprite))));
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
		prototypeView = View.inflate(context, R.layout.brick_wait, null);
		TextView textWait = (TextView) prototypeView.findViewById(R.id.brick_wait_prototype_text_view);
		textWait.setText(String.valueOf(timeToWaitInSeconds.interpretInteger(sprite)));
		TextView times = (TextView) prototypeView.findViewById(R.id.brick_wait_second_text_view);
		times.setText(context.getResources().getQuantityString(R.plurals.second_plural,
				Utils.convertDoubleToPluralInteger(timeToWaitInSeconds.interpretDouble(sprite))));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new WaitBrick(getSprite(), timeToWaitInSeconds.clone());
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_wait_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textWaitLabel = (TextView) view.findViewById(R.id.brick_wait_label);
			TextView textWaitSeconds = (TextView) view.findViewById(R.id.brick_wait_second_text_view);
			TextView editWait = (TextView) view.findViewById(R.id.brick_wait_edit_text);

			textWaitLabel.setTextColor(textWaitLabel.getTextColors().withAlpha(alphaValue));
			textWaitSeconds.setTextColor(textWaitSeconds.getTextColors().withAlpha(alphaValue));
			editWait.setTextColor(editWait.getTextColors().withAlpha(alphaValue));
			editWait.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, timeToWaitInSeconds);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		//sequence.addAction(ExtendedActions.delay(sprite, timeToWaitInSeconds));
		sequence.addAction(sprite.getActionFactory().createWaitAction(sprite, timeToWaitInSeconds)); // TODO[physics]
		return null;
	}
}
