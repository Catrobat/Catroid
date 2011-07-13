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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Sprite;

public class IfOnEdgeBounceBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	public IfOnEdgeBounceBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public void execute() {

		double width = sprite.getCostume().getRelativeWidth();
		double height = sprite.getCostume().getRelativeHeight();

		if (sprite.getXPosition() < -Consts.MAX_REL_COORDINATES + width / 2) {

			sprite.setXYPosition(-Consts.MAX_REL_COORDINATES + (int) Math.floor(width / 2), sprite.getYPosition());
			sprite.setDirection(-sprite.getDirection());

		} else if (sprite.getXPosition() > Consts.MAX_REL_COORDINATES - width / 2) {

			sprite.setXYPosition(Consts.MAX_REL_COORDINATES - (int) Math.floor(width / 2), sprite.getYPosition());
			sprite.setDirection(-sprite.getDirection());
		}

		if (sprite.getYPosition() > Consts.MAX_REL_COORDINATES - height / 2) {

			sprite.setXYPosition(sprite.getXPosition(), Consts.MAX_REL_COORDINATES - (int) Math.floor(height / 2));
			sprite.setDirection(180 - sprite.getDirection());

		} else if (sprite.getYPosition() < -Consts.MAX_REL_COORDINATES + height / 2) {

			sprite.setXYPosition(sprite.getXPosition(), -Consts.MAX_REL_COORDINATES + (int) Math.floor(height / 2));
			sprite.setDirection(180 - sprite.getDirection());
		}
	}

	public Sprite getSprite() {
		return sprite;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.construction_brick_if_on_edge_bounce, null);
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.toolbox_brick_if_on_edge_bounce, null);
	}

	@Override
	public Brick clone() {
		return new IfOnEdgeBounceBrick(sprite);
	}

}
