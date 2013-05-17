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

public class LegoNxtPlayToneBrick extends BrickBaseType implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	private transient EditText editFreq;

	private Formula frequency;
	private Formula durationInSeconds;

	public LegoNxtPlayToneBrick(Sprite sprite, int frequencyValue, int durationValue) {
		this.sprite = sprite;

		this.frequency = new Formula(frequencyValue);
		this.durationInSeconds = new Formula(durationValue);
	}

	public LegoNxtPlayToneBrick(Sprite sprite, Formula frequencyFormula, Formula durationFormula) {
		this.sprite = sprite;

		this.frequency = frequencyFormula;
		this.durationInSeconds = durationFormula;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		LegoNxtPlayToneBrick copyBrick = (LegoNxtPlayToneBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_nxt_play_tone, null);
		TextView textDuration = (TextView) prototypeView.findViewById(R.id.nxt_tone_duration_text_view);
		textDuration.setText(String.valueOf(durationInSeconds.interpretInteger(sprite)));
		TextView textFreq = (TextView) prototypeView.findViewById(R.id.nxt_tone_freq_text_view);
		textFreq.setText(String.valueOf(frequency.interpretInteger(sprite)));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new LegoNxtPlayToneBrick(getSprite(), frequency.clone(), durationInSeconds.clone());
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_nxt_play_tone, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_nxt_play_tone_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textDuration = (TextView) view.findViewById(R.id.nxt_tone_duration_text_view);
		EditText editDuration = (EditText) view.findViewById(R.id.nxt_tone_duration_edit_text);
		//		editDuration.setText(String.valueOf(durationInMs / 1000.0));
		durationInSeconds.setTextFieldId(R.id.nxt_tone_duration_edit_text);
		durationInSeconds.refreshTextField(view);
		//		EditDoubleDialog dialogDuration = new EditDoubleDialog(context, editDuration, duration, MIN_DURATION,
		//				MAX_DURATION);
		//		dialogDuration.setOnDismissListener(this);
		//		dialogDuration.setOnCancelListener((OnCancelListener) context);
		//		editDuration.setOnClickListener(dialogDuration);

		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);

		TextView textFreq = (TextView) view.findViewById(R.id.nxt_tone_freq_text_view);
		editFreq = (EditText) view.findViewById(R.id.nxt_tone_freq_edit_text);
		//		editFreq.setText(String.valueOf(hertz / 100));
		frequency.setTextFieldId(R.id.nxt_tone_freq_edit_text);
		frequency.refreshTextField(view);

		textFreq.setVisibility(View.GONE);
		editFreq.setVisibility(View.VISIBLE);

		editFreq.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		switch (view.getId()) {
			case R.id.nxt_tone_freq_edit_text:
				FormulaEditorFragment.showFragment(view, this, frequency);
				break;
			case R.id.nxt_tone_duration_edit_text:
				FormulaEditorFragment.showFragment(view, this, durationInSeconds);
				break;
		}
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_nxt_play_tone_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);

		TextView textLegoPlayToneLabel = (TextView) view.findViewById(R.id.brick_nxt_play_tone_label);
		TextView textLegoPlayToneDuration = (TextView) view.findViewById(R.id.brick_nxt_play_tone_duration);
		TextView textLegoPlayToneDurationTextView = (TextView) view.findViewById(R.id.nxt_tone_duration_text_view);
		TextView textLegoPlayToneSeconds = (TextView) view.findViewById(R.id.brick_nxt_play_tone_seconds);
		TextView textLegoPlayToneFrequency = (TextView) view.findViewById(R.id.brick_nxt_play_tone_frequency);
		TextView textLegoPlayToneOz = (TextView) view.findViewById(R.id.brick_nxt_play_tone_hundred_hz);

		EditText editLegoDuration = (EditText) view.findViewById(R.id.nxt_tone_duration_edit_text);
		EditText editLegoFrequency = (EditText) view.findViewById(R.id.nxt_tone_freq_edit_text);
		textLegoPlayToneLabel.setTextColor(textLegoPlayToneLabel.getTextColors().withAlpha(alphaValue));
		textLegoPlayToneDuration.setTextColor(textLegoPlayToneDuration.getTextColors().withAlpha(alphaValue));
		textLegoPlayToneDurationTextView.setTextColor(textLegoPlayToneDurationTextView.getTextColors().withAlpha(
				alphaValue));
		textLegoPlayToneSeconds.setTextColor(textLegoPlayToneSeconds.getTextColors().withAlpha(alphaValue));
		textLegoPlayToneFrequency.setTextColor(textLegoPlayToneFrequency.getTextColors().withAlpha(alphaValue));
		textLegoPlayToneOz.setTextColor(textLegoPlayToneOz.getTextColors().withAlpha(alphaValue));

		editLegoFrequency.setTextColor(editLegoFrequency.getTextColors().withAlpha(alphaValue));
		editLegoFrequency.getBackground().setAlpha(alphaValue);
		editLegoDuration.setTextColor(editLegoDuration.getTextColors().withAlpha(alphaValue));
		editLegoDuration.getBackground().setAlpha(alphaValue);

		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.legoNxtPlayTone(sprite, frequency, durationInSeconds));
		return null;
	}
}
