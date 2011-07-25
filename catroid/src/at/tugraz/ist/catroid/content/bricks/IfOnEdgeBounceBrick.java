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

		double width = sprite.getCostume().getVirtuelWidth();
		int xPosition = sprite.getXPosition();
		int yPosition = sprite.getYPosition();

		if (sprite.getXPosition() < -Consts.MAX_REL_COORDINATES + width / 2) {

			sprite.setDirection(Math.abs(sprite.getDirection()));

			double newWidth = sprite.getCostume().getVirtuelWidth();
			xPosition = -Consts.MAX_REL_COORDINATES + (int) (newWidth / 2);

		} else if (sprite.getXPosition() > Consts.MAX_REL_COORDINATES - width / 2) {

			sprite.setDirection(-Math.abs(sprite.getDirection()));

			double newWidth = sprite.getCostume().getVirtuelWidth();
			xPosition = Consts.MAX_REL_COORDINATES - (int) (newWidth / 2);
		}

		double height = sprite.getCostume().getVirtuelHeight();

		if (sprite.getYPosition() > Consts.MAX_REL_COORDINATES - height / 2) {

			if (Math.abs(sprite.getDirection()) < 90) {
				sprite.setDirection(180 - sprite.getDirection());
			}

			double newHeight = sprite.getCostume().getVirtuelHeight();
			yPosition = Consts.MAX_REL_COORDINATES - (int) (newHeight / 2);

		} else if (sprite.getYPosition() < -Consts.MAX_REL_COORDINATES + height / 2) {

			if (Math.abs(sprite.getDirection()) > 90) {
				sprite.setDirection(180 - sprite.getDirection());
			}

			double newHeight = sprite.getCostume().getVirtuelHeight();
			yPosition = -Consts.MAX_REL_COORDINATES + (int) (newHeight / 2);
		}

		sprite.setXYPosition(xPosition, yPosition);
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
