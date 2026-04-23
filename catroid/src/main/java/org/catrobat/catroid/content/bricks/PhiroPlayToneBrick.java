/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

import kotlin.Unit;

public class PhiroPlayToneBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private String tone;

	public enum Tone {
		DO, RE, MI, FA, SO, LA, TI
	}

	public PhiroPlayToneBrick() {
		tone = Tone.DO.name();
		addAllowedBrickField(BrickField.PHIRO_DURATION_IN_SECONDS, R.id.brick_phiro_play_tone_duration_edit_text);
	}

	public PhiroPlayToneBrick(Tone toneEnum, int duration) {
		this(toneEnum, new Formula(duration));
	}

	public PhiroPlayToneBrick(Tone toneEnum, Formula formula) {
		this();
		tone = toneEnum.name();
		setFormulaWithBrickField(BrickField.PHIRO_DURATION_IN_SECONDS, formula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_phiro_play_tone;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(BLUETOOTH_LEGO_EV3);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_phiro_select_tone_spinner, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = view.findViewById(R.id.brick_phiro_select_tone_spinner);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			tone = Tone.values()[position].name();
			return Unit.INSTANCE;
		}));
		spinner.setSelection(Tone.valueOf(tone).ordinal());
		setSecondsLabel(view, BrickField.PHIRO_DURATION_IN_SECONDS);
		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPhiroPlayToneActionAction(sprite,
				sequence, Tone.valueOf(tone),
				getFormulaWithBrickField(BrickField.PHIRO_DURATION_IN_SECONDS)));
		sequence.addAction(sprite.getActionFactory()
				.createDelayAction(sprite, sequence,
						getFormulaWithBrickField(BrickField.PHIRO_DURATION_IN_SECONDS)));
	}
}
