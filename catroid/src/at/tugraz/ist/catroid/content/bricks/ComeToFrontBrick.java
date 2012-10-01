/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

public class ComeToFrontBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private transient View view;

	public ComeToFrontBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public ComeToFrontBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		int highestPosition = 0;
		for (Sprite sprite : spriteList) {
			if (highestPosition < sprite.costume.zPosition) {
				highestPosition = sprite.costume.zPosition;
				if (sprite == this.sprite) {
					highestPosition--;
				}
			}
		}
		if (highestPosition > highestPosition + 1) {
			sprite.costume.zPosition = Integer.MAX_VALUE;
		} else {
			sprite.costume.zPosition = highestPosition + 1;
		}
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.brick_come_to_front, null);
		}

		return view;
	}

	@Override
	public Brick clone() {
		return new ComeToFrontBrick(getSprite());
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_come_to_front, null);
	}
}
