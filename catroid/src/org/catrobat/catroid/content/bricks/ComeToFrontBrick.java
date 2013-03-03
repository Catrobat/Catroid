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

import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class ComeToFrontBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;

	public ComeToFrontBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public ComeToFrontBrick() {

	}

	@Override
	public void execute() {
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		int highestPosition = 0;
		for (Sprite sprite : spriteList) {
			if (highestPosition < sprite.look.zPosition) {
				highestPosition = sprite.look.zPosition;
				if (sprite == this.sprite) {
					highestPosition--;
				}
			}
		}
		if (highestPosition > highestPosition + 1) {
			sprite.look.zPosition = Integer.MAX_VALUE;
		} else {
			sprite.look.zPosition = highestPosition + 1;
		}
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.brick_go_to_front, null);
			checkbox = (CheckBox) view.findViewById(R.id.brick_go_to_front_checkbox);
			final Brick brickInstance = this;

			checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checked = !checked;
					adapter.handleCheck(brickInstance, checked);
				}
			});
		}

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_go_to_front_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		return view;
	}

	@Override
	public Brick clone() {
		return new ComeToFrontBrick(getSprite());
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_go_to_front, null);
	}
}
