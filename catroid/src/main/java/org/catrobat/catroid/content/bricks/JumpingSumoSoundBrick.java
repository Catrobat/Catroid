/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.SingleSeekbar;

import java.util.List;

public class JumpingSumoSoundBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	private String sound;
	private transient Sounds soundenum;
	private transient TextView editVolume;
	private transient SingleSeekbar volumeSeekbar =
			new SingleSeekbar(this, BrickField.JUMPING_SUMO_VOLUME, R.string.jumping_sumo_volume);

	public enum Sounds {
		DEFAULT, ROBOT, INSECT, MONSTER
	}

	public JumpingSumoSoundBrick() {
		addAllowedBrickField(BrickField.JUMPING_SUMO_VOLUME);
	}

	public JumpingSumoSoundBrick(Sounds sound, int volumeInPercent) {
		this.soundenum = sound;
		this.sound = soundenum.name();
		initializeBrickFields(new Formula(volumeInPercent));
	}

	public JumpingSumoSoundBrick(Sounds sound, Formula volumeInPercent) {
		this.soundenum = sound;
		this.sound = soundenum.name();

		initializeBrickFields(volumeInPercent);
	}

	private void initializeBrickFields(Formula volumeInPercent) {
		addAllowedBrickField(BrickField.JUMPING_SUMO_VOLUME);
		setFormulaWithBrickField(BrickField.JUMPING_SUMO_VOLUME, volumeInPercent);
	}

	protected Object readResolve() {
		if (sound != null) {
			soundenum = Sounds.valueOf(sound);
		}
		return this;
	}

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.JUMPING_SUMO;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_jumping_sumo_sound, null);
		TextView textVolume = (TextView) prototypeView.findViewById(R.id.brick_jumping_sumo_sound_edit_text);
		textVolume.setText(String.valueOf(BrickValues.JUMPING_SUMO_SOUND_BRICK_DEFAULT_VOLUME_PERCENT));

		Spinner soundSpinner = (Spinner) prototypeView.findViewById(R.id.brick_jumping_sumo_sound_spinner);
		soundSpinner.setFocusableInTouchMode(false);
		soundSpinner.setFocusable(false);
		soundSpinner.setEnabled(false);

		ArrayAdapter<CharSequence> soundAdapter = ArrayAdapter.createFromResource(context, R.array.brick_jumping_sumo_select_sound_spinner,
				android.R.layout.simple_spinner_item);
		soundAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		soundSpinner.setAdapter(soundAdapter);
		soundSpinner.setSelection(soundenum.ordinal());

		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new JumpingSumoSoundBrick(soundenum,
				getFormulaWithBrickField(BrickField.JUMPING_SUMO_VOLUME).clone());
	}

	@Override
	public View getCustomView(Context context, int brickId, BaseAdapter baseAdapter) {
		return volumeSeekbar.getView(context);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}

		view = View.inflate(context, R.layout.brick_jumping_sumo_sound, null);
		//view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_jumping_sumo_sound_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textVolume = (TextView) view.findViewById(R.id.brick_jumping_sumo_sound_text_view);
		editVolume = (TextView) view.findViewById(R.id.brick_jumping_sumo_sound_edit_text);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_VOLUME).setTextFieldId(R.id.brick_jumping_sumo_sound_edit_text);
		getFormulaWithBrickField(BrickField.JUMPING_SUMO_VOLUME).refreshTextField(view);

		textVolume.setVisibility(View.GONE);
		editVolume.setVisibility(View.VISIBLE);

		editVolume.setOnClickListener(this);

		ArrayAdapter<CharSequence> soundAdapter = ArrayAdapter.createFromResource(context, R.array.brick_jumping_sumo_select_sound_spinner,
				android.R.layout.simple_spinner_item);
		soundAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner soundSpinner = (Spinner) view.findViewById(R.id.brick_jumping_sumo_sound_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			soundSpinner.setClickable(true);
			soundSpinner.setEnabled(true);
		} else {
			soundSpinner.setClickable(false);
			soundSpinner.setEnabled(false);
		}

		soundSpinner.setAdapter(soundAdapter);
		soundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				soundenum = Sounds.values()[position];
				sound = soundenum.name();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		soundSpinner.setSelection(soundenum.ordinal());

		return view;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		if (isVolumeOnlyANumber()) {
			FormulaEditorFragment.showCustomFragment(view, this, BrickField.JUMPING_SUMO_VOLUME);
		} else {
			FormulaEditorFragment.showFragment(view, this, BrickField.JUMPING_SUMO_VOLUME);
		}
	}

	private boolean isVolumeOnlyANumber() {
		return getFormulaWithBrickField(BrickField.JUMPING_SUMO_VOLUME).getRoot().getElementType()
				== FormulaElement.ElementType.NUMBER;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createJumpingSumoSoundAction(sprite, soundenum, getFormulaWithBrickField(BrickField.JUMPING_SUMO_VOLUME)));
		return null;
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}
}
