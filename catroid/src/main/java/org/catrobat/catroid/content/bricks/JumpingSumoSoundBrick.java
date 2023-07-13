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

public class JumpingSumoSoundBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private String soundName;

	public enum Sounds {
		DEFAULT, ROBOT, INSECT, MONSTER
	}

	public JumpingSumoSoundBrick() {
		soundName = Sounds.DEFAULT.name();
		addAllowedBrickField(BrickField.JUMPING_SUMO_VOLUME, R.id.brick_jumping_sumo_sound_edit_text);
	}

	public JumpingSumoSoundBrick(Sounds soundEnum, int volumeInPercent) {
		this(soundEnum, new Formula(volumeInPercent));
	}

	public JumpingSumoSoundBrick(Sounds soundEnum, Formula formula) {
		this();
		soundName = soundEnum.name();
		setFormulaWithBrickField(BrickField.JUMPING_SUMO_VOLUME, formula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_jumping_sumo_sound;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_jumping_sumo_select_sound_spinner, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = view.findViewById(R.id.brick_jumping_sumo_sound_spinner);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			soundName = Sounds.values()[position].name();
			return Unit.INSTANCE;
		}));
		spinner.setSelection(Sounds.valueOf(soundName).ordinal());
		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}
}
