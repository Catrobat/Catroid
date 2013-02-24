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
import org.catrobat.catroid.io.SoundManager;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

public class StopAllSoundsBrick implements Brick {
	private static final long serialVersionUID = 1L;

	private Sprite sprite;

	private transient View view;

	private CheckBox checkbox;

	public StopAllSoundsBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public StopAllSoundsBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		SoundManager.getInstance().stopAllSounds();
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.brick_stop_all_sounds, null);
			checkbox = (CheckBox) view.findViewById(R.id.brick_stop_all_sounds_checkbox);
		}
		return view;
	}

	@Override
	public Brick clone() {
		return new StopAllSoundsBrick(getSprite());
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_stop_all_sounds, null);
	}

	@Override
	public void setCheckboxVisibility(int visibility) {
		checkbox.setVisibility(visibility);
	}
}
