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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.SpinnerWithNewOption;
import org.catrobat.catroid.ui.recyclerview.dialog.NewSoundDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.List;

public class PlaySoundBrick extends BrickBaseType implements NewItemInterface<SoundInfo>,
		SpinnerWithNewOption.SpinnerSelectionListener<SoundInfo> {

	private static final long serialVersionUID = 1L;

	protected SoundInfo sound;

	private transient SpinnerWithNewOption<SoundInfo> spinner;

	public PlaySoundBrick() {
	}

	public SoundInfo getSound() {
		return sound;
	}

	public void setSound(SoundInfo sound) {
		this.sound = sound;
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		PlaySoundBrick clone = (PlaySoundBrick) super.clone();
		clone.spinner = null;
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_play_sound;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		onViewCreated(view);
		List<SoundInfo> sounds = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		spinner = new SpinnerWithNewOption<>(R.id.brick_play_sound_spinner, view, sounds, this);
		spinner.setSelection(sound);
		return view;
	}

	protected void onViewCreated(View view) {
	}

	@Override
	public View getPrototypeView(Context context) {
		super.getPrototypeView(context);
		return getView(context);
	}

	@Override
	public boolean onNewOptionClicked() {
		new NewSoundDialogFragment(this,
				ProjectManager.getInstance().getCurrentlyEditedScene(),
				ProjectManager.getInstance().getCurrentSprite()) {

			@Override
			public void onCancel(DialogInterface dialog) {
				super.onCancel(dialog);
				spinner.setSelection(sound);
			}
		}.show(((Activity) view.getContext()).getFragmentManager(), NewSoundDialogFragment.TAG);
		return false;
	}

	@Override
	public void addItem(SoundInfo item) {
		ProjectManager.getInstance().getCurrentSprite().getSoundList().add(item);
		sound = item;
		spinner.add(item);
	}

	@Override
	public void onItemSelected(SoundInfo item) {
		sound = item;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPlaySoundAction(sprite, sound));
		return null;
	}
}
