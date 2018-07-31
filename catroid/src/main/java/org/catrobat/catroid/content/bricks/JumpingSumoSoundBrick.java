/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.List;

public class JumpingSumoSoundBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private String soundName;
	private transient Sounds soundType;

	public enum Sounds {
		DEFAULT, ROBOT, INSECT, MONSTER
	}

	public JumpingSumoSoundBrick(Sounds sound, int volumeInPercent) {
		this(sound, new Formula(volumeInPercent));
	}

	public JumpingSumoSoundBrick(Sounds sound, Formula formula) {
		this.soundType = sound;
		this.soundName = soundType.name();
		addAllowedBrickField(BrickField.JUMPING_SUMO_VOLUME, R.id.brick_jumping_sumo_sound_edit_text);
		setFormulaWithBrickField(BrickField.JUMPING_SUMO_VOLUME, formula);
	}

	protected Object readResolve() {
		if (soundName != null) {
			soundType = Sounds.valueOf(soundName);
		}
		return this;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_jumping_sumo_sound;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);

		Spinner soundSpinner = prototypeView.findViewById(R.id.brick_jumping_sumo_sound_spinner);
		soundSpinner.setFocusableInTouchMode(false);
		soundSpinner.setFocusable(false);
		soundSpinner.setEnabled(false);

		ArrayAdapter<CharSequence> soundAdapter = ArrayAdapter.createFromResource(context, R.array.brick_jumping_sumo_select_sound_spinner,
				android.R.layout.simple_spinner_item);
		soundAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		soundSpinner.setAdapter(soundAdapter);
		soundSpinner.setSelection(soundType.ordinal());

		return prototypeView;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		ArrayAdapter<CharSequence> soundAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_jumping_sumo_select_sound_spinner,
				android.R.layout.simple_spinner_item);
		soundAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner soundSpinner = view.findViewById(R.id.brick_jumping_sumo_sound_spinner);
		soundSpinner.setAdapter(soundAdapter);
		soundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				soundType = Sounds.values()[position];
				soundName = soundType.name();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		if (soundType == null) {
			readResolve();
		}

		soundSpinner.setSelection(soundType.ordinal());

		return view;
	}

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.JUMPING_SUMO;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createJumpingSumoSoundAction(sprite, soundType,
				getFormulaWithBrickField(BrickField.JUMPING_SUMO_VOLUME)));
		return null;
	}
}
