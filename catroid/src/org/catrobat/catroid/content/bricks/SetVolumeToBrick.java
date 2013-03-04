/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class SetVolumeToBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;

	private Sprite sprite;
	private Formula volume;

	public SetVolumeToBrick(Sprite sprite, float volumeValue) {
		this.sprite = sprite;
		volume = new Formula(volumeValue);
	}

	public SetVolumeToBrick(Sprite sprite, Formula volume) {
		this.sprite = sprite;
		this.volume = volume;
	}

	public SetVolumeToBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		SoundManager.getInstance().setVolume(volume.interpretFloat(0.0f, 100.0f));
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		View view = View.inflate(context, R.layout.brick_set_volume_to, null);

		TextView text = (TextView) view.findViewById(R.id.brick_set_volume_to_prototype_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_set_volume_to_edit_text);
		volume.setTextFieldId(R.id.brick_set_volume_to_edit_text);
		volume.refreshTextField(view);
		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);

		edit.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_set_volume_to, null);
	}

	@Override
	public Brick clone() {
		return new SetVolumeToBrick(getSprite(), volume);
	}

	@Override
	public void onClick(View view) {
		FormulaEditorFragment.showFragment(view, this, volume);
	}
}
