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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class LegoNxtPlayToneBrick extends FormulaBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;

	public LegoNxtPlayToneBrick() {
		addAllowedBrickField(BrickField.LEGO_NXT_FREQUENCY);
		addAllowedBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS);
	}

	public LegoNxtPlayToneBrick(int frequencyValue, int durationValue) {
		initializeBrickFields(new Formula(frequencyValue), new Formula(durationValue));
	}

	public LegoNxtPlayToneBrick(Formula frequencyFormula, Formula durationFormula) {
		initializeBrickFields(frequencyFormula, durationFormula);
	}

	private void initializeBrickFields(Formula frequencyFormula, Formula durationFormula) {
		addAllowedBrickField(BrickField.LEGO_NXT_FREQUENCY);
		addAllowedBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS);
		setFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY, frequencyFormula);
		setFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS, durationFormula);
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT | getFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY).getRequiredResources() | getFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS).getRequiredResources();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		//OK
		view = View.inflate(context, R.layout.brick_nxt_play_tone, null);

		TextView editDuration = (TextView) view.findViewById(R.id.nxt_tone_duration_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS)
				.setTextFieldId(R.id.nxt_tone_duration_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS).refreshTextField(view);

		editDuration.setOnClickListener(this);

		TextView editFreq = (TextView) view.findViewById(R.id.nxt_tone_freq_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY).setTextFieldId(R.id.nxt_tone_freq_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY).refreshTextField(view);

		editFreq.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View view) {
		if (!clickAllowed()) {
			return;
		}
//		if (checkbox.getVisibility() == View.VISIBLE) {
//			return;
//		}
		switch (view.getId()) {
			case R.id.nxt_tone_freq_edit_text:
				FormulaEditorFragment.showFragment(view, this, getFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY));
				break;
			case R.id.nxt_tone_duration_edit_text:
				FormulaEditorFragment.showFragment(view, this,
						getFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS));
				break;
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.legoNxtPlayTone(sprite,
				getFormulaWithBrickField(BrickField.LEGO_NXT_FREQUENCY),
				getFormulaWithBrickField(BrickField.LEGO_NXT_DURATION_IN_SECONDS)));
		return null;
	}
}
