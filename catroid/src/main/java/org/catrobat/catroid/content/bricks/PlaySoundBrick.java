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
import android.widget.AdapterView;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.SpinnerAdapterWithNewOption;
import org.catrobat.catroid.ui.recyclerview.dialog.NewSoundDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

import java.util.ArrayList;
import java.util.List;

public class PlaySoundBrick extends BrickBaseType implements
		SpinnerAdapterWithNewOption.OnNewOptionInDropDownClickListener,
		NewItemInterface<SoundInfo> {

	private static final long serialVersionUID = 1L;

	protected SoundInfo sound;

	private transient int spinnerSelectionBuffer = 0;
	private transient Spinner spinner;
	private transient SpinnerAdapterWithNewOption spinnerAdapter;

	public PlaySoundBrick() {
	}

	public SoundInfo getSound() {
		return sound;
	}

	public void setSound(SoundInfo sound) {
		this.sound = sound;
	}

	protected void prepareView(View view) {
	}

	protected Spinner findSpinner(View view) {
		return view.findViewById(R.id.brick_play_sound_spinner);
	}

	@Override
	protected int getLayoutRes() {
		return R.layout.brick_play_sound;
	}

	@Override
	public View onCreateView(final Context context) {
		super.onCreateView(context);
		prepareView(view);

		spinner = findSpinner(view);
		spinnerAdapter = new SpinnerAdapterWithNewOption(context, getSoundNames());
		spinnerAdapter.setOnDropDownItemClickListener(this);

		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position != 0) {
					sound = getSoundByName(spinnerAdapter.getItem(position));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spinner.setSelection(spinnerAdapter.getPosition(sound != null ? sound.getName() : null));
		return view;
	}

	private SoundInfo getSoundByName(String name) {
		for (SoundInfo sound : ProjectManager.getInstance().getCurrentSprite().getSoundList()) {
			if (sound.getName().equals(name)) {
				return sound;
			}
		}
		return null;
	}

	private List<String> getSoundNames() {
		List<String> soundNames = new ArrayList<>();
		for (SoundInfo sound : ProjectManager.getInstance().getCurrentSprite().getSoundList()) {
			soundNames.add(sound.getName());
		}
		return soundNames;
	}

	@Override
	public boolean onNewOptionInDropDownClicked(View v) {
		spinnerSelectionBuffer = spinner.getSelectedItemPosition();
		new NewSoundDialogFragment(this,
				ProjectManager.getInstance().getCurrentlyEditedScene(),
				ProjectManager.getInstance().getCurrentSprite()) {

			@Override
			public void onCancel(DialogInterface dialog) {
				super.onCancel(dialog);
				spinner.setSelection(spinnerSelectionBuffer);
			}
		}.show(((Activity) v.getContext()).getFragmentManager(), NewSoundDialogFragment.TAG);
		return false;
	}

	@Override
	public void addItem(SoundInfo item) {
		ProjectManager.getInstance().getCurrentSprite().getSoundList().add(item);
		spinnerAdapter.add(item.getName());
		sound = item;
		spinner.setSelection(spinnerAdapter.getPosition(item.getName()));
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = super.getPrototypeView(context);
		prepareView(view);
		spinner = findSpinner(view);
		spinnerAdapter = new SpinnerAdapterWithNewOption(context, getSoundNames());
		spinner.setAdapter(spinnerAdapter);
		spinner.setSelection(spinnerAdapter.getPosition(sound != null ? sound.getName() : null));
		return view;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPlaySoundAction(sprite, sound));
		return null;
	}
}
