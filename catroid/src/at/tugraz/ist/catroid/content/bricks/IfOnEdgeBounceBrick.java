/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Sprite;

public class IfOnEdgeBounceBrick implements Brick {

	private Sprite sprite;

	public IfOnEdgeBounceBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public void execute() {

		int width = sprite.getCostume().getImageWidthHeight().first;
		int height = sprite.getCostume().getImageWidthHeight().second;

		if (sprite.getXPosition() < -Consts.MAX_REL_COORDINATES) {
			sprite.setXYPosition(-Consts.MAX_REL_COORDINATES, sprite.getYPosition());
			sprite.setDirection(180 - sprite.getDirection());
		} else if (sprite.getXPosition() + width > Consts.MAX_REL_COORDINATES) {
			sprite.setXYPosition(Consts.MAX_REL_COORDINATES - width, sprite.getYPosition());
			sprite.setDirection(180 - sprite.getDirection());
		}

		if (sprite.getYPosition() > Consts.MAX_REL_COORDINATES) {
			sprite.setXYPosition(sprite.getYPosition(), -Consts.MAX_REL_COORDINATES);
			sprite.setDirection(360 - sprite.getDirection());
		} else if (sprite.getYPosition() - height < -Consts.MAX_REL_COORDINATES) {
			sprite.setXYPosition(sprite.getXPosition(), Consts.MAX_REL_COORDINATES + height);
			sprite.setDirection(360 - sprite.getDirection());
		}
	}

	public Sprite getSprite() {
		return sprite;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public View getPrototypeView(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Brick clone() {
		return new IfOnEdgeBounceBrick(sprite);
	}

}
