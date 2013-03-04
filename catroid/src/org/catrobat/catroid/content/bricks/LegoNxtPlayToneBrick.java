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
import org.catrobat.catroid.LegoNXT.LegoNXT;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class LegoNxtPlayToneBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;

	private static final int MIN_FREQ_IN_HERTZ = 200;
	private static final int MAX_FREQ_IN_HERTZ = 14000;
	private static final int MIN_DURATION = 0;
	private static final int MAX_DURATION = Integer.MAX_VALUE;

	private Sprite sprite;

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
	public void execute() {
		int frequencyValue = frequency.interpretInteger(MIN_FREQ_IN_HERTZ, MAX_FREQ_IN_HERTZ);
		int durationInMillisecondsValue = (int) durationInSeconds.interpretFloat(MIN_DURATION, MAX_DURATION) * 1000;

		LegoNXT.sendBTCPlayToneMessage(frequencyValue, durationInMillisecondsValue);

	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_nxt_play_tone, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new LegoNxtPlayToneBrick(getSprite(), frequency, durationInSeconds);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		View brickView = View.inflate(context, R.layout.brick_nxt_play_tone, null);

		TextView textDuration = (TextView) brickView.findViewById(R.id.nxt_tone_duration_text_view);
		EditText editDuration = (EditText) brickView.findViewById(R.id.nxt_tone_duration_edit_text);
		//		editDuration.setText(String.valueOf(durationInMs / 1000.0));
		durationInSeconds.setTextFieldId(R.id.nxt_tone_duration_edit_text);
		durationInSeconds.refreshTextField(brickView);
		//		EditDoubleDialog dialogDuration = new EditDoubleDialog(context, editDuration, duration, MIN_DURATION,
		//				MAX_DURATION);
		//		dialogDuration.setOnDismissListener(this);
		//		dialogDuration.setOnCancelListener((OnCancelListener) context);
		//		editDuration.setOnClickListener(dialogDuration);

		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);

		TextView textFreq = (TextView) brickView.findViewById(R.id.nxt_tone_freq_text_view);
		editFreq = (EditText) brickView.findViewById(R.id.nxt_tone_freq_edit_text);
		//		editFreq.setText(String.valueOf(hertz / 100));
		frequency.setTextFieldId(R.id.nxt_tone_freq_edit_text);
		frequency.refreshTextField(brickView);

		textFreq.setVisibility(View.GONE);
		editFreq.setVisibility(View.VISIBLE);

		editFreq.setOnClickListener(this);

		return brickView;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.nxt_tone_freq_edit_text:
				FormulaEditorFragment.showFragment(view, this, frequency);
				break;
			case R.id.nxt_tone_duration_edit_text:
				FormulaEditorFragment.showFragment(view, this, durationInSeconds);
				break;
		}

	}

}
