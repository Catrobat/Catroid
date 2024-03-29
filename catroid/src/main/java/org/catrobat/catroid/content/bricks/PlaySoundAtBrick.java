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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PlaySoundAtBrick extends FormulaBrick implements BrickSpinner.OnItemSelectedListener<SoundInfo>,
		NewItemInterface<SoundInfo> {

	private static final long serialVersionUID = 1L;
	protected SoundInfo sound;
	private transient BrickSpinner<SoundInfo> spinner;

	public PlaySoundAtBrick() {
		addAllowedBrickField(BrickField.PLAY_SOUND_AT, R.id.brick_play_sound_at_edit_text);
	}

	public PlaySoundAtBrick(double value) {
		this(new Formula(value));
	}

	private PlaySoundAtBrick(Formula formula) {
		this();
		setFormulaWithBrickField(BrickField.PLAY_SOUND_AT, formula);
	}

	public SoundInfo getSound() {
		return sound;
	}

	public void setSound(SoundInfo sound) {
		this.sound = sound;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		PlaySoundAtBrick clone = (PlaySoundAtBrick) super.clone();
		clone.spinner = null;
		return clone;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		if (sound == null || sound.getFile() == null || !sprite.getSoundList().contains(sound)) {
			return;
		}

		sequence.addAction(sprite.getActionFactory().createPlaySoundAtAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.PLAY_SOUND_AT), sound));
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_play_sound_at;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.new_option)));
		items.addAll(ProjectManager.getInstance().getCurrentSprite().getSoundList());
		spinner = new BrickSpinner<>(R.id.brick_play_sound_at_spinner, view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(sound);

		return view;
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (!(activity instanceof SpriteActivity)) {
			return;
		}
		((SpriteActivity) activity).registerOnNewSoundListener(this);
		((SpriteActivity) activity).handleAddSoundButton();
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable SoundInfo item) {
		sound = item;
	}

	@Override
	public void addItem(SoundInfo item) {
		spinner.add(item);
		spinner.setSelection(item);
	}
}
