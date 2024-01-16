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
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageAttributes;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;

import java.util.ArrayList;
import java.util.Collection;

import kotlin.Unit;

@CatrobatLanguageBrick(command = "Play Phiro")
public class PhiroPlayToneBrick extends FormulaBrick implements UpdateableSpinnerBrick, CatrobatLanguageAttributes {

	private static final long serialVersionUID = 1L;

	private String tone;

	public enum Tone {
		DO, RE, MI, FA, SO, LA, TI
	}

	public PhiroPlayToneBrick() {
		tone = Tone.DO.name();
		addAllowedBrickField(BrickField.PHIRO_DURATION_IN_SECONDS, R.id.brick_phiro_play_tone_duration_edit_text, "seconds");
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

	@Override
	public void updateSelectedItem(Context context, int spinnerId, String itemName, int itemIndex) {
		Tone[] tones = Tone.values();
		if (itemIndex >= 0 && itemIndex < tones.length) {
			tone = tones[itemIndex].name();
		}
	}

	@Override
	protected String getCatrobatLanguageSpinnerValue(int spinnerIndex) {
		switch (spinnerIndex) {
			case 0:
				return "do";
			case 1:
				return "re";
			case 2:
				return "mi";
			case 3:
				return "fa";
			case 4:
				return "so";
			case 5:
				return "la";
			case 6:
				return "ti";
			default:
				throw new IndexOutOfBoundsException("Invalid spinnerIndex");
		}
	}

	@Override
	public void appendCatrobatLanguageArguments(StringBuilder brickBuilder) {
		brickBuilder.append("tone: (");
		brickBuilder.append(this.getCatrobatLanguageSpinnerValue(Tone.valueOf(tone).ordinal()));
		brickBuilder.append("), ");
		super.appendCatrobatLanguageArguments(brickBuilder);
	}

	@Override
	protected Collection<String> getRequiredArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredArgumentNames());
		requiredArguments.add("tone");
		return requiredArguments;
	}
}
