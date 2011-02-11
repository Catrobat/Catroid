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
package at.tugraz.ist.catroid.content.brick;

import at.tugraz.ist.catroid.content.entities.PrimitiveWrapper;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public abstract class PlaceAtBrickBase implements BrickBase {
	private static final long serialVersionUID = 1L;
	protected PrimitiveWrapper<Integer> xPosition;
	protected PrimitiveWrapper<Integer> yPosition;
	private Sprite sprite;
	
	public PlaceAtBrickBase(Sprite sprite, int xPosition, int yPosition) {
		this.sprite    = sprite;
		this.xPosition = new PrimitiveWrapper<Integer>(xPosition);
		this.yPosition = new PrimitiveWrapper<Integer>(yPosition);
	}
	
	public void execute() {
		sprite.setXYPosition(xPosition.getValue(), yPosition.getValue());
	}


	public Sprite getSprite() {
		return this.sprite;
	}

	
	public int getXPosition() {
		return xPosition.getValue();
	}

	public int getYPosition() {
		return yPosition.getValue();
	}
}
