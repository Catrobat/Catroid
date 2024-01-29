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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "Play Jumping Sumo")
public class JumpingSumoSoundBrick extends FormulaBrick implements UpdateableSpinnerBrick {

	private static final long serialVersionUID = 1L;
	private static final String SOUND_CATLANG_PARAMETER_NAME = "sound";
	private static final BiMap<String, String> CATLANG_SPINNER_VALUES = HashBiMap.create(new HashMap<String, String>()
	{{
		put("DEFAULT", "normal");
		put("ROBOT", "robot");
		put("INSECT", "insect");
		put("MONSTER", "monster");
	}});

	private String soundName;

	public enum Sounds {
		DEFAULT, ROBOT, INSECT, MONSTER
	}

	public JumpingSumoSoundBrick() {
		soundName = Sounds.DEFAULT.name();
		addAllowedBrickField(BrickField.JUMPING_SUMO_VOLUME, R.id.brick_jumping_sumo_sound_edit_text, "volume");
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

	@Override
	public void updateSelectedItem(Context context, int spinnerId, String itemName, int itemIndex) {
		Sounds[] sounds = Sounds.values();
		if (itemIndex >= 0 && itemIndex < sounds.length) {
			soundName = sounds[itemIndex].name();
		}
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(SOUND_CATLANG_PARAMETER_NAME)) {
			return new HashMap.SimpleEntry<>(SOUND_CATLANG_PARAMETER_NAME, CATLANG_SPINNER_VALUES.get(soundName));
		}
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>();
		requiredArguments.add("sound");
		requiredArguments.addAll(super.getRequiredCatlangArgumentNames());
		return requiredArguments;
	}
}
