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
package at.tugraz.ist.catroid.content.sprite;

import java.io.Serializable;

public class Sprite implements Serializable {
	private String name;
	private int xPosition;
	private int yPosition;
	private int zPosition;
	private double scale;
	private boolean isVisible;
	private static final long serialVersionUID = 3196641561527900213L;

	public Sprite(String name) {
		this.name = name;
		this.xPosition = 0;
		this.yPosition = 0;
		this.scale = 1.0;
		this.isVisible = true;
	}

	public Sprite(String name, int xPosition, int yPosition) {
		this.name = name;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.scale = 1.0;
		this.isVisible = true;
	}

	public String getName() {
		return name;
	}

	public int getXPosition() {
		return xPosition;
	}

	public int getYPosition() {
		return yPosition;
	}
	
	public int getZPosition() {
		return zPosition;
	}

	public double getScale() {
		return scale;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setPosition(int xPosition, int yPosition) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}
	
	public void setZPosition(int zPosition) {
		this.zPosition = zPosition;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public void show() {
		isVisible = true;
	}

	public void hide() {
		isVisible = false;
	}
}
